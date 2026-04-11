package com.sportine.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * DTO de estadísticas históricas de un alumno en un deporte específico.
 * Agrega datos de Health Connect y métricas manuales a lo largo del tiempo.
 * Útil para gráficas de progreso tanto en Android como en la web.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstadisticasDeporteDTO {

    @JsonProperty("id_deporte")
    private Integer idDeporte;

    @JsonProperty("nombre_deporte")
    private String nombreDeporte;

    @JsonProperty("total_entrenamientos")
    private Integer totalEntrenamientos;

    @JsonProperty("total_entrenamientos_con_hc")
    private Integer totalEntrenamientosConHc;

    // ── Promedios generales de Health Connect ──────────────────────
    @JsonProperty("promedio_calorias_kcal")
    private Double promedioCaloriasKcal;

    @JsonProperty("promedio_duracion_min")
    private Double promedioDuracionMin;

    @JsonProperty("promedio_distancia_metros")
    private Double promedioDistanciaMetros;

    @JsonProperty("promedio_fc")
    private Double promedioFc;

    @JsonProperty("promedio_pasos")
    private Double promedioPasos;

    // Pace en m/s promedio — la app convierte según el deporte
    @JsonProperty("promedio_velocidad_ms")
    private Double promedioVelocidadMs;

    // ── Evolución temporal para gráficas ──────────────────────────
    @JsonProperty("evolucion")
    private List<PuntoEvolucionDTO> evolucion;

    // ── Métricas específicas del deporte (manuales) ───────────────
    @JsonProperty("metricas_deporte")
    private List<ResumenMetricaDTO> metricasDeporte;

    // ── DTOs internos ──────────────────────────────────────────────

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PuntoEvolucionDTO {
        @JsonProperty("fecha")
        private String fecha;

        @JsonProperty("id_entrenamiento")
        private Integer idEntrenamiento;

        @JsonProperty("calorias_kcal")
        private Integer caloriasKcal;

        @JsonProperty("duracion_min")
        private Integer duracionMin;

        @JsonProperty("distancia_metros")
        private Float distanciaMetros;

        @JsonProperty("fc_promedio")
        private Integer fcPromedio;

        @JsonProperty("pasos")
        private Integer pasos;

        @JsonProperty("velocidad_ms")
        private Float velocidadMs;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResumenMetricaDTO {
        @JsonProperty("nombre_metrica")
        private String nombreMetrica;

        @JsonProperty("etiqueta_display")
        private String etiquetaDisplay;

        @JsonProperty("unidad")
        private String unidad;

        @JsonProperty("promedio")
        private Double promedio;

        @JsonProperty("maximo")
        private Double maximo;

        @JsonProperty("total_registros")
        private Integer totalRegistros;
    }
}