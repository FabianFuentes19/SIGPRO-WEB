package com.sigpro.exception;

/**
 * Se lanza cuando el costo del material excede el presupuesto disponible del proyecto
 * (presupuesto inicial menos gastos ya registrados en materiales).
 */
public class PresupuestoInsuficienteException extends RuntimeException {

    public PresupuestoInsuficienteException(String message) {
        super(message);
    }
}
