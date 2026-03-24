package com.sigpro.controller;

import com.sigpro.dto.PagoDTO;
import com.sigpro.service.PagoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/pagos")
@CrossOrigin(origins = "*") 
public class PagoController {

    @Autowired
    private PagoService pagoService;

    @PostMapping("/registrar")
    public ResponseEntity<?> registrarPago(@RequestBody PagoDTO dto) {
        try {
            PagoDTO pago = pagoService.registrarPago(dto);
            return ResponseEntity.ok(pago);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "No fue posible registrar el pago"));
        }
    }

    @GetMapping("/mis-pagos")
    public ResponseEntity<?> consultarMisPagos(Authentication auth) {
        try {
            List<PagoDTO> pagos = pagoService.consultarMisPagos(auth);
            return ResponseEntity.ok(pagos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "No fue posible consultar sus pagos"));
        }
    }

    @GetMapping("/miembro/{matricula}")
    public ResponseEntity<?> consultarPagosMiembro(@PathVariable String matricula, Authentication auth) {
        try {
            List<PagoDTO> pagos = pagoService.consultarPagosMiembro(matricula, auth);
            return ResponseEntity.ok(pagos);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "No fue posible consultar los pagos del miembro"));
        }
    }
}

