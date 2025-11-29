package com.example.sportine.models;

public class SolicitudResponseDTO {

    private Integer idSolicitud;
    private String mensaje;
    private String status;
    private String fechaSolicitud;

    // Constructor vacío (necesario para Gson/Retrofit)
    public SolicitudResponseDTO() {
    }

    // Constructor completo
    public SolicitudResponseDTO(Integer idSolicitud, String mensaje, String status, String fechaSolicitud) {
        this.idSolicitud = idSolicitud;
        this.mensaje = mensaje;
        this.status = status;
        this.fechaSolicitud = fechaSolicitud;
    }

    // Getters y Setters
    public Integer getIdSolicitud() {
        return idSolicitud;
    }

    public void setIdSolicitud(Integer idSolicitud) {
        this.idSolicitud = idSolicitud;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFechaSolicitud() {
        return fechaSolicitud;
    }

    public void setFechaSolicitud(String fechaSolicitud) {
        this.fechaSolicitud = fechaSolicitud;
    }
}
