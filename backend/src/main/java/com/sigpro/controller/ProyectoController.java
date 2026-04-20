package com.sigpro.controller;

import com.sigpro.dto.PaginatedResponse;
import com.sigpro.dto.ProyectoRequestDTO;
import com.sigpro.dto.ProyectoResponseDTO;
import com.sigpro.dto.UsuarioRequestDTO;
import com.sigpro.dto.UsuarioResponseDTO;
import com.sigpro.dto.AlertaDTO;
import com.sigpro.model.ProyectoUsuario;
import com.sigpro.model.Proyecto;
import com.sigpro.service.ProyectoService;
import com.sigpro.repository.ProyectoRepository;
import com.sigpro.util.AlertaCalculator;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/proyectos")
public class ProyectoController {

    @Autowired
    private ProyectoService proyectoService;

    @Autowired
    private ProyectoRepository proyectoRepository;

    @GetMapping
    public ResponseEntity<?> consultarTodos(
            @RequestParam(required = false) String buscar,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication auth) {
        try {
            PaginatedResponse<ProyectoResponseDTO> respuesta = proyectoService.consultarTodos(page, size, buscar, auth);
            return ResponseEntity.ok(respuesta);
        } catch (SecurityException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "No fue posible consultar los proyectos");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/buscar")
    public ResponseEntity<?> buscarPorNombre(@RequestParam String nombre, Authentication auth) {
        try {
            List<ProyectoResponseDTO> proyectos = proyectoService.buscarPorNombre(nombre, auth);
            return ResponseEntity.ok(proyectos);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (SecurityException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "No fue posible buscar el proyecto");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PostMapping
    public ResponseEntity<?> crearProyecto(@Valid @RequestBody ProyectoRequestDTO dto, Authentication auth) {
        try {
            ProyectoResponseDTO nuevoProyecto = proyectoService.crearProyecto(dto, auth);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoProyecto);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (SecurityException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "No fue posible crear el proyecto");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editarProyecto(@PathVariable Long id, @Valid @RequestBody ProyectoRequestDTO dto, Authentication auth) {
        try {
            ProyectoResponseDTO proyectoActualizado = proyectoService.editarProyecto(id, dto, auth);
            return ResponseEntity.ok(proyectoActualizado);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (SecurityException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "No fue posible actualizar el proyecto");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/mi-proyecto/lider")
    public ResponseEntity<?> consultarProyectoLider(Authentication auth) {
        try {
            ProyectoResponseDTO proyecto = proyectoService.consultarProyectoLider(auth);
            return ResponseEntity.ok(proyecto);
        } catch (IllegalArgumentException e) {
            // Si no tiene proyecto, devolvemos un objeto vacío en lugar de un error 404
            if (e.getMessage().contains("proyecto")) {
                return ResponseEntity.ok(new ProyectoResponseDTO());
            }
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (SecurityException e) {
            System.out.println("[DEBUG] Security Error: " + e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", "No fue posible consultar el proyecto del líder");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/mi-proyecto/miembro")
    public ResponseEntity<?> consultarProyectoMiembro(Authentication auth) {
        try {
            ProyectoResponseDTO proyecto = proyectoService.consultarProyectoMiembro(auth);
            return ResponseEntity.ok(proyecto);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (SecurityException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "No fue posible consultar el proyecto del miembro");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/mi-equipo")
    public ResponseEntity<?> consultarEquipoCompleto(Authentication auth) {
        try {
            List<UsuarioResponseDTO> equipo = proyectoService.consultarEquipoCompleto(auth);
            return ResponseEntity.ok(equipo);
        } catch (SecurityException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al consultar el equipo del proyecto");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> consultarDetalle(@PathVariable Long id, Authentication auth) {
        try {
            ProyectoResponseDTO proyecto = proyectoService.obtenerDetalleProyecto(id, auth);
            return ResponseEntity.ok(proyecto);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (SecurityException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "No fue posible consultar el detalle del proyecto");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PostMapping("/{proyectoId}/miembros")
    public ResponseEntity<?> registrarMiembro(@PathVariable Long proyectoId, @Valid @RequestBody UsuarioRequestDTO dto, Authentication auth) {
        try {
            ProyectoUsuario nuevoMiembro = proyectoService.registrarMiembro(proyectoId, dto, auth);
            
            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("mensaje", "Miembro registrado y agregado al proyecto correctamente");
            respuesta.put("matricula", nuevoMiembro.getUsuario().getMatricula());
            
            return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (SecurityException e) {
             Map<String, String> error = new HashMap<>();
             error.put("error", e.getMessage());
             return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "No fue posible registrar al miembro");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/{id}/alertas")
    public ResponseEntity<?> consultarAlertas(@PathVariable Long id, Authentication auth) {
        try {
            Proyecto proyecto = proyectoRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Proyecto no encontrado"));

            // Verificar que el usuario tenga acceso al proyecto (líder o miembro)
            // Por ahora permitimos acceso si está autenticado
            // Puedes agregar validación adicional si lo deseas

            AlertaDTO alerta = AlertaCalculator.calcularAlerta(proyecto);
            
            if (alerta != null) {
                return ResponseEntity.ok(alerta);
            } else {
                // Si no hay alerta, retornar una respuesta vacía o indicar que está bien
                Map<String, String> respuesta = new HashMap<>();
                respuesta.put("estado", "sin_alerta");
                respuesta.put("mensaje", "El presupuesto es suficiente");
                return ResponseEntity.ok(respuesta);
            }

        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "No fue posible consultar las alertas");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }







}
