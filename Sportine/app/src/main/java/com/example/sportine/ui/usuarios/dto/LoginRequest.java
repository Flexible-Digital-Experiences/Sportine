package com.example.sportine.ui.usuarios.dto;

public class LoginRequest {

    // Los nombres DEBEN coincidir con el JSON de Postman
    String usuario;
    String contrasena;

    public LoginRequest(String usuario, String contrasena) {
        this.usuario = usuario;
        this.contrasena = contrasena;
    }

    // Getters y Setters
    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }
    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }
}