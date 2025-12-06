package com.example.sportine.models;

public class AlumnoEntrenadorDTO {
    private String usuarioAlumno;
    private String nombreCompleto;
    private String fotoPerfil;
    private Integer edad;
    private String deportes;
    private String fechaInicio;
    private String statusRelacion;

    // Constructor vacío
    public AlumnoEntrenadorDTO() {}

    // Constructor completo
    public AlumnoEntrenadorDTO(String usuarioAlumno, String nombreCompleto, String fotoPerfil,
                               Integer edad, String deportes, String fechaInicio, String statusRelacion) {
        this.usuarioAlumno = usuarioAlumno;
        this.nombreCompleto = nombreCompleto;
        this.fotoPerfil = fotoPerfil;
        this.edad = edad;
        this.deportes = deportes;
        this.fechaInicio = fechaInicio;
        this.statusRelacion = statusRelacion;
    }

    // Getters y Setters
    public String getUsuarioAlumno() {
        return usuarioAlumno;
    }

    public void setUsuarioAlumno(String usuarioAlumno) {
        this.usuarioAlumno = usuarioAlumno;
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

    public Integer getEdad() {
        return edad;
    }

    public void setEdad(Integer edad) {
        this.edad = edad;
    }

    public String getDeportes() {
        return deportes;
    }

    public void setDeportes(String deportes) {
        this.deportes = deportes;
    }

    public String getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(String fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public String getStatusRelacion() {
        return statusRelacion;
    }

    public void setStatusRelacion(String statusRelacion) {
        this.statusRelacion = statusRelacion;
    }
}