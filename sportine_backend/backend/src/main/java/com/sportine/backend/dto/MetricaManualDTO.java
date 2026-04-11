package com.sportine.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * DTO para que el alumno reporte métricas manuales de su entrenamiento.
 * Ej: goles, tiros libres, vueltas en natación, rounds de boxeo, etc.
 * Soporta envío de múltiples métricas en una sola llamada.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MetricaManualDTO {

    @JsonProperty("id_entrenamiento")
    private Integer idEntrenamiento;

    @JsonProperty("metricas")
    private List<MetricaItemDTO> metricas;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MetricaItemDTO {

        // FK a Plantilla_Metricas_Deporte
        @JsonProperty("id_plantilla")
        private Integer idPlantilla;

        @JsonProperty("valor_numerico")
        private Float valorNumerico;

        // NULL si es del entrenamiento completo, 1/2/3 si es por serie
        @JsonProperty("numero_serie")
        private Integer numeroSerie;

        @JsonProperty("notas")
        private String notas;
    }
}