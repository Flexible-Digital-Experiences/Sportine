package com.example.sportine.ui.usuarios.dto;

import com.google.gson.annotations.SerializedName;

public class UsuarioDetalleDTO {

    private String usuario;
    private String nombre;
    private String apellidos;
    private String sexo;

    // ✅ NUEVO: Campo correo
    @SerializedName("correo")
    private String correo;

    private String estado;
    private String ciudad;
    private String rol;

    // Campo para saber si ya lo sigo
    @SerializedName("siguiendo")
    private boolean siguiendo;

    @SerializedName("fotoPerfil")
    private String fotoPerfil;

    // Constructor vacío
    public UsuarioDetalleDTO() {}

    // ========================================
    // GETTERS
    // ========================================
    public String getUsuario() { return usuario; }
    public String getNombre() { return nombre; }
    public String getApellidos() { return apellidos; }
    public String getSexo() { return sexo; }
    public String getCorreo() { return correo; }  // ✅ NUEVO
    public String getEstado() { return estado; }
    public String getCiudad() { return ciudad; }
    public String getRol() { return rol; }
    public boolean isSiguiendo() { return siguiendo; }
    public String getFotoPerfil() { return fotoPerfil; }

    // ========================================
    // SETTERS
    // ========================================
    public void setUsuario(String usuario) { this.usuario = usuario; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }
    public void setSexo(String sexo) { this.sexo = sexo; }
    public void setCorreo(String correo) { this.correo = correo; }  // ✅ NUEVO
    public void setEstado(String estado) { this.estado = estado; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }
    public void setRol(String rol) { this.rol = rol; }
    public void setSiguiendo(boolean siguiendo) { this.siguiendo = siguiendo; }
    public void setFotoPerfil(String fotoPerfil) { this.fotoPerfil = fotoPerfil; }
}