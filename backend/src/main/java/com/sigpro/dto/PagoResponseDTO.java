package com.sigpro.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class PagoResponseDTO {
    private Long id;
    private Long proyectoId;
    private String matriculaUsuario;
    private BigDecimal monto; // Use este para manejar la cantidad real
    private LocalDate fecha;
}
