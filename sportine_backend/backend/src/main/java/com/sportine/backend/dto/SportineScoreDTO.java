package com.sportine.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * DTO de respuesta para GET /sportine-score/{usuario} de FastAPI.
 *
 * Refleja el schema Pydantic SportineScoreResponse:
 *   usuario          → String
 *   sportine_score   → Double  (0-100)
 *   desglose         → objeto con 5 dimensiones
 *   nivel            → "Principiante" | "Intermedio" | "Avanzado" | "Elite"
 *
 * Los @JsonProperty mapean snake_case de Python → camelCase de Java.
 */
@Data
public class SportineScoreDTO {

    private String usuario;

    /** Score global 0-100. En JSON viene como "sportine_score". */
    @JsonProperty("sportine_score")
    private Double sportineScore;

    /** Desglose de los 5 componentes del score. */
    private Desglose desglose;

    /** "Principiante" / "Intermedio" / "Avanzado" / "Elite" */
    private String nivel;

    @Data
    public static class Desglose {

        /** Puntos de constancia — peso 30% */
        private Double constancia;

        /** Puntos de completitud — peso 20% */
        private Double completitud;

        /** Puntos de esfuerzo percibido — peso 20% */
        private Double esfuerzo;

        /** Puntos de progreso de carrera — peso 20% */
        private Double carrera;

        /** Puntos de actividad Health Connect — peso 10%. En JSON: "actividad_hc" */
        @JsonProperty("actividad_hc")
        private Double actividadHc;
    }
}