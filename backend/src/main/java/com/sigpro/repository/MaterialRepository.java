package com.sigpro.repository;

import com.sigpro.model.Material;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

/**
 * Repositorio de materiales. No se declaran operaciones de borrado o actualización
 * personalizadas; el DFR prohíbe modificar o eliminar materiales tras el registro.
 */
public interface MaterialRepository extends JpaRepository<Material, Long> {

    List<Material> findByProyectoId(Long proyectoId);

    @Query("select coalesce(sum(m.costoTotal), 0) from Material m where m.proyecto.id = :proyectoId")
    BigDecimal sumCostoTotalByProyectoId(@Param("proyectoId") Long proyectoId);
}
