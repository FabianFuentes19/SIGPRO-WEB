package com.sigpro.repository;


import com.sigpro.model.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {

    // Consultar pagos por matrícula de usuario
    List<Pago> findByUsuarioMatricula(String matricula);

    // Consultar pagos por proyecto
    List<Pago> findByProyectoId(Long proyectoId);

    // Consultar pagos por proyecto
    List<Pago> findByUsuarioMatriculaAndProyectoId(String matricula, Long proyectoId);

    @org.springframework.data.jpa.repository.Query("select coalesce(sum(p.monto), 0) from Pago p where p.proyecto.id = :proyectoId")
    java.math.BigDecimal sumMontoByProyectoId(@org.springframework.data.repository.query.Param("proyectoId") Long proyectoId);
}
