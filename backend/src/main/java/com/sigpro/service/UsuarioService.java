package com.sigpro.service;

import com.sigpro.dto.UsuarioDTO;
import com.sigpro.dto.UsuarioMapper;
import com.sigpro.model.Rol;
import com.sigpro.model.Usuario;
import com.sigpro.model.ProyectoUsuario;
import com.sigpro.repository.ProyectoRepository;
import com.sigpro.repository.ProyectoUsuarioRepository;
import com.sigpro.repository.RolRepository;
import com.sigpro.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.regex.Pattern;

@Service
public class UsuarioService {

    private static final String ESTADO_ACTIVO = "ACTIVO";
    private static final String ESTADO_INACTIVO = "INACTIVO";

    // Mínimo 8, 1 mayúscula, 1 minúscula, 1 número, 1 especial
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,}$"
    );

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private ProyectoRepository proyectoRepository;

    @Autowired
    private ProyectoUsuarioRepository proyectoUsuarioRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public Usuario registrarUsuario(UsuarioDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Datos de registro inválidos");
        }

        String matricula = safeTrim(dto.getMatricula());
        String contrasena = safeTrim(dto.getContrasena());

        if (matricula == null || matricula.isEmpty()
                || contrasena == null || contrasena.isEmpty()
                || safeTrim(dto.getNombreCompleto()) == null || safeTrim(dto.getNombreCompleto()).isEmpty()
                || safeTrim(dto.getGrupo()) == null || safeTrim(dto.getGrupo()).isEmpty()
                || safeTrim(dto.getCarrera()) == null || safeTrim(dto.getCarrera()).isEmpty()
                || dto.getCuatrimestre() == null
                || dto.getRolId() == null) {
            throw new IllegalArgumentException("Campos incompletos");
        }

        if (usuarioRepository.findByMatricula(matricula).isPresent()) {
            throw new IllegalArgumentException("La matrícula ya existe");
        }

        if (!PASSWORD_PATTERN.matcher(contrasena).matches()) {
            throw new IllegalArgumentException(
                    "La contraseña no cumple con los criterios de seguridad: mínimo 8 caracteres, una mayúscula, una minúscula, un número y un carácter especial"
            );
        }

        Rol rol = rolRepository.findById(dto.getRolId())
                .orElseThrow(() -> new IllegalArgumentException("Rol no válido"));

        Usuario usuario = UsuarioMapper.toEntity(dto, rol);
        usuario.setMatricula(matricula);
        usuario.setContrasena(passwordEncoder.encode(contrasena));
        usuario.setEstado(ESTADO_ACTIVO);

        System.out.println("[UsuarioService] Insertando usuario:");
        System.out.println("  nombreCompleto=" + usuario.getNombreCompleto());
        System.out.println("  contrasena(BCrypt)=(no se imprime por seguridad)");
        System.out.println("  grupo=" + usuario.getGrupo());
        System.out.println("  matricula=" + usuario.getMatricula());
        System.out.println("  carrera=" + usuario.getCarrera());
        System.out.println("  cuatrimestre=" + usuario.getCuatrimestre());
        System.out.println("  puesto=" + usuario.getPuesto());
        System.out.println("  salarioQuincenal=" + usuario.getSalarioQuincenal());
        System.out.println("  estado=" + usuario.getEstado());
        System.out.println("  rolId=" + (usuario.getRol() != null ? usuario.getRol().getId() : null));
        System.out.println("  rolNombre=" + (usuario.getRol() != null ? usuario.getRol().getNombre() : null));

        try {
            return usuarioRepository.save(usuario);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al insertar en la tabla usuarios: " + e.getMessage(), e);
        }
    }

    public UsuarioDTO bajaLogica(String matricula) {
        String m = safeTrim(matricula);
        if (m == null || m.isEmpty()) {
            throw new IllegalArgumentException("Matrícula obligatoria");
        }

        Usuario usuario = usuarioRepository.findByMatricula(m)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        usuario.setEstado(ESTADO_INACTIVO);
        Usuario actualizado = usuarioRepository.save(usuario);
        return UsuarioMapper.toDto(actualizado);
    }

    public List<UsuarioDTO> listarUsuarios() {
        return usuarioRepository.findAll().stream()
                .map(UsuarioMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<UsuarioDTO> obtenerUsuariosPorRol(String rolNombre) {
        if (rolNombre == null || rolNombre.isBlank()) {
            return List.of();
        }
        return usuarioRepository.findByRolNombre(rolNombre).stream()
                .map(UsuarioMapper::toDto)
                .collect(Collectors.toList());
    }

    public UsuarioDTO obtenerDetallePorMatricula(String matricula) {
        String m = safeTrim(matricula);
        if (m == null || m.isEmpty()) {
            throw new IllegalArgumentException("Matrícula obligatoria");
        }
        Usuario usuario = usuarioRepository.findByMatricula(m)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        return UsuarioMapper.toDto(usuario);
    }

    public UsuarioDTO modificarUsuario(String matricula, UsuarioDTO dto) {
        String m = safeTrim(matricula);
        if (m == null || m.isEmpty()) {
            throw new IllegalArgumentException("Matrícula obligatoria");
        }
        if (dto == null) {
            throw new IllegalArgumentException("Datos de modificación inválidos");
        }

        Usuario usuario = usuarioRepository.findByMatricula(m)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        // Campos permitidos por DFR: nombre completo, grupo, carrera, cuatrimestre
        if (dto.getNombreCompleto() != null) {
            usuario.setNombreCompleto(dto.getNombreCompleto().trim());
        }
        if (dto.getGrupo() != null) {
            usuario.setGrupo(dto.getGrupo().trim());
        }
        if (dto.getCarrera() != null) {
            usuario.setCarrera(dto.getCarrera().trim());
        }
        if (dto.getCuatrimestre() != null) {
            usuario.setCuatrimestre(dto.getCuatrimestre());
        }

        // No se permite modificar: matricula, puesto, salarioQuincenal, fechaIngreso, contrasena, rol
        Usuario actualizado = usuarioRepository.save(usuario);
        return UsuarioMapper.toDto(actualizado);
    }

    public Usuario registrarUsuarioConRol(UsuarioDTO dto, String nombreRol) {
        Rol rol = rolRepository.findByNombreIgnoreCase(nombreRol)
                .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado: " + nombreRol));
        dto.setRolId(rol.getId());
        dto.setRolNombre(rol.getNombre());
        return registrarUsuario(dto);
    }

    /**
     * Lista los miembros del proyecto del líder dado por matrícula (excluye al líder).
     */
    public List<UsuarioDTO> listarMiembrosPorLider(String matriculaLider) {
        String m = safeTrim(matriculaLider);
        if (m == null || m.isEmpty()) {
            return List.of();
        }
        Usuario lider = usuarioRepository.findByMatricula(m)
                .orElseThrow(() -> new IllegalArgumentException("Líder no encontrado"));
        var proyecto = proyectoRepository.findByLiderId(lider.getId());
        if (proyecto == null) {
            return List.of();
        }
        List<Usuario> usuarios = proyectoUsuarioRepository.findByProyectoId(proyecto.getId()).stream()
                .map(ProyectoUsuario::getUsuario)
                .filter(u -> !u.getId().equals(lider.getId()))
                .collect(Collectors.toList());
        return usuarios.stream().map(UsuarioMapper::toDto).collect(Collectors.toList());
    }

    private static String safeTrim(String v) {
        return v == null ? null : v.trim();
    }
}
