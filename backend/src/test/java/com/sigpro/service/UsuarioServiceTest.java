package com.sigpro.service;

import com.sigpro.dto.UsuarioDTO;
import com.sigpro.model.Proyecto;
import com.sigpro.model.ProyectoUsuario;
import com.sigpro.model.Rol;
import com.sigpro.model.Usuario;
import com.sigpro.repository.ProyectoRepository;
import com.sigpro.repository.ProyectoUsuarioRepository;
import com.sigpro.repository.RolRepository;
import com.sigpro.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    private static final String HASHED = "$2a$10$HASHEDPASSWORDFORUNITTESTS";

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private RolRepository rolRepository;

    @Mock
    private ProyectoRepository proyectoRepository;

    @Mock
    private ProyectoUsuarioRepository proyectoUsuarioRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    private Rol rolId2;

    @BeforeEach
    void setUp() {
        rolId2 = new Rol();
        rolId2.setId(2L);
        rolId2.setNombre("LIDER");
    }

    @Test
    @DisplayName("CP-US-001: Registro exitoso")
    void cpUs001_registroExitoso() {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setMatricula("2023001");
        dto.setContrasena("Password123!");
        dto.setNombreCompleto("Juan Perez");
        dto.setRolId(2L);
        dto.setGrupo("A");
        dto.setCarrera("ITI");
        dto.setCuatrimestre(4);

        when(usuarioRepository.findByMatricula("2023001")).thenReturn(Optional.empty());
        when(rolRepository.findById(2L)).thenReturn(Optional.of(rolId2));
        when(passwordEncoder.encode("Password123!")).thenReturn(HASHED);
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> {
            Usuario u = invocation.getArgument(0);
            u.setId(100L);
            return u;
        });

        Usuario resultado = usuarioService.registrarUsuario(dto);

        assertNotNull(resultado);
        assertEquals("ACTIVO", resultado.getEstado());
        assertEquals(HASHED, resultado.getContrasena());
        assertEquals("2023001", resultado.getMatricula());
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    @DisplayName("CP-US-002: Registro rechazado — DTO nulo")
    void cpUs002_registroDtoNulo() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> usuarioService.registrarUsuario(null));
        assertEquals("Datos de registro inválidos", ex.getMessage());
    }

    @Test
    @DisplayName("CP-US-003: Registro rechazado — Campos faltantes")
    void cpUs003_registroCamposFaltantes() {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setMatricula("2023002");
        dto.setNombreCompleto(null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> usuarioService.registrarUsuario(dto));
        assertEquals("Campos incompletos", ex.getMessage());
    }

    @Test
    @DisplayName("CP-US-004: Registro rechazado — Matrícula duplicada")
    void cpUs004_registroMatriculaDuplicada() {
        Usuario existente = new Usuario();
        existente.setMatricula("2023001");
        when(usuarioRepository.findByMatricula("2023001")).thenReturn(Optional.of(existente));

        UsuarioDTO dto = new UsuarioDTO();
        dto.setMatricula("2023001");
        dto.setContrasena("Password456!");
        dto.setNombreCompleto("José Juan ");
        dto.setRolId(2L);
        dto.setGrupo("b");
        dto.setCarrera("merk");
        dto.setCuatrimestre(5);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> usuarioService.registrarUsuario(dto));
        assertEquals("La matrícula ya existe", ex.getMessage());
    }

    @Test
    @DisplayName("CP-US-005: Registro rechazado — Contraseña débil")
    void cpUs005_registroContrasenaDebil() {
        when(usuarioRepository.findByMatricula("2023002")).thenReturn(Optional.empty());

        UsuarioDTO dto = new UsuarioDTO();
        dto.setMatricula("2023002");
        dto.setContrasena("123!");
        dto.setNombreCompleto("paco el chato ");
        dto.setRolId(2L);
        dto.setGrupo("c");
        dto.setCarrera("contabilidad");
        dto.setCuatrimestre(5);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> usuarioService.registrarUsuario(dto));
        assertTrue(ex.getMessage().contains("criterios de seguridad"));
    }

    @Test
    @DisplayName("CP-US-006: Registro rechazado — Rol inexistente")
    void cpUs006_registroRolInexistente() {
        when(usuarioRepository.findByMatricula("2023001")).thenReturn(Optional.empty());
        when(rolRepository.findById(99L)).thenReturn(Optional.empty());

        UsuarioDTO dto = new UsuarioDTO();
        dto.setMatricula("2023001");
        dto.setContrasena("Password456!");
        dto.setNombreCompleto("José Juan ");
        dto.setRolId(99L);
        dto.setGrupo("b");
        dto.setCarrera("merk");
        dto.setCuatrimestre(5);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> usuarioService.registrarUsuario(dto));
        assertEquals("Rol no válido", ex.getMessage());
    }

    @Test
    @DisplayName("CP-US-007: Registro — Fallo de persistencia")
    void cpUs007_registroFalloPersistencia() {
        when(usuarioRepository.findByMatricula("2023002")).thenReturn(Optional.empty());
        when(rolRepository.findById(2L)).thenReturn(Optional.of(rolId2));
        when(passwordEncoder.encode(anyString())).thenReturn(HASHED);
        when(usuarioRepository.save(any(Usuario.class))).thenThrow(new RuntimeException("ORA-00001"));

        UsuarioDTO dto = new UsuarioDTO();
        dto.setMatricula("2023002");
        dto.setContrasena("Password456!");
        dto.setNombreCompleto("José Juan ");
        dto.setRolId(2L);
        dto.setGrupo("b");
        dto.setCarrera("merk");
        dto.setCuatrimestre(5);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> usuarioService.registrarUsuario(dto));
        assertTrue(ex.getMessage().contains("Error al insertar en la tabla usuarios"));
    }

    @Test
    @DisplayName("CP-BL-001: Baja lógica exitosa")
    void cpBl001_bajaLogicaExitosa() {
        Usuario usuario = new Usuario();
        usuario.setMatricula("2023001");
        usuario.setEstado("ACTIVO");
        usuario.setRol(rolId2);
        usuario.setNombreCompleto("X");
        usuario.setGrupo("A");
        usuario.setCarrera("ITI");
        usuario.setCuatrimestre(1);
        usuario.setPuesto("P");
        usuario.setSalarioQuincenal(BigDecimal.ONE);

        when(usuarioRepository.findByMatricula("2023001")).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UsuarioDTO dto = usuarioService.bajaLogica("2023001");

        assertEquals("INACTIVO", dto.getEstado());
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    @DisplayName("CP-BL-002: Baja — matrícula vacía o nula")
    void cpBl002_bajaMatriculaVacia(String matricula) {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> usuarioService.bajaLogica(matricula));
        assertEquals("Matrícula obligatoria", ex.getMessage());
    }

    @Test
    @DisplayName("CP-BL-003: Baja — usuario no encontrado")
    void cpBl003_bajaNoEncontrado() {
        when(usuarioRepository.findByMatricula("99999")).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> usuarioService.bajaLogica("99999"));
        assertEquals("Usuario no encontrado", ex.getMessage());
    }

    @Test
    @DisplayName("CP-LS-001: Listar todo con datos")
    void cpLs001_listarTodosConDatos() {
        List<Usuario> lista = new ArrayList<>();
        IntStream.range(0, 23).forEach(i -> {
            Usuario u = new Usuario();
            u.setId((long) i);
            u.setMatricula("M" + i);
            u.setNombreCompleto("N" + i);
            u.setGrupo("G");
            u.setCarrera("C");
            u.setCuatrimestre(1);
            u.setPuesto("P");
            u.setSalarioQuincenal(BigDecimal.ONE);
            u.setRol(rolId2);
            lista.add(u);
        });
        when(usuarioRepository.findAll()).thenReturn(lista);

        List<UsuarioDTO> resultado = usuarioService.listarUsuarios();

        assertEquals(23, resultado.size());
        assertTrue(resultado.stream().allMatch(d -> d instanceof UsuarioDTO));
    }

    @Test
    @DisplayName("CP-LS-002: Listar todo vacío")
    void cpLs002_listarTodosVacio() {
        when(usuarioRepository.findAll()).thenReturn(List.of());

        List<UsuarioDTO> resultado = usuarioService.listarUsuarios();

        assertTrue(resultado.isEmpty());
    }

    @Test
    @DisplayName("CP-RL-001: Listar por rol — éxito")
    void cpRl001_listarPorRolExito() {
        Usuario u1 = usuarioConRol(1L, "A1", "LIDER");
        Usuario u2 = usuarioConRol(2L, "A2", "LIDER");
        when(usuarioRepository.findByRolNombre("LIDER")).thenReturn(List.of(u1, u2));

        List<UsuarioDTO> resultado = usuarioService.obtenerUsuariosPorRol("LIDER");

        assertEquals(2, resultado.size());
        assertTrue(resultado.stream().allMatch(d -> "LIDER".equals(d.getRolNombre())));
    }

    @Test
    @DisplayName("CP-RL-002: Listar por rol — sin datos")
    void cpRl002_listarPorRolSinDatos() {
        when(usuarioRepository.findByRolNombre("MIEMBRO")).thenReturn(List.of());

        List<UsuarioDTO> resultado = usuarioService.obtenerUsuariosPorRol("MIEMBRO");

        assertTrue(resultado.isEmpty());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t"})
    @DisplayName("CP-RL-003: Listar por rol — inválido (null o en blanco)")
    void cpRl003_listarPorRolInvalido(String rolNombre) {
        List<UsuarioDTO> resultado = usuarioService.obtenerUsuariosPorRol(rolNombre);

        assertTrue(resultado.isEmpty());
        verify(usuarioRepository, never()).findByRolNombre(any());
    }

    @Test
    @DisplayName("CP-DT-001: Detalle por matrícula")
    void cpDt001_detallePorMatricula() {
        Usuario usuario = usuarioConRol(10L, "2023001", "LIDER");
        usuario.setNombreCompleto("Juan Perez");
        when(usuarioRepository.findByMatricula("2023001")).thenReturn(Optional.of(usuario));

        UsuarioDTO dto = usuarioService.obtenerDetallePorMatricula("2023001");

        assertEquals("2023001", dto.getMatricula());
        assertEquals("Juan Perez", dto.getNombreCompleto());
        assertEquals("LIDER", dto.getRolNombre());
        assertNull(dto.getContrasena());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    @DisplayName("CP-DT-002: Detalle — matrícula nula o vacía")
    void cpDt002_detalleMatriculaInvalida(String matricula) {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> usuarioService.obtenerDetallePorMatricula(matricula));
        assertEquals("Matrícula obligatoria", ex.getMessage());
    }

    @Test
    @DisplayName("CP-DT-003: Detalle — no encontrado")
    void cpDt003_detalleNoEncontrado() {
        when(usuarioRepository.findByMatricula("00000")).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> usuarioService.obtenerDetallePorMatricula("00000"));
        assertEquals("Usuario no encontrado", ex.getMessage());
    }

    @Test
    @DisplayName("CP-MD-001: Modificación exitosa (nombre y grupo; id y rol sin cambio)")
    void cpMd001_modificacionExitosa() {
        Rol rol = new Rol();
        rol.setId(2L);
        rol.setNombre("LIDER");

        Usuario existente = new Usuario();
        existente.setId(50L);
        existente.setMatricula("2023001");
        existente.setNombreCompleto("Antes");
        existente.setGrupo("A");
        existente.setCarrera("ITI");
        existente.setCuatrimestre(4);
        existente.setPuesto("P");
        existente.setSalarioQuincenal(BigDecimal.TEN);
        existente.setRol(rol);

        when(usuarioRepository.findByMatricula("2023001")).thenReturn(Optional.of(existente));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UsuarioDTO cambios = new UsuarioDTO();
        cambios.setNombreCompleto("Juan Perez Modificado");
        cambios.setGrupo("B");

        UsuarioDTO resultado = usuarioService.modificarUsuario("2023001", cambios);

        assertEquals("Juan Perez Modificado", resultado.getNombreCompleto());
        assertEquals("B", resultado.getGrupo());
        assertEquals(50L, resultado.getId());
        assertEquals(2L, resultado.getRolId());

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(captor.capture());
        assertEquals(50L, captor.getValue().getId());
        assertEquals(rol, captor.getValue().getRol());
    }

    @Test
    @DisplayName("CP-MD-002: Modificar — DTO nulo")
    void cpMd002_modificarDtoNulo() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> usuarioService.modificarUsuario("2023001", null));
        assertEquals("Datos de modificación inválidos", ex.getMessage());
    }

    @Test
    @DisplayName("CP-MD-003: Modificar — usuario no existe")
    void cpMd003_modificarNoExiste() {
        when(usuarioRepository.findByMatricula("999")).thenReturn(Optional.empty());

        UsuarioDTO dto = new UsuarioDTO();
        dto.setNombreCompleto("X");

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> usuarioService.modificarUsuario("999", dto));
        assertEquals("Usuario no encontrado", ex.getMessage());
    }

    @Test
    @DisplayName("CP-MD-004: Modificación parcial (nombre; carrera sin cambio efectivo)")
    void cpMd004_modificacionParcial() {
        Rol rol = new Rol();
        rol.setId(1L);
        rol.setNombre("ALUMNO");

        Usuario existente = new Usuario();
        existente.setId(1L);
        existente.setMatricula("2023001");
        existente.setNombreCompleto("Antes");
        existente.setGrupo("A");
        existente.setCarrera("ITI");
        existente.setCuatrimestre(4);
        existente.setPuesto("P");
        existente.setSalarioQuincenal(BigDecimal.ONE);
        existente.setRol(rol);

        when(usuarioRepository.findByMatricula("2023001")).thenReturn(Optional.of(existente));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UsuarioDTO cambios = new UsuarioDTO();
        cambios.setNombreCompleto("Juan Perez Nuevo");
        cambios.setCarrera("ITI");

        UsuarioDTO resultado = usuarioService.modificarUsuario("2023001", cambios);

        assertEquals("Juan Perez Nuevo", resultado.getNombreCompleto());
        assertEquals("ITI", resultado.getCarrera());
    }

    @Test
    @DisplayName("CP-RR-001: Registro con nombre de rol (ALUMNO)")
    void cpRr001_registroConNombreRol() {
        Rol rolAlumno = new Rol();
        rolAlumno.setId(3L);
        rolAlumno.setNombre("ALUMNO");

        when(rolRepository.findByNombreIgnoreCase("alumno")).thenReturn(Optional.of(rolAlumno));
        when(usuarioRepository.findByMatricula("2023001")).thenReturn(Optional.empty());
        when(rolRepository.findById(3L)).thenReturn(Optional.of(rolAlumno));
        when(passwordEncoder.encode(anyString())).thenReturn(HASHED);
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> {
            Usuario u = invocation.getArgument(0);
            u.setId(1L);
            return u;
        });

        UsuarioDTO dto = new UsuarioDTO();
        dto.setMatricula("2023001");
        dto.setContrasena("Password456!");
        dto.setNombreCompleto("Estudiante");
        dto.setGrupo("A");
        dto.setCarrera("ITI");
        dto.setCuatrimestre(5);

        Usuario resultado = usuarioService.registrarUsuarioConRol(dto, "alumno");

        assertEquals("ALUMNO", resultado.getRol().getNombre());
        assertEquals(3L, resultado.getRol().getId());
    }

    @Test
    @DisplayName("registrarUsuarioConRol — rol no encontrado por nombre")
    void registrarUsuarioConRol_rolNoEncontrado() {
        when(rolRepository.findByNombreIgnoreCase("INEXISTENTE")).thenReturn(Optional.empty());

        UsuarioDTO dto = new UsuarioDTO();
        dto.setMatricula("2023001");
        dto.setContrasena("Password456!");
        dto.setNombreCompleto("X");
        dto.setGrupo("A");
        dto.setCarrera("ITI");
        dto.setCuatrimestre(1);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> usuarioService.registrarUsuarioConRol(dto, "INEXISTENTE"));
        assertTrue(ex.getMessage().startsWith("Rol no encontrado:"));
    }

    @Test
    @DisplayName("listarMiembrosPorLider — líder no encontrado")
    void listarMiembrosPorLider_liderNoEncontrado() {
        when(usuarioRepository.findByMatricula("NOEXISTE")).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> usuarioService.listarMiembrosPorLider("NOEXISTE"));
        assertEquals("Líder no encontrado", ex.getMessage());
    }

    @Test
    @DisplayName("CP-ML-001: Listar miembros por líder (excluye al líder)")
    void cpMl001_listarMiembrosPorLider() {
        Usuario lider = new Usuario();
        lider.setId(1L);
        lider.setMatricula("LID01");

        Usuario m1 = usuarioConRol(2L, "M1", "MIEMBRO");
        Usuario m2 = usuarioConRol(3L, "M2", "MIEMBRO");
        Usuario m3 = usuarioConRol(4L, "M3", "MIEMBRO");

        Proyecto proyecto = new Proyecto();
        proyecto.setId(100L);
        proyecto.setLider(lider);
        proyecto.setNombre("P");
        proyecto.setPresupuesto(BigDecimal.ONE);
        proyecto.setFechaInicio(LocalDate.now());
        proyecto.setFechaFin(LocalDate.now().plusDays(1));

        ProyectoUsuario pu1 = new ProyectoUsuario();
        pu1.setProyecto(proyecto);
        pu1.setUsuario(m1);
        ProyectoUsuario pu2 = new ProyectoUsuario();
        pu2.setProyecto(proyecto);
        pu2.setUsuario(m2);
        ProyectoUsuario pu3 = new ProyectoUsuario();
        pu3.setProyecto(proyecto);
        pu3.setUsuario(m3);

        when(usuarioRepository.findByMatricula("LID01")).thenReturn(Optional.of(lider));
        when(proyectoRepository.findByLiderId(1L)).thenReturn(proyecto);
        when(proyectoUsuarioRepository.findByProyectoId(100L)).thenReturn(List.of(pu1, pu2, pu3));

        List<UsuarioDTO> resultado = usuarioService.listarMiembrosPorLider("LID01");

        assertEquals(3, resultado.size());
        assertTrue(resultado.stream().noneMatch(d -> "LID01".equals(d.getMatricula())));
    }

    private Usuario usuarioConRol(long id, String matricula, String rolNombre) {
        Rol r = new Rol();
        r.setId(id);
        r.setNombre(rolNombre);
        Usuario u = new Usuario();
        u.setId(id);
        u.setMatricula(matricula);
        u.setNombreCompleto("N");
        u.setGrupo("G");
        u.setCarrera("C");
        u.setCuatrimestre(1);
        u.setPuesto("P");
        u.setSalarioQuincenal(BigDecimal.ONE);
        u.setRol(r);
        return u;
    }
}
