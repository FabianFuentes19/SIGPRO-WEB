package com.sigpro.dto;

import com.sigpro.model.Material;

public final class MaterialMapper {

    private MaterialMapper() {
    }

    public static MaterialResponseDTO toResponseDto(Material material) {
        if (material == null) {
            return null;
        }
        MaterialResponseDTO dto = new MaterialResponseDTO();
        dto.setId(material.getId());
        dto.setDescripcion(material.getDescripcion());
        dto.setMonto(material.getMonto());
        dto.setCantidad(material.getCantidad());
        dto.setCostoTotal(material.getCostoTotal());
        dto.setProyectoId(material.getProyecto() != null ? material.getProyecto().getId() : null);
        return dto;
    }
}
