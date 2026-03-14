package com.sigpro.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    // permite la conexión con SMTP
    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String senderEmail;

    public void enviarCorreoRecuperacion(String matricula, String otpToken) {
        // se crea el correo con la matricula del estudiante + @utez.edu.mx
        String correoDestino = matricula.toLowerCase() + "@utez.edu.mx";

        // objeto que permite construir el correo
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setFrom(senderEmail); // quien lo envía
        mensaje.setTo(correoDestino); // a quien se le envía
        mensaje.setSubject("SIGPRO - Recuperación de Contraseña");

        // creación del cuerpo del correo
        mensaje.setText("Estudiante con matrícula:  " + matricula + ",\n\n"
                + "Se ha solicitado un restablecimiento de contraseña para tu cuenta en SIGPRO.\n\n"
                + "Tu código temporal de 6 dígitos es: " + otpToken + "\n\n"
                + "Este código expirará en 5 minutos.\n"
                + "Si no solicitaste esto, ignora este correo.\n\n"
                + "Atentamente,\nEl Equipo SIGPRO");

        // detona el envío del correo
        mailSender.send(mensaje);

        System.out.println("Correo enviado exitosamente a: " + correoDestino);
    }
}
