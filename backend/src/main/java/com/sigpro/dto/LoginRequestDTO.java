package com.sigpro.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequestDTO {
    @NotBlank(message = "La matrícula es obligatoria")
    private String matricula;

    @NotBlank(message = "La contraseña es obligatoria")
    private String contrasena;
}
