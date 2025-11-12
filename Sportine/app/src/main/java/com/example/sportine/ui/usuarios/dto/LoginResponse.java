package com.example.sportine.ui.usuarios.dto;

public class LoginResponse {
    private String token;
    private String rol;
    private String nombreUsuario;

    // Getters
    public String getToken() { return token; }
    public String getRol() { return rol; }
    public String getNombreUsuario() { return nombreUsuario; }
}
