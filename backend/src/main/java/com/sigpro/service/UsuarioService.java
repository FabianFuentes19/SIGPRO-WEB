package com.sigpro.service;

import com.sigpro.dto.PaginatedResponse;
import com.sigpro.dto.UsuarioRequestDTO;
import com.sigpro.dto.UsuarioResponseDTO;
import com.sigpro.dto.UsuarioMapper;
import com.sigpro.model.Rol;
import com.sigpro.model.Usuario;
import com.sigpro.model.ProyectoUsuario;
import com.sigpro.repository.ProyectoRepository;
import com.sigpro.repository.ProyectoUsuarioRepository;
import com.sigpro.repository.RolRepository;
import com.sigpro.repository.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

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

    public Usuario registrarUsuario(@Valid UsuarioRequestDTO dto) {
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
 
        try {
            return usuarioRepository.save(usuario);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al insertar en la tabla usuarios: " + e.getMessage(), e);
        }
    }

    public UsuarioResponseDTO bajaLogica(String matricula) {
        String m = safeTrim(matricula);
        if (m == null || m.isEmpty()) {
            throw new IllegalArgumentException("Matrícula obligatoria");
        }

        Usuario usuario = usuarioRepository.findByMatricula(m)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        usuario.setEstado(ESTADO_INACTIVO);
        Usuario actualizado = usuarioRepository.save(usuario);
        return UsuarioMapper.toResponseDto(actualizado);
    }

    public UsuarioResponseDTO activarUsuario(String matricula) {
        String m = safeTrim(matricula);
        if (m == null || m.isEmpty()) {
            throw new IllegalArgumentException("Matrícula obligatoria");
        }

        Usuario usuario = usuarioRepository.findByMatricula(m)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        usuario.setEstado(ESTADO_ACTIVO);
        Usuario actualizado = usuarioRepository.save(usuario);
        return UsuarioMapper.toResponseDto(actualizado);
    }

    public List<UsuarioResponseDTO> listarUsuarios() {
        return usuarioRepository.findAll().stream()
                .map(UsuarioMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public PaginatedResponse<UsuarioResponseDTO> obtenerUsuariosPorRol(String rolNombre, String buscar, int page, int size) {
        if (rolNombre == null || rolNombre.isBlank()) {
            return new PaginatedResponse<>();
        }
        Pageable pageable = PageRequest.of(page, size);
        
        // Si no hay búsqueda, enviamos vacío para que el LIKE traiga todo
        String termino = (buscar != null) ? buscar.trim() : "";
        Page<Usuario> pageResult = usuarioRepository.findByRolNombreConBusqueda(rolNombre, termino, pageable);

        List<UsuarioResponseDTO> content = pageResult.getContent().stream()
                .map(UsuarioMapper::toResponseDto)
                .collect(Collectors.toList());

        return PaginatedResponse.<UsuarioResponseDTO>builder()
                .content(content)
                .pageNumber(pageResult.getNumber())
                .pageSize(pageResult.getSize())
                .totalElements(pageResult.getTotalElements())
                .totalPages(pageResult.getTotalPages())
                .last(pageResult.isLast())
                .build();
    }

    public UsuarioResponseDTO obtenerDetallePorMatricula(String matricula) {
        String m = safeTrim(matricula);
        if (m == null || m.isEmpty()) {
            throw new IllegalArgumentException("Matrícula obligatoria");
        }
        Usuario usuario = usuarioRepository.findByMatricula(m)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        return UsuarioMapper.toResponseDto(usuario);
    }

    public UsuarioResponseDTO modificarUsuario(String matricula, UsuarioRequestDTO dto) {
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
        return UsuarioMapper.toResponseDto(actualizado);
    }

    public Usuario registrarUsuarioConRol(@Valid UsuarioRequestDTO dto, String nombreRol) {
        Rol rol = rolRepository.findByNombreIgnoreCase(nombreRol)
                .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado: " + nombreRol));
        dto.setRolId(rol.getId());
        return registrarUsuario(dto);
    }

    /**
     * Lista los miembros del proyecto del líder dado por matrícula (excluye al líder).
     */
    public List<UsuarioResponseDTO> listarMiembrosPorLider(String matriculaLider) {
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
                .filter(u -> "ACTIVO".equalsIgnoreCase(u.getEstado())) // solo activos
                .collect(Collectors.toList());
        return usuarios.stream().map(UsuarioMapper::toResponseDto).collect(Collectors.toList());
    }

    public List<UsuarioResponseDTO> consultarLideresSinProyecto(){
        return usuarioRepository.findLideresSinProyecto()
                .stream()
                .map(UsuarioMapper::toResponseDto)
                .toList();
    }

    private static String safeTrim(String v) {
        return v == null ? null : v.trim();
    }


    public void cambiarPassword(String matricula, String actual, String nueva) {
        Usuario usuario = usuarioRepository.findByMatricula(matricula)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        if (!passwordEncoder.matches(actual, usuario.getContrasena())) {
            throw new IllegalArgumentException("La contraseña actual es incorrecta");
        }

        if (!PASSWORD_PATTERN.matcher(nueva).matches()) {
            throw new IllegalArgumentException(
                    "La nueva contraseña no cumple con los criterios de seguridad: " +
                    "mínimo 8 caracteres, una mayúscula, una minúscula, un número y un carácter especial"
            );
        }

        usuario.setContrasena(passwordEncoder.encode(nueva));
        usuarioRepository.save(usuario);
    }



}
