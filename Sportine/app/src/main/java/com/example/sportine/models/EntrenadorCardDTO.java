package com.example.sportine.models;

import java.util.List;

public class EntrenadorCardDTO {
    private String usuario;
    private String nombreCompleto;
    private String fotoPerfil;
    private Double ratingPromedio;
    private List<String> especialidades;
    private Integer limiteAlumnos;
    private Integer alumnosActuales;

    // Constructor vacío
    public EntrenadorCardDTO() {
    }

    // Constructor completo
    public EntrenadorCardDTO(String usuario, String nombreCompleto, String fotoPerfil,
                             Double ratingPromedio, List<String> especialidades,
                             Integer limiteAlumnos, Integer alumnosActuales) {
        this.usuario = usuario;
        this.nombreCompleto = nombreCompleto;
        this.fotoPerfil = fotoPerfil;
        this.ratingPromedio = ratingPromedio;
        this.especialidades = especialidades;
        this.limiteAlumnos = limiteAlumnos;
        this.alumnosActuales = alumnosActuales;
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

    public Double getRatingPromedio() {
        return ratingPromedio;
    }

    public void setRatingPromedio(Double ratingPromedio) {
        this.ratingPromedio = ratingPromedio;
    }

    public List<String> getEspecialidades() {
        return especialidades;
    }

    public void setEspecialidades(List<String> especialidades) {
        this.especialidades = especialidades;
    }

    public Integer getLimiteAlumnos() {
        return limiteAlumnos;
    }

    public void setLimiteAlumnos(Integer limiteAlumnos) {
        this.limiteAlumnos = limiteAlumnos;
    }

    public Integer getAlumnosActuales() {
        return alumnosActuales;
    }

    public void setAlumnosActuales(Integer alumnosActuales) {
        this.alumnosActuales = alumnosActuales;
    }

    // Métodos útiles
    public int getEspaciosDisponibles() {
        if (limiteAlumnos == null || alumnosActuales == null) {
            return 0;
        }
        return limiteAlumnos - alumnosActuales;
    }

    public int getPorcentajeOcupacion() {
        if (limiteAlumnos == null || limiteAlumnos == 0 || alumnosActuales == null) {
            return 0;
        }
        return (int) ((alumnosActuales * 100.0) / limiteAlumnos);
    }

    public boolean estaCasiLleno() {
        return getPorcentajeOcupacion() >= 80;
    }

    public boolean estaLleno() {
        return getEspaciosDisponibles() <= 0;
    }
}