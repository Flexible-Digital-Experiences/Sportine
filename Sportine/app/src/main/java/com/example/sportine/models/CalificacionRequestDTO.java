package com.example.sportine.models;

public class CalificacionRequestDTO {
    private String usuarioEntrenador;
    private int calificacion;
    private String comentario;

    // Constructor vacío
    public CalificacionRequestDTO() {
    }

    // Constructor con parámetros
    public CalificacionRequestDTO(String usuarioEntrenador, int calificacion, String comentario) {
        this.usuarioEntrenador = usuarioEntrenador;
        this.calificacion = calificacion;
        this.comentario = comentario;
    }

    // Getters
    public String getUsuarioEntrenador() {
        return usuarioEntrenador;
    }

    public int getCalificacion() {
        return calificacion;
    }

    public String getComentario() {
        return comentario;
    }

    // Setters
    public void setUsuarioEntrenador(String usuarioEntrenador) {
        this.usuarioEntrenador = usuarioEntrenador;
    }

    public void setCalificacion(int calificacion) {
        this.calificacion = calificacion;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    @Override
    public String toString() {
        return "CalificacionRequestDTO{" +
                "usuarioEntrenador='" + usuarioEntrenador + '\'' +
                ", calificacion=" + calificacion +
                ", comentario='" + comentario + '\'' +
                '}';
    }
}
