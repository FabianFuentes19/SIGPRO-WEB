package com.sigpro.service;

import com.sigpro.dto.AuthResponseDTO;
import com.sigpro.dto.LoginRequestDTO;
import com.sigpro.model.PasswordResetToken;
import com.sigpro.model.Rol;
import com.sigpro.model.Usuario;
import com.sigpro.repository.PasswordResetTokenRepository;
import com.sigpro.repository.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordResetTokenRepository tokenRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    // --- PRUEBAS DE LOGIN ---

    @Test
    @DisplayName("CP-LOGIN-001: Login Exitoso")
    void loginExitoso() throws Exception {
        LoginRequestDTO request = new LoginRequestDTO();
        request.setMatricula("2023001");
        request.setContrasena("Password123!");

        Rol rol = new Rol();
        rol.setNombre("LIDER");

        Usuario usuarioFake = new Usuario();
        usuarioFake.setMatricula("2023001");
        usuarioFake.setContrasena("HASH_ENCRIPTADO");
        usuarioFake.setEstado("ACTIVO");
        usuarioFake.setRol(rol);

        when(usuarioRepository.findByMatricula("2023001")).thenReturn(Optional.of(usuarioFake));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        AuthResponseDTO response = authService.validarLogin(request);

        assertNotNull(response);
        assertEquals("2023001", response.getMatricula());
        assertEquals("LIDER", response.getRol());
    }

    @Test
    @DisplayName("CP-LOGIN-003: Usuario No Encontrado")
    void loginUsuarioNoEncontrado() {
        LoginRequestDTO request = new LoginRequestDTO();
        request.setMatricula("999");
        request.setContrasena("123");

        when(usuarioRepository.findByMatricula("999")).thenReturn(Optional.empty());

        Exception ex = assertThrows(Exception.class, () -> authService.validarLogin(request));
        assertEquals("Credenciales incorrectas", ex.getMessage());
    }

    @Test
    @DisplayName("CP-LOGIN-005: Contraseña Incorrecta")
    void loginContrasenaIncorrecta() {
        LoginRequestDTO request = new LoginRequestDTO();
        request.setMatricula("2023001");
        request.setContrasena("WrongPass");

        Usuario usuario = new Usuario();
        usuario.setMatricula("2023001");
        usuario.setContrasena("HASH");
        usuario.setEstado("ACTIVO");

        when(usuarioRepository.findByMatricula("2023001")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        Exception ex = assertThrows(Exception.class, () -> authService.validarLogin(request));
        assertEquals("Credenciales incorrectas", ex.getMessage());
    }

    // --- PRUEBAS DE RECUPERACIÓN ---

    @Test
    @DisplayName("CP-REQ-001: Solicitud de recuperación exitosa")
    void solicitarRestablecimientoExitoso() throws Exception {
        String matricula = "2023001";
        Usuario usuario = new Usuario();
        usuario.setMatricula(matricula);

        when(usuarioRepository.findByMatricula(matricula)).thenReturn(Optional.of(usuario));

        authService.solicitarRestablecimiento(matricula);

        // Verificamos acciones (porque el método es void)
        verify(tokenRepository).save(any(PasswordResetToken.class));
        verify(emailService).enviarCorreoRecuperacion(eq(matricula), anyString());
    }

    @Test
    @DisplayName("CP-REQ-002: Solicitud fallida — Usuario inexistente")
    void solicitarRestablecimientoFallido() {
        String matricula = "999";
        when(usuarioRepository.findByMatricula("999")).thenReturn(Optional.empty());

        Exception ex = assertThrows(Exception.class, () -> authService.solicitarRestablecimiento(matricula));
        assertEquals("Usuario no encontrado", ex.getMessage());

        // Verificamos que NO se llamó a lo demás por seguridad
        verify(tokenRepository, never()).save(any());
        verify(emailService, never()).enviarCorreoRecuperacion(anyString(), anyString());
    }
}
