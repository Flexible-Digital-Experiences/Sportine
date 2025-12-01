package com.example.sportine.ui.usuarios.dto;

import com.google.gson.annotations.SerializedName;

/**
 * DTO para enviar datos al crear/actualizar el perfil básico.
 * NO incluye deportes ni nivel (se agregan después).
 */
public class PerfilAlumnoDTO {

    private String usuario;

    // Datos físicos
    private Float estatura;
    private Float peso;

    // Datos de salud
    private String lesiones;
    private String padecimientos;

    // Foto de perfil
    @SerializedName("fotoPerfil")
    private String fotoPerfil;

    // Fecha de nacimiento (formato: yyyy-MM-dd)
    @SerializedName("fechaNacimiento")
    private String fechaNacimiento;

    // ========================================
    // CONSTRUCTOR VACÍO
    // ========================================
    public PerfilAlumnoDTO() {
    }

    // ========================================
    // CONSTRUCTOR COMPLETO
    // ========================================
    public PerfilAlumnoDTO(String usuario, Float estatura, Float peso,
                           String lesiones, String padecimientos,
                           String fotoPerfil, String fechaNacimiento) {
        this.usuario = usuario;
        this.estatura = estatura;
        this.peso = peso;
        this.lesiones = lesiones;
        this.padecimientos = padecimientos;
        this.fotoPerfil = fotoPerfil;
        this.fechaNacimiento = fechaNacimiento;
    }

    // ========================================
    // GETTERS Y SETTERS
    // ========================================

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public Float getEstatura() {
        return estatura;
    }

    public void setEstatura(Float estatura) {
        this.estatura = estatura;
    }

    public Float getPeso() {
        return peso;
    }

    public void setPeso(Float peso) {
        this.peso = peso;
    }

    public String getLesiones() {
        return lesiones;
    }

    public void setLesiones(String lesiones) {
        this.lesiones = lesiones;
    }

    public String getPadecimientos() {
        return padecimientos;
    }

    public void setPadecimientos(String padecimientos) {
        this.padecimientos = padecimientos;
    }

    public String getFotoPerfil() {
        return fotoPerfil;
    }

    public void setFotoPerfil(String fotoPerfil) {
        this.fotoPerfil = fotoPerfil;
    }

    public String getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(String fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }
}