package com.example.sportine.models;

import com.google.gson.annotations.SerializedName;

public class HistorialEntrenamientoDTO {
    @SerializedName("id_entrenamiento") private Integer idEntrenamiento;
    @SerializedName("titulo")           private String titulo;
    @SerializedName("fecha")            private String fecha;
    @SerializedName("dificultad")       private String dificultad;
    @SerializedName("duracion_min")     private Integer duracionMin;
    @SerializedName("calorias_kcal")    private Integer caloriasKcal;
    @SerializedName("distancia_metros") private Float distanciaMetros;
    @SerializedName("pasos")            private Integer pasos;
    @SerializedName("tiene_hc")         private Boolean tieneHc;

    public Integer getIdEntrenamiento() { return idEntrenamiento; }
    public String getTitulo()           { return titulo; }
    public String getFecha()            { return fecha; }
    public String getDificultad()       { return dificultad; }
    public Integer getDuracionMin()     { return duracionMin; }
    public Integer getCaloriasKcal()    { return caloriasKcal; }
    public Float getDistanciaMetros()   { return distanciaMetros; }
    public Integer getPasos()           { return pasos; }
    public Boolean getTieneHc()         { return tieneHc != null && tieneHc; }
}