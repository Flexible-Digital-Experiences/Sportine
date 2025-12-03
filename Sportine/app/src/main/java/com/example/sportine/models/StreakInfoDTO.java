package com.example.sportine.models;

/**
 * DTO para información detallada sobre las rachas de entrenamiento del alumno.
 */
public class StreakInfoDTO {

    // Racha actual
    private Integer rachaActual;
    private String fechaInicioRacha;
    private Boolean entrenoHoy;

    // Récords
    private Integer mejorRacha;
    private String fechaMejorRacha;

    // Motivación
    private String mensaje;
    private Integer diasParaProximoMilestone;
    private Integer proximoMilestone;

    // Estadísticas de Consistencia
    private Integer diasEntrenados;
    private Integer diasTotales;
    private Double porcentajeConsistencia;

    // Constructor vacío
    public StreakInfoDTO() {
    }

    // Getters y Setters
    public Integer getRachaActual() {
        return rachaActual;
    }

    public void setRachaActual(Integer rachaActual) {
        this.rachaActual = rachaActual;
    }

    public String getFechaInicioRacha() {
        return fechaInicioRacha;
    }

    public void setFechaInicioRacha(String fechaInicioRacha) {
        this.fechaInicioRacha = fechaInicioRacha;
    }

    public Boolean getEntrenoHoy() {
        return entrenoHoy;
    }

    public void setEntrenoHoy(Boolean entrenoHoy) {
        this.entrenoHoy = entrenoHoy;
    }

    public Integer getMejorRacha() {
        return mejorRacha;
    }

    public void setMejorRacha(Integer mejorRacha) {
        this.mejorRacha = mejorRacha;
    }

    public String getFechaMejorRacha() {
        return fechaMejorRacha;
    }

    public void setFechaMejorRacha(String fechaMejorRacha) {
        this.fechaMejorRacha = fechaMejorRacha;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public Integer getDiasParaProximoMilestone() {
        return diasParaProximoMilestone;
    }

    public void setDiasParaProximoMilestone(Integer diasParaProximoMilestone) {
        this.diasParaProximoMilestone = diasParaProximoMilestone;
    }

    public Integer getProximoMilestone() {
        return proximoMilestone;
    }

    public void setProximoMilestone(Integer proximoMilestone) {
        this.proximoMilestone = proximoMilestone;
    }

    public Integer getDiasEntrenados() {
        return diasEntrenados;
    }

    public void setDiasEntrenados(Integer diasEntrenados) {
        this.diasEntrenados = diasEntrenados;
    }

    public Integer getDiasTotales() {
        return diasTotales;
    }

    public void setDiasTotales(Integer diasTotales) {
        this.diasTotales = diasTotales;
    }

    public Double getPorcentajeConsistencia() {
        return porcentajeConsistencia;
    }

    public void setPorcentajeConsistencia(Double porcentajeConsistencia) {
        this.porcentajeConsistencia = porcentajeConsistencia;
    }
}