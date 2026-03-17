package com.sigpro.repository;

import com.sigpro.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByMatricula(String matricula);

    @Query("select u from Usuario u where upper(u.rol.nombre) = upper(:rolNombre)")
    List<Usuario> findByRolNombre(@Param("rolNombre") String rolNombre);
}