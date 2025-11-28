package com.example.sportine.models;

public class EstadoRelacionDTO {
    private Boolean tieneRelacion;
    private String estadoRelacion; // null, "pendiente", "activo", "finalizado"
    private Integer idDeporte;
    private String nombreDeporte;
    private Boolean yaCalificado;

    // Constructor vacío
    public EstadoRelacionDTO() {}

    // Constructor completo
    public EstadoRelacionDTO(Boolean tieneRelacion, String estadoRelacion,
                             Integer idDeporte, String nombreDeporte, Boolean yaCalificado) {
        this.tieneRelacion = tieneRelacion;
        this.estadoRelacion = estadoRelacion;
        this.idDeporte = idDeporte;
        this.nombreDeporte = nombreDeporte;
        this.yaCalificado = yaCalificado;
    }

    // Getters y Setters
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
}
