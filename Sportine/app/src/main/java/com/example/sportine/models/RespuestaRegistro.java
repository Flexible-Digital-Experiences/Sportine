package com.example.sportine.models;

public class RespuestaRegistro {
    private boolean success;
    private String mensaje;
    private Long idUsuario; // Ajusta según lo que devuelva tu Spring Boot

    public boolean isSuccess() { return success; }
    public String getMensaje() { return mensaje; }
    public Long getIdUsuario() { return idUsuario; }
}
