package com.sigpro.controller;


import com.sigpro.dto.PagoDTO;
import com.sigpro.service.PagoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/pagos")
@CrossOrigin(origins = "*") // esto es para que el endpoint pueda ser llamado desde movil o el frontend de web
public class PagoController {

    @Autowired
    private PagoService pagoService;

    @PostMapping("/registrar") // Define el endpoint
    public ResponseEntity<?> registrarPago(@RequestBody PagoDTO dto) { // recibe el JSON del pago y lo convierte automáticamente en un objeto PagoDTO
        try {
            PagoDTO pago = pagoService.registrarPago(dto); // llama al servicio para registrar el pago en la BD.
            return ResponseEntity.ok(pago);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "No fue posible registrar el pago"));
        }
    }
}

