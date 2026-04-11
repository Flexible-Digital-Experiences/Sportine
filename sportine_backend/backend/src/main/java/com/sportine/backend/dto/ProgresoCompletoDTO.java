package com.sportine.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO de respuesta que unifica todo el progreso de un entrenamiento:
 * - Datos del entrenamiento base
 * - Métricas de Health Connect
 * - Ejercicios con sus series y resultados
 * - Métricas manuales del deporte
 * Usado tanto por Android como por la web de Sportine.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProgresoCompletoDTO {

    // ── Info del entrenamiento ──────────────────────────────────────
    @JsonProperty("id_entrenamiento")
    private Integer idEntrenamiento;

    @JsonProperty("titulo")
    private String titulo;

    @JsonProperty("nombre_deporte")
    private String nombreDeporte;

    @JsonProperty("fecha_entrenamiento")
    private String fechaEntrenamiento;

    @JsonProperty("estado_entrenamiento")
    private String estadoEntrenamiento;

    // ── Progreso general ───────────────────────────────────────────
    @JsonProperty("completado")
    private Boolean completado;

    @JsonProperty("fecha_inicio")
    private LocalDateTime fechaInicio;

    @JsonProperty("fecha_finalizacion")
    private LocalDateTime fechaFinalizacion;

    @JsonProperty("porcentaje_completado")
    private Double porcentajeCompletado; // % ejercicios completados o parciales

    // ── Datos de Health Connect ────────────────────────────────────
    @JsonProperty("tiene_datos_hc")
    private Boolean tieneDatosHc;

    @JsonProperty("hc_tipo_ejercicio")
    private String hcTipoEjercicio;

    @JsonProperty("hc_duracion_activa_min")
    private Integer hcDuracionActivaMin;

    @JsonProperty("hc_calorias_kcal")
    private Integer hcCaloriasKcal;

    @JsonProperty("hc_pasos")
    private Integer hcPasos;

    @JsonProperty("hc_distancia_metros")
    private Float hcDistanciaMetros;

    @JsonProperty("hc_fc_promedio")
    private Integer hcFcPromedio;

    @JsonProperty("hc_fc_maxima")
    private Integer hcFcMaxima;

    @JsonProperty("hc_velocidad_promedio_ms")
    private Float hcVelocidadPromedioMs;

    @JsonProperty("hc_elevacion_ganada_metros")
    private Float hcElevacionGanadaMetros;

    @JsonProperty("hc_fuente_datos")
    private String hcFuenteDatos;

    @JsonProperty("hc_sincronizado_en")
    private LocalDateTime hcSincronizadoEn;

    // ── Ejercicios con series ──────────────────────────────────────
    @JsonProperty("ejercicios")
    private List<EjercicioConSeriesDTO> ejercicios;

    // ── Métricas manuales del deporte ──────────────────────────────
    @JsonProperty("metricas_deporte")
    private List<MetricaResultadoDTO> metricasDeporte;

    // ── DTOs internos ──────────────────────────────────────────────

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EjercicioConSeriesDTO {
        @JsonProperty("id_asignado")
        private Integer idAsignado;

        @JsonProperty("nombre_ejercicio")
        private String nombreEjercicio;

        @JsonProperty("series_esperadas")
        private Integer seriesEsperadas;

        @JsonProperty("reps_esperadas")
        private Integer repsEsperadas;

        @JsonProperty("peso_esperado")
        private Float pesoEsperado;

        @JsonProperty("duracion_esperada_min")
        private Integer duracionEsperadaMin;

        @JsonProperty("distancia_esperada_metros")
        private Float distanciaEsperadaMetros;

        @JsonProperty("status_ejercicio")
        private String statusEjercicio;

        @JsonProperty("notas_entrenador")
        private String notasEntrenador;

        @JsonProperty("notas_alumno")
        private String notasAlumno;

        @JsonProperty("series")
        private List<SerieResultadoDTO> series;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SerieResultadoDTO {
        @JsonProperty("id_resultado")
        private Integer idResultado;

        @JsonProperty("numero_serie")
        private Integer numeroSerie;

        @JsonProperty("reps_esperadas")
        private Integer repsEsperadas;

        @JsonProperty("reps_completadas")
        private Integer repsCompletadas;

        @JsonProperty("peso_esperado")
        private Float pesoEsperado;

        @JsonProperty("peso_usado")
        private Float pesoUsado;

        @JsonProperty("duracion_esperada_seg")
        private Integer duracionEsperadaSeg;

        @JsonProperty("duracion_completada_seg")
        private Integer duracionCompletadaSeg;

        @JsonProperty("distancia_esperada_metros")
        private Float distanciaEsperadaMetros;

        @JsonProperty("distancia_completada_metros")
        private Float distanciaCompletadaMetros;

        @JsonProperty("status")
        private String status;

        @JsonProperty("registrado_en")
        private LocalDateTime registradoEn;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MetricaResultadoDTO {
        @JsonProperty("id_plantilla")
        private Integer idPlantilla;

        @JsonProperty("nombre_metrica")
        private String nombreMetrica;

        @JsonProperty("etiqueta_display")
        private String etiquetaDisplay;

        @JsonProperty("valor_numerico")
        private Float valorNumerico;

        @JsonProperty("unidad")
        private String unidad;

        @JsonProperty("fuente")
        private String fuente;

        @JsonProperty("numero_serie")
        private Integer numeroSerie;
    }
}