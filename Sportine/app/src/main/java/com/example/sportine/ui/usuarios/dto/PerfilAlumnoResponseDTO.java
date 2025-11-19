package com.example.sportine.ui.usuarios.dto;

import java.util.Date;
import java.util.List;

public class PerfilAlumnoResponseDTO {
    private String usuario;
    private String nombre;
    private String apellidos;
    private String sexo;
    private String estado;
    private String ciudad;
    private Float estatura;
    private Float peso;
    private String lesiones;
    private String nivel;
    private String padecimientos;
    private String fotoPerfil;
    private Date fechaNacimiento;
    private Integer edad;
    private List<String> deportes;
    private String mensaje;

    public PerfilAlumnoResponseDTO() {}

    // Getters
    public String getUsuario() { return usuario; }
    public String getNombre() { return nombre; }
    public String getApellidos() { return apellidos; }
    public String getSexo() { return sexo; }
    public String getEstado() { return estado; }
    public String getCiudad() { return ciudad; }
    public Float getEstatura() { return estatura; }
    public Float getPeso() { return peso; }
    public String getLesiones() { return lesiones; }
    public String getNivel() { return nivel; }
    public String getPadecimientos() { return padecimientos; }
    public String getFotoPerfil() { return fotoPerfil; }
    public Date getFechaNacimiento() { return fechaNacimiento; }
    public Integer getEdad() { return edad; }
    public List<String> getDeportes() { return deportes; }
    public String getMensaje() { return mensaje; }

    // Setters
    public void setUsuario(String usuario) { this.usuario = usuario; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }
    public void setSexo(String sexo) { this.sexo = sexo; }
    public void setEstado(String estado) { this.estado = estado; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }
    public void setEstatura(Float estatura) { this.estatura = estatura; }
    public void setPeso(Float peso) { this.peso = peso; }
    public void setLesiones(String lesiones) { this.lesiones = lesiones; }
    public void setNivel(String nivel) { this.nivel = nivel; }
    public void setPadecimientos(String padecimientos) { this.padecimientos = padecimientos; }
    public void setFotoPerfil(String fotoPerfil) { this.fotoPerfil = fotoPerfil; }
    public void setFechaNacimiento(Date fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }
    public void setEdad(Integer edad) { this.edad = edad; }
    public void setDeportes(List<String> deportes) { this.deportes = deportes; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
}
