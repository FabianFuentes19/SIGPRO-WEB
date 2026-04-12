package com.sigpro.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagoConAlertaResponseDTO {
    private PagoResponseDTO pago;
    private AlertaDTO alerta; // Puede ser null si no hay alerta
}
