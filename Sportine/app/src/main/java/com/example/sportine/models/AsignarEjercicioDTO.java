package com.example.sportine.models;

import java.io.Serializable;

public class AsignarEjercicioDTO implements Serializable {
    private Integer idAsignado;
    private Integer idEntrenamiento;
    private String nombreEjercicio;

    // Métricas
    private Integer series;
    private Integer repeticiones;
    private Double peso;
    private Double distancia;
    private Integer duracion;

    // Estado
    private String statusEjercicio; // "pendiente", "completado"
    private boolean completado;
    private String notas;

    // Getters
    public Integer getIdAsignado() { return idAsignado; }
    public String getNombreEjercicio() { return nombreEjercicio; }
    public Integer getSeries() { return series; }
    public Integer getRepeticiones() { return repeticiones; }
    public Double getPeso() { return peso; }
    public Double getDistancia() { return distancia; }
    public Integer getDuracion() { return duracion; }
    public boolean isCompletado() { return completado; }

    // Setter para el checkbox
    public void setCompletado(boolean completado) { this.completado = completado; }

    // Helper para saber qué tipo de ejercicio es (visual)
    public boolean esCardio() {
        return (distancia != null && distancia > 0) || (duracion != null && duracion > 0);
    }
}