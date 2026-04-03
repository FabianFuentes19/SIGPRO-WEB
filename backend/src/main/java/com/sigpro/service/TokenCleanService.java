package com.sigpro.service;

import com.sigpro.repository.PasswordResetTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TokenCleanService {

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Scheduled(fixedRate = 300000) // cada 5 minutos
    public void limpiarTokensExpirados() {
        LocalDateTime ahora = LocalDateTime.now();
        tokenRepository.deleteExpiredTokens(ahora);
        System.out.println("[TokenCleanupService] Tokens expirados eliminados a las " + ahora);
    }
}