package com.sigpro.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "Proyectos")
@Data
public class Proyecto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_PK")
    private Long id;

    @Column(name = "NOMBRE", nullable = false, length = 150)
    private String nombre;

    @Column(name = "DESCRIPCION", length = 1000)
    private String descripcion;

    @Column(name = "OBJETIVO_GENERAL", length = 1000)
    private String objetivoGeneral;

    @ManyToOne
    @JoinColumn(name = "LIDER_ID_FK", nullable = false)
    private Usuario lider;

    @Column(name = "PRESUPUESTO", nullable = false, precision = 15, scale = 2)
    private BigDecimal presupuesto;

    @Column(name = "FECHA_INICIO", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "FECHA_FIN", nullable = false)
    private LocalDate fechaFin;

    @Column(name = "ESTADO", length = 50)
    private String estado;
}
