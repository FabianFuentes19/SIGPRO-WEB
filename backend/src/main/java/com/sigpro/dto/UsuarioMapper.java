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

    public static UsuarioDTO toDto(Usuario usuario) {
        if (usuario == null) {
            return null;
        }

        UsuarioDTO dto = new UsuarioDTO();
        dto.setNombreCompleto(usuario.getNombreCompleto());
        dto.setGrupo(usuario.getGrupo());
        dto.setMatricula(usuario.getMatricula());
        dto.setCarrera(usuario.getCarrera());
        dto.setCuatrimestre(usuario.getCuatrimestre());
        dto.setPuesto(usuario.getPuesto());
        dto.setSalarioQuincenal(usuario.getSalarioQuincenal());
        dto.setEstado(usuario.getEstado());
        dto.setRolId(usuario.getRol() != null ? usuario.getRol().getId() : null);
        dto.setRolNombre(usuario.getRol() != null ? usuario.getRol().getNombre() : null);


        dto.setContrasena(null);
        return dto;
    }
}
