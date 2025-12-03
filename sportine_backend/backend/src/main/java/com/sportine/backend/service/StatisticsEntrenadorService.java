package com.sportine.backend.service;

import com.sportine.backend.dto.*;

import java.util.List;

/**
 * Service para manejar las estadísticas que ve el entrenador sobre sus alumnos.
 * Permite monitorear el progreso de cada alumno individualmente.
 */
public interface StatisticsEntrenadorService {

    /**
     * Obtiene una lista resumida de todos los alumnos del entrenador con métricas básicas.
     * Se muestra en la pantalla principal de estadísticas del entrenador.
     *
     * @param usuarioEntrenador Usuario del entrenador
     * @return Lista de AlumnoCardStatsDTO con métricas resumidas de cada alumno
     */
    List<AlumnoCardStatsDTO> obtenerResumenAlumnos(String usuarioEntrenador);

    /**
     * Obtiene las estadísticas detalladas de un alumno específico.
     * Incluye todas las gráficas y métricas disponibles.
     *
     * @param usuarioEntrenador Usuario del entrenador
     * @param usuarioAlumno Usuario del alumno a consultar
     * @return DetalleEstadisticasAlumnoDTO con información completa
     * @throws com.sportine.backend.exception.AccesoNoAutorizadoException si no existe relación activa
     */
    DetalleEstadisticasAlumnoDTO obtenerDetalleEstadisticasAlumno(
            String usuarioEntrenador,
            String usuarioAlumno
    );

    /**
     * Obtiene la frecuencia de entrenamientos de un alumno específico.
     *
     * @param usuarioEntrenador Usuario del entrenador
     * @param usuarioAlumno Usuario del alumno
     * @param periodo "WEEK", "MONTH", o "YEAR"
     * @return TrainingFrequencyDTO con datos de frecuencia
     */
    TrainingFrequencyDTO obtenerFrecuenciaAlumno(
            String usuarioEntrenador,
            String usuarioAlumno,
            String periodo
    );

    /**
     * Obtiene la distribución de deportes que entrena un alumno con este entrenador.
     *
     * @param usuarioEntrenador Usuario del entrenador
     * @param usuarioAlumno Usuario del alumno
     * @return SportsDistributionDTO con distribución de deportes
     */
    SportsDistributionDTO obtenerDistribucionDeportesAlumno(
            String usuarioEntrenador,
            String usuarioAlumno
    );

    /**
     * Obtiene el feedback promedio de un alumno.
     *
     * @param usuarioEntrenador Usuario del entrenador
     * @param usuarioAlumno Usuario del alumno
     * @return FeedbackPromedioDTO con promedios de feedback
     */
    FeedbackPromedioDTO obtenerFeedbackAlumno(
            String usuarioEntrenador,
            String usuarioAlumno
    );
}