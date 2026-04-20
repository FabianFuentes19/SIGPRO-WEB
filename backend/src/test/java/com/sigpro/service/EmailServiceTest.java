package com.sigpro.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    @Test
    @DisplayName("CP-EMAIL-001: Envío de correo de recuperación")
    void enviarCorreoRecuperacionExitoso() {
        // Arrange
        String matricula = "2023001";
        String token = "123456";
        String mockSender = "noreply@sigpro.com";
        
        // Seteamos el valor de @Value usando ReflectionTestUtils
        ReflectionTestUtils.setField(emailService, "senderEmail", mockSender);

        // Act
        emailService.enviarCorreoRecuperacion(matricula, token);

        // Assert
        // Usamos ArgumentCaptor para capturar el mensaje que se envió
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(messageCaptor.capture());

        SimpleMailMessage sentMessage = messageCaptor.getValue();
        
        assertEquals(mockSender, sentMessage.getFrom());
        assertEquals("2023001@utez.edu.mx", sentMessage.getTo()[0]);
        assertTrue(sentMessage.getSubject().contains("Recuperación"));
        assertTrue(sentMessage.getText().contains(token));
        assertTrue(sentMessage.getText().contains(matricula));
    }
}
