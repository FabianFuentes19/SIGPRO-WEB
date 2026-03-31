package com.sigpro.dto;

import com.sigpro.model.Pago;
import com.sigpro.model.Proyecto;
import com.sigpro.model.Usuario;

import java.time.LocalDate;

public class PagoMapper {

    public static PagoResponseDTO toResponseDto(Pago pago) {
        PagoResponseDTO dto = new PagoResponseDTO();
        dto.setId(pago.getId());
        dto.setProyectoId(pago.getProyecto().getId());
        dto.setMatriculaUsuario(pago.getUsuario().getMatricula());
        dto.setMonto(pago.getMonto());
        dto.setFecha(pago.getFecha());
        return dto;
    }

    public static Pago toEntity(PagoRequestDTO dto, Usuario usuario, Proyecto proyecto) {
        Pago pago = new Pago();
        pago.setUsuario(usuario);
        pago.setProyecto(proyecto);
        pago.setMonto(dto.getMonto());
        pago.setFecha(dto.getFecha() != null ? dto.getFecha() : LocalDate.now());
        return pago;
    }

}
