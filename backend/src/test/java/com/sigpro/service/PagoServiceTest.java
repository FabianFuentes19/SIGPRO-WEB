package com.sigpro.service;

import com.sigpro.dto.PagoConAlertaResponseDTO;
import com.sigpro.dto.PagoRequestDTO;
import com.sigpro.model.Proyecto;
import com.sigpro.model.Usuario;
import com.sigpro.model.Pago;
import com.sigpro.repository.PagoRepository;
import com.sigpro.repository.ProyectoRepository;
import com.sigpro.repository.ProyectoUsuarioRepository;
import com.sigpro.repository.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PagoServiceTest {

    @Mock
    private PagoRepository pagoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ProyectoRepository proyectoRepository;

    @Mock
    private ProyectoUsuarioRepository proyectoUsuarioRepository;

    @Mock
    private Authentication auth;

    @InjectMocks
    private PagoService pagoService;

    @Test
    @DisplayName("CP-PAGO-001: Registro de pago exitoso (Rol LIDER)")
    void registrarPagoExitoso() {
        // 1. Arrange
        PagoRequestDTO request = new PagoRequestDTO();
        request.setMatriculaUsuario("2023002");
        request.setProyectoId(1L);
        request.setMonto(new BigDecimal("1000.00"));
        request.setFechaCorte(LocalDate.now());

        // Simulamos el Rol de LIDER en la seguridad
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_LIDER");
        doReturn(List.of(authority)).when(auth).getAuthorities();

        Usuario usuario = new Usuario();
        usuario.setMatricula("2023002");

        Proyecto proyecto = new Proyecto();
        proyecto.setId(1L);
        proyecto.setEstado("ACTIVO");
        proyecto.setPresupuesto(new BigDecimal("5000.00")); // Presupuesto suficiente
        proyecto.setPresupuestoInicial(new BigDecimal("5000.00"));
        proyecto.setPresupuestoAutorizado(new BigDecimal("5000.00"));

        when(usuarioRepository.findByMatricula("2023002")).thenReturn(Optional.of(usuario));
        when(proyectoRepository.findById(1L)).thenReturn(Optional.of(proyecto));
        when(pagoRepository.save(any(Pago.class))).thenAnswer(i -> i.getArgument(0));

        // 2. Act
        PagoConAlertaResponseDTO response = pagoService.registrarPago(request, auth);

        // 3. Assert
        assertNotNull(response);
        // Verificamos que el presupuesto del proyecto disminuyó: 5000 - 1000 = 4000
        assertEquals(0, new BigDecimal("4000.00").compareTo(proyecto.getPresupuesto()));
        verify(pagoRepository).save(any(Pago.class));
        verify(proyectoRepository).save(proyecto);
    }

    @Test
    @DisplayName("CP-PAGO-002: Error - Usuario no autorizado (Rol MIEMBRO)")
    void registrarPagoErrorRol() {
        // 1. Arrange
        PagoRequestDTO request = new PagoRequestDTO();
        // Simulamos un rol que NO es LIDER
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_MIEMBRO");
        doReturn(List.of(authority)).when(auth).getAuthorities();

        // 2. Act & 3. Assert
        assertThrows(SecurityException.class, () -> {
            pagoService.registrarPago(request, auth);
        });

        // Verificamos que nunca se llegó a interactuar con los repositorios
        verifyNoInteractions(usuarioRepository, proyectoRepository, pagoRepository);
    }

    @Test
    @DisplayName("CP-PAGO-003: Error - Presupuesto insuficiente")
    void registrarPagoPresupuestoExcedido() {
        // 1. Arrange (LIDER logueado)
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_LIDER");
        doReturn(List.of(authority)).when(auth).getAuthorities();

        PagoRequestDTO request = new PagoRequestDTO();
        request.setMatriculaUsuario("M1");
        request.setProyectoId(1L);
        request.setMonto(new BigDecimal("2000.00")); // El pago es de 2000

        Proyecto proyecto = new Proyecto();
        proyecto.setId(1L);
        proyecto.setEstado("ACTIVO");
        proyecto.setPresupuesto(new BigDecimal("500.00")); // Solo hay 500

        when(usuarioRepository.findByMatricula("M1")).thenReturn(Optional.of(new Usuario()));
        when(proyectoRepository.findById(1L)).thenReturn(Optional.of(proyecto));

        // 2. Act & 3. Assert
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            pagoService.registrarPago(request, auth);
        });

        assertTrue(ex.getMessage().contains("excede del presupuesto"));
    }

    @Test
    @DisplayName("CP-PAGO-004: Historial - Pago Proporcional (Menos de 15 días)")
    void historialPagoProporcional() {
        // Arrange
        String matricula = "M1";
        Usuario usuario = new Usuario();
        usuario.setMatricula(matricula);
        usuario.setFechaIngreso(LocalDate.of(2024, 1, 10)); // Entró el 10 de enero
        usuario.setSalarioQuincenal(new BigDecimal("1500.00"));
        usuario.setEstado("ACTIVO");

        Proyecto proyecto = new Proyecto();
        proyecto.setFechaFin(LocalDate.of(2024, 12, 31));

        when(auth.getAuthorities()).thenAnswer(i -> List.of(new SimpleGrantedAuthority("ROLE_LIDER")));
        when(auth.getPrincipal()).thenReturn("LIDER_MAT");
        when(usuarioRepository.findByMatricula(matricula)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.findByMatricula("LIDER_MAT")).thenReturn(Optional.of(new Usuario()));
        when(proyectoRepository.findByLiderId(any())).thenReturn(proyecto);

        // Act
        var vouchers = pagoService.obtenerHistorialVouchers(matricula, auth);

        // Assert
        assertFalse(vouchers.isEmpty());
        // Primer voucher (10 al 15 de enero) = 6 días.
        // 1500 * 6 / 15 = 600.
        assertEquals(0, new BigDecimal("600.00").compareTo(vouchers.get(0).getMontoEsperado()));
    }

    @Test
    @DisplayName("CP-PAGO-005: Consultar Pagos Miembro - Error No Autorizado")
    void consultarPagosMiembroNoAutorizado() {
        // Arrange
        String matriculaMiembro = "MIE001";
        Usuario lider = new Usuario();
        lider.setId(10L);

        Proyecto proyecto = new Proyecto();
        proyecto.setId(1L);

        when(auth.getAuthorities()).thenAnswer(i -> List.of(new SimpleGrantedAuthority("ROLE_LIDER")));
        when(auth.getPrincipal()).thenReturn("LID001");
        when(usuarioRepository.findByMatricula("LID001")).thenReturn(Optional.of(lider));
        when(proyectoRepository.findByLiderId(10L)).thenReturn(proyecto);
        // El miembro NO pertenece al proyecto
        when(proyectoUsuarioRepository.existsByProyectoIdAndUsuarioMatricula(1L, matriculaMiembro)).thenReturn(false);

        // Act & Assert
        assertThrows(SecurityException.class, () -> {
            pagoService.consultarPagosMiembro(matriculaMiembro, auth);
        });
    }
}
