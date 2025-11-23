package com.example.sportine.ui.usuarios.dto;

public class UsuarioDetalleDTO {
    private String usuario;
    private String nombre;
    private String apellidos;
    private String sexo;
    private String estado;
    private String ciudad;
    private String rol;

    public UsuarioDetalleDTO() {}

    // Getters
    public String getUsuario() { return usuario; }
    public String getNombre() { return nombre; }
    public String getApellidos() { return apellidos; }
    public String getSexo() { return sexo; }
    public String getEstado() { return estado; }
    public String getCiudad() { return ciudad; }
    public String getRol() { return rol; }

    // Setters
    public void setUsuario(String usuario) { this.usuario = usuario; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }
    public void setSexo(String sexo) { this.sexo = sexo; }
    public void setEstado(String estado) { this.estado = estado; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }
    public void setRol(String rol) { this.rol = rol; }
}
