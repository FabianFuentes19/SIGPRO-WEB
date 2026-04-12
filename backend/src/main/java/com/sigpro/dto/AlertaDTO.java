package com.sigpro.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlertaDTO {
    private String tipo; // "advertencia" (amarilla), "error" (roja)
    private String mensaje;
    private BigDecimal porcentajeRestante;
    private BigDecimal montoRestante;
    private BigDecimal presupuestoTotal;
}
