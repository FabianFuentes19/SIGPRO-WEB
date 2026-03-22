package com.sigpro.model;


import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "PAGOS")
@Data
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_PK")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "PROYECTO_ID_FK", nullable = false)
    private Proyecto proyecto;

    @ManyToOne
    @JoinColumn(name = "USUARIO_ID_FK", nullable = false)
    private Usuario usuario;

    @Column(name = "MONTO", nullable = false, precision = 12, scale = 2)
    private BigDecimal monto;

    @Column(name = "FECHA", nullable = false)
    private LocalDate fecha;


}
