package com.sportine.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * DTO de respuesta con las métricas configuradas para un deporte.
 * La app Android lo usa para saber qué campos mostrar dinámicamente
 * sin hardcodear nada en el frontend.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlantillaMetricasDTO {

    @JsonProperty("id_deporte")
    private Integer idDeporte;

    @JsonProperty("nombre_deporte")
    private String nombreDeporte;

    // Métricas que vienen de Health Connect automáticamente
    @JsonProperty("metricas_health_connect")
    private List<MetricaConfigDTO> metricasHealthConnect;

    // Métricas que el alumno llena manualmente
    @JsonProperty("metricas_manuales")
    private List<MetricaConfigDTO> metricasManuales;

    // Métricas calculadas a partir de otras (ej: puntos totales en basketball)
    @JsonProperty("metricas_calculadas")
    private List<MetricaConfigDTO> metricasCalculadas;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MetricaConfigDTO {
        @JsonProperty("id_plantilla")
        private Integer idPlantilla;

        @JsonProperty("nombre_metrica")
        private String nombreMetrica;

        @JsonProperty("etiqueta_display")
        private String etiquetaDisplay;

        @JsonProperty("unidad")
        private String unidad;

        @JsonProperty("fuente")
        private String fuente; // health_connect, manual, calculada

        @JsonProperty("es_por_serie")
        private Boolean esPorSerie;

        @JsonProperty("orden_display")
        private Integer ordenDisplay;
    }
}