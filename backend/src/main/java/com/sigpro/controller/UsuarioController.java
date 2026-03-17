package com.sigpro.controller;

import com.sigpro.dto.UsuarioDTO;
import com.sigpro.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<?> listarTodos() {
        try {
            List<UsuarioDTO> usuarios = usuarioService.listarUsuarios();
            return ResponseEntity.ok(usuarios);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "No fue posible consultar los usuarios");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/lider/{matriculaLider}")
    public ResponseEntity<?> listarMiembrosPorLider(@PathVariable String matriculaLider) {
        try {
            List<UsuarioDTO> miembros = usuarioService.listarMiembrosPorLider(matriculaLider);
            return ResponseEntity.ok(miembros);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "No fue posible consultar los miembros");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/{matricula}")
    public ResponseEntity<?> buscarPorMatricula(@PathVariable String matricula) {
        try {
            UsuarioDTO usuario = usuarioService.obtenerDetallePorMatricula(matricula);
            return ResponseEntity.ok(usuario);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "No fue posible consultar el usuario");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PutMapping("/{matricula}")
    public ResponseEntity<?> actualizar(@PathVariable String matricula, @RequestBody UsuarioDTO dto) {
        try {
            UsuarioDTO actualizado = usuarioService.modificarUsuario(matricula, dto);
            return ResponseEntity.ok(actualizado);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "No fue posible actualizar el usuario");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PatchMapping("/{matricula}/desactivar")
    public ResponseEntity<?> desactivar(@PathVariable String matricula) {
        try {
            UsuarioDTO actualizado = usuarioService.bajaLogica(matricula);

            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("mensaje", "Usuario desactivado correctamente");
            respuesta.put("usuario", actualizado);
            return ResponseEntity.ok(respuesta);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "No fue posible desactivar el usuario");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}