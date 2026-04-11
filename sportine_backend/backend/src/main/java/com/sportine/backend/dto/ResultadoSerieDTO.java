package com.sportine.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO para que el alumno reporte el resultado de una serie específica.
 *
 * exitosos = reps exitosas:
 *   Fútbol → tiros que fueron gol
 *   Basketball → tiros que entraron
 *   Boxeo → golpes que conectaron
 *   null → no aplica (gym, cardio puro, etc.)
 *
 * El agente n8n lee exitosos + nombre_ejercicio para poblar Resultado_Metrica_Manual.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResultadoSerieDTO {

    @JsonProperty("id_asignado")
    private Integer idAsignado;

    @JsonProperty("numero_serie")
    private Integer numeroSerie;

    @JsonProperty("reps_completadas")
    private Integer repsCompletadas;

    @JsonProperty("peso_usado")
    private Float pesoUsado;

    @JsonProperty("duracion_completada_seg")
    private Integer duracionCompletadaSeg;

    @JsonProperty("distancia_completada_metros")
    private Float distanciaCompletadaMetros;

    // null = no aplica, 0+ = cuántos salieron bien
    @JsonProperty("exitosos")
    private Integer exitosos;

    @JsonProperty("status")
    private String status;

    @JsonProperty("notas")
    private String notas;
}