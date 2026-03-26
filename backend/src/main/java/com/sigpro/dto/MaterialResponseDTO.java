package com.sigpro.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MaterialResponseDTO {

    private Long id;
    private String nombre;
    private BigDecimal monto;
    private Integer cantidad;
    private BigDecimal costoTotal;
    private Long proyectoId;
}
