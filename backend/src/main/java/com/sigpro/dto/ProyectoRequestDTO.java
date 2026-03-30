package com.sigpro.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ProyectoRequestDTO {
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 150)
    private String nombre;

    @NotBlank(message = "La descripción del proyecto es obligatoria")
    @Size(max = 1000)
    private String descripcion;

    @NotBlank(message = "El objetivo general del proyecto es obligatoria")
    @Size(max = 1000)
    private String objetivoGeneral;

    @NotNull(message = "El presupuesto es obligatorio")
    @Positive(message = "El presupuesto debe ser mayor a cero")
    private BigDecimal presupuesto;

    @NotNull(message = "La fecha de inicio es obligatoria")
    @FutureOrPresent(message = "La fecha de inicio no puede ser anterior a la fecha actual")
    private LocalDate fechaInicio;

    @NotNull(message = "La fecha de fin es obligatoria")
    @Future(message = "La fecha de fin debe ser posterior a la fecha actual")
    private LocalDate fechaFin;

    private String liderMatricula;

    private Long liderId;
}
