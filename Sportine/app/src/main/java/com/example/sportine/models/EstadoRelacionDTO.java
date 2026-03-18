package com.example.sportine.models;

import java.util.List;

public class EstadoRelacionDTO {
    private Boolean tieneRelacion;
    private String estadoRelacion;
    private Integer idDeporte;
    private String nombreDeporte;
    private Boolean yaCalificado;
    private String finMensualidad;
    private List<RelacionDeporteDTO> relaciones;

    public static class RelacionDeporteDTO {
        private Integer idRelacion;
        private Integer idDeporte;
        private String nombreDeporte;
        private String statusRelacion;
        private String finMensualidad;
        private String statusSuscripcion; // "active", "cancelled", "pending", ""

        public Integer getIdRelacion() { return idRelacion; }
        public void setIdRelacion(Integer idRelacion) { this.idRelacion = idRelacion; }
        public Integer getIdDeporte() { return idDeporte; }
        public void setIdDeporte(Integer idDeporte) { this.idDeporte = idDeporte; }
        public String getNombreDeporte() { return nombreDeporte; }
        public void setNombreDeporte(String nombreDeporte) { this.nombreDeporte = nombreDeporte; }
        public String getStatusRelacion() { return statusRelacion; }
        public void setStatusRelacion(String statusRelacion) { this.statusRelacion = statusRelacion; }
        public String getFinMensualidad() { return finMensualidad; }
        public void setFinMensualidad(String finMensualidad) { this.finMensualidad = finMensualidad; }
        public String getStatusSuscripcion() { return statusSuscripcion; }
        public void setStatusSuscripcion(String statusSuscripcion) { this.statusSuscripcion = statusSuscripcion; }
    }

    public Boolean getTieneRelacion() { return tieneRelacion; }
    public void setTieneRelacion(Boolean tieneRelacion) { this.tieneRelacion = tieneRelacion; }
    public String getEstadoRelacion() { return estadoRelacion; }
    public void setEstadoRelacion(String estadoRelacion) { this.estadoRelacion = estadoRelacion; }
    public Integer getIdDeporte() { return idDeporte; }
    public void setIdDeporte(Integer idDeporte) { this.idDeporte = idDeporte; }
    public String getNombreDeporte() { return nombreDeporte; }
    public void setNombreDeporte(String nombreDeporte) { this.nombreDeporte = nombreDeporte; }
    public Boolean getYaCalificado() { return yaCalificado; }
    public void setYaCalificado(Boolean yaCalificado) { this.yaCalificado = yaCalificado; }
    public String getFinMensualidad() { return finMensualidad; }
    public void setFinMensualidad(String finMensualidad) { this.finMensualidad = finMensualidad; }
    public List<RelacionDeporteDTO> getRelaciones() { return relaciones; }
    public void setRelaciones(List<RelacionDeporteDTO> relaciones) { this.relaciones = relaciones; }
}