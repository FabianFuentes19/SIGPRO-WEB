package com.sigpro.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class UsuarioDTO {
    private String nombreCompleto;
    private String contrasena;
    private String grupo;
    private String matricula;
    private String carrera;
    private Integer cuatrimestre;
    private String puesto;
    private BigDecimal salarioQuincenal;
    private String estado;
    private Long rolId;
    private String rolNombre;
}
