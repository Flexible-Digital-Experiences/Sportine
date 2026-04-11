package com.example.sportine.models.healthconnect;

import java.time.Instant;

/**
 * Representa una sesión de ejercicio leída de Health Connect.
 * Se usa internamente en Android — no se envía tal cual al backend.
 * El alumno selecciona una de estas sesiones y se construye el DTO del backend.
 */
public class HcSesionEjercicio {

    // Identificadores de Health Connect
    private String sesionId;        // UUID interno de HC
    private int tipoEjercicio;      // ExerciseSessionRecord.EXERCISE_TYPE_* (ej. 79=RUNNING)

    // Tiempos
    private Instant horaInicio;
    private Instant horaFin;
    private int duracionActivaMin;

    // Métricas
    private Integer fcPromedio;
    private Integer fcMaxima;
    private Integer pasos;
    private Double distanciaMetros;
    private Integer caloriasKcal;
    private Double velocidadPromedioMs;
    private Double elevacionGanadaMetros;

    // Etiqueta legible para mostrar en el spinner
    // (se calcula a partir de tipoEjercicio)
    private String etiquetaTipo;

    public HcSesionEjercicio() {}

    // ══════════════════════════════════════════════════════════════════
    // HELPERS
    // ══════════════════════════════════════════════════════════════════

    /**
     * Convierte el tipo numérico de HC a un nombre legible para el spinner.
     * Basado en ExerciseSessionRecord.EXERCISE_TYPE_* constants.
     */
    public String getEtiquetaTipo() {
        switch (tipoEjercicio) {
            case 11:   return "Boxeo";
            case 8:   return "Ciclismo";
            case 5:   return "Basketball";
            case 0:  return "Natación";
            case 73:  return "Natación";
            case 56:  return "Running";
            case 10:  return "Gimnasio";
            case 76:  return "Tenis";
            case 64: return "Fútbol";
            case 4: return "Béisbol";
            default:  return "Ejercicio #" + tipoEjercicio;
        }
    }

    /**
     * Texto para mostrar en el spinner de selección de sesión.
     * Formato: "Running  ·  45 min  ·  350 kcal"
     */
    public String getTextoSpinner() {
        StringBuilder sb = new StringBuilder(getEtiquetaTipo());
        sb.append("  ·  ").append(duracionActivaMin).append(" min");
        if (caloriasKcal != null && caloriasKcal > 0) {
            sb.append("  ·  ").append(caloriasKcal).append(" kcal");
        }
        return sb.toString();
    }

    // ══════════════════════════════════════════════════════════════════
    // GETTERS Y SETTERS
    // ══════════════════════════════════════════════════════════════════

    public String getSesionId() { return sesionId; }
    public void setSesionId(String sesionId) { this.sesionId = sesionId; }

    public int getTipoEjercicio() { return tipoEjercicio; }
    public void setTipoEjercicio(int tipoEjercicio) { this.tipoEjercicio = tipoEjercicio; }

    public Instant getHoraInicio() { return horaInicio; }
    public void setHoraInicio(Instant horaInicio) { this.horaInicio = horaInicio; }

    public Instant getHoraFin() { return horaFin; }
    public void setHoraFin(Instant horaFin) { this.horaFin = horaFin; }

    public int getDuracionActivaMin() { return duracionActivaMin; }
    public void setDuracionActivaMin(int duracionActivaMin) { this.duracionActivaMin = duracionActivaMin; }

    public Integer getFcPromedio() { return fcPromedio; }
    public void setFcPromedio(Integer fcPromedio) { this.fcPromedio = fcPromedio; }

    public Integer getFcMaxima() { return fcMaxima; }
    public void setFcMaxima(Integer fcMaxima) { this.fcMaxima = fcMaxima; }

    public Integer getPasos() { return pasos; }
    public void setPasos(Integer pasos) { this.pasos = pasos; }

    public Double getDistanciaMetros() { return distanciaMetros; }
    public void setDistanciaMetros(Double distanciaMetros) { this.distanciaMetros = distanciaMetros; }

    public Integer getCaloriasKcal() { return caloriasKcal; }
    public void setCaloriasKcal(Integer caloriasKcal) { this.caloriasKcal = caloriasKcal; }

    public Double getVelocidadPromedioMs() { return velocidadPromedioMs; }
    public void setVelocidadPromedioMs(Double velocidadPromedioMs) { this.velocidadPromedioMs = velocidadPromedioMs; }

    public Double getElevacionGanadaMetros() { return elevacionGanadaMetros; }
    public void setElevacionGanadaMetros(Double elevacionGanadaMetros) { this.elevacionGanadaMetros = elevacionGanadaMetros; }
}