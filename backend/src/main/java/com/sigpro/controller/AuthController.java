package com.sigpro.controller;

import com.sigpro.model.Usuario;
import com.sigpro.security.JwtUtil;
import com.sigpro.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*") // Para interoperatividad Web/Móvil
public class AuthController {
    @Autowired
    private AuthService authService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credenciales) {
        try {
            Usuario usuario = authService.validarLogin(
                    credenciales.get("matricula"),
                    credenciales.get("contrasena")
            );

            String token = jwtUtil.generateToken(usuario);

            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("token", token);
            respuesta.put("rol", usuario.getRol().getNombre());

            return ResponseEntity.ok(respuesta);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }
}

