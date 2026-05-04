package com.example.sportine.models;

public class EliminarCuentaRequest {

    private String contrasena;

    public EliminarCuentaRequest() {}

    public EliminarCuentaRequest(String contrasena) {
        this.contrasena = contrasena;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }
}