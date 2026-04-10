package com.sigpro.service;

import com.sigpro.dto.MaterialMapper;
import com.sigpro.dto.MaterialRequestDTO;
import com.sigpro.dto.MaterialResponseDTO;
import com.sigpro.exception.PresupuestoInsuficienteException;
import com.sigpro.model.Material;
import com.sigpro.model.Proyecto;
import com.sigpro.repository.MaterialRepository;
import com.sigpro.repository.ProyectoRepository;
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
    public MaterialResponseDTO registrarMaterial(@Valid MaterialRequestDTO dto) {
        Proyecto proyecto = proyectoRepository.findById(dto.getProyectoId())
                .orElseThrow(() -> new IllegalArgumentException("El proyecto no existe"));

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
        return MaterialMapper.toResponseDto(guardado);
    }

    /**
     * Lista los materiales de un proyecto (solo lectura).
     */
    @Transactional(readOnly = true)
    public List<MaterialResponseDTO> listarMaterialesPorProyecto(Long proyectoId, String nombre) {
        List<Material> lista;
        if (nombre != null && !nombre.isBlank()) {
            lista = materialRepository.findByProyectoIdAndNombreContainingIgnoreCase(proyectoId, nombre.trim());
        } else {
            lista = materialRepository.findByProyectoId(proyectoId);
        }
        return lista.stream()
                .map(MaterialMapper::toResponseDto)
                .collect(Collectors.toList());
    }
}