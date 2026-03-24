package com.sigpro.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Gasto en materiales asociado a un {@link Proyecto}.
 * <p>
 * La relación con el proyecto se modela con la FK {@code PROYECTO_ID_FK}
 * (el líder del proyecto sigue modelado en {@link Proyecto#LIDER_ID_FK}).
 * El presupuesto restante se calculará en servicio: PRESUPUESTO inicial − suma de costos.
 */
@Entity
@Table(name = "MATERIAL")
@Data
public class Material {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_PK")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PROYECTO_ID_FK", nullable = false)
    private Proyecto proyecto;

    @NotBlank
    @Column(name = "DESCRIPCION", nullable = false, length = 500)
    private String descripcion;

    @NotNull
    @Positive
    @Column(name = "MONTO", nullable = false, precision = 15, scale = 2)
    private BigDecimal monto;

    @NotNull
    @Positive
    @Column(name = "CANTIDAD", nullable = false)
    private Integer cantidad;

    /**
     * monto × cantidad (persistido para consultas y sumas en repositorio).
     */
    @NotNull
    @Positive
    @Column(name = "COSTO_TOTAL", nullable = false, precision = 15, scale = 2)
    private BigDecimal costoTotal;
}
