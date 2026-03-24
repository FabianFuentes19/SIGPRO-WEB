package com.sigpro.dto;

import com.sigpro.model.Pago;
import com.sigpro.model.Proyecto;
import com.sigpro.model.Usuario;

public class PagoMapper {

    public static Pago toEntity(PagoDTO dto, Usuario usuario, Proyecto proyecto) {
        if (dto == null) return null;

        Pago pago = new Pago();
        pago.setUsuario(usuario);
        pago.setProyecto(proyecto);
        pago.setMonto(dto.getMonto());
        pago.setFecha(dto.getFecha());

        return pago;
    }

    public static PagoDTO toDto(Pago pago) {
        if (pago == null) return null;

        PagoDTO dto = new PagoDTO();
        dto.setProyectoId(pago.getProyecto().getId());
        dto.setMatriculaUsuario(pago.getUsuario().getMatricula());
        dto.setMonto(pago.getMonto());
        dto.setFecha(pago.getFecha());

        return dto;
    }
}
