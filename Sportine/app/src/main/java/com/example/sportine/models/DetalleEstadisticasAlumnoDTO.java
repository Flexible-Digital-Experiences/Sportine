package com.example.sportine.models;

/**
 * DTO para el detalle completo de estadísticas de un alumno específico.
 * Usado por el entrenador para ver información detallada de uno de sus alumnos.
 */
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
    private String fechaInicioRelacion;
    private Integer diasJuntos;
    private Integer entrenamientosJuntos;

    // Constructor vacío
    public DetalleEstadisticasAlumnoDTO() {
    }

    // Getters y Setters
    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getFotoPerfil() {
        return fotoPerfil;
    }

    public void setFotoPerfil(String fotoPerfil) {
        this.fotoPerfil = fotoPerfil;
    }

    public StatisticsOverviewDTO getResumenGeneral() {
        return resumenGeneral;
    }

    public void setResumenGeneral(StatisticsOverviewDTO resumenGeneral) {
        this.resumenGeneral = resumenGeneral;
    }

    public TrainingFrequencyDTO getFrecuenciaEntrenamientos() {
        return frecuenciaEntrenamientos;
    }

    public void setFrecuenciaEntrenamientos(TrainingFrequencyDTO frecuenciaEntrenamientos) {
        this.frecuenciaEntrenamientos = frecuenciaEntrenamientos;
    }

    public SportsDistributionDTO getDistribucionDeportes() {
        return distribucionDeportes;
    }

    public void setDistribucionDeportes(SportsDistributionDTO distribucionDeportes) {
        this.distribucionDeportes = distribucionDeportes;
    }

    public StreakInfoDTO getInfoRacha() {
        return infoRacha;
    }

    public void setInfoRacha(StreakInfoDTO infoRacha) {
        this.infoRacha = infoRacha;
    }

    public FeedbackPromedioDTO getFeedbackPromedio() {
        return feedbackPromedio;
    }

    public void setFeedbackPromedio(FeedbackPromedioDTO feedbackPromedio) {
        this.feedbackPromedio = feedbackPromedio;
    }

    public String getFechaInicioRelacion() {
        return fechaInicioRelacion;
    }

    public void setFechaInicioRelacion(String fechaInicioRelacion) {
        this.fechaInicioRelacion = fechaInicioRelacion;
    }

    public Integer getDiasJuntos() {
        return diasJuntos;
    }

    public void setDiasJuntos(Integer diasJuntos) {
        this.diasJuntos = diasJuntos;
    }

    public Integer getEntrenamientosJuntos() {
        return entrenamientosJuntos;
    }

    public void setEntrenamientosJuntos(Integer entrenamientosJuntos) {
        this.entrenamientosJuntos = entrenamientosJuntos;
    }
}
