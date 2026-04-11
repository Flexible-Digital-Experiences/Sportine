package com.sportine.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO que Android manda al backend después de leer una sesión de Health Connect.
 * El backend NO accede a HC directamente — Android lo hace y manda estos datos.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProgresoHealthConnectDTO {

    @JsonProperty("id_entrenamiento")
    private Integer idEntrenamiento;

    // ── Identificación de la sesión en Health Connect ──────────────
    @JsonProperty("hc_sesion_id")
    private String hcSesionId;

    @JsonProperty("hc_tipo_ejercicio")
    private String hcTipoEjercicio; // EXERCISE_TYPE_RUNNING, EXERCISE_TYPE_FOOTBALL, etc.

    // ── Métricas obtenidas de Health Connect ───────────────────────
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

    // Velocidad en m/s — la app Android convierte a pace o km/h según el deporte para mostrar
    @JsonProperty("hc_velocidad_promedio_ms")
    private Float hcVelocidadPromedioMs;

    @JsonProperty("hc_elevacion_ganada_metros")
    private Float hcElevacionGanadaMetros;

    // Fuente de los datos: 'health_connect', 'strava', 'manual'
    @JsonProperty("hc_fuente_datos")
    private String hcFuenteDatos;
}