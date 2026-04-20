package com.sigpro.service;

import com.sigpro.dto.MaterialConAlertaResponseDTO;
import com.sigpro.dto.MaterialRequestDTO;
import com.sigpro.dto.MaterialResponseDTO;
import com.sigpro.exception.PresupuestoInsuficienteException;
import com.sigpro.model.Material;
import com.sigpro.model.Proyecto;
import com.sigpro.repository.MaterialRepository;
import com.sigpro.repository.ProyectoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MaterialServiceTest {

    @Mock
    private MaterialRepository materialRepository;
    @Mock
    private ProyectoRepository proyectoRepository;

    @InjectMocks
    private MaterialService materialService;

    @Test
    @DisplayName("CP-MAT-001: Registro exitoso")
    public void registroExitoso() {
        // arrange
        MaterialRequestDTO request = new MaterialRequestDTO();
        request.setNombre("Plumones");
        request.setCantidad(2);
        request.setMonto(new BigDecimal("25.00"));
        request.setProyectoId(1L);

        Proyecto proyecto = new Proyecto();
        proyecto.setId(1L);
        proyecto.setEstado("ACTIVO");
        proyecto.setPresupuesto(new BigDecimal("100.00"));
        proyecto.setPresupuestoInicial(new BigDecimal("100.00"));
        proyecto.setPresupuestoAutorizado(new BigDecimal("100.00"));

        when(proyectoRepository.findById(1L)).thenReturn(Optional.of(proyecto));
        when(materialRepository.save(any(Material.class))).thenAnswer(i -> i.getArgument(0));

        //
        MaterialConAlertaResponseDTO response = materialService.registrarMaterial(request);

        // 3. Assert
        assertNotNull(response);
        assertEquals(0, new BigDecimal("50.00").compareTo(proyecto.getPresupuesto()));
        verify(materialRepository).save(any(Material.class));
        verify(proyectoRepository).save(proyecto);
    }

    @Test
    @DisplayName("CP-MAT-002: Error - Presupuesto Insuficiente")
    void presupuestoInsuficiente() {
        // 1. Arrange
        MaterialRequestDTO request = new MaterialRequestDTO();
        request.setNombre("Laptop");
        request.setCantidad(1);
        request.setMonto(new BigDecimal("1500.00")); 
        request.setProyectoId(1L);

        Proyecto proyecto = new Proyecto();
        proyecto.setId(1L);
        proyecto.setEstado("ACTIVO");
        proyecto.setPresupuesto(new BigDecimal("1000.00")); // Solo hay 1000

        when(proyectoRepository.findById(1L)).thenReturn(Optional.of(proyecto));

        // 2. Act & 3. Assert
        assertThrows(PresupuestoInsuficienteException.class, () -> {
            materialService.registrarMaterial(request);
        });

        // Verificamos que NO se guardó nada
        verify(materialRepository, never()).save(any());
    }

    @Test
    @DisplayName("CP-MAT-003: Error - Proyecto Inactivo")
    void proyectoInactivo() {
        MaterialRequestDTO request = new MaterialRequestDTO();
        request.setProyectoId(1L);
        request.setMonto(new BigDecimal("10.00"));
        request.setCantidad(1);

        Proyecto proyecto = new Proyecto();
        proyecto.setEstado("TERMINADO"); // Inactivo

        when(proyectoRepository.findById(1L)).thenReturn(Optional.of(proyecto));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            materialService.registrarMaterial(request);
        });

        assertTrue(ex.getMessage().contains("TERMINADO"));
    }

    @Test
    @DisplayName("CP-MAT-004: Listar materiales con filtro")
    void listarMaterialesConFiltro() {
        // Arrange
        Long proyectoId = 1L;
        String filtro = "Plumones";
        
        Material m = new Material();
        m.setNombre("Plumones");
        
        when(materialRepository.findByProyectoIdAndNombreSinAcentos(proyectoId, filtro)).thenReturn(List.of(m));

        // Act
        List<MaterialResponseDTO> lista = materialService.listarMaterialesPorProyecto(proyectoId, filtro);

        // Assert
        assertFalse(lista.isEmpty());
        assertEquals("Plumones", lista.get(0).getNombre());
    }
}
