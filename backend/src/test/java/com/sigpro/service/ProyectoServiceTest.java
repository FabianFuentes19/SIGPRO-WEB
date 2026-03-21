package com.sigpro.service;

import com.sigpro.dto.ProyectoDTO;
import com.sigpro.dto.UsuarioDTO;
import com.sigpro.model.ProyectoUsuario;
import com.sigpro.model.Rol;
import com.sigpro.model.Usuario;
import com.sigpro.repository.ProyectoRepository;
import com.sigpro.repository.ProyectoUsuarioRepository;
import com.sigpro.repository.RolRepository;
import com.sigpro.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ProyectoServiceTest {

    @Autowired
    private ProyectoService proyectoService;
    @Autowired
    private ProyectoRepository proyectoRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private RolRepository rolRepository;
    @Autowired
    private ProyectoUsuarioRepository proyectoUsuarioRepository;

    private Authentication authAdmin, authLider, authMiembro;
    private final String PASS = "Secure123!";
    private Usuario uLider;

    @BeforeEach
    void setup() {
        Rol rA = findOrCreateRol("ROLE_ADMINISTRADOR");
        Rol rL = findOrCreateRol("LIDER");
        Rol rM = findOrCreateRol("ROLE_MIEMBRO");

        uLider = createUsuario("MAT-2024-001", "Ing. Roberto García", rL);
        createUsuario("MAT-2024-088", "Lic. Ana Martínez", rM);

        authAdmin = getAuth("admin_system", "ROLE_ADMINISTRADOR");
        authLider = getAuth("MAT-2024-001", "ROLE_LIDER");
        authMiembro = getAuth("MAT-2024-088", "ROLE_MIEMBRO");
    }

    private Rol findOrCreateRol(String n) {
        return rolRepository.findByNombreIgnoreCase(n).orElseGet(() -> {
            Rol r = new Rol(); r.setNombre(n); return rolRepository.save(r);
        });
    }

    private Usuario createUsuario(String m, String n, Rol r) {
        Usuario u = new Usuario();
        u.setMatricula(m); u.setNombreCompleto(n); u.setContrasena(PASS); u.setRol(r);
        u.setGrupo("ITI-901"); u.setCarrera("ITI"); u.setCuatrimestre(9);
        u.setPuesto("P"); u.setSalarioQuincenal(new BigDecimal("5000.00"));
        return usuarioRepository.save(u);
    }

    private Authentication getAuth(String p, String r) {
        return new UsernamePasswordAuthenticationToken(p, PASS, Collections.singletonList(new SimpleGrantedAuthority(r)));
    }

    private ProyectoDTO dBase() {
        ProyectoDTO d = new ProyectoDTO();
        d.setNombre("Sistema de Control de Inventarios Automatizado");
        d.setDescripcion("Desarrollo de una plataforma web");
        d.setObjetivoGeneral("Optimizar los tiempos de auditoría");
        d.setPresupuesto(new BigDecimal("45000.00"));
        d.setFechaInicio(LocalDate.now());
        d.setFechaFin(LocalDate.now().plusMonths(4));
        // IMPORTANTE: Debe coincidir con el usuario creado en setup()
        d.setLiderMatricula("MAT-2024-001");
        return d;
    }

    @Test void crearProyectoExitoso() {
        assertNotNull(proyectoService.crearProyecto(dBase(), authAdmin));
    }

    @Test void crearProyectoCamposNulos() {
        ProyectoDTO d = dBase(); d.setNombre(null);
        assertThrows(IllegalArgumentException.class, () -> proyectoService.crearProyecto(d, authAdmin));
    }

    @Test void crearProyectoCamposVacios() {
        ProyectoDTO d = dBase(); d.setDescripcion(" ");
        assertThrows(IllegalArgumentException.class, () -> proyectoService.crearProyecto(d, authAdmin));
    }

    @Test void crearProyectoPresupuestoInvalido() {
        ProyectoDTO d = dBase(); d.setPresupuesto(BigDecimal.ZERO);
        assertThrows(IllegalArgumentException.class, () -> proyectoService.crearProyecto(d, authAdmin));
    }

    @Test void crearProyectoFechasInvertidas() {
        ProyectoDTO d = dBase();
        d.setFechaInicio(LocalDate.now().plusDays(10));
        d.setFechaFin(LocalDate.now());
        assertThrows(IllegalArgumentException.class, () -> proyectoService.crearProyecto(d, authAdmin));
    }

    @Test void crearProyectorConLiderPorId() {
        ProyectoDTO d = dBase(); d.setLiderMatricula(null); d.setLiderId(uLider.getId());
        assertNotNull(proyectoService.crearProyecto(d, authAdmin));
    }

    @Test void crearProyectoLiderNoEncontrado() {
        ProyectoDTO d = dBase(); d.setLiderMatricula("NON-EXISTENT");
        assertThrows(IllegalArgumentException.class, () -> proyectoService.crearProyecto(d, authAdmin));
    }

    @Test void editarProyectoCamposBasicos() {
        ProyectoDTO c = proyectoService.crearProyecto(dBase(), authAdmin);
        ProyectoDTO e = new ProyectoDTO();
        e.setNombre("Sistema de control");
        e.setDescripcion("Desarrollo de app");
        ProyectoDTO res = proyectoService.editarProyecto(c.getId(), e, authAdmin);
        assertEquals("Sistema de control", res.getNombre());
    }

    @Test void editarProyectoPresupuesto() {
        ProyectoDTO c = proyectoService.crearProyecto(dBase(), authAdmin);
        ProyectoDTO e = new ProyectoDTO();
        BigDecimal nuevoMonto = new BigDecimal("50000.00");
        e.setPresupuesto(nuevoMonto);
        ProyectoDTO res = proyectoService.editarProyecto(c.getId(), e, authAdmin);
        assertEquals(0, nuevoMonto.compareTo(res.getPresupuesto()));
    }

    @Test void editarProyectoIdInexistente() {
        assertThrows(IllegalArgumentException.class, () -> proyectoService.editarProyecto(999L, dBase(), authAdmin));
    }

    @Test void consultarProyectoAdminExito() {
        assertNotNull(proyectoService.consultarTodos(authAdmin));
    }

    @Test void consultarProyectoLiderError() {
        assertThrows(SecurityException.class, () -> proyectoService.consultarTodos(authLider));
    }

    @Test void buscarProyectoPorNombreExito() {
        proyectoService.crearProyecto(dBase(), authAdmin);
        assertFalse(proyectoService.buscarPorNombre("Sistema de Control de Inventarios Automatizado", authAdmin).isEmpty());
    }

    @Test void buscarProyectoPorNombreError() {
        assertThrows(IllegalArgumentException.class, () -> proyectoService.buscarPorNombre(" ", authAdmin));
    }

    @Test void consultarProyectoLider() {
        proyectoService.crearProyecto(dBase(), authAdmin);
        assertNotNull(proyectoService.consultarProyectoLider(authLider));
    }

    @Test void consultarProyectoMiembro() {
        ProyectoDTO p = proyectoService.crearProyecto(dBase(), authAdmin);
        ProyectoUsuario pu = new ProyectoUsuario();
        pu.setProyecto(proyectoRepository.findById(p.getId()).get());
        // Corregido: Usar la matrícula que existe en el setup
        pu.setUsuario(usuarioRepository.findByMatricula("MAT-2024-088").get());
        proyectoUsuarioRepository.save(pu);
        assertNotNull(proyectoService.consultarProyectoMiembro(authMiembro));
    }

    @Test void registrarMiembroExito() {
        ProyectoDTO p = proyectoService.crearProyecto(dBase(), authAdmin);
        UsuarioDTO m = new UsuarioDTO();
        m.setMatricula("MAT-NEW-123");
        m.setNombreCompleto("Nuevo Miembro");
        m.setContrasena(PASS); m.setGrupo("G"); m.setCarrera("C"); m.setCuatrimestre(1);
        m.setPuesto("P"); m.setSalarioQuincenal(BigDecimal.ZERO);
        assertNotNull(proyectoService.registrarMiembro(p.getId(), m, authLider));
    }

    @Test void obtenerDetalleExito() {
        ProyectoDTO p = proyectoService.crearProyecto(dBase(), authAdmin);
        assertNotNull(proyectoService.obtenerDetalleProyecto(p.getId(), authAdmin));
    }
}