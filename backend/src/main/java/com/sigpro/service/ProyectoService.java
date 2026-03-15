package com.sigpro.service;

import com.sigpro.dto.ProyectoDTO;
import com.sigpro.dto.ProyectoMapper;
import com.sigpro.model.Proyecto;
import com.sigpro.repository.ProyectoRepository;
import com.sigpro.repository.ProyectoUsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;


import java.util.List;

@Service
public class ProyectoService {
    @Autowired
    private ProyectoRepository proyectoRepository;

    @Autowired
    private ProyectoUsuarioRepository proyectoUsuarioRepository;

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

    private void validarRol(Authentication auth, String rolEsperado) {
        if (!auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(rolEsperado))) {
            throw new SecurityException("No autorizado para esta operación");
        }
    }

}
