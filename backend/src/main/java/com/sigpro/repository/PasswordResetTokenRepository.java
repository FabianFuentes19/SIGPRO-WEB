package com.sigpro.repository;

import com.sigpro.model.PasswordResetToken;
import com.sigpro.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByTokenAndUsuario(String token, Usuario usuario);

    //elimina el codigo temporal generado por usuario
    @Modifying
    @Transactional
    @Query("DELETE FROM PasswordResetToken t WHERE t.usuario = :usuario")
    void deleteByUsuario(@Param("usuario") Usuario usuario);


    @Modifying
    @Transactional
    @Query("DELETE FROM PasswordResetToken t WHERE t.expiryDate < :fecha")
    void deleteExpiredTokens(@Param("fecha") LocalDateTime fecha);


}