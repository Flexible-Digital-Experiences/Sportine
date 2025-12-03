package com.sportine.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO para el resumen general de estadísticas del alumno.
 * Contiene las métricas principales que se muestran en cards.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatisticsOverviewDTO {

    // Métricas Principales
    private Integer totalEntrenamientos;           // Total de entrenamientos completados
    private Integer rachaActual;                   // Días consecutivos entrenando
    private Integer mejorRacha;                    // Récord de días consecutivos
    private Integer entrenamientosMesActual;       // Entrenamientos en el mes actual
    private Integer entrenamientosSemanaActual;    // Entrenamientos en la semana actual

    // Tiempo Total
    private Integer tiempoTotalMinutos;            // Tiempo total invertido en entrenamientos
    private String tiempoTotalFormateado;          // Ej: "15h 30m"

    // Deportes
    private Integer deportesPracticados;           // Número de deportes diferentes

    // Tendencia
    private String tendencia;                      // "mejorando", "estable", "decreciendo"
    private Double porcentajeCambio;               // Comparado con periodo anterior

    // Feedback Promedio
    private Double nivelCansancioPromedio;         // 1-10
    private Double dificultadPercibidaPromedio;    // 1-10

    // Consistencia
    private Double porcentajeCompletado;           // % de entrenamientos completados vs asignados
}