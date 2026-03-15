package com.sigpro.model;

import jakarta.persistence.*;

@Entity
@Table(name = "PROYECTO_USUARIO")
public class ProyectoUsuario {
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "PROYECTO_ID_FK", nullable = false)
    private Proyecto proyecto;

    @ManyToOne
    @JoinColumn(name = "USUARIO_ID_FK", nullable = false)
    private Usuario usuario;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Proyecto getProyecto() {
        return proyecto;
    }

    public void setProyecto(Proyecto proyecto) {
        this.proyecto = proyecto;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}
