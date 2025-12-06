package com.example.sportine.models;

public class RespuestaSolicitudRequestDTO {
    private Integer idSolicitud;
    private String accion; // "aceptar" o "rechazar"

    public RespuestaSolicitudRequestDTO() {}

    public RespuestaSolicitudRequestDTO(Integer idSolicitud, String accion) {
        this.idSolicitud = idSolicitud;
        this.accion = accion;
    }

    public Integer getIdSolicitud() { return idSolicitud; }
    public void setIdSolicitud(Integer idSolicitud) { this.idSolicitud = idSolicitud; }

    public String getAccion() { return accion; }
    public void setAccion(String accion) { this.accion = accion; }
}