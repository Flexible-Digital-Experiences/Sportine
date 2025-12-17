package com.example.sportine.ui.usuarios.dto;

import com.google.gson.annotations.SerializedName;

/**
 * DTO para actualizar datos del usuario (tabla Usuario)
 * ✅ SPORTINE V2: Incluye correo
 * ❌ NO INCLUYE EL CAMPO "usuario" porque es la PRIMARY KEY
 * Todos los campos son opcionales (nullable)
 */
public class ActualizarUsuarioDTO {

    @SerializedName("nombre")
    private String nombre;

    @SerializedName("apellidos")
    private String apellidos;

    @SerializedName("sexo")
    private String sexo;

    // ✅ NUEVO: Campo correo
    @SerializedName("correo")
    private String correo;

    @SerializedName("estado")
    private String estado;

    @SerializedName("ciudad")
    private String ciudad;

    @SerializedName("password")
    private String password;

    // Constructor vacío
    public ActualizarUsuarioDTO() {}

    // ========================================
    // GETTERS Y SETTERS
    // ========================================
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public String getSexo() { return sexo; }
    public void setSexo(String sexo) { this.sexo = sexo; }

    public String getCorreo() { return correo; }  // ✅ NUEVO
    public void setCorreo(String correo) { this.correo = correo; }  // ✅ NUEVO

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    @Override
    public String toString() {
        return "ActualizarUsuarioDTO{" +
                "nombre='" + nombre + '\'' +
                ", apellidos='" + apellidos + '\'' +
                ", sexo='" + sexo + '\'' +
                ", correo='" + correo + '\'' +  // ✅ NUEVO
                ", estado='" + estado + '\'' +
                ", ciudad='" + ciudad + '\'' +
                ", password='***'" +
                '}';
    }
}