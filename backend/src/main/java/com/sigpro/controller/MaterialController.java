package com.sigpro.controller;

import com.sigpro.dto.MaterialRequestDTO;
import com.sigpro.dto.MaterialResponseDTO;
import com.sigpro.service.MaterialService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.constraints.Positive;
import java.util.List;

@RestController
@RequestMapping("/api/materiales")
@Validated
@PreAuthorize("hasRole('LIDER')")
public class MaterialController {

    @Autowired
    private MaterialService materialService;

    @PostMapping
    public ResponseEntity<MaterialResponseDTO> registrar(@Valid @RequestBody MaterialRequestDTO dto) {
        MaterialResponseDTO creado = materialService.registrarMaterial(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @GetMapping("/proyecto/{proyectoId}")
    public ResponseEntity<List<MaterialResponseDTO>> listarPorProyecto(
            @PathVariable @Positive(message = "El identificador del proyecto debe ser mayor que cero") Long proyectoId) {
        List<MaterialResponseDTO> lista = materialService.listarMaterialesPorProyecto(proyectoId);
        return ResponseEntity.ok(lista);
    }
}
