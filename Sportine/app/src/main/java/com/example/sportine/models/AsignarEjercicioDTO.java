package com.example.sportine.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class AsignarEjercicioDTO implements Serializable {

    private Integer idAsignado;
    private Integer idEntrenamiento;
    private String nombreEjercicio;

    // Métricas de Fuerza
    private Integer series;
    private Integer repeticiones;
    private Float peso;

    // Métricas de Cardio
    private Float distancia;
    private Integer duracion;

    /**
     * TRUE = el alumno debe reportar exitosos por serie
     * (goles, tiros anotados, jabs conectados, regates exitosos, etc.)
     * FALSE = no aplica (gym, cardio puro)
     * Lo define el entrenador al crear el ejercicio.
     */
    @SerializedName("tiene_exitosos")
    private boolean tieneExitosos = false;

    // Estado
    private String statusEjercicio;
    private boolean completado;
    private String notas;

    public AsignarEjercicioDTO() {}

    // ── Getters y Setters ─────────────────────────────────────────────────────

    public Integer getIdAsignado() { return idAsignado; }
    public void setIdAsignado(Integer idAsignado) { this.idAsignado = idAsignado; }

    public Integer getIdEntrenamiento() { return idEntrenamiento; }
    public void setIdEntrenamiento(Integer idEntrenamiento) { this.idEntrenamiento = idEntrenamiento; }

    public String getNombreEjercicio() { return nombreEjercicio; }
    public void setNombreEjercicio(String nombreEjercicio) { this.nombreEjercicio = nombreEjercicio; }

    public Integer getSeries() { return series; }
    public void setSeries(Integer series) { this.series = series; }

    public Integer getRepeticiones() { return repeticiones; }
    public void setRepeticiones(Integer repeticiones) { this.repeticiones = repeticiones; }

    public Float getPeso() { return peso; }
    public void setPeso(Float peso) { this.peso = peso; }

    public Float getDistancia() { return distancia; }
    public void setDistancia(Float distancia) { this.distancia = distancia; }

    public Integer getDuracion() { return duracion; }
    public void setDuracion(Integer duracion) { this.duracion = duracion; }

    public boolean isTieneExitosos() { return tieneExitosos; }
    public void setTieneExitosos(boolean tieneExitosos) { this.tieneExitosos = tieneExitosos; }

    public String getStatusEjercicio() { return statusEjercicio; }
    public void setStatusEjercicio(String statusEjercicio) { this.statusEjercicio = statusEjercicio; }

    public boolean isCompletado() { return completado; }
    public void setCompletado(boolean completado) { this.completado = completado; }

    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }

    public boolean esCardio() {
        return (distancia != null && distancia > 0) || (duracion != null && duracion > 0);
    }
}