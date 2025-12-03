package com.example.sportine.models;

import java.util.List;

public class HomeEntrenadorDTO {
    private String saludo;
    private String fecha;
    private String mensajeDinamico;
    private List<AlumnoProgresoDTO> alumnos;
    private Integer totalAlumnos;
    private Integer alumnosActivos;

    // Getters
    public String getSaludo() { return saludo; }
    public String getFecha() { return fecha; }
    public String getMensajeDinamico() { return mensajeDinamico; }
    public List<AlumnoProgresoDTO> getAlumnos() { return alumnos; }
    public Integer getTotalAlumnos() { return totalAlumnos; }
    public Integer getAlumnosActivos() { return alumnosActivos; }
}