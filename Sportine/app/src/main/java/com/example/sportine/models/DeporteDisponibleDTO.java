package com.example.sportine.models;
public class DeporteDisponibleDTO {
    private Integer idDeporte;
    private String nombreDeporte;

    public DeporteDisponibleDTO() {}

    public DeporteDisponibleDTO(Integer idDeporte, String nombreDeporte) {
        this.idDeporte = idDeporte;
        this.nombreDeporte = nombreDeporte;
    }

    public Integer getIdDeporte() { return idDeporte; }
    public void setIdDeporte(Integer idDeporte) { this.idDeporte = idDeporte; }

    public String getNombreDeporte() { return nombreDeporte; }
    public void setNombreDeporte(String nombreDeporte) { this.nombreDeporte = nombreDeporte; }
}