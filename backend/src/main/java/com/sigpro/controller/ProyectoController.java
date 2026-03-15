package com.sigpro.controller;

import com.sigpro.dto.ProyectoDTO;
import com.sigpro.dto.UsuarioDTO;
import com.sigpro.model.ProyectoUsuario;
import com.sigpro.service.ProyectoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/proyectos")
@CrossOrigin(origins = "*") 
public class ProyectoController {

    @Autowired
    private ProyectoService proyectoService;

    @GetMapping
    public ResponseEntity<?> consultarTodos(Authentication auth) {
        try {
            List<ProyectoDTO> proyectos = proyectoService.consultarTodos(auth);
            return ResponseEntity.ok(proyectos);
        } catch (SecurityException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "No fue posible consultar los proyectos");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/buscar")
    public ResponseEntity<?> buscarPorNombre(@RequestParam String nombre, Authentication auth) {
        try {
            List<ProyectoDTO> proyectos = proyectoService.buscarPorNombre(nombre, auth);
            return ResponseEntity.ok(proyectos);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (SecurityException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "No fue posible buscar el proyecto");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PostMapping
    public ResponseEntity<?> crearProyecto(@RequestBody ProyectoDTO dto, Authentication auth) {
        try {
            ProyectoDTO nuevoProyecto = proyectoService.crearProyecto(dto, auth);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoProyecto);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (SecurityException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "No fue posible crear el proyecto");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editarProyecto(@PathVariable Long id, @RequestBody ProyectoDTO dto, Authentication auth) {
        try {
            ProyectoDTO proyectoActualizado = proyectoService.editarProyecto(id, dto, auth);
            return ResponseEntity.ok(proyectoActualizado);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (SecurityException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "No fue posible actualizar el proyecto");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/mi-proyecto/lider")
    public ResponseEntity<?> consultarProyectoLider(Authentication auth) {
        try {
            ProyectoDTO proyecto = proyectoService.consultarProyectoLider(auth);
            return ResponseEntity.ok(proyecto);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (SecurityException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "No fue posible consultar el proyecto del líder");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/mi-proyecto/miembro")
    public ResponseEntity<?> consultarProyectoMiembro(Authentication auth) {
        try {
            ProyectoDTO proyecto = proyectoService.consultarProyectoMiembro(auth);
            return ResponseEntity.ok(proyecto);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (SecurityException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "No fue posible consultar el proyecto del miembro");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> consultarDetalle(@PathVariable Long id, Authentication auth) {
        try {
            ProyectoDTO proyecto = proyectoService.obtenerDetalleProyecto(id, auth);
            return ResponseEntity.ok(proyecto);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (SecurityException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "No fue posible consultar el detalle del proyecto");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PostMapping("/{proyectoId}/miembros")
    public ResponseEntity<?> registrarMiembro(@PathVariable Long proyectoId, @RequestBody UsuarioDTO dto, Authentication auth) {
        try {
            ProyectoUsuario nuevoMiembro = proyectoService.registrarMiembro(proyectoId, dto, auth);
            
            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("mensaje", "Miembro registrado y agregado al proyecto correctamente");
            respuesta.put("matricula", nuevoMiembro.getUsuario().getMatricula());
            
            return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (SecurityException e) {
             Map<String, String> error = new HashMap<>();
             error.put("error", e.getMessage());
             return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "No fue posible registrar al miembro");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
