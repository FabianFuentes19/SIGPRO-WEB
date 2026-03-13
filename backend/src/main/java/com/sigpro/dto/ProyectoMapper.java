package com.sigpro.dto;

import com.sigpro.model.Proyecto;
import com.sigpro.model.Usuario;

public class ProyectoMapper {

    // convierte un dto en una entidad para guardar en bd
    public static Proyecto toEntity(ProyectoDTO dto, Usuario lider){
        if(dto == null){
            return null;
        }

        Proyecto proyecto = new Proyecto();
        proyecto.setNombre(dto.getNombre());
        proyecto.setDescripcion(dto.getDescripcion());
        proyecto.setObjetivoGeneral(dto.getObjetivoGeneral());
        proyecto.setLider(lider);
        proyecto.setPresupuesto(dto.getPresupuesto());
        proyecto.setFechaInicio(dto.getFechaInicio());
        proyecto.setFechaFin(dto.getFechaFin());
        proyecto.setEstado(dto.getEstado());

        return proyecto;
    }

    // convierte una entidad en dto para que se pueda enviar al cliente
    public static ProyectoDTO toDto(Proyecto proyecto){
        if(proyecto == null){
            return null;
        }

        ProyectoDTO dto = new ProyectoDTO();
        dto.setNombre(proyecto.getNombre());
        dto.setDescripcion(proyecto.getDescripcion());
        dto.setObjetivoGeneral(proyecto.getObjetivoGeneral());
        dto.setLiderId(proyecto.getLider() != null ? proyecto.getLider().getId() : null);
        dto.setPresupuesto(proyecto.getPresupuesto());
        dto.setFechaInicio(proyecto.getFechaInicio());
        dto.setFechaFin(proyecto.getFechaFin());
        dto.setEstado(proyecto.getEstado());

        return dto;
    }
}
