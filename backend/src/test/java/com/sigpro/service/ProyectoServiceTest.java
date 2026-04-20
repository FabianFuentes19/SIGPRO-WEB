package com.sigpro.service;

import com.sigpro.dto.ProyectoRequestDTO;
import com.sigpro.dto.ProyectoResponseDTO;
import com.sigpro.dto.UsuarioRequestDTO;
import com.sigpro.model.Proyecto;
import com.sigpro.model.ProyectoUsuario;
import com.sigpro.model.Rol;
import com.sigpro.model.Usuario;
import com.sigpro.repository.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class ProyectoServiceTest {

    @Mock
    private ProyectoRepository proyectoRepository;
    @Mock
    private ProyectoUsuarioRepository proyectoUsuarioRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private MaterialRepository materialRepository;
    @Mock
    private PagoRepository pagoRepository;
    @Mock
    private Authentication auth;

    @InjectMocks
    private ProyectoService proyectoService;

    @Test
    @DisplayName("CP-PROY-001: Crear proyecto exitoso")
    void crearProyectoExitoso() {
        // Arrange
        ProyectoRequestDTO dto = new ProyectoRequestDTO();
        dto.setNombre("Nuevo Proyecto");
        dto.setFechaInicio(LocalDate.now());
        dto.setFechaFin(LocalDate.now().plusMonths(3));
        dto.setPresupuesto(new BigDecimal("10000.00"));
        dto.setLiderMatricula("LID001");

        // Mock Seguridad
        when(auth.getAuthorities()).thenAnswer(i -> List.of(new SimpleGrantedAuthority("ROLE_ADMINISTRADOR")));

        // Mock Líder
        Rol rolLider = new Rol();
        rolLider.setNombre("LIDER");
        Usuario lider = new Usuario();
        lider.setId(10L);
        lider.setMatricula("LID001");
        lider.setRol(rolLider);

        when(usuarioRepository.findByMatricula("LID001")).thenReturn(Optional.of(lider));
        when(proyectoRepository.findByLiderId(10L)).thenReturn(null);
        when(proyectoRepository.save(any(Proyecto.class))).thenAnswer(i -> i.getArgument(0));

        // Mocks para evitar NullPointerException en el recálculo de presupuesto
        lenient().when(materialRepository.sumCostoTotalByProyectoId(any())).thenReturn(BigDecimal.ZERO);
        lenient().when(pagoRepository.sumMontoByProyectoId(any())).thenReturn(BigDecimal.ZERO);

        // Act
        ProyectoResponseDTO response = proyectoService.crearProyecto(dto, auth);

        // Assert
        assertNotNull(response);
        assertEquals("Nuevo Proyecto", response.getNombre());
        verify(proyectoRepository).save(any(Proyecto.class));
        verify(proyectoUsuarioRepository).save(any(ProyectoUsuario.class));
    }

    @Test
    @DisplayName("CP-PROY-002: Error - Fechas inválidas")
    void crearProyectoErrorFechas() {
        // Arrange
        ProyectoRequestDTO dto = new ProyectoRequestDTO();
        dto.setFechaInicio(LocalDate.now().plusDays(10));
        dto.setFechaFin(LocalDate.now()); // Fin ANTES que inicio

        when(auth.getAuthorities()).thenAnswer(i -> List.of(new SimpleGrantedAuthority("ROLE_ADMINISTRADOR")));

        // Act & Assert
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            proyectoService.crearProyecto(dto, auth);
        });
        assertTrue(ex.getMessage().contains("fecha de fin no puede ser anterior"));
    }

    @Test
    @DisplayName("CP-PROY-003: Error - Líder ya tiene proyecto")
    void crearProyectoErrorLiderOcupado() {
        // Arrange
        ProyectoRequestDTO dto = new ProyectoRequestDTO();
        dto.setLiderMatricula("LID001");
        dto.setFechaInicio(LocalDate.now());
        dto.setFechaFin(LocalDate.now().plusDays(1));

        when(auth.getAuthorities()).thenAnswer(i -> List.of(new SimpleGrantedAuthority("ROLE_ADMINISTRADOR")));

        Usuario lider = new Usuario();
        lider.setId(10L);
        lider.setRol(new Rol());
        lider.getRol().setNombre("LIDER");

        when(usuarioRepository.findByMatricula("LID001")).thenReturn(Optional.of(lider));
        // El mock dice que el líder YA tiene un proyecto
        when(proyectoRepository.findByLiderId(10L)).thenReturn(new Proyecto());

        // Mocks para evitar NullPointerException en el recálculo de presupuesto
        lenient().when(materialRepository.sumCostoTotalByProyectoId(any())).thenReturn(BigDecimal.ZERO);
        lenient().when(pagoRepository.sumMontoByProyectoId(any())).thenReturn(BigDecimal.ZERO);

        // Act & Assert
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            proyectoService.crearProyecto(dto, auth);
        });
        assertEquals("El líder ya tiene un proyecto asignado", ex.getMessage());
    }

    @Test
    @DisplayName("CP-PROY-004: Editar Proyecto - Recálculo de presupuesto")
    void editarProyectoCalculoPresupuesto() {
        // Arrange
        Long proyectoId = 1L;
        ProyectoRequestDTO dto = new ProyectoRequestDTO();
        dto.setPresupuesto(new BigDecimal("5000.00")); // Nuevo presupuesto autorizado

        Proyecto proyectoExistente = new Proyecto();
        proyectoExistente.setId(proyectoId);
        proyectoExistente.setPresupuesto(new BigDecimal("1000.00"));

        when(auth.getAuthorities()).thenAnswer(i -> List.of(new SimpleGrantedAuthority("ROLE_ADMINISTRADOR")));
        when(proyectoRepository.findById(proyectoId)).thenReturn(Optional.of(proyectoExistente));
        
        // Simulamos gastos previos
        when(materialRepository.sumCostoTotalByProyectoId(proyectoId)).thenReturn(new BigDecimal("1000.00"));
        when(pagoRepository.sumMontoByProyectoId(proyectoId)).thenReturn(new BigDecimal("1500.00"));
        // Gasto total = 2500. Presupuesto disponible = 5000 - 2500 = 2500.

        when(proyectoRepository.save(any(Proyecto.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        proyectoService.editarProyecto(proyectoId, dto, auth);

        // Assert
        // El presupuesto disponible debe ser 2500
        assertEquals(0, new BigDecimal("2500.00").compareTo(proyectoExistente.getPresupuesto()));
        assertEquals(0, new BigDecimal("5000.00").compareTo(proyectoExistente.getPresupuestoAutorizado()));
    }

    @Test
    @DisplayName("CP-PROY-005: Registrar Miembro - Éxito")
    void registrarMiembroExitoso() {
        // Arrange
        Long proyectoId = 1L;
        UsuarioRequestDTO dto = new UsuarioRequestDTO();
        dto.setMatricula("MIE001");

        Usuario lider = new Usuario();
        lider.setId(10L);
        lider.setMatricula("LID001");

        Proyecto proyecto = new Proyecto();
        proyecto.setId(proyectoId);
        proyecto.setEstado("ACTIVO");
        proyecto.setLider(lider);

        when(auth.getAuthorities()).thenAnswer(i -> List.of(new SimpleGrantedAuthority("ROLE_LIDER")));
        when(auth.getPrincipal()).thenReturn("LID001");
        when(usuarioRepository.findByMatricula("LID001")).thenReturn(Optional.of(lider));
        when(proyectoRepository.findById(proyectoId)).thenReturn(Optional.of(proyecto));

        // Act
        proyectoService.registrarMiembro(proyectoId, dto, auth);

        // Assert
        verify(proyectoUsuarioRepository).save(any(ProyectoUsuario.class));
    }

    @Test
    @DisplayName("CP-PROY-006: Registrar Miembro - Error No es el Líder")
    void registrarMiembroErrorNoEsLider() {
        // Arrange
        Long proyectoId = 1L;
        Usuario liderReal = new Usuario();
        liderReal.setId(10L);
        Usuario liderIntruso = new Usuario();
        liderIntruso.setId(99L); // ID diferente

        Proyecto proyecto = new Proyecto();
        proyecto.setId(proyectoId);
        proyecto.setLider(liderReal);
        proyecto.setEstado("ACTIVO");

        when(auth.getAuthorities()).thenAnswer(i -> List.of(new SimpleGrantedAuthority("ROLE_LIDER")));
        when(auth.getPrincipal()).thenReturn("INTRUSO");
        when(usuarioRepository.findByMatricula("INTRUSO")).thenReturn(Optional.of(liderIntruso));
        when(proyectoRepository.findById(proyectoId)).thenReturn(Optional.of(proyecto));

        // Act & Assert
        assertThrows(SecurityException.class, () -> {
            proyectoService.registrarMiembro(proyectoId, new UsuarioRequestDTO(), auth);
        });
    }

    @Test
    @DisplayName("CP-PROY-007: Consultar Equipo Completo - Éxito")
    void consultarEquipoCompletoExitoso() {
        // Arrange
        String matriculaLider = "LID001";
        Usuario lider = new Usuario();
        lider.setId(10L);
        lider.setMatricula(matriculaLider);

        Proyecto proyecto = new Proyecto();
        proyecto.setId(1L);

        Usuario miembro = new Usuario();
        miembro.setEstado("ACTIVO");

        ProyectoUsuario pu = new ProyectoUsuario();
        pu.setUsuario(miembro);

        when(auth.getAuthorities()).thenAnswer(i -> List.of(new SimpleGrantedAuthority("ROLE_LIDER")));
        when(auth.getPrincipal()).thenReturn(matriculaLider);
        when(usuarioRepository.findByMatricula(matriculaLider)).thenReturn(Optional.of(lider));
        when(proyectoRepository.findByLiderId(10L)).thenReturn(proyecto);
        when(proyectoUsuarioRepository.findByProyectoId(1L)).thenReturn(List.of(pu));

        // Act
        var equipo = proyectoService.consultarEquipoCompleto(auth);

        // Assert
        assertFalse(equipo.isEmpty());
        verify(proyectoUsuarioRepository).findByProyectoId(1L);
    }
}
