package com.example.sportine.models;

import com.google.gson.annotations.SerializedName;
import java.util.Date;

public class Comentario {

    @SerializedName("idComentario")
    private Integer idComentario;

    @SerializedName("texto")
    private String texto;

    @SerializedName("fecha")
    private Date fecha;

    // Datos del autor (Fusionados)
    @SerializedName("autorUsername")
    private String autorUsername;

    @SerializedName("autorNombre")
    private String autorNombre;

    @SerializedName("autorFoto")
    private String autorFoto;

    @SerializedName("mine")
    private boolean isMine;

    // Getters
    public Integer getIdComentario() { return idComentario; }
    public String getTexto() { return texto; }
    public Date getFecha() { return fecha; }
    public String getAutorUsername() { return autorUsername; }
    public String getAutorNombre() { return autorNombre; }
    public String getAutorFoto() { return autorFoto; }
    public boolean isMine() { return isMine; }
}