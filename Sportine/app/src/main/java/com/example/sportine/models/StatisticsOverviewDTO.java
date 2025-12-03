package com.example.sportine.models;


/**
 * DTO para el resumen general de estadísticas del alumno.
 * Debe coincidir exactamente con el DTO del backend.
 */
public class StatisticsOverviewDTO {

    // Métricas Principales
    private Integer totalEntrenamientos;
    private Integer rachaActual;
    private Integer mejorRacha;
    private Integer entrenamientosMesActual;
    private Integer entrenamientosSemanaActual;

    // Tiempo Total
    private Integer tiempoTotalMinutos;
    private String tiempoTotalFormateado;

    // Deportes
    private Integer deportesPracticados;

    // Tendencia
    private String tendencia;
    private Double porcentajeCambio;

    // Feedback Promedio
    private Double nivelCansancioPromedio;
    private Double dificultadPercibidaPromedio;

    // Consistencia
    private Double porcentajeCompletado;

    // Constructor vacío (necesario para Gson)
    public StatisticsOverviewDTO() {
    }

    // Getters y Setters
    public Integer getTotalEntrenamientos() {
        return totalEntrenamientos;
    }

    public void setTotalEntrenamientos(Integer totalEntrenamientos) {
        this.totalEntrenamientos = totalEntrenamientos;
    }

    public Integer getRachaActual() {
        return rachaActual;
    }

    public void setRachaActual(Integer rachaActual) {
        this.rachaActual = rachaActual;
    }

    public Integer getMejorRacha() {
        return mejorRacha;
    }

    public void setMejorRacha(Integer mejorRacha) {
        this.mejorRacha = mejorRacha;
    }

    public Integer getEntrenamientosMesActual() {
        return entrenamientosMesActual;
    }

    public void setEntrenamientosMesActual(Integer entrenamientosMesActual) {
        this.entrenamientosMesActual = entrenamientosMesActual;
    }

    public Integer getEntrenamientosSemanaActual() {
        return entrenamientosSemanaActual;
    }

    public void setEntrenamientosSemanaActual(Integer entrenamientosSemanaActual) {
        this.entrenamientosSemanaActual = entrenamientosSemanaActual;
    }

    public Integer getTiempoTotalMinutos() {
        return tiempoTotalMinutos;
    }

    public void setTiempoTotalMinutos(Integer tiempoTotalMinutos) {
        this.tiempoTotalMinutos = tiempoTotalMinutos;
    }

    public String getTiempoTotalFormateado() {
        return tiempoTotalFormateado;
    }

    public void setTiempoTotalFormateado(String tiempoTotalFormateado) {
        this.tiempoTotalFormateado = tiempoTotalFormateado;
    }

    public Integer getDeportesPracticados() {
        return deportesPracticados;
    }

    public void setDeportesPracticados(Integer deportesPracticados) {
        this.deportesPracticados = deportesPracticados;
    }

    public String getTendencia() {
        return tendencia;
    }

    public void setTendencia(String tendencia) {
        this.tendencia = tendencia;
    }

    public Double getPorcentajeCambio() {
        return porcentajeCambio;
    }

    public void setPorcentajeCambio(Double porcentajeCambio) {
        this.porcentajeCambio = porcentajeCambio;
    }

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

    public Double getPorcentajeCompletado() {
        return porcentajeCompletado;
    }

    public void setPorcentajeCompletado(Double porcentajeCompletado) {
        this.porcentajeCompletado = porcentajeCompletado;
    }
}
