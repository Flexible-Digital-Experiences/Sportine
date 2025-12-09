package com.example.sportine.models;

import java.util.List;

public class DetalleEntrenamientoDTO {
    private Integer idEntrenamiento;
    private String titulo;
    private String objetivo;
    private String fecha;
    private String hora;
    private String estado;

    // Info Entrenador
    private String nombreEntrenador;
    private String especialidadEntrenador;
    private String fotoEntrenador;
    private String deporteIcono;

    private String dificultad;

    // Lista de Ejercicios
    private List<AsignarEjercicioDTO> ejercicios;

    // Getters
    public String getTitulo() { return titulo; }
    public String getFecha() { return fecha; }
    public String getHora() { return hora; }

    public String getDificultad() { return dificultad; }
    public String getNombreEntrenador() { return nombreEntrenador; }
    public String getEspecialidadEntrenador() { return especialidadEntrenador; }
    public String getFotoEntrenador() { return fotoEntrenador; }
    public String getObjetivo() { return objetivo; }
    public List<AsignarEjercicioDTO> getEjercicios() { return ejercicios; }
    public String getDeporteIcono() {return deporteIcono;}

    public void setDificultad(String dificultad) { this.dificultad = dificultad; }
}