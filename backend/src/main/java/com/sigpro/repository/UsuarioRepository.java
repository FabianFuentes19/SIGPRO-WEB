package com.sigpro.repository;

import com.sigpro.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByMatricula(String matricula);

    @Query("SELECT u FROM Usuario u " +
            "WHERE UPPER(u.rol.nombre) = UPPER(:rolNombre) " +
            "AND (UPPER(u.nombreCompleto) LIKE UPPER(CONCAT('%', :buscar, '%')) " +
            "OR UPPER(u.matricula) LIKE UPPER(CONCAT('%', :buscar, '%')))")
    Page<Usuario> findByRolNombreConBusqueda(@Param("rolNombre") String rolNombre, @Param("buscar") String buscar, Pageable pageable);

    @Query("SELECT u FROM Usuario u " +
            "WHERE UPPER(u.rol.nombre) = 'LIDER' " +
            "AND UPPER(u.estado) = 'ACTIVO' " +
            "AND u.id NOT IN (SELECT p.lider.id FROM Proyecto p)")
    List<Usuario> findLideresSinProyecto();
}