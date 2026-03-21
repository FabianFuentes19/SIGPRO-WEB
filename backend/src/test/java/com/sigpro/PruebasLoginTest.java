package com.sigpro;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.sigpro.model.PasswordResetToken;
import com.sigpro.model.Usuario;
import com.sigpro.repository.PasswordResetTokenRepository;
import com.sigpro.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;


import java.time.LocalDateTime;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
public class PruebasLoginTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;



    //Caso de prueba 1: Login con credenciales correctas
    @Test
    void loginConCredencialesCorrectas() throws Exception {
        Map<String, String> credenciales = Map.of(
                "matricula", "20243ds036",
                "contrasena", "MiguelR@2026"
        );

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(credenciales)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.rol").exists())
                .andExpect(jsonPath("$.matricula").value("20243ds036"));

    }


    // Caso de prueba 2: Login credenciales incorrectas
    @Test
    void loginConCredencialesIncorrectas() throws Exception {
        Map<String, String> credenciales =Map.of(
                "matricula", "20243ds036",
                "contrasena", "MiguelR@2027"
        );

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(credenciales)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Credenciales incorrectas"));
    }



    // Caso de preuba 3: Campos vacíos
    @Test
    void loginConCamposVacios() throws Exception{
        Map<String, String> credenciales =Map.of(
                "matricula", "",
                "contrasena", ""
        );

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(credenciales)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Campos incompletos"));

    }


    // Caso 5: Recuperar contraseña con matrícula inválida
    @Test
    void recuperarContrasenaConMatriculaInvalida() throws Exception {
        Map<String, String> request = Map.of("matricula", "noexiste");

        mockMvc.perform(post("/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Usuario no encontrado"));
    }

    // Caso 6: Restablecer contraseña con campos vacíos
    @Test
    void restablecerContrasenaConCamposVacios() throws Exception {
        Map<String, String> request = Map.of(
                "matricula", "20243ds036",
                "token", "123456",
                "nuevaContrasena", ""
        );

        mockMvc.perform(post("/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("La nueva contraseña no puede estar vacía"));
    }

    // Caso 7: Restablecer contraseña con contraseña insegura
    @Test
    void restablecerContrasenaConContrasenaInsegura() throws Exception {
        Map<String, String> request = Map.of(
                "matricula", "20243ds036",
                "token", "123456",
                "nuevaContrasena", "abc"
        );

        mockMvc.perform(post("/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    // Caso 8: Restablecer contraseña con token inválido
    @Test
    void restablecerContrasenaConTokenInvalido() throws Exception {
        Map<String, String> request = Map.of(
                "matricula", "20243ds036",
                "token", "tokenIncorrecto",
                "nuevaContrasena", "PasswordValida123!"
        );

        mockMvc.perform(post("/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("El código es inválido"));
    }


    @Test
    void loginConContrasenaVacia() throws Exception {
        Map<String, String> credenciales = Map.of(
                "matricula", "20243ds036",
                "contrasena", ""
        );

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(credenciales)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Campos incompletos"));
    }


    @Test
    void loginConMatriculaVacia() throws Exception {
        Map<String, String> credenciales = Map.of(
                "matricula", "",
                "contrasena", "MiguelR@2026"
        );

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(credenciales)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Campos incompletos"));
    }


    @Test
    void loginConFormatoMatriculaInvalido() throws Exception {
        Map<String, String> credenciales = Map.of(
                "matricula", "abc123",
                "contrasena", "MiguelR@2026"
        );

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(credenciales)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").exists());
    }


    @Test
    void restablecerContrasenaValida() throws Exception {
        Map<String, String> request = Map.of(
                "matricula", "20243ds036",
                "token", "123456",
                "nuevaContrasena", "PasswordValida123!"
        );

        mockMvc.perform(post("/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Contraseña actualizada correctamente."));


    }



    @Test
    void recuperarContrasenaConMatriculaValida() throws Exception {
        Map<String, String> request = Map.of("matricula", "20243ds036");

        mockMvc.perform(post("/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Si la matrícula existe, se ha enviado un correo con las instrucciones."));

    }


    // Caso: Login con usuario inactivo
    @Test
    void loginConUsuarioInactivo() throws Exception {
        Map<String, String> credenciales = Map.of(
                "matricula", "20243ds009",
                "contrasena", "Angel@2026"
        );

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(credenciales)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Cuenta inactiva"));
    }

    // Caso: Recuperar contraseña con usuario inactivo
    @Test
    void recuperarContrasenaUsuarioInactivo() throws Exception {
        Map<String, String> request = Map.of("matricula", "20243ds009");

        mockMvc.perform(post("/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Usuario no encontrado"));
    }

    // Caso: Restablecer contraseña con usuario inactivo
    @Test
    void restablecerContrasenaUsuarioInactivo() throws Exception {
        Map<String, String> request = Map.of(
                "matricula", "20243ds009",
                "token", "123456",
                "nuevaContrasena", "PasswordValida123!"
        );

        mockMvc.perform(post("/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Usuario no encontrado"));
    }









}
