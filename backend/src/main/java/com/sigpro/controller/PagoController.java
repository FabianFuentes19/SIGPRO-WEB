package com.sigpro.controller;

import com.sigpro.dto.PagoRequestDTO;
import com.sigpro.dto.PagoResponseDTO;
import com.sigpro.dto.VoucherDTO;
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
    public ResponseEntity<?> registrarPago(@RequestBody PagoRequestDTO dto) {
        try {
            PagoResponseDTO pago = pagoService.registrarPago(dto);
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
            List<PagoResponseDTO> pagos = pagoService.consultarMisPagos(auth);
            return ResponseEntity.ok(pagos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "No fue posible consultar sus pagos"));
        }
    }

    @GetMapping("/miembro/{matricula}")
    public ResponseEntity<?> consultarPagosMiembro(@PathVariable String matricula, Authentication auth) {
        try {
            List<PagoResponseDTO> pagos = pagoService.consultarPagosMiembro(matricula, auth);
            return ResponseEntity.ok(pagos);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "No fue posible consultar los pagos del miembro"));
        }
    }

    @GetMapping("/proyecto")
    public ResponseEntity<?> consultarPagosProyecto(Authentication auth) {
        try {
            List<PagoResponseDTO> pagos = pagoService.consultarPagosProyecto(auth);
            return ResponseEntity.ok(pagos);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Error en la consulta del proyecto"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "No fue posible consultar los pagos del proyecto"));
        }
    }

    @GetMapping("/vouchers/{matricula}")
    public ResponseEntity<?> consultarVouchersMiembro(@PathVariable String matricula, Authentication auth) {
        try {
            List<VoucherDTO> vouchers = pagoService.obtenerHistorialVouchers(matricula, auth);
            return ResponseEntity.ok(vouchers);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error inesperado al consultar los vouchers"));
        }
    }
}

