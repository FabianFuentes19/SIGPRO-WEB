package com.sigpro.repository;

import com.sigpro.model.Proyecto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProyectoRepository extends JpaRepository<Proyecto, Long> {
    //Buscar proyectos por coincidencia de nombre (ignora mayúsculas/minúsculas)
    List<Proyecto> findByNombreContainingIgnoreCase(String nombre);

    // proyecto por líder
    Proyecto findByLiderId(Long liderId);

    // proyectos por estado del líder
    org.springframework.data.domain.Page<Proyecto> findAllByLiderEstado(String estado, org.springframework.data.domain.Pageable pageable);

    @Query("SELECT p FROM Proyecto p " +
            "WHERE (UPPER(p.nombre) LIKE UPPER(CONCAT('%', :buscar, '%')) " +
            "OR UPPER(p.lider.nombreCompleto) LIKE UPPER(CONCAT('%', :buscar, '%')))")
    Page<Proyecto> findByNombreConBusqueda(@Param("buscar") String buscar, Pageable pageable);
}
