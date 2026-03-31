package com.sigpro.controller;

import com.sigpro.dto.AuthResponseDTO;
import com.sigpro.dto.LoginRequestDTO;
import com.sigpro.dto.UsuarioRequestDTO;
import com.sigpro.dto.UsuarioResponseDTO;
import com.sigpro.model.Usuario;
import com.sigpro.security.JwtUtil;
import com.sigpro.service.AuthService;
import jakarta.validation.Valid;
import com.sigpro.dto.UsuarioMapper;
import com.sigpro.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*") // Para interoperatividad Web/Móvil
public class AuthController {

    // Mínimo 8, 1 mayúscula, 1 minúscula, 1 número, 1 especial
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,}$"
    );

    @Autowired
    private AuthService authService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO request) {
        try {
            AuthResponseDTO response = authService.validarLogin(request);

            UsuarioResponseDTO usuarioResponseDto = new UsuarioResponseDTO();
            usuarioResponseDto.setMatricula(response.getMatricula());
            usuarioResponseDto.setRolNombre(response.getRol());

            String token = jwtUtil.generateToken(usuarioResponseDto);
            response.setToken(token);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UsuarioRequestDTO dto) {
        return registrar(dto, null);
    }

    /** Registro de Líder: se ignora el rol del cliente y se fuerza ROL_ID_FK = 2 (Líder). */
    @PostMapping("/register/lider")
    public ResponseEntity<?> registerLider(@Valid @RequestBody UsuarioRequestDTO dto) {
        return registrar(dto, 2L);
    }

    /** Registro de Miembro: se ignora el rol del cliente y se fuerza ROL_ID_FK = 3 (Miembro). */
    @PostMapping("/register/miembro")
    public ResponseEntity<?> registerMiembro(@Valid @RequestBody UsuarioRequestDTO dto) {
        return registrar(dto, 3L);
    }

    private ResponseEntity<?> registrar(@Valid UsuarioRequestDTO dto, Long rolIdForzado) {
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

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request) {
        try {
            String matricula = request.get("matricula");
            authService.solicitarRestablecimiento(matricula);

            Map<String, String> respuesta = new HashMap<>();
            respuesta.put("mensaje", "Si la matrícula existe, se ha enviado un correo con las instrucciones.");
            return ResponseEntity.ok(respuesta);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
        try {
            String matricula = request.get("matricula");
            String token = request.get("token");
            String nuevaContrasena = request.get("nuevaContrasena");

            // validación de campo vacío
            if(nuevaContrasena == null || nuevaContrasena.trim().isEmpty()){
                throw new Exception("La nueva contraseña no puede estar vacía");
            }

            // validación de contraseña segura
            if (!PASSWORD_PATTERN.matcher(nuevaContrasena).matches()) {
                throw new Exception("\"La nueva contraseña no cumple con los criterios de seguridad: mínimo 8 caracteres, una mayúscula, una minúscula, un número y un carácter especial\"");
            }

            authService.restablecerPassword(matricula, token, nuevaContrasena);

            Map<String, String> respuesta = new HashMap<>();
            respuesta.put("mensaje", "Contraseña actualizada correctamente.");
            return ResponseEntity.ok(respuesta);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
}
