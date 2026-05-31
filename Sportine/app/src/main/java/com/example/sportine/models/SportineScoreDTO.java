package com.example.sportine.models;

import com.google.gson.annotations.SerializedName;

public class SportineScoreDTO {

    @SerializedName("usuario")
    private String usuario;

    @SerializedName("sportine_score")
    private double sportineScore;

    @SerializedName("nivel")
    private String nivel;

    public String getUsuario() { return usuario; }
    public double getSportineScore() { return sportineScore; }
    public String getNivel() { return nivel; }
}