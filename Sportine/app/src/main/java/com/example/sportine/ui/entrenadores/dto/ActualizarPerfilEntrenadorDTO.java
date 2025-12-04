package com.example.sportine.ui.entrenadores.dto;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ActualizarPerfilEntrenadorDTO {

    @SerializedName("costoMensualidad")
    private Integer costoMensualidad;

    @SerializedName("descripcionPerfil")
    private String descripcionPerfil;

    @SerializedName("limiteAlumnos")
    private Integer limiteAlumnos;

    @SerializedName("deportes")
    private List<Integer> deportes;

    // Constructor vacío
    public ActualizarPerfilEntrenadorDTO() {}

    // Getters y Setters
    public Integer getCostoMensualidad() { return costoMensualidad; }
    public void setCostoMensualidad(Integer costoMensualidad) {
        this.costoMensualidad = costoMensualidad;
    }

    public String getDescripcionPerfil() { return descripcionPerfil; }
    public void setDescripcionPerfil(String descripcionPerfil) {
        this.descripcionPerfil = descripcionPerfil;
    }

    public Integer getLimiteAlumnos() { return limiteAlumnos; }
    public void setLimiteAlumnos(Integer limiteAlumnos) {
        this.limiteAlumnos = limiteAlumnos;
    }

    public List<Integer> getDeportes() { return deportes; }
    public void setDeportes(List<Integer> deportes) {
        this.deportes = deportes;
    }
}