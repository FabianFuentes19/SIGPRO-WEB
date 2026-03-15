package com.sigpro.service;

import com.sigpro.dto.ProyectoDTO;
import com.sigpro.dto.ProyectoMapper;
import com.sigpro.model.Proyecto;
import com.sigpro.model.ProyectoUsuario;
import com.sigpro.model.Usuario;
import com.sigpro.repository.ProyectoRepository;
import com.sigpro.repository.ProyectoUsuarioRepository;
import com.sigpro.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;


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

    public List<ProyectoDTO> consultarTodos(Authentication auth){
        validarRol(auth, "ROLE_ADMINISTRADOR");
        return proyectoRepository.findAll().stream()
                .map(ProyectoMapper::toDto).toList();
    }

    public List<ProyectoDTO> buscarPorNombre(String nombre, Authentication auth){
        validarRol(auth, "ROLE_ADMINISTRADOR");
        if(nombre == null || nombre.isBlank()){
            throw new IllegalArgumentException("El criterio de búsqueda no puede estar vacío");
        }

        List<Proyecto> proyectos = proyectoRepository.findByNombre(nombre);
        if(proyectos.isEmpty()){
            throw new IllegalArgumentException("No se encontraron resultados");
        }

        return proyectos.stream().map(ProyectoMapper::toDto).toList();
    }

    public ProyectoDTO crearProyecto(ProyectoDTO dto, Authentication auth){
        validarRol(auth, "ROLE_ADMINISTRADOR");

        // validación de campos
        if (dto.getNombre() == null || dto.getNombre().isBlank() ||
                dto.getDescripcion() == null || dto.getDescripcion().isBlank() ||
                dto.getObjetivoGeneral() == null || dto.getObjetivoGeneral().isBlank() ||
                dto.getPresupuesto() == null || dto.getPresupuesto().compareTo(BigDecimal.ZERO) <= 0 ||
                dto.getFechaInicio() == null || dto.getFechaFin() == null ||
                dto.getFechaInicio().isAfter(dto.getFechaFin())) {
            throw new IllegalArgumentException("Campos obligatorios inválidos o reglas de negocio incumplidas");
        }

        Usuario lider = usuarioRepository.findById(dto.getLiderId())
                .orElseThrow(() -> new IllegalArgumentException("Líder no válido"));

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

        return ProyectoMapper.toDto(proyectoGuardado);
    }

    private void validarRol(Authentication auth, String rolEsperado) {
        if (!auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(rolEsperado))) {
            throw new SecurityException("No autorizado para esta operación");
        }
    }

}
