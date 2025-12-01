package com.example.sportine.models;

public class SolicitudEnviadaDTO {
    private Integer idSolicitud;
    private String usuarioEntrenador;
    private String nombreEntrenador;
    private String fotoEntrenador;
    private String nombreDeporte;
    private String statusSolicitud;
    private String fechaSolicitud;
    private String motivo;

    public SolicitudEnviadaDTO() {
    }

    // Getters y Setters
    public Integer getIdSolicitud() {
        return idSolicitud;
    }

    public void setIdSolicitud(Integer idSolicitud) {
        this.idSolicitud = idSolicitud;
    }

    public String getUsuarioEntrenador() {
        return usuarioEntrenador;
    }

    public void setUsuarioEntrenador(String usuarioEntrenador) {
        this.usuarioEntrenador = usuarioEntrenador;
    }

    public String getNombreEntrenador() {
        return nombreEntrenador;
    }

    public void setNombreEntrenador(String nombreEntrenador) {
        this.nombreEntrenador = nombreEntrenador;
    }

    public String getFotoEntrenador() {
        return fotoEntrenador;
    }

    public void setFotoEntrenador(String fotoEntrenador) {
        this.fotoEntrenador = fotoEntrenador;
    }

    public String getNombreDeporte() {
        return nombreDeporte;
    }

    public void setNombreDeporte(String nombreDeporte) {
        this.nombreDeporte = nombreDeporte;
    }

    public String getStatusSolicitud() {
        return statusSolicitud;
    }

    public void setStatusSolicitud(String statusSolicitud) {
        this.statusSolicitud = statusSolicitud;
    }

    public String getFechaSolicitud() {
        return fechaSolicitud;
    }

    public void setFechaSolicitud(String fechaSolicitud) {
        this.fechaSolicitud = fechaSolicitud;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }
}