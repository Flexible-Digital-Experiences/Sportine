package com.example.sportine.ui.usuarios.dto;

import com.google.gson.annotations.SerializedName;

/**
 * DTO para actualizar los datos del perfil de un alumno
 * Solo incluye los campos que pueden ser actualizados
 */
public class ActualizarDatosAlumnoDTO {

    @SerializedName("estatura")
    private Float estatura;

    @SerializedName("peso")
    private Float peso;

    @SerializedName("lesiones")
    private String lesiones;

    @SerializedName("padecimientos")
    private String padecimientos;

    @SerializedName("fecha_nacimiento")
    private String fechaNacimiento; // Formato: "YYYY-MM-DD"

    @SerializedName("sexo")
    private String sexo;

    // Constructor vacío
    public ActualizarDatosAlumnoDTO() {
    }

    // Getters y Setters
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

    public String getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(String fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    @Override
    public String toString() {
        return "ActualizarDatosAlumnoDTO{" +
                "estatura=" + estatura +
                ", peso=" + peso +
                ", lesiones='" + lesiones + '\'' +
                ", padecimientos='" + padecimientos + '\'' +
                ", fechaNacimiento='" + fechaNacimiento + '\'' +
                ", sexo='" + sexo + '\'' +
                '}';
    }
}