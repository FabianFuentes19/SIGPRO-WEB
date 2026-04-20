package com.sigpro.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class ProyectoResponseDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private String objetivoGeneral;
    private Long liderId;
    private String liderMatricula;
    private String liderNombre;
    private BigDecimal presupuesto;
    private BigDecimal presupuestoInicial;
    private BigDecimal presupuestoAutorizado;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String estado;
    private List<UsuarioResponseDTO> miembros;

    // agrego esto
    // Agregue nuevo examen
    private Long totalMiembros;



}
