package com.example.sportine.models;

public class RespuestaSolicitudResponseDTO {
    private String mensaje;
    private boolean exito;

    public RespuestaSolicitudResponseDTO() {}

    public RespuestaSolicitudResponseDTO(String mensaje, boolean exito) {
        this.mensaje = mensaje;
        this.exito = exito;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public boolean isExito() {
        return exito;
    }

    public void setExito(boolean exito) {
        this.exito = exito;
    }
}