package com.sportine.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

/**
 * DTO para información detallada sobre las rachas de entrenamiento del alumno.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StreakInfoDTO {

    // Racha actual
    private Integer rachaActual;                   // Días consecutivos entrenando
    private LocalDate fechaInicioRacha;            // Cuándo empezó la racha actual
    private Boolean entrenoHoy;                    // Si ya entrenó hoy

    // Récords
    private Integer mejorRacha;                    // Récord histórico
    private LocalDate fechaMejorRacha;             // Cuándo logró su mejor racha

    // Motivación
    private String mensaje;                        // Mensaje motivacional
    private Integer diasParaProximoMilestone;      // Ej: 2 días para llegar a 10 días consecutivos
    private Integer proximoMilestone;              // Ej: 10, 20, 30 días

    // Estadísticas de Consistencia
    private Integer diasEntrenados;                // Total de días que ha entrenado
    private Integer diasTotales;                   // Días desde que se registró
    private Double porcentajeConsistencia;         // % de días entrenados
}