package com.sigpro.service;

import com.sigpro.dto.UsuarioRequestDTO;
import com.sigpro.dto.UsuarioUpdateDTO;
import com.sigpro.model.Rol;
import com.sigpro.model.Usuario;
import com.sigpro.repository.UsuarioRepository;
import com.sigpro.repository.RolRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceExamTest {

    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private RolRepository rolRepository;
    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    @DisplayName("EXAM-USR-001: Registro - Error Contraseña Débil")
    void registroContrasenaDebil() {
        // Arrange
        UsuarioRequestDTO dto = new UsuarioRequestDTO();
        dto.setMatricula("2023001");
        dto.setContrasena("12345");
        dto.setNombreCompleto("Prueba");
        dto.setGrupo("A");
        dto.setCarrera("ITI");
        dto.setCuatrimestre(5);
        dto.setRolId(1L);

        when(usuarioRepository.findByMatricula("2023001")).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.registrarUsuario(dto);
        });
        assertTrue(ex.getMessage().contains("criterios de seguridad"));
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("EXAM-USR-002: Bája Lógica - Cambio de estado")
    void bajaLogicaExitoso() {
        // Arrange
        String matricula = "2023001";
        Usuario usuario = new Usuario();
        usuario.setMatricula(matricula);
        usuario.setEstado("ACTIVO");
        usuario.setGrupo("A");
        usuario.setCarrera("ITI");
        usuario.setCuatrimestre(5);

        when(usuarioRepository.findByMatricula(matricula)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        usuarioService.bajaLogica(matricula);

        // Assert
        assertEquals("INACTIVO", usuario.getEstado());
        verify(usuarioRepository).save(usuario);
    }

    @Test
    @DisplayName("EXAM-USR-003: Modificar Usuario - Éxito")
    void modificarUsuarioExitoso() {
        // Arrange
        String matricula = "2023001";
        Usuario usuario = new Usuario();
        usuario.setMatricula(matricula);
        usuario.setNombreCompleto("Nombre Original");

        UsuarioUpdateDTO dto = new UsuarioUpdateDTO();
        dto.setNombreCompleto("Nombre Editado");
        dto.setPuesto("Desarrollador");
        dto.setSalarioQuincenal(new BigDecimal("5000.00"));

        when(usuarioRepository.findByMatricula(matricula)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        usuarioService.modificarUsuario(matricula, dto);

        // Assert
        assertEquals("Nombre Editado", usuario.getNombreCompleto());
        assertEquals("Desarrollador", usuario.getPuesto());
        verify(usuarioRepository).save(usuario);
    }

    @Test
    @DisplayName("EXAM-USR-004: Modificar - Error Salario Negativo")
    void modificarUsuarioErrorSalario() {
        // Arrange
        String matricula = "2023001";
        UsuarioUpdateDTO dto = new UsuarioUpdateDTO();
        dto.setSalarioQuincenal(new BigDecimal("-100.00"));

        when(usuarioRepository.findByMatricula(matricula)).thenReturn(Optional.of(new Usuario()));

        // Act & Assert
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.modificarUsuario(matricula, dto);
        });
        assertTrue(ex.getMessage().contains("mayor a cero"));
    }

    @Test
    @DisplayName("EXAM-USR-005: Cambiar Password - Éxito")
    void cambiarPasswordExitoso() {
        // Arrange
        String matricula = "2023001";
        String actual = "OldPass123!";
        String nueva = "NewPass123!";
        Usuario usuario = new Usuario();
        usuario.setContrasena("EncodedOldPass");

        when(usuarioRepository.findByMatricula(matricula)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches(actual, "EncodedOldPass")).thenReturn(true);
        when(passwordEncoder.encode(nueva)).thenReturn("EncodedNewPass");

        // Act
        usuarioService.cambiarPassword(matricula, actual, nueva);

        // Assert
        assertEquals("EncodedNewPass", usuario.getContrasena());
        verify(usuarioRepository).save(usuario);
    }

    @Test
    @DisplayName("EXAM-USR-006: Cambiar Password - Error Actual Incorrecta")
    void cambiarPasswordErrorActual() {
        // Arrange
        String matricula = "2023001";
        Usuario usuario = new Usuario();
        usuario.setContrasena("EncodedOldPass");

        when(usuarioRepository.findByMatricula(matricula)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        // Act & Assert
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.cambiarPassword(matricula, "wrong", "NewPass123!");
        });
        assertEquals("La contraseña actual es incorrecta", ex.getMessage());
    }

    @Test
    @DisplayName("EXAM-USR-007: Registrar con Rol - Éxito")
    void registrarConRolExitoso() {
        // Arrange
        UsuarioRequestDTO dto = new UsuarioRequestDTO();
        dto.setMatricula("REG001");
        dto.setContrasena("ValidPass123!");
        dto.setNombreCompleto("Reg");
        dto.setGrupo("A");
        dto.setCarrera("ITI");
        dto.setCuatrimestre(1);

        Rol rol = new Rol();
        rol.setId(2L);
        rol.setNombre("Miembro");

        when(rolRepository.findByNombreIgnoreCase("Miembro")).thenReturn(Optional.of(rol));
        when(rolRepository.findById(2L)).thenReturn(Optional.of(rol));
        when(usuarioRepository.findByMatricula("REG001")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("hashed");

        // Act
        usuarioService.registrarUsuarioConRol(dto, "Miembro");

        // Assert
        verify(usuarioRepository).save(any(Usuario.class));
    }
}
