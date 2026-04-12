package com.sigpro.util;

import com.sigpro.dto.AlertaDTO;
import com.sigpro.model.Proyecto;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class AlertaCalculator {

    /**
     * Calcula la alerta basada en el presupuesto restante del proyecto
     * 
     * @param proyecto El proyecto a analizar
     * @return AlertaDTO con la alerta correspondiente, o null si no hay alerta
     */
    public static AlertaDTO calcularAlerta(Proyecto proyecto) {
        if (proyecto == null || proyecto.getPresupuesto() == null || proyecto.getPresupuestoInicial() == null) {
            return null;
        }

        BigDecimal presupuestoRestante = proyecto.getPresupuesto();
        BigDecimal presupuestoTotal = proyecto.getPresupuestoAutorizado() != null ?
                proyecto.getPresupuestoAutorizado() : proyecto.getPresupuestoInicial();

        // Si el presupuesto inicial es 0 o negativo, no calcular
        if (presupuestoTotal.compareTo(BigDecimal.ZERO) <= 0) {
            return null;
        }

        // Calcular porcentaje: (presupuesto_restante / presupuesto_total) * 100
        BigDecimal porcentaje = presupuestoRestante
                .divide(presupuestoTotal, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP);

        // Presupuesto agotado (0%)
        if (presupuestoRestante.compareTo(BigDecimal.ZERO) <= 0) {
            AlertaDTO alerta = new AlertaDTO();
            alerta.setTipo("error");
            alerta.setMensaje("Presupuesto agotado");
            alerta.setPorcentajeRestante(BigDecimal.ZERO);
            alerta.setMontoRestante(BigDecimal.ZERO);
            alerta.setPresupuestoTotal(presupuestoTotal);
            return alerta;
        }

        // Presupuesto crítico (≤ 10%)
        if (porcentaje.compareTo(BigDecimal.TEN) <= 0) {
            AlertaDTO alerta = new AlertaDTO();
            alerta.setTipo("error");
            alerta.setMensaje("Te queda menos del 10% de presupuesto");
            alerta.setPorcentajeRestante(porcentaje);
            alerta.setMontoRestante(presupuestoRestante);
            alerta.setPresupuestoTotal(presupuestoTotal);
            return alerta;
        }

        // Presupuesto en riesgo (≤ 20%)
        if (porcentaje.compareTo(BigDecimal.valueOf(20)) <= 0) {
            AlertaDTO alerta = new AlertaDTO();
            alerta.setTipo("advertencia");
            alerta.setMensaje("Te queda menos del 20% de presupuesto)");
            alerta.setPorcentajeRestante(porcentaje);
            alerta.setMontoRestante(presupuestoRestante);
            alerta.setPresupuestoTotal(presupuestoTotal);
            return alerta;
        }

        // Si está entre 20% y 100%, no hay alerta
        return null;
    }
}
