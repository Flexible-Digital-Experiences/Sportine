package com.example.sportine.models;

import com.google.gson.annotations.SerializedName;

public class Notificacion {

    @SerializedName("idNotificacion")
    private Integer idNotificacion;

    @SerializedName("titulo")
    private String titulo;

    @SerializedName("mensaje")
    private String mensaje;

    @SerializedName("fecha")
    private String fecha;

    @SerializedName("leido")
    private boolean leido;

    @SerializedName("tipo")
    private String tipo;

    // ✅ NUEVOS CAMPOS (Coinciden con el DTO del backend)
    @SerializedName("fotoActor")
    private String fotoActor;

    @SerializedName("nombreActor")
    private String nombreActor;

    // --- Getters ---
    public Integer getIdNotificacion() { return idNotificacion; }
    public String getTitulo() { return titulo; }
    public String getMensaje() { return mensaje; }
    public String getFecha() { return fecha; }
    public boolean isLeido() { return leido; }
    public String getTipo() { return tipo; }

    // ✅ Getters nuevos
    public String getFotoActor() { return fotoActor; }
    public String getNombreActor() { return nombreActor; }
}