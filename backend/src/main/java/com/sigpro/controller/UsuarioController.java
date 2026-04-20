package com.sigpro.controller;

import com.sigpro.dto.PaginatedResponse;
import com.sigpro.dto.UsuarioRequestDTO;
import com.sigpro.dto.UsuarioResponseDTO;
import com.sigpro.dto.UsuarioUpdateDTO;
import com.sigpro.model.Usuario;
import com.sigpro.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> listarTodos() {
        try {
            List<UsuarioResponseDTO> usuarios = usuarioService.listarUsuarios();
            return ResponseEntity.ok(usuarios);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "No fue posible consultar los usuarios");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/rol/{rolNombre}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> listarPorRol(
            @PathVariable String rolNombre,
            @RequestParam(required = false) String buscar,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            PaginatedResponse<UsuarioResponseDTO> respuesta = usuarioService.obtenerUsuariosPorRol(rolNombre, buscar, page, size);
            return ResponseEntity.ok(respuesta);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "No fue posible consultar los usuarios por rol");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/lider/{matriculaLider}")
    public ResponseEntity<?> listarMiembrosPorLider(@PathVariable String matriculaLider) {
        try {
            List<UsuarioResponseDTO> miembros = usuarioService.listarMiembrosPorLider(matriculaLider);
            return ResponseEntity.ok(miembros);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "No fue posible consultar los miembros");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/{matricula}")
    public ResponseEntity<?> buscarPorMatricula(@PathVariable String matricula, Authentication auth) {
        try {
            String requesterMatricula = (String) auth.getPrincipal(); // En este proyecto el principal es la matrícula
            boolean isAdmin = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMINISTRADOR"));

            // Solo el dueño o el administrador pueden ver el detalle
            if (!isAdmin && !requesterMatricula.equals(matricula)) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "No autorizado: No puedes consultar datos de otros usuarios");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
            }

            UsuarioResponseDTO usuario = usuarioService.obtenerDetallePorMatricula(matricula);
            return ResponseEntity.ok(usuario);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "No fue posible consultar el usuario");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PutMapping("/{matricula}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'LIDER')")
    public ResponseEntity<?> actualizar(
            @PathVariable String matricula,
            @Valid @RequestBody UsuarioUpdateDTO dto,
            Authentication auth) {
        try {
            boolean isAdmin = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMINISTRADOR"));

            if (!isAdmin) {
                // lideres solo pueden actualizar miembros
                UsuarioResponseDTO destino = usuarioService.obtenerDetallePorMatricula(matricula);
                if (!"Miembro".equalsIgnoreCase(destino.getRolNombre())) {
                    Map<String, String> error = new HashMap<>();
                    error.put("error", "No autorizado: Los líderes solo pueden gestionar miembros");
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
                }
            }

            UsuarioResponseDTO actualizado = usuarioService.modificarUsuario(matricula, dto);
            return ResponseEntity.ok(actualizado);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "No fue posible actualizar el usuario");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PatchMapping("/{matricula}/desactivar")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'LIDER')")
    public ResponseEntity<?> desactivar(@PathVariable String matricula, Authentication auth) {
        try {
            boolean isAdmin = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMINISTRADOR"));

            if (!isAdmin) {
                UsuarioResponseDTO destino = usuarioService.obtenerDetallePorMatricula(matricula);
                if (!"Miembro".equalsIgnoreCase(destino.getRolNombre())) {
                    Map<String, String> error = new HashMap<>();
                    error.put("error", "No autorizado: Los líderes solo pueden desactivar miembros");
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
                }
            }

            UsuarioResponseDTO actualizado = usuarioService.bajaLogica(matricula);

            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("mensaje", "Usuario desactivado correctamente");
            respuesta.put("usuario", actualizado);
            return ResponseEntity.ok(respuesta);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "No fue posible desactivar el usuario");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PatchMapping("/{matricula}/activar")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> activar(@PathVariable String matricula) {
        try {
            UsuarioResponseDTO actualizado = usuarioService.activarUsuario(matricula);

            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("mensaje", "Usuario activado correctamente");
            respuesta.put("usuario", actualizado);
            return ResponseEntity.ok(respuesta);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "No fue posible activar el usuario");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }



    @PutMapping("/{matricula}/cambiar-contrasena")
    public ResponseEntity<?> cambiarContrasena(
            @PathVariable String matricula,
            @RequestBody Map<String, String> request,
            Authentication auth) {
        try {
            String requesterMatricula = (String) auth.getPrincipal();
            boolean isAdmin = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMINISTRADOR"));

            // Solo el dueño o el administrador pueden cambiar la contraseña
            if (!isAdmin && !requesterMatricula.equals(matricula)) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "No autorizado: No puedes cambiar la contraseña de otro usuario");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
            }

            String actual = request.get("actual");
            String nueva = request.get("nueva");

            if (nueva == null || nueva.trim().isEmpty()) {
                throw new IllegalArgumentException("La nueva contraseña no puede estar vacía");
            }

            usuarioService.cambiarPassword(matricula, actual, nueva);

            Map<String, String> respuesta = new HashMap<>();
            respuesta.put("mensaje", "Contraseña actualizada correctamente.");
            return ResponseEntity.ok(respuesta);

        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "No fue posible cambiar la contraseña");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }


    /** Registro genérico de usuario (solo ADMINISTRADOR). */
    @PostMapping("/registrar")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> registrar(@Valid @RequestBody UsuarioRequestDTO dto) {
        return procesarRegistro(dto, null);
    }

    /** Registro de Líder: se fuerza ROL_ID_FK = 2 (Líder). */
    @PostMapping("/registrar/lider")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> registrarLider(@Valid @RequestBody UsuarioRequestDTO dto) {
        return procesarRegistro(dto, 2L);
    }

    /** Registro de Miembro: se fuerza ROL_ID_FK = 3 (Miembro). */
    @PostMapping("/registrar/miembro")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> registrarMiembro(@Valid @RequestBody UsuarioRequestDTO dto) {
        return procesarRegistro(dto, 3L);
    }

    private ResponseEntity<?> procesarRegistro(@Valid UsuarioRequestDTO dto, Long rolIdForzado) {
        try {
            if (rolIdForzado != null) {
                dto.setRolId(rolIdForzado);
            }
            Usuario usuario = usuarioService.registrarUsuario(dto);

            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("mensaje", "Usuario registrado correctamente");
            respuesta.put("matricula", usuario.getMatricula());
            respuesta.put("rol", usuario.getRol() != null ? usuario.getRol().getNombre() : null);

            return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "No fue posible registrar el usuario");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }


    @GetMapping("lideres/sin-proyecto")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<List<UsuarioResponseDTO>> listarLideresSinProyecto(){
        List<UsuarioResponseDTO> lideres = usuarioService.consultarLideresSinProyecto();
        return ResponseEntity.ok(lideres);
    }

}