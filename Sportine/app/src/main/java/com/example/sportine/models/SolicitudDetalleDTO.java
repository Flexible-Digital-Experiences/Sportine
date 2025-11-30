package com.example.sportine.models;

public class SolicitudDetalleDTO {
    private Integer idSolicitud;
    private String nombreDeporte;
    private String fechaSolicitud;
    private String motivo;

    public SolicitudDetalleDTO() {
    }

    public SolicitudDetalleDTO(Integer idSolicitud, String nombreDeporte, String fechaSolicitud, String motivo) {
        this.idSolicitud = idSolicitud;
        this.nombreDeporte = nombreDeporte;
        this.fechaSolicitud = fechaSolicitud;
        this.motivo = motivo;
    }

    public Integer getIdSolicitud() {
        return idSolicitud;
    }

    public void setIdSolicitud(Integer idSolicitud) {
        this.idSolicitud = idSolicitud;
    }

    public String getNombreDeporte() {
        return nombreDeporte;
    }

    public void setNombreDeporte(String nombreDeporte) {
        this.nombreDeporte = nombreDeporte;
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