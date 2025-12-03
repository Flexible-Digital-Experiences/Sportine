package com.example.sportine.models;

import java.util.List;

public class CrearEntrenamientoRequestDTO {

    private String usuarioAlumno;
    private String tituloEntrenamiento;
    private String objetivo;
    private String fechaEntrenamiento; // String para facilitar el envío "yyyy-MM-dd"
    private String horaEntrenamiento;  // String para facilitar el envío "HH:mm:ss"
    private String dificultad;
    private List<AsignarEjercicioDTO> ejercicios;

    // Constructores
    public CrearEntrenamientoRequestDTO() {}

    // Getters y Setters
    public String getUsuarioAlumno() { return usuarioAlumno; }
    public void setUsuarioAlumno(String usuarioAlumno) { this.usuarioAlumno = usuarioAlumno; }

    public String getTituloEntrenamiento() { return tituloEntrenamiento; }
    public void setTituloEntrenamiento(String tituloEntrenamiento) { this.tituloEntrenamiento = tituloEntrenamiento; }

    public String getObjetivo() { return objetivo; }
    public void setObjetivo(String objetivo) { this.objetivo = objetivo; }

    public String getFechaEntrenamiento() { return fechaEntrenamiento; }
    public void setFechaEntrenamiento(String fechaEntrenamiento) { this.fechaEntrenamiento = fechaEntrenamiento; }

    public String getHoraEntrenamiento() { return horaEntrenamiento; }
    public void setHoraEntrenamiento(String horaEntrenamiento) { this.horaEntrenamiento = horaEntrenamiento; }

    public String getDificultad() { return dificultad; }
    public void setDificultad(String dificultad) { this.dificultad = dificultad; }

    public List<AsignarEjercicioDTO> getEjercicios() { return ejercicios; }
    public void setEjercicios(List<AsignarEjercicioDTO> ejercicios) { this.ejercicios = ejercicios; }
}