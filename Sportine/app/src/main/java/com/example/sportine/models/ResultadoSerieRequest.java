package com.example.sportine.models;

import com.google.gson.annotations.SerializedName;

public class ResultadoSerieRequest {

    @SerializedName("numero_serie")
    private Integer numeroSerie;

    @SerializedName("reps_completadas")
    private Integer repsCompletadas;

    @SerializedName("peso_usado")
    private Float pesoUsado;

    @SerializedName("duracion_completada_seg")
    private Integer duracionCompletadaSeg;

    @SerializedName("distancia_completada_metros")
    private Double distanciaCompletadaMetros;

    // null = no aplica al ejercicio, 0+ = cuántos salieron bien
    @SerializedName("exitosos")
    private Integer exitosos;

    @SerializedName("status")
    private String status;

    @SerializedName("notas")
    private String notas;

    public Integer getNumeroSerie() { return numeroSerie; }
    public Integer getRepsCompletadas() { return repsCompletadas; }
    public Float getPesoUsado() { return pesoUsado; }
    public Integer getDuracionCompletadaSeg() { return duracionCompletadaSeg; }
    public Double getDistanciaCompletadaMetros() { return distanciaCompletadaMetros; }
    public Integer getExitosos() { return exitosos; }
    public String getStatus() { return status; }
    public String getNotas() { return notas; }

    public void setNumeroSerie(Integer numeroSerie) { this.numeroSerie = numeroSerie; }
    public void setRepsCompletadas(Integer repsCompletadas) { this.repsCompletadas = repsCompletadas; }
    public void setPesoUsado(Float pesoUsado) { this.pesoUsado = pesoUsado; }
    public void setDuracionCompletadaSeg(Integer duracionCompletadaSeg) { this.duracionCompletadaSeg = duracionCompletadaSeg; }
    public void setDistanciaCompletadaMetros(Double distanciaCompletadaMetros) { this.distanciaCompletadaMetros = distanciaCompletadaMetros; }
    public void setExitosos(Integer exitosos) { this.exitosos = exitosos; }
    public void setStatus(String status) { this.status = status; }
    public void setNotas(String notas) { this.notas = notas; }
}