package com.example.sportine.models;

import com.google.gson.annotations.SerializedName;

public class UsuarioDetalle {

    @SerializedName("usuario")
    private String usuario;

    @SerializedName("nombre")
    private String nombre;

    @SerializedName("apellidos")
    private String apellidos;


    @SerializedName("ciudad")
    private String fotoPerfil;

    @SerializedName("amigo")
    private boolean isAmigo;

    public boolean isAmigo() { return isAmigo; }

    public void setAmigo(boolean amigo) { isAmigo = amigo; }

    public String getUsuario() { return usuario; }
    public String getNombre() { return nombre; }
    public String getApellidos() { return apellidos; }
    public String getFotoPerfil() { return fotoPerfil; }
}