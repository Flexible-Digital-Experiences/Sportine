package com.example.sportine.models;

import com.google.gson.annotations.SerializedName;

public class Notificacion {

    @SerializedName("idNotificacion")
    private Integer id;

    @SerializedName("usuarioActor")
    private String usuarioActor;

    @SerializedName("tipo")
    private String tipo;

    @SerializedName("mensaje")
    private String mensaje;

    @SerializedName("fecha")
    private String fecha;

    // Getters
    public Integer getId() { return id; }
    public String getUsuarioActor() { return usuarioActor; }
    public String getTipo() { return tipo; }
    public String getMensaje() { return mensaje; }
    public String getFecha() { return fecha; }
}