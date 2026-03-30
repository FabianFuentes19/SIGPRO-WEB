package com.sigpro.dto;

import com.sigpro.model.Proyecto;
import com.sigpro.model.Usuario;

import java.util.List;

public class ProyectoMapper {

    public static Proyecto toEntity(ProyectoRequestDTO dto, Usuario lider) {
        if (dto == null) return null;
        Proyecto proyecto = new Proyecto();
        proyecto.setNombre(dto.getNombre());
        proyecto.setDescripcion(dto.getDescripcion());
        proyecto.setObjetivoGeneral(dto.getObjetivoGeneral());
        proyecto.setPresupuesto(dto.getPresupuesto());
        proyecto.setFechaInicio(dto.getFechaInicio());
        proyecto.setFechaFin(dto.getFechaFin());
        proyecto.setLider(lider);
        return proyecto;
    }

    public static ProyectoResponseDTO toResponseDto(Proyecto proyecto) {
        if (proyecto == null) return null;
        ProyectoResponseDTO response = new ProyectoResponseDTO();
        response.setId(proyecto.getId());
        response.setNombre(proyecto.getNombre());
        response.setDescripcion(proyecto.getDescripcion());
        response.setObjetivoGeneral(proyecto.getObjetivoGeneral());
        response.setPresupuesto(proyecto.getPresupuesto());
        response.setFechaInicio(proyecto.getFechaInicio());
        response.setFechaFin(proyecto.getFechaFin());
        response.setEstado(proyecto.getEstado());
        if (proyecto.getLider() != null) {
            response.setLiderId(proyecto.getLider().getId());
            response.setLiderNombre(proyecto.getLider().getNombreCompleto());
            response.setLiderMatricula(proyecto.getLider().getMatricula());
        }
        return response;
    }

    public static ProyectoResponseDTO toDetailedDto(Proyecto proyecto, List<Usuario> miembros) {
        ProyectoResponseDTO response = toResponseDto(proyecto);
        if (miembros != null) {
            response.setMiembros(
                    miembros.stream()
                            .map(u -> {
                                UsuarioDTO dto = new UsuarioDTO();
                                dto.setId(u.getId());
                                dto.setNombreCompleto(u.getNombreCompleto());
                                dto.setMatricula(u.getMatricula());
                                return dto;
                            })
                            .toList()
            );
        }
        return response;
    }
}
