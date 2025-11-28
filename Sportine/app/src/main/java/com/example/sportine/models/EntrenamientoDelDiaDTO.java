package com.example.sportine.models;

import java.util.Date;

/**
 * DTO para representar un entrenamiento en la lista del home del alumno
 * Corresponde a EntrenamientoDelDiaDTO del backend
 */
public class EntrenamientoDelDiaDTO {

    private Integer idEntrenamiento;
    private String titulo;
    private String objetivo;
    private Date fechaEntrenamiento;
    private String horaEntrenamiento; // "08:00"
    private String dificultad; // "facil", "media", "dificil"
    private String estadoEntrenamiento; // "pendiente", "en_progreso", "finalizado"

    // Información del entrenador
    private String nombreEntrenador;
    private String apellidosEntrenador;
    private String fotoPerfil;

    // Progreso del entrenamiento
    private Integer totalEjercicios;
    private Integer ejerciciosCompletados;

    // Constructores
    public EntrenamientoDelDiaDTO() {
    }

    // Getters y Setters
    public Integer getIdEntrenamiento() {
        return idEntrenamiento;
    }

    public void setIdEntrenamiento(Integer idEntrenamiento) {
        this.idEntrenamiento = idEntrenamiento;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getObjetivo() {
        return objetivo;
    }

    public void setObjetivo(String objetivo) {
        this.objetivo = objetivo;
    }

    public Date getFechaEntrenamiento() {
        return fechaEntrenamiento;
    }

    public void setFechaEntrenamiento(Date fechaEntrenamiento) {
        this.fechaEntrenamiento = fechaEntrenamiento;
    }

    public String getHoraEntrenamiento() {
        return horaEntrenamiento;
    }

    public void setHoraEntrenamiento(String horaEntrenamiento) {
        this.horaEntrenamiento = horaEntrenamiento;
    }

    public String getDificultad() {
        return dificultad;
    }

    public void setDificultad(String dificultad) {
        this.dificultad = dificultad;
    }

    public String getEstadoEntrenamiento() {
        return estadoEntrenamiento;
    }

    public void setEstadoEntrenamiento(String estadoEntrenamiento) {
        this.estadoEntrenamiento = estadoEntrenamiento;
    }

    public String getNombreEntrenador() {
        return nombreEntrenador;
    }

    public void setNombreEntrenador(String nombreEntrenador) {
        this.nombreEntrenador = nombreEntrenador;
    }

    public String getApellidosEntrenador() {
        return apellidosEntrenador;
    }

    public void setApellidosEntrenador(String apellidosEntrenador) {
        this.apellidosEntrenador = apellidosEntrenador;
    }

    public String getFotoPerfil() {
        return fotoPerfil;
    }

    public void setFotoPerfil(String fotoPerfil) {
        this.fotoPerfil = fotoPerfil;
    }

    public Integer getTotalEjercicios() {
        return totalEjercicios;
    }

    public void setTotalEjercicios(Integer totalEjercicios) {
        this.totalEjercicios = totalEjercicios;
    }

    public Integer getEjerciciosCompletados() {
        return ejerciciosCompletados;
    }

    public void setEjerciciosCompletados(Integer ejerciciosCompletados) {
        this.ejerciciosCompletados = ejerciciosCompletados;
    }

    /**
     * Helper para obtener el nombre completo del entrenador
     */
    public String getNombreCompletoEntrenador() {
        return nombreEntrenador + " " + apellidosEntrenador;
    }

    /**
     * Helper para obtener el texto del progreso
     */
    public String getTextoProgreso() {
        return ejerciciosCompletados + "/" + totalEjercicios + " completados";
    }

    /**
     * Helper para calcular el porcentaje de progreso
     */
    public int getPorcentajeProgreso() {
        if (totalEjercicios == null || totalEjercicios == 0) {
            return 0;
        }
        return (int) ((ejerciciosCompletados * 100.0) / totalEjercicios);
    }
}