package com.example.sportine.models;

import java.io.Serializable;

public class AsignarEjercicioDTO implements Serializable {

    // Identificadores
    private Integer idAsignado;
    private Integer idEntrenamiento;

    // Info Principal
    private String nombreEjercicio;

    // Métricas de Fuerza (Gym)
    private Integer series;
    private Integer repeticiones;
    private Float peso; // Backend usa Float, mejor coincidir

    // Métricas de Cardio (Running/Bici)
    private Float distancia; // Backend usa Float
    private Integer duracion; // minutos

    // Estado y Notas
    private String statusEjercicio; // "pendiente", "completado"
    private boolean completado;
    private String notas;

    // Constructor vacío (necesario para Gson/Retrofit)
    public AsignarEjercicioDTO() {}

    // ==========================
    // GETTERS Y SETTERS
    // ==========================

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

    public String getStatusEjercicio() { return statusEjercicio; }
    public void setStatusEjercicio(String statusEjercicio) { this.statusEjercicio = statusEjercicio; }

    public boolean isCompletado() { return completado; }
    public void setCompletado(boolean completado) { this.completado = completado; }

    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }

    // Helper visual para saber si es cardio (útil para el Adapter)
    public boolean esCardio() {
        return (distancia != null && distancia > 0) || (duracion != null && duracion > 0);
    }
}