package com.sigpro.service;

import com.sigpro.repository.PasswordResetTokenRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TokenCleanServiceTest {

    @Mock
    private PasswordResetTokenRepository tokenRepository;

    @InjectMocks
    private TokenCleanService tokenCleanService;

    @Test
    @DisplayName("CP-CLEAN-001: Limpieza de tokens expirados")
    void limpiarTokensExpirados() {
        // Act
        tokenCleanService.limpiarTokensExpirados();

        // Assert
        // Verificamos que se llamó al repositorio para borrar
        verify(tokenRepository).deleteExpiredTokens(any(LocalDateTime.class));
    }
}
