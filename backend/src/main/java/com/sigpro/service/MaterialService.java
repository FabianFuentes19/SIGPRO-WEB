package com.sigpro.service;

import com.sigpro.dto.AlertaDTO;
import com.sigpro.dto.MaterialConAlertaResponseDTO;
import com.sigpro.dto.MaterialMapper;
import com.sigpro.dto.MaterialRequestDTO;
import com.sigpro.dto.MaterialResponseDTO;
import com.sigpro.exception.PresupuestoInsuficienteException;
import com.sigpro.model.Material;
import com.sigpro.model.Proyecto;
import com.sigpro.repository.MaterialRepository;
import com.sigpro.repository.ProyectoRepository;
import com.sigpro.util.AlertaCalculator;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Validated
public class MaterialService {

    private static final int MONEDA_SCALE = 2;

    @Autowired
    private MaterialRepository materialRepository;

    @Autowired
    private ProyectoRepository proyectoRepository;

    /**
     * Registra un material, validando que exista saldo suficiente en el proyecto.
     * No incluye actualización ni eliminación (DFR).
     */
    @Transactional
    public MaterialConAlertaResponseDTO registrarMaterial(@Valid MaterialRequestDTO dto) {
        if (dto.getMonto() == null || dto.getMonto().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("El monto debe ser mayor a cero");
        }
        if (dto.getCantidad() == null || dto.getCantidad() <= 0) {
            throw new RuntimeException("La cantidad debe ser mayor a cero");
        }

        Proyecto proyecto = proyectoRepository.findById(dto.getProyectoId())
                .orElseThrow(() -> new IllegalArgumentException("El proyecto no existe"));

        if (!"ACTIVO".equalsIgnoreCase(proyecto.getEstado())) {
            throw new IllegalStateException("No se puede agregar materila ya que el proyecto está " + proyecto.getEstado());
        }

        BigDecimal costoTotal = dto.getMonto()
                .multiply(BigDecimal.valueOf(dto.getCantidad()))
                .setScale(MONEDA_SCALE, RoundingMode.HALF_UP);

        BigDecimal disponible = proyecto.getPresupuesto();
        if (disponible == null) {
            disponible = BigDecimal.ZERO;
        }

        if (disponible.compareTo(costoTotal) < 0) {
            throw new PresupuestoInsuficienteException(
                    "Presupuesto insuficiente. Disponible: " + disponible + ", requerido: " + costoTotal
            );
        }

        Material material = new Material();
        material.setProyecto(proyecto);
        material.setNombre(dto.getNombre().trim());
        material.setMonto(dto.getMonto().setScale(MONEDA_SCALE, RoundingMode.HALF_UP));
        material.setCantidad(dto.getCantidad());
        material.setCostoTotal(costoTotal);

        Material guardado = materialRepository.save(material);

        proyecto.setPresupuesto(proyecto.getPresupuesto().subtract(costoTotal));
        proyectoRepository.save(proyecto);

        AlertaDTO alerta = AlertaCalculator.calcularAlerta(proyecto);
        return new MaterialConAlertaResponseDTO(MaterialMapper.toResponseDto(guardado), alerta);
    }

    @Transactional(readOnly = true)
    public List<MaterialResponseDTO> listarTodos() {
        return materialRepository.findAll().stream()
                .map(MaterialMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public List<MaterialResponseDTO> buscarPorNombre(String nombre) {
        String normalizado = nombre == null ? "" : nombre.trim();
        if (normalizado.isEmpty()) {
            return listarTodos();
        }
        return materialRepository.findByNombreContainingIgnoreCase(normalizado).stream()
                .map(MaterialMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Lista los materiales de un proyecto (solo lectura).
     */
    @Transactional(readOnly = true)
    public List<MaterialResponseDTO> listarMaterialesPorProyecto(Long proyectoId, String nombre) {
        List<Material> lista;
        if (nombre != null && !nombre.isBlank()) {
            lista = materialRepository.findByProyectoIdAndNombreSinAcentos(proyectoId, nombre.trim());
        } else {
            lista = materialRepository.findByProyectoId(proyectoId);
        }
        return lista.stream()
                .map(MaterialMapper::toResponseDto)
                .collect(Collectors.toList());
    }
}

/*
Dejar de filtrar todos los materiales en memoria y usar la búsqueda ya soportada por la API.
 */

/*
- **Cambio mínimo requerido en backend**:
- Asegurar que el endpoint de materiales acepte y procese correctamente el parámetro `nombre` materiales controller.
- Si hace falta, normalizar trim o comportamiento cuando la búsqueda va vacía.
*/ 