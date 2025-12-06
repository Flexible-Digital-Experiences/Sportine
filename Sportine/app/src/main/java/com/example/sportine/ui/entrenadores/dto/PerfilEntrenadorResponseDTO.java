package com.example.sportine.ui.entrenadores.dto;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

public class PerfilEntrenadorResponseDTO {

    // Datos del usuario
    private String usuario;
    private String nombre;
    private String apellidos;
    private String sexo;
    private String estado;
    private String ciudad;

    // Datos del entrenador
    @SerializedName("costoMensualidad")
    private Integer costoMensualidad;

    @SerializedName("tipoCuenta")
    private String tipoCuenta;

    @SerializedName("limiteAlumnos")
    private Integer limiteAlumnos;

    @SerializedName("descripcionPerfil")
    private String descripcionPerfil;

    @SerializedName("fotoPerfil")
    private String fotoPerfil;

    // Deportes que imparte (SOLO LISTA DE NOMBRES)
    private List<String> deportes;

    // Contadores
    @SerializedName("totalAlumnos")
    private Integer totalAlumnos;

    @SerializedName("totalAmigos")
    private Integer totalAmigos;

    @SerializedName("correo")
    private String correo;

    @SerializedName("telefono")
    private String telefono;

    // Mensaje
    private String mensaje;

    // Constructor vacío
    public PerfilEntrenadorResponseDTO() {}

    // ========================================
    // GETTERS
    // ========================================
    public String getUsuario() { return usuario; }
    public String getNombre() { return nombre; }
    public String getApellidos() { return apellidos; }
    public String getSexo() { return sexo; }
    public String getEstado() { return estado; }
    public String getCiudad() { return ciudad; }
    public Integer getCostoMensualidad() { return costoMensualidad; }
    public String getTipoCuenta() { return tipoCuenta; }
    public Integer getLimiteAlumnos() { return limiteAlumnos; }
    public String getDescripcionPerfil() { return descripcionPerfil; }
    public String getFotoPerfil() { return fotoPerfil; }
    public String getMensaje() { return mensaje; }
    public String getCorreo() { return correo; }
    public String getTelefono() { return telefono; }

    public List<String> getDeportes() {
        return deportes != null ? deportes : new ArrayList<>();
    }

    public Integer getTotalAlumnos() {
        return totalAlumnos != null ? totalAlumnos : 0;
    }

    public Integer getTotalAmigos() {
        return totalAmigos != null ? totalAmigos : 0;
    }

    // ========================================
    // SETTERS
    // ========================================
    public void setUsuario(String usuario) { this.usuario = usuario; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }
    public void setSexo(String sexo) { this.sexo = sexo; }
    public void setEstado(String estado) { this.estado = estado; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }
    public void setCostoMensualidad(Integer costoMensualidad) { this.costoMensualidad = costoMensualidad; }
    public void setTipoCuenta(String tipoCuenta) { this.tipoCuenta = tipoCuenta; }
    public void setLimiteAlumnos(Integer limiteAlumnos) { this.limiteAlumnos = limiteAlumnos; }
    public void setDescripcionPerfil(String descripcionPerfil) { this.descripcionPerfil = descripcionPerfil; }
    public void setFotoPerfil(String fotoPerfil) { this.fotoPerfil = fotoPerfil; }
    public void setDeportes(List<String> deportes) { this.deportes = deportes; }
    public void setTotalAlumnos(Integer totalAlumnos) { this.totalAlumnos = totalAlumnos; }
    public void setTotalAmigos(Integer totalAmigos) { this.totalAmigos = totalAmigos; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
}