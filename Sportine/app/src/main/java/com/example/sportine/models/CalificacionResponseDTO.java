package com.example.sportine.models;

public class CalificacionResponseDTO {
    private boolean exito;
    private String mensaje;
    private ResenaDTO calificacion;

    // Constructor vacío
    public CalificacionResponseDTO() {
    }

    // Constructor con parámetros
    public CalificacionResponseDTO(boolean exito, String mensaje, ResenaDTO calificacion) {
        this.exito = exito;
        this.mensaje = mensaje;
        this.calificacion = calificacion;
    }

    // Getters
    public boolean isExito() {
        return exito;
    }

    public String getMensaje() {
        return mensaje;
    }

    public ResenaDTO getCalificacion() {
        return calificacion;
    }

    // Setters
    public void setExito(boolean exito) {
        this.exito = exito;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public void setCalificacion(ResenaDTO calificacion) {
        this.calificacion = calificacion;
    }

    @Override
    public String toString() {
        return "CalificacionResponseDTO{" +
                "exito=" + exito +
                ", mensaje='" + mensaje + '\'' +
                ", calificacion=" + calificacion +
                '}';
    }
}