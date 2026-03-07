package com.sigpro.service;

import com.sigpro.model.Usuario;
import com.sigpro.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public Usuario validarLogin(String matricula, String contrasena) throws Exception {
        System.out.println("[AuthService] Validando login para matricula: " + matricula);

        // Verificación de campos vacíos
        if (matricula == null || matricula.isEmpty() || contrasena == null || contrasena.isEmpty()) {
            throw new Exception("Campos incompletos");
        }

        // Buscar usuario
        Usuario usuario = usuarioRepository.findByMatricula(matricula)
                .orElseThrow(() -> new Exception("Credenciales incorrectas"));

        // Verificar estado
        if (!"Activo".equalsIgnoreCase(usuario.getEstado())) {
            throw new Exception("Cuenta inactiva");
        }

        // Validar contraseña con BCrypt
        boolean matches = passwordEncoder.matches(contrasena.trim(), usuario.getContrasena().trim());
        if (!matches) {
            throw new Exception("Credenciales incorrectas");
        }

        System.out.println("Login exitoso para: " + matricula);
        return usuario;
    }
}
