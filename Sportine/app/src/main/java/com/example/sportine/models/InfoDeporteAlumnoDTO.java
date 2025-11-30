package com.example.sportine.models;

public class InfoDeporteAlumnoDTO {
    private Integer idDeporte;
    private String nombreDeporte;
    private boolean tieneNivelRegistrado;
    private String nivelActual;

    public InfoDeporteAlumnoDTO() {}

    public Integer getIdDeporte() { return idDeporte; }
    public void setIdDeporte(Integer idDeporte) { this.idDeporte = idDeporte; }

    public String getNombreDeporte() { return nombreDeporte; }
    public void setNombreDeporte(String nombreDeporte) { this.nombreDeporte = nombreDeporte; }

    public boolean isTieneNivelRegistrado() { return tieneNivelRegistrado; }
    public void setTieneNivelRegistrado(boolean tieneNivelRegistrado) {
        this.tieneNivelRegistrado = tieneNivelRegistrado;
    }

    public String getNivelActual() { return nivelActual; }
    public void setNivelActual(String nivelActual) { this.nivelActual = nivelActual; }
}