package com.example.sportine.models;
public class Usuario {

    private String usuario;
    private String nombre;
    private String apellidos;
    private String sexo;
    private Integer idEstado;
    private String ciudad;
    private String rol;
    private String contrasena;

    public Usuario(String usuario, String nombre, String apellidos,
                   String sexo, Integer idEstado, String ciudad,
                   String rol, String contrasena) {

        this.usuario = usuario;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.sexo = sexo;
        this.idEstado = idEstado;
        this.ciudad = ciudad;
        this.rol = rol;
        this.contrasena = contrasena;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

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

    public Integer getEstado() {
        return idEstado;
    }

    public void setEstado(Integer idEstado) {
        this.idEstado = idEstado;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }
}
