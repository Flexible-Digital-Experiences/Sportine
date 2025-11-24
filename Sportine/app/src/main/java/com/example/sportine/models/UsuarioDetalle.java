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
    private String ciudad;

    @SerializedName("fotoPerfil")
    private String fotoPerfil;

    @SerializedName("isAmigo")

    private boolean isAmigo;

    // Getters y Setters
    public boolean isAmigo() { return isAmigo; }
    public void setAmigo(boolean amigo) { isAmigo = amigo; }

    public String getUsuario() { return usuario; }
    public String getNombre() { return nombre; }
    public String getApellidos() { return apellidos; }

    public String getCiudad() { return ciudad; }

    public String getFotoPerfil() { return fotoPerfil; }
}