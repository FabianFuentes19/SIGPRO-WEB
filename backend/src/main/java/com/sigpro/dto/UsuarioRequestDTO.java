package com.sigpro.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class UsuarioRequestDTO {

    @NotBlank(message = "La matrícula es obligatoria")
    private String matricula;

    @NotBlank(message = "El nombre completo es obligatorio")
    private String nombreCompleto;

    @NotBlank(message = "La contraseña es obligatoria")
    private String contrasena;

    @NotBlank(message = "El grupo es obligatorio")
    private String grupo;

    @NotBlank(message = "La carrera es obligatoria")
    private String carrera;

    @NotNull(message = "El cuatrimestre es obligatorio")
    @Positive(message = "El cuatrimestre debe ser mayor a cero")
    private Integer cuatrimestre;

    private String puesto;

    @Positive(message = "El salario quincenal debe ser mayor a cero")
    private BigDecimal salarioQuincenal;

    private Long rolId;
}