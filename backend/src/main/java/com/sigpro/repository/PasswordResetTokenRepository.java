package com.sigpro.repository;

import com.sigpro.model.PasswordResetToken;
import com.sigpro.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByTokenAndUsuario(String token, Usuario usuario);

    //elimina el codigo temporal generado por usuario
    void deleteByUsuario(Usuario usuario);
}