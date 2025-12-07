package com.example.sportine.ui.entrenadores.dto;

public class DeporteRequestDTO {
    private String nombreDeporte;

    public DeporteRequestDTO(String nombreDeporte) {
        this.nombreDeporte = nombreDeporte;
    }

    public String getNombreDeporte() {
        return nombreDeporte;
    }
}
