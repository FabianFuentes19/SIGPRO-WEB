package com.sigpro.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "USUARIOS")
@Data
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_PK")
    private Long id;

    @Column(name = "NOMBRE_COMPLETO", nullable = false, length = 150)
    private String nombreCompleto;

    @Column(name = "CONTRASENA", nullable = false, length = 255)
    private String contrasena;

    @Column(name = "GRUPO", nullable = false, length = 50)
    private String grupo;

    @Column(name = "MATRICULA", nullable = false, unique = true, length = 20)
    private String matricula;

    @Column(name = "CARRERA", nullable = false, length = 100)
    private String carrera;

    @Column(name = "CUATRIMESTRE", nullable = false)
    private Integer cuatrimestre;

    @Column(name = "PUESTO", nullable = false, length = 100)
    private String puesto;

    @Column(name = "SALARIO_QUINCENAL", nullable = false, precision = 12, scale = 2)
    private BigDecimal salarioQuincenal;

    @Column(name = "FECHA_INGRESO", insertable = false, updatable = false)
    private LocalDate fechaIngreso;

    @Column(name = "ESTADO", length = 20)
    private String estado; // Se asigna por defecto activo

    // Relación con la tabla ROL
    @ManyToOne
    @JoinColumn(name = "ROL_ID_FK", nullable = false)
    private Rol rol;

}
