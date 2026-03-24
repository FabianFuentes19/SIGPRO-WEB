package com.sigpro.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Manejo centralizado de excepciones para la API REST.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Conflicto de negocio: el costo del material supera el presupuesto disponible.
     * HTTP 409 Conflict (alternativa válida frente a 400 según criterio de API).
     */
    @ExceptionHandler(PresupuestoInsuficienteException.class)
    public ResponseEntity<Map<String, String>> handlePresupuestoInsuficiente(PresupuestoInsuficienteException ex) {
        Map<String, String> body = new HashMap<>();
        body.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    /**
     * Errores de Bean Validation en cuerpos JSON (@Valid).
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        Map<String, String> erroresPorCampo = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        fe -> fe.getDefaultMessage() != null ? fe.getDefaultMessage() : "Valor no válido",
                        (a, b) -> a + "; " + b
                ));
        Map<String, Object> body = new HashMap<>();
        body.put("error", "Los datos enviados no son válidos");
        body.put("detalles", erroresPorCampo);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    /**
     * Validación en parámetros de ruta o query (@Validated en controlador, {@code @Positive}, etc.).
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConstraintViolation(ConstraintViolationException ex) {
        Map<String, String> erroresPorCampo = new HashMap<>();
        for (ConstraintViolation<?> v : ex.getConstraintViolations()) {
            erroresPorCampo.put(v.getPropertyPath().toString(), v.getMessage());
        }
        Map<String, Object> body = new HashMap<>();
        body.put("error", "Los datos enviados no son válidos");
        body.put("detalles", erroresPorCampo);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }
}
