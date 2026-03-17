package com.sigpro.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ProyectoDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private String objetivoGeneral;
    private Long liderId;
    private String liderMatricula;
    private String liderNombre;
    private BigDecimal presupuesto;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String estado;
    private java.util.List<UsuarioDTO> miembros;
}
