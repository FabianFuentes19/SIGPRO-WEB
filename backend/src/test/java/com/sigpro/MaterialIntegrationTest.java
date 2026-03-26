package com.sigpro;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sigpro.dto.MaterialRequestDTO;
import com.sigpro.model.Proyecto;
import com.sigpro.model.Rol;
import com.sigpro.model.Usuario;
import com.sigpro.repository.ProyectoRepository;
import com.sigpro.repository.RolRepository;
import com.sigpro.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class MaterialIntegrationTest {

    @MockBean
    private JavaMailSender javaMailSender;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ProyectoRepository proyectoRepository;

    private Long proyectoIdAmplio;
    private Long proyectoIdMil;

    @BeforeEach
    void seedProyectos() {
        Rol rolLider = new Rol();
        rolLider.setNombre("LIDER");
        rolLider = rolRepository.save(rolLider);

        Usuario lider = new Usuario();
        lider.setNombreCompleto("Líder Integración");
        lider.setContrasena("hash");
        lider.setGrupo("A");
        lider.setMatricula(UUID.randomUUID().toString().replace("-", "").substring(0, 20));
        lider.setCarrera("ITI");
        lider.setCuatrimestre(8);
        lider.setPuesto("Líder");
        lider.setSalarioQuincenal(new BigDecimal("5000.00"));
        lider.setRol(rolLider);
        lider = usuarioRepository.save(lider);

        Proyecto amplio = buildProyecto(lider, new BigDecimal("50000.00"));
        proyectoIdAmplio = proyectoRepository.save(amplio).getId();

        Proyecto mil = buildProyecto(lider, new BigDecimal("1000.00"));
        proyectoIdMil = proyectoRepository.save(mil).getId();
    }

    private static Proyecto buildProyecto(Usuario lider, BigDecimal presupuesto) {
        Proyecto p = new Proyecto();
        p.setNombre("Proyecto " + presupuesto);
        p.setDescripcion("Desc");
        p.setObjetivoGeneral("Obj");
        p.setLider(lider);
        p.setPresupuesto(presupuesto);
        p.setFechaInicio(LocalDate.now());
        p.setFechaFin(LocalDate.now().plusMonths(6));
        p.setEstado("ACTIVO");
        return p;
    }

    @Test
    @DisplayName("DFR: registro exitoso — LIDER, 201 y costoTotal = monto × cantidad")
    @WithMockUser(roles = "LIDER")
    void registroExitoso_devuelve201_yCostoTotal500() throws Exception {
        MaterialRequestDTO body = new MaterialRequestDTO();
        body.setNombre("Material de prueba");
        body.setMonto(new BigDecimal("100"));
        body.setCantidad(5);
        body.setProyectoId(proyectoIdAmplio);

        mockMvc.perform(post("/api/materiales")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.costoTotal").value(500))
                .andExpect(jsonPath("$.nombre").value("Material de prueba"));
    }

    @Test
    @DisplayName("DFR: presupuesto excedido — 409 y mensaje Presupuesto insuficiente")
    @WithMockUser(roles = "LIDER")
    void presupuestoExcedido_devuelve409() throws Exception {
        MaterialRequestDTO body = new MaterialRequestDTO();
        body.setNombre("Gasto mayor al disponible");
        body.setMonto(new BigDecimal("1200"));
        body.setCantidad(1);
        body.setProyectoId(proyectoIdMil);

        mockMvc.perform(post("/api/materiales")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error", containsString("Presupuesto insuficiente")));
    }

    @Test
    @DisplayName("DFR: seguridad — MIEMBRO no lista materiales (403)")
    @WithMockUser(roles = "MIEMBRO")
    void miembroNoAutorizado_devuelve403() throws Exception {
        mockMvc.perform(get("/api/materiales/proyecto/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("DFR: validación — cantidad negativa, 400 y detalle en español")
    @WithMockUser(roles = "LIDER")
    void validacionCantidadNegativa_devuelve400() throws Exception {
        String json = """
                {
                  "nombre": "X",
                  "monto": 10,
                  "cantidad": -1,
                  "proyectoId": 1
                }
                """;

        mockMvc.perform(post("/api/materiales")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Los datos enviados no son válidos"))
                .andExpect(jsonPath("$.detalles.cantidad", containsString("mayor que cero")));
    }
}
