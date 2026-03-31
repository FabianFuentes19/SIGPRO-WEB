package com.sigpro.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class VoucherDTO {
    private Integer numeroQuincena;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private BigDecimal montoEsperado;
    private String estado;
    private Long pagoId;
    private LocalDate fechaPagoReal;
    private BigDecimal montoPagado;
    private String puesto;
}
