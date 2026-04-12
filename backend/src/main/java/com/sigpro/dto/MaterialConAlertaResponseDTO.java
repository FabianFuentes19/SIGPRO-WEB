package com.sigpro.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MaterialConAlertaResponseDTO {
    private MaterialResponseDTO material;
    private AlertaDTO alerta; // Puede ser null si no hay alerta
}
