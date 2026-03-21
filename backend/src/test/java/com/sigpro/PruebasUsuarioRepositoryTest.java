package com.sigpro;


import com.sigpro.model.Usuario;
import com.sigpro.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class PruebasUsuarioRepositoryTest {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Test
    void buscarUsuarioPorMatriculaExistente() {
        Usuario usuario = usuarioRepository.findByMatricula("20243ds036").orElse(null);
        assertNotNull(usuario);
        assertEquals("20243ds036", usuario.getMatricula());
    }

    @Test
    void buscarUsuarioPorMatriculaInexistente() {
        Optional<Usuario> usuario = usuarioRepository.findByMatricula("noexiste");
        assertTrue(usuario.isEmpty());
    }
}
