package com.sportine.backend.dto;

import lombok.Data;

@Data
public class ResultadoProcesamientoDTO {
    private int totalProcesadas = 0;
    private int pagosExitosos = 0;
    private int pagosFallidos = 0;
    private int suscripcionesCanceladas = 0;
    private int reintentosExitosos = 0;
    private int reintentosFallidos = 0;

    public void incrementarExitosos() {
        this.pagosExitosos++;
    }

    public void incrementarFallidos() {
        this.pagosFallidos++;
    }

    public void incrementarCanceladas() {
        this.suscripcionesCanceladas++;
    }

    public void incrementarReintentosExitosos() {
        this.reintentosExitosos++;
    }

    public void incrementarReintentosFallidos() {
        this.reintentosFallidos++;
    }
}