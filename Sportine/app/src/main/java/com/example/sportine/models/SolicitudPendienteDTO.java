package com.example.sportine.models;

import java.util.List;

public class SolicitudPendienteDTO {
    private Boolean tieneSolicitudPendiente;
    private List<SolicitudDetalleDTO> solicitudes;

    public SolicitudPendienteDTO() {
    }

    public Boolean getTieneSolicitudPendiente() {
        return tieneSolicitudPendiente;
    }

    public void setTieneSolicitudPendiente(Boolean tieneSolicitudPendiente) {
        this.tieneSolicitudPendiente = tieneSolicitudPendiente;
    }

    public List<SolicitudDetalleDTO> getSolicitudes() {
        return solicitudes;
    }

    public void setSolicitudes(List<SolicitudDetalleDTO> solicitudes) {
        this.solicitudes = solicitudes;
    }
}