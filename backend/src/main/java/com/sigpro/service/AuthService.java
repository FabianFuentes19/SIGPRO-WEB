package com.sigpro.service;

import com.sigpro.dto.UsuarioDTO;
import com.sigpro.dto.UsuarioMapper;
import com.sigpro.dto.UsuarioDTO;
import com.sigpro.dto.UsuarioMapper;
import com.sigpro.model.PasswordResetToken;
import com.sigpro.model.Usuario;
import com.sigpro.repository.PasswordResetTokenRepository;
import com.sigpro.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class AuthService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public UsuarioDTO validarLogin(String matricula, String contrasena) throws Exception {
        System.out.println("[AuthService] Validando login para matricula: " + matricula);

        // Verificación de campos vacíos
        if (matricula == null || matricula.isEmpty() || contrasena == null || contrasena.isEmpty()) {
            throw new Exception("Campos incompletos");
        }

        // Buscar usuario
        Usuario usuario = usuarioRepository.findByMatricula(matricula)
                .orElseThrow(() -> new Exception("Credenciales incorrectas"));

        // Verificar estado
        if (!"ACTIVO".equalsIgnoreCase(usuario.getEstado())) {
            throw new Exception("Cuenta inactiva");
        }

        // Validar contraseña con BCrypt
        boolean matches = passwordEncoder.matches(contrasena.trim(), usuario.getContrasena().trim());
        if (!matches) {
            throw new Exception("Credenciales incorrectas");
        }

        System.out.println("Login exitoso para: " + matricula);

        // Retornar a través del Mapper por seguridad (la contraseña no se expone)
        return UsuarioMapper.toDto(usuario);
    }

    // @Transactional asegura que si ocurre un error la inserción en BD se deshaga.
    @Transactional
    public void solicitarRestablecimiento(String matricula) throws Exception {
        //se valida que el usuario este registrado
        Usuario usuario = usuarioRepository.findByMatricula(matricula)
                .orElseThrow(() -> new Exception("Usuario no encontrado"));

        // borra el registro si el usuario dio click 2 veces
        tokenRepository.deleteByUsuario(usuario);

        // generación de código aleatorio
        String otp = String.format("%06d", new Random().nextInt(999999));

        // se crea la fila para la tabla
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(otp);
        resetToken.setUsuario(usuario);

        // define el tiempo de vida del código
        resetToken.setExpiryDate(LocalDateTime.now().plusMinutes(5));

        // se guarda el registro en bd
        tokenRepository.save(resetToken);

        emailService.enviarCorreoRecuperacion(matricula, otp);
    }

    @Transactional
    public void restablecerPassword(String matricula, String token, String nuevaContrasena) throws Exception {
        // se vuelve a verificar que realmente el usaurio exista
        Usuario usuario = usuarioRepository.findByMatricula(matricula)
                .orElseThrow(() -> new Exception("Usuario no encontrado"));

        // se verific a que en la tabal del token este el código ingresado
        PasswordResetToken resetToken = tokenRepository.findByTokenAndUsuario(token, usuario)
                .orElseThrow(() -> new Exception("El código es inválido"));

        // se valida si no ha expirado el tiempo de vida
        if (resetToken.isExpired()) {
            tokenRepository.delete(resetToken); // se elimina
            throw new Exception("El código ha expirado, por favor solicita uno nuevo");
        }

        // se guarda la contraseña encriptada
        String hash = passwordEncoder.encode(nuevaContrasena.trim());
        usuario.setContrasena(hash);
        usuarioRepository.save(usuario); // Guardamos la nueva contraseña en la tabla USUARIOS

        //  se borra el token por seguridad
        tokenRepository.delete(resetToken);
        System.out.println("Contraseña restablecida correctamente para la matricula: " + matricula);
    }
}
