package com.sigpro.repository;

import com.sigpro.model.ProyectoUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProyectoUsuarioRepository extends JpaRepository<ProyectoUsuario, Long> {
    ProyectoUsuario findByUsuarioId(Long usuarioId);

    // usuarios en el proyecto
    List<ProyectoUsuario> findByProyectoId(Long proyectoId);
}
