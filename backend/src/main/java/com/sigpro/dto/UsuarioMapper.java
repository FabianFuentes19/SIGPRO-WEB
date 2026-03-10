package com.sigpro.dto;

import com.sigpro.model.Rol;
import com.sigpro.model.Usuario;

public class UsuarioMapper {

    public static Usuario toEntity(UsuarioDTO dto, Rol rol) {
        if (dto == null) {
            return null;
        }

        Usuario usuario = new Usuario();
        usuario.setNombreCompleto(dto.getNombreCompleto());
        usuario.setContrasena(dto.getContrasena());
        usuario.setGrupo(dto.getGrupo());
        usuario.setMatricula(dto.getMatricula());
        usuario.setCarrera(dto.getCarrera());
        usuario.setCuatrimestre(dto.getCuatrimestre());
        usuario.setPuesto(dto.getPuesto());
        usuario.setSalarioQuincenal(dto.getSalarioQuincenal());
        usuario.setEstado(dto.getEstado());
        usuario.setRol(rol);

        return usuario;
    }
}

