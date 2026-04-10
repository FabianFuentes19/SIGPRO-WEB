package com.sigpro.service;

import com.sigpro.dto.*;
import com.sigpro.model.Proyecto;
import com.sigpro.model.ProyectoUsuario;
import com.sigpro.model.Usuario;
import com.sigpro.repository.ProyectoRepository;
import com.sigpro.repository.ProyectoUsuarioRepository;
import com.sigpro.repository.RolRepository;
import com.sigpro.repository.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;


import java.util.List;

@Service
public class ProyectoService {
    private static final String ESTADO_ACTIVO = "ACTIVO";
    private static final String ESTADO_INACTIVO = "INACTIVO";

    @Autowired
    private ProyectoRepository proyectoRepository;

    @Autowired
    private ProyectoUsuarioRepository proyectoUsuarioRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private UsuarioService usuarioService;

    public PaginatedResponse<ProyectoResponseDTO> consultarTodos(int page, int size, Authentication auth){
        validarRol(auth, "ROLE_ADMINISTRADOR");
        Pageable pageable = PageRequest.of(page, size);
        Page<Proyecto> pageResult = proyectoRepository.findAllByLiderEstado(ESTADO_ACTIVO, pageable);

        List<ProyectoResponseDTO> content = pageResult.getContent().stream()
                .map(ProyectoMapper::toResponseDto).toList();

        return PaginatedResponse.<ProyectoResponseDTO>builder()
                .content(content)
                .pageNumber(pageResult.getNumber())
                .pageSize(pageResult.getSize())
                .totalElements(pageResult.getTotalElements())
                .totalPages(pageResult.getTotalPages())
                .last(pageResult.isLast())
                .build();
    }

    public List<ProyectoResponseDTO> buscarPorNombre(String nombre, Authentication auth){
        validarRol(auth, "ROLE_ADMINISTRADOR");
        if(nombre == null || nombre.isBlank()){
            throw new IllegalArgumentException("El criterio de búsqueda no puede estar vacío");
        }

        List<Proyecto> proyectos = proyectoRepository.findByNombreContainingIgnoreCase(nombre);
        if(proyectos.isEmpty()){
            throw new IllegalArgumentException("No se encontraron resultados");
        }

        return proyectos.stream().map(ProyectoMapper::toResponseDto).toList();
    }

    @Transactional
    public ProyectoResponseDTO crearProyecto(@Valid ProyectoRequestDTO dto, Authentication auth){
        validarRol(auth, "ROLE_ADMINISTRADOR");
        validarFechas(dto.getFechaInicio(), dto.getFechaFin());

        Usuario lider;
        if (dto.getLiderMatricula() != null && !dto.getLiderMatricula().isBlank()) {
            lider = usuarioRepository.findByMatricula(dto.getLiderMatricula().trim())
                    .orElseThrow(() -> new IllegalArgumentException("Líder no válido"));
        } else if (dto.getLiderId() != null) {
            lider = usuarioRepository.findById(dto.getLiderId())
                    .orElseThrow(() -> new IllegalArgumentException("Líder no válido"));
        } else {
            throw new IllegalArgumentException("Debe seleccionar un líder");
        }

        String rolLider = lider.getRol() != null ? lider.getRol().getNombre() : null;
        String rolUpper = rolLider == null ? "" : rolLider.toUpperCase();
        if (!rolUpper.equals("LIDER")) {
            throw new IllegalArgumentException("El usuario seleccionado no tiene rol de LÍDER");
        }

        if(proyectoRepository.findByLiderId(lider.getId()) != null){
            throw new IllegalArgumentException("El líder ya tiene un proyecto asignado");
        }

        Proyecto proyecto = ProyectoMapper.toEntity(dto, lider);
        proyecto.setEstado(ESTADO_ACTIVO);
        Proyecto proyectoGuardado = proyectoRepository.save(proyecto);
 
        // inserción de líder en proyecto_usuario
        ProyectoUsuario pUsuario = new ProyectoUsuario();
        pUsuario.setProyecto(proyectoGuardado);
        pUsuario.setUsuario(lider);
        proyectoUsuarioRepository.save(pUsuario);
 
        return ProyectoMapper.toResponseDto(proyectoGuardado);
    }

    @Transactional
    public ProyectoResponseDTO editarProyecto(Long id, @Valid ProyectoRequestDTO dto, Authentication auth){
        validarRol(auth, "ROLE_ADMINISTRADOR");

        Proyecto proyecto = proyectoRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Proyecto no encontrado"));

        // valida que las fechas sean coherentes
        if (dto.getFechaInicio() != null || dto.getFechaFin() != null) {
            java.time.LocalDate inicio = dto.getFechaInicio() != null ? dto.getFechaInicio() : proyecto.getFechaInicio();
            java.time.LocalDate fin = dto.getFechaFin() != null ? dto.getFechaFin() : proyecto.getFechaFin();
            validarFechas(inicio, fin);
        }

        // solo se pueden editar nombre, descripción, objetivo general y presupuesto
        if (dto.getNombre() != null) {
            proyecto.setNombre(dto.getNombre());
        }
        if (dto.getDescripcion() != null) {
            proyecto.setDescripcion(dto.getDescripcion());
        }
        if (dto.getObjetivoGeneral() != null) {
            proyecto.setObjetivoGeneral(dto.getObjetivoGeneral());
        }
        if (dto.getPresupuesto() != null) {
            proyecto.setPresupuesto(dto.getPresupuesto());
            // Sincronizamos el inicial para mantener consistencia en la barra de progreso
            proyecto.setPresupuestoInicial(dto.getPresupuesto());
        }

        return ProyectoMapper.toResponseDto(proyectoRepository.save(proyecto));
    }

    public ProyectoResponseDTO consultarProyectoLider(Authentication auth) {
        validarRol(auth, "ROLE_LIDER");

        Usuario lider = usuarioRepository.findByMatricula((String) auth.getPrincipal())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        Proyecto proyecto = proyectoRepository.findByLiderId(lider.getId());
        if (proyecto == null) throw new IllegalArgumentException("No tiene proyecto asignado");

        return ProyectoMapper.toResponseDto(proyecto);
    }

    public ProyectoResponseDTO consultarProyectoMiembro(Authentication auth) {
        validarRol(auth, "ROLE_MIEMBRO");

        Usuario usuario = usuarioRepository.findByMatricula((String) auth.getPrincipal())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        ProyectoUsuario pu = proyectoUsuarioRepository.findByUsuarioId(usuario.getId());
        if (pu == null) throw new IllegalArgumentException("No pertenece a ningún proyecto");

        return ProyectoMapper.toResponseDto(pu.getProyecto());
    }

    @Transactional
    public ProyectoUsuario registrarMiembro(Long proyectoId, UsuarioRequestDTO dto, Authentication auth) {
        validarRol(auth, "ROLE_LIDER");

        // valida que el líder sea dueño del proyecto
        Usuario lider = usuarioRepository.findByMatricula((String) auth.getPrincipal())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        Proyecto proyecto = proyectoRepository.findById(proyectoId)
                .orElseThrow(() -> new IllegalArgumentException("Proyecto no encontrado"));

        if (!proyecto.getLider().getId().equals(lider.getId())) {
            throw new SecurityException("No autorizado: solo el líder del proyecto puede agregar miembros");
        }

        Usuario nuevoMiembro = usuarioService.registrarUsuarioConRol(dto, "Miembro");

        ProyectoUsuario pu = new ProyectoUsuario();
        pu.setProyecto(proyecto);
        pu.setUsuario(nuevoMiembro);

        return proyectoUsuarioRepository.save(pu);
    }


    public ProyectoResponseDTO obtenerDetalleProyecto(Long id, Authentication auth) {
        validarRol(auth, "ROLE_ADMINISTRADOR");

        Proyecto proyecto = proyectoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Proyecto no encontrado"));

        // Buscamos los miembros asociados en la tabla intermedia
        List<Usuario> miembros = proyectoUsuarioRepository.findByProyectoId(id).stream()
                .map(ProyectoUsuario::getUsuario)
                .toList();

        return ProyectoMapper.toDetailedDto(proyecto, miembros);
    }

    public List<UsuarioResponseDTO> consultarEquipoCompleto(Authentication auth) {
        validarRol(auth, "ROLE_LIDER");
        String matriculaAutenticada = (String) auth.getPrincipal();

        Usuario lider = usuarioRepository.findByMatricula(matriculaAutenticada)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        Proyecto proyecto = proyectoRepository.findByLiderId(lider.getId());
        if (proyecto == null) throw new IllegalArgumentException("No tiene proyecto asignado");

        // se consulta solo los activos
        return proyectoUsuarioRepository.findByProyectoId(proyecto.getId()).stream()
                .map(ProyectoUsuario::getUsuario)
                .filter(u -> "ACTIVO".equalsIgnoreCase(u.getEstado()) || u.getMatricula().equals(matriculaAutenticada))
                .map(UsuarioMapper::toResponseDto)
                .toList();
    }

    private void validarRol(Authentication auth, String rolEsperado) {
        if (!auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(rolEsperado))) {
            throw new SecurityException("No autorizado para esta operación");
        }
    }

    private void validarFechas(java.time.LocalDate inicio, java.time.LocalDate fin) {
        if (inicio != null && fin != null && fin.isBefore(inicio)) {
            throw new IllegalArgumentException("La fecha de fin no puede ser anterior a la fecha de inicio");
        }
    }

}
