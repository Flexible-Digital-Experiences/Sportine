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

    // ✅ CORRECCIÓN: Mapeamos al campo 'siguiendo' del backend
    @SerializedName("siguiendo")
    private boolean siguiendo;

    // Getters y Setters
    public boolean isSiguiendo() { return siguiendo; }
    public void setSiguiendo(boolean siguiendo) { this.siguiendo = siguiendo; }

    // Métodos de compatibilidad para que no rompa tu código viejo
    public boolean isAmigo() { return siguiendo; }
    public void setAmigo(boolean amigo) { this.siguiendo = amigo; }

    public String getUsuario() { return usuario; }
    public String getNombre() { return nombre; }
    public String getApellidos() { return apellidos; }
    public String getCiudad() { return ciudad; }
    public String getFotoPerfil() { return fotoPerfil; }
}