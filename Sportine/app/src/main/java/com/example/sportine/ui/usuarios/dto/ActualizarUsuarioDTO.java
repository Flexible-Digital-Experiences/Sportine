package com.example.sportine.ui.usuarios.dto;

import com.google.gson.annotations.SerializedName;

/**
 * DTO para actualizar datos del usuario (tabla Usuario)
 * ❌ NO INCLUYE EL CAMPO "usuario" porque es la PRIMARY KEY
 * Todos los campos son opcionales (nullable)
 */
public class ActualizarUsuarioDTO {

    @SerializedName("nombre")
    private String nombre;

    @SerializedName("apellidos")
    private String apellidos;

    // ❌ ELIMINADO: No se puede modificar el username (PRIMARY KEY)
    // private String usuario;

    @SerializedName("sexo")
    private String sexo;

    @SerializedName("estado")
    private String estado;

    @SerializedName("ciudad")
    private String ciudad;

    @SerializedName("password")
    private String password;

    // Constructor vacío
    public ActualizarUsuarioDTO() {
    }

    // Getters y Setters
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "ActualizarUsuarioDTO{" +
                "nombre='" + nombre + '\'' +
                ", apellidos='" + apellidos + '\'' +
                ", sexo='" + sexo + '\'' +
                ", estado='" + estado + '\'' +
                ", ciudad='" + ciudad + '\'' +
                ", password='***'" +
                '}';
    }
}