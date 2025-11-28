package com.example.sportine.models;

import java.util.List;

public class PerfilEntrenadorDTO {
    private String usuario;
    private String fotoPerfil;
    private String nombreCompleto;
    private String ubicacion;
    private String acercaDeMi;
    private Integer limiteAlumnos;
    private Integer alumnosActuales;
    private Integer costoMensual;
    private CalificacionDTO calificacion;
    private List<String> especialidades;
    private List<ResenaDTO> resenas;
    private EstadoRelacionDTO estadoRelacion; // NUEVO

    // Constructor vacío
    public PerfilEntrenadorDTO() {}

    // Getters y Setters
    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }

    public String getFotoPerfil() { return fotoPerfil; }
    public void setFotoPerfil(String fotoPerfil) { this.fotoPerfil = fotoPerfil; }

    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }

    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }

    public String getAcercaDeMi() { return acercaDeMi; }
    public void setAcercaDeMi(String acercaDeMi) { this.acercaDeMi = acercaDeMi; }

    public Integer getLimiteAlumnos() { return limiteAlumnos; }
    public void setLimiteAlumnos(Integer limiteAlumnos) { this.limiteAlumnos = limiteAlumnos; }

    public Integer getAlumnosActuales() { return alumnosActuales; }
    public void setAlumnosActuales(Integer alumnosActuales) { this.alumnosActuales = alumnosActuales; }

    public Integer getCostoMensual() { return costoMensual; }
    public void setCostoMensual(Integer costoMensual) { this.costoMensual = costoMensual; }

    public CalificacionDTO getCalificacion() { return calificacion; }
    public void setCalificacion(CalificacionDTO calificacion) { this.calificacion = calificacion; }

    public List<String> getEspecialidades() { return especialidades; }
    public void setEspecialidades(List<String> especialidades) { this.especialidades = especialidades; }

    public List<ResenaDTO> getResenas() { return resenas; }
    public void setResenas(List<ResenaDTO> resenas) { this.resenas = resenas; }

    public EstadoRelacionDTO getEstadoRelacion() { return estadoRelacion; }
    public void setEstadoRelacion(EstadoRelacionDTO estadoRelacion) { this.estadoRelacion = estadoRelacion; }
}