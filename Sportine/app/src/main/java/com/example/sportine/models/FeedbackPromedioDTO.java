package com.example.sportine.models;

/**
 * DTO para los promedios de feedback del alumno.
 * Muestra cómo el alumno percibe la dificultad y cansancio de los entrenamientos.
 */
public class FeedbackPromedioDTO {

    // Promedios Generales
    private Double nivelCansancioPromedio;
    private Double dificultadPercibidaPromedio;

    // Estadísticas de Feedback
    private Integer totalFeedbacksRecibidos;
    private String estadoAnimoPredominante;

    // Tendencias
    private String tendenciaCansancio;
    private String tendenciaDificultad;

    // Comparación con otros alumnos (opcional, para el entrenador)
    private String comparacionGeneral;

    // Recomendaciones
    private String recomendacion;

    // Constructor vacío
    public FeedbackPromedioDTO() {
    }

    // Getters y Setters
    public Double getNivelCansancioPromedio() {
        return nivelCansancioPromedio;
    }

    public void setNivelCansancioPromedio(Double nivelCansancioPromedio) {
        this.nivelCansancioPromedio = nivelCansancioPromedio;
    }

    public Double getDificultadPercibidaPromedio() {
        return dificultadPercibidaPromedio;
    }

    public void setDificultadPercibidaPromedio(Double dificultadPercibidaPromedio) {
        this.dificultadPercibidaPromedio = dificultadPercibidaPromedio;
    }

    public Integer getTotalFeedbacksRecibidos() {
        return totalFeedbacksRecibidos;
    }

    public void setTotalFeedbacksRecibidos(Integer totalFeedbacksRecibidos) {
        this.totalFeedbacksRecibidos = totalFeedbacksRecibidos;
    }

    public String getEstadoAnimoPredominante() {
        return estadoAnimoPredominante;
    }

    public void setEstadoAnimoPredominante(String estadoAnimoPredominante) {
        this.estadoAnimoPredominante = estadoAnimoPredominante;
    }

    public String getTendenciaCansancio() {
        return tendenciaCansancio;
    }

    public void setTendenciaCansancio(String tendenciaCansancio) {
        this.tendenciaCansancio = tendenciaCansancio;
    }

    public String getTendenciaDificultad() {
        return tendenciaDificultad;
    }

    public void setTendenciaDificultad(String tendenciaDificultad) {
        this.tendenciaDificultad = tendenciaDificultad;
    }

    public String getComparacionGeneral() {
        return comparacionGeneral;
    }

    public void setComparacionGeneral(String comparacionGeneral) {
        this.comparacionGeneral = comparacionGeneral;
    }

    public String getRecomendacion() {
        return recomendacion;
    }

    public void setRecomendacion(String recomendacion) {
        this.recomendacion = recomendacion;
    }
}