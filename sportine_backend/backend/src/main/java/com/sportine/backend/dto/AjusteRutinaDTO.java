package com.sportine.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * DTO de respuesta para GET /ajuste-rutina/{usuario}?n_sesiones=5 de FastAPI.
 *
 * Refleja el schema Pydantic AjusteRutinaResponse:
 *   usuario        → String
 *   recomendacion  → "mantener" | "subir_intensidad" | "bajar_intensidad" | "revisar_motivacion"
 *   mensaje        → String explicativo para mostrar al entrenador
 *   metricas_base  → promedios de las últimas N sesiones analizadas
 */
@Data
public class AjusteRutinaDTO {

    private String usuario;

    /**
     * Recomendación del algoritmo.
     * Valores posibles: "mantener", "subir_intensidad", "bajar_intensidad", "revisar_motivacion"
     */
    private String recomendacion;

    /** Mensaje explicativo listo para mostrar en la UI. */
    private String mensaje;

    /** Métricas promedio de las sesiones analizadas. En JSON: "metricas_base". */
    @JsonProperty("metricas_base")
    private MetricasBase metricasBase;

    @Data
    public static class MetricasBase {

        /** Promedio de cansancio reportado (1-10). En JSON: "prom_cansancio". */
        @JsonProperty("prom_cansancio")
        private Double promCansancio;

        /** Promedio de dificultad percibida (1-10). En JSON: "prom_dificultad". */
        @JsonProperty("prom_dificultad")
        private Double promDificultad;

        /** Promedio de FC máxima — puede ser null si no hay datos HC. En JSON: "prom_fc_max". */
        @JsonProperty("prom_fc_max")
        private Double promFcMax;

        /** Cuántas sesiones se usaron para calcular estos promedios. En JSON: "n_sesiones_analizadas". */
        @JsonProperty("n_sesiones_analizadas")
        private Integer nSesionesAnalizadas;
    }
}