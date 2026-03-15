package com.sigpro.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "PROYECTO_USUARIO")
@Data
public class ProyectoUsuario {
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    @Column(name = "ID_PK")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "PROYECTO_ID_FK", nullable = false)
    private Proyecto proyecto;

    @ManyToOne
    @JoinColumn(name = "USUARIO_ID_FK", nullable = false)
    private Usuario usuario;
}
