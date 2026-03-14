package com.sigpro.repository;

import com.sigpro.model.Proyecto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProyectoRepository extends JpaRepository<Proyecto, Long> {
    //Buscar proyectos por nombre
    List<Proyecto> findByNombre(String nombre);

    // proyecto por líder
    Proyecto findByLiderId(Long liderId);
}
