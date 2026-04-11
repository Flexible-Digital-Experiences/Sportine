package com.sportine.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistorialEntrenamientoDTO {

    @JsonProperty("id_entrenamiento")
    private Integer idEntrenamiento;

    @JsonProperty("titulo")
    private String titulo;

    @JsonProperty("fecha")
    private String fecha; // "31 mar."

    @JsonProperty("dificultad")
    private String dificultad;

    @JsonProperty("duracion_min")
    private Integer duracionMin;       // hc_duracion_activa_min

    @JsonProperty("calorias_kcal")
    private Integer caloriasKcal;      // hc_calorias_kcal

    @JsonProperty("distancia_metros")
    private Float distanciaMetros;     // hc_distancia_metros

    @JsonProperty("pasos")
    private Integer pasos;             // hc_pasos

    @JsonProperty("tiene_hc")
    private Boolean tieneHc;           // true si tiene datos de Health Connect
}