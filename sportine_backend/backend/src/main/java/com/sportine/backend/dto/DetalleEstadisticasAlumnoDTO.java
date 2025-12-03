package com.sportine.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO para el detalle completo de estadísticas de un alumno específico.
 * Usado por el entrenador para ver información detallada de uno de sus alumnos.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetalleEstadisticasAlumnoDTO {

    // Información del Alumno
    private String usuario;
    private String nombreCompleto;
    private String fotoPerfil;

    // Resumen General (similar a StatisticsOverviewDTO pero filtrado por entrenador)
    private StatisticsOverviewDTO resumenGeneral;

    // Frecuencia de Entrenamientos
    private TrainingFrequencyDTO frecuenciaEntrenamientos;

    // Distribución de Deportes (solo los que entrena con este entrenador)
    private SportsDistributionDTO distribucionDeportes;

    // Información de Racha
    private StreakInfoDTO infoRacha;

    // Feedback Detallado
    private FeedbackPromedioDTO feedbackPromedio;

    // Relación con el Entrenador
    private String fechaInicioRelacion;            // Desde cuándo entrenan juntos
    private Integer diasJuntos;                    // Días desde que empezaron
    private Integer entrenamientosJuntos;          // Total de entrenamientos asignados por este entrenador
}