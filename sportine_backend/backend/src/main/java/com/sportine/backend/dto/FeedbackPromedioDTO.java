package com.sportine.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO para los promedios de feedback del alumno.
 * Muestra cómo el alumno percibe la dificultad y cansancio de los entrenamientos.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackPromedioDTO {

    // Promedios Generales
    private Double nivelCansancioPromedio;         // 1-10
    private Double dificultadPercibidaPromedio;    // 1-10

    // Estadísticas de Feedback
    private Integer totalFeedbacksRecibidos;
    private String estadoAnimoPredominante;        // El estado de ánimo más frecuente

    // Tendencias
    private String tendenciaCansancio;             // "aumentando", "estable", "disminuyendo"
    private String tendenciaDificultad;            // "aumentando", "estable", "disminuyendo"

    // Comparación con otros alumnos (opcional, para el entrenador)
    private String comparacionGeneral;             // "Por encima del promedio", "Promedio", "Por debajo"

    // Recomendaciones
    private String recomendacion;                  // Mensaje automático basado en los datos
}