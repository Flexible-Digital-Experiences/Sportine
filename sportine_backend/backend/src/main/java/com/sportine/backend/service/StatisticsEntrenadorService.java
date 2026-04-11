package com.sportine.backend.service;

import com.sportine.backend.dto.*;

import java.util.List;

public interface StatisticsEntrenadorService {

    List<AlumnoCardStatsDTO> obtenerResumenAlumnos(String usuarioEntrenador);

    DetalleEstadisticasAlumnoDTO obtenerDetalleEstadisticasAlumno(
            String usuarioEntrenador, String usuarioAlumno);

    TrainingFrequencyDTO obtenerFrecuenciaAlumno(
            String usuarioEntrenador, String usuarioAlumno, String periodo);

    SportsDistributionDTO obtenerDistribucionDeportesAlumno(
            String usuarioEntrenador, String usuarioAlumno);

    FeedbackPromedioDTO obtenerFeedbackAlumno(
            String usuarioEntrenador, String usuarioAlumno);

    // ── Nuevos métodos para estadísticas por deporte ──────────────────────────

    /**
     * Deportes que el entrenador imparte a un alumno específico.
     * Filtra por Entrenador_Alumno con status activo.
     */
    List<DeporteAlumnoDTO> obtenerDeportesEntrenadorParaAlumno(
            String usuarioEntrenador, String usuarioAlumno);

    /**
     * Cards de carrera (acumulados históricos) del alumno en un deporte,
     * verificando que el entrenador tenga relación activa en ese deporte.
     */
    CarreraDeporteDTO obtenerCarreraAlumno(
            String usuarioEntrenador, String usuarioAlumno, Integer idDeporte);

    /**
     * Gráficas de métricas del alumno en un deporte para los últimos N entrenamientos.
     */
    MetricasUltimosDTO obtenerMetricasAlumno(
            String usuarioEntrenador, String usuarioAlumno, Integer idDeporte, int limite);

    /**
     * Historial de últimos N entrenamientos finalizados del alumno en un deporte.
     */
    List<HistorialEntrenamientoDTO> obtenerHistorialAlumno(
            String usuarioEntrenador, String usuarioAlumno, Integer idDeporte, int limite);
}