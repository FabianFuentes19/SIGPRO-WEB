package com.sigpro.service;

import com.sigpro.dto.*;
import com.sigpro.model.Proyecto;
import com.sigpro.model.ProyectoUsuario;
import com.sigpro.model.Usuario;
import com.sigpro.repository.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
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
    private UsuarioService usuarioService;

    @Autowired
    private MaterialRepository materialRepository;

    @Autowired
    private PagoRepository pagoRepository;

    public PaginatedResponse<ProyectoResponseDTO> consultarTodos(int page, int size, String buscar, Authentication auth){
        validarRol(auth, "ROLE_ADMINISTRADOR");
        Pageable pageable = PageRequest.of(page, size);
        
        Page<Proyecto> pageResult;
        if (buscar != null && !buscar.isBlank()) {
            pageResult = proyectoRepository.findByNombreConBusqueda(buscar.trim(), pageable);
        } else {
            pageResult = proyectoRepository.findAll(pageable);
        }

        List<ProyectoResponseDTO> content = pageResult.getContent().stream()
                .map(ProyectoMapper::toResponseDto) // Usa el mapeador simple
                .toList();

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

        return proyectos.stream().map(this::toResponseDtoConCalculos).toList();
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
 
        return toResponseDtoConCalculos(proyectoGuardado);
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
            BigDecimal nuevoAutorizado = dto.getPresupuesto();
            proyecto.setPresupuestoAutorizado(nuevoAutorizado);

            // recalculo el presupuesto disponible a partir de los gastos
            BigDecimal gastoMateriales = materialRepository.sumCostoTotalByProyectoId(proyecto.getId());
            BigDecimal gastoNominas = pagoRepository.sumMontoByProyectoId(proyecto.getId());
            BigDecimal gastoTotal = gastoMateriales.add(gastoNominas);
            BigDecimal presupuestoDisponible = nuevoAutorizado.subtract(gastoTotal);

            proyecto.setPresupuesto(presupuestoDisponible.max(BigDecimal.ZERO));
        }

        return toResponseDtoConCalculos(proyectoRepository.save(proyecto));
    }

    public ProyectoResponseDTO consultarProyectoLider(Authentication auth) {
        validarRol(auth, "ROLE_LIDER");

        Usuario lider = usuarioRepository.findByMatricula((String) auth.getPrincipal())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        Proyecto proyecto = proyectoRepository.findByLiderId(lider.getId());
        if (proyecto == null) throw new IllegalArgumentException("No tiene proyecto asignado");

        return toResponseDtoConCalculos(proyecto);
    }

    public ProyectoResponseDTO consultarProyectoMiembro(Authentication auth) {
        validarRol(auth, "ROLE_MIEMBRO");

        Usuario usuario = usuarioRepository.findByMatricula((String) auth.getPrincipal())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        ProyectoUsuario pu = proyectoUsuarioRepository.findByUsuarioId(usuario.getId());
        if (pu == null) throw new IllegalArgumentException("No pertenece a ningún proyecto");

        return toResponseDtoConCalculos(pu.getProyecto());
    }

    @Transactional
    public ProyectoUsuario registrarMiembro(Long proyectoId, UsuarioRequestDTO dto, Authentication auth) {
        validarRol(auth, "ROLE_LIDER");

        // valida que el líder sea dueño del proyecto
        Usuario lider = usuarioRepository.findByMatricula((String) auth.getPrincipal())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        Proyecto proyecto = proyectoRepository.findById(proyectoId)
                .orElseThrow(() -> new IllegalArgumentException("Proyecto no encontrado"));

        if (!ESTADO_ACTIVO.equalsIgnoreCase(proyecto.getEstado())) {
            throw new IllegalArgumentException("No se pueden agregar miembros a un proyecto que está " + proyecto.getEstado());
        }

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

        ProyectoResponseDTO response = toResponseDtoConCalculos(proyecto);
        if (miembros != null) {
            response.setMiembros(
                    miembros.stream()
                            .map(u -> {
                                UsuarioResponseDTO dto = new UsuarioResponseDTO();
                                dto.setId(u.getId());
                                dto.setNombreCompleto(u.getNombreCompleto());
                                dto.setMatricula(u.getMatricula());
                                return dto;
                            })
                            .collect(java.util.stream.Collectors.toList())
            );
        }
        return response;
    }

    public List<UsuarioResponseDTO> consultarEquipoCompleto(Authentication auth) {
        validarRol(auth, "ROLE_LIDER");
        String matriculaAutenticada = (String) auth.getPrincipal();

        Usuario lider = usuarioRepository.findByMatricula(matriculaAutenticada)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        Proyecto proyecto = proyectoRepository.findByLiderId(lider.getId());
        if (proyecto == null) {
            return java.util.Collections.emptyList();
        }

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

    private ProyectoResponseDTO toResponseDtoConCalculos(Proyecto proyecto) {
        ProyectoResponseDTO dto = ProyectoMapper.toResponseDto(proyecto);
        
        BigDecimal gastoMateriales = materialRepository.sumCostoTotalByProyectoId(proyecto.getId());
        BigDecimal gastoNominas = pagoRepository.sumMontoByProyectoId(proyecto.getId());
        BigDecimal gastoTotal = gastoMateriales.add(gastoNominas);
        

        BigDecimal autorizado = proyecto.getPresupuestoAutorizado() != null ?
                proyecto.getPresupuestoAutorizado() : proyecto.getPresupuestoInicial();
        
        BigDecimal restante = autorizado.subtract(gastoTotal);

        //BigDecimal contador = UsuarioMapper.toResponseDto(consultarProyectoMiembro(autorizado));

        BigDecimal totalMiembros = consultarProyectoMiembro(autorizado);
        
        dto.setPresupuesto(restante);
        dto.setPresupuestoAutorizado(autorizado);
        dto.setTotalMiembros(totalMiembros.longValue());
        
        return dto;
    }

}
