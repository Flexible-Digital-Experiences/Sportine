package com.sportine.backend.service;

import com.sportine.backend.dto.*;

/**
 * Service para manejar todas las estadísticas del alumno.
 * Proporciona métricas, gráficas y análisis de progreso.
 */
public interface StatisticsAlumnoService {

    /**
     * Obtiene el resumen general de estadísticas del alumno.
     * Incluye métricas principales como total de entrenamientos, racha, deportes, etc.
     *
     * @param username Usuario del alumno
     * @return StatisticsOverviewDTO con todas las métricas principales
     */
    StatisticsOverviewDTO obtenerResumenGeneral(String username);

    /**
     * Obtiene datos de frecuencia de entrenamientos para gráficas.
     * Puede ser por semana, mes o año.
     *
     * @param username Usuario del alumno
     * @param periodo "WEEK", "MONTH", o "YEAR"
     * @return TrainingFrequencyDTO con datos para la gráfica de barras
     */
    TrainingFrequencyDTO obtenerFrecuenciaEntrenamientos(String username, String periodo);

    /**
     * Obtiene la distribución de deportes que practica el alumno.
     * Muestra qué porcentaje de entrenamientos corresponde a cada deporte.
     *
     * @param username Usuario del alumno
     * @return SportsDistributionDTO con datos para gráfica de pastel
     */
    SportsDistributionDTO obtenerDistribucionDeportes(String username);

    /**
     * Obtiene información detallada sobre las rachas del alumno.
     * Incluye racha actual, mejor racha, y datos de consistencia.
     *
     * @param username Usuario del alumno
     * @return StreakInfoDTO con información completa de rachas
     */
    StreakInfoDTO obtenerInformacionRacha(String username);

    /**
     * Obtiene el feedback promedio del alumno.
     * Incluye nivel de cansancio y dificultad percibida.
     *
     * @param username Usuario del alumno
     * @return FeedbackPromedioDTO con promedios de feedback
     */
    FeedbackPromedioDTO obtenerFeedbackPromedio(String username);
}