package com.sigpro.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * EXPLICACIÓN DE CONTROLADORES:
 * @AutoConfigureMockMvc: Nos permite simular llamadas HTTP (POST, GET, PUT) sin arrancar el servidor real.
 * MockMvc: Es la herramienta que enviará las peticiones a tus controladores para ver si responden bien.
 */
@SpringBootTest
@AutoConfigureMockMvc
class ProyectoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // =========================================================================
    // SECCIÓN: PRUEBAS DE SEGURIDAD (DISPONIBILIDAD DE ENDPOINTS)
    // Probamos que el sistema PROTEJA la información si no hay un token válido.
    // =========================================================================

    @Test
    @DisplayName("CP-CONT-001: Denegar acceso a lista global si no hay login")
    void testListarSinToken() throws Exception {
        // Paso 1 (Dado): El endpoint "/proyectos" está protegido.
        // Paso 2 (Cuando): Hacemos una petición sin enviar ningún usuario.
        mockMvc.perform(get("/proyectos")
                .contentType(MediaType.APPLICATION_JSON))
        // Paso 3 (Entonces): El sistema debe responder con un error 403 (FORBIDDEN).
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("CP-CONT-002: Denegar búsqueda si no hay login")
    void testBuscarSinToken() throws Exception {
        // Paso 1: Intentamos buscar con el parámetro 'nombre'.
        mockMvc.perform(get("/proyectos/buscar")
                .param("nombre", "alfa")
                .contentType(MediaType.APPLICATION_JSON))
        // Paso 2 y 3: Esperamos que la seguridad lo bloquee (403).
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("CP-CONT-003: Impedir creación de proyecto sin permisos")
    void testCrearSinToken() throws Exception {
        // Paso 1: Enviamos un objeto JSON vacío al intentar crear.
        mockMvc.perform(post("/proyectos")
                .content("{}")
                .contentType(MediaType.APPLICATION_JSON))
        // Paso 2 y 3: Debe fallar con error 403.
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("CP-CONT-004: Impedir edición de proyecto sin permisos")
    void testEditarSinToken() throws Exception {
        // Paso 1: Intentamos editar el proyecto con ID 1.
        mockMvc.perform(put("/proyectos/1")
                .content("{}")
                .contentType(MediaType.APPLICATION_JSON))
        // Paso 2 y 3: Debe fallar con 403.
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("CP-CONT-005: Denegar ver detalle de proyecto sin login")
    void testDetalleSinToken() throws Exception {
        // Paso 1 y 2: Consultamos el detalle directo por ID.
        mockMvc.perform(get("/proyectos/1"))
        // Paso 3: Debe estar bloqueado.
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("CP-CONT-006: Proteger el acceso de líder")
    void testLiderSinToken() throws Exception {
        // Paso 1: Intentamos entrar a "mi-proyecto/lider".
        mockMvc.perform(get("/proyectos/mi-proyecto/lider"))
        // Paso 2 y 3: Sin login, debe ser 403.
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("CP-CONT-007: Proteger el registro de nuevos miembros")
    void testRegMiembroSinToken() throws Exception {
        // Paso 1: Intentamos registrar un miembro en el proyecto 1 vía POST.
        mockMvc.perform(post("/proyectos/1/miembros")
                .content("{}")
                .contentType(MediaType.APPLICATION_JSON))
        // Paso 2 y 3: Debe responder 403.
                .andExpect(status().isForbidden());
    }
}
