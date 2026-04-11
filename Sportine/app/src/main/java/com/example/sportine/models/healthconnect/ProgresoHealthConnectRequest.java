package com.example.sportine.models.healthconnect;

import com.google.gson.annotations.SerializedName;

/**
 * Request para sincronizar una sesión de Health Connect con el backend.
 * Se construye desde HcSesionEjercicio después de que el alumno confirma.
 */
public class ProgresoHealthConnectRequest {
    // Datos de la sesión de Health Connect
    @SerializedName("id_entrenamiento")
    private Integer idEntrenamiento;

    @SerializedName("hc_sesion_id")
    private String hcSesionId;

    @SerializedName("hc_tipo_ejercicio")
    private Integer hcTipoEjercicio;

    @SerializedName("hc_duracion_activa_min")
    private Integer hcDuracionActivaMin;

    @SerializedName("hc_calorias_kcal")
    private Integer hcCaloriasKcal;

    @SerializedName("hc_pasos")
    private Integer hcPasos;

    @SerializedName("hc_distancia_metros")
    private Double hcDistanciaMetros;

    @SerializedName("hc_fc_promedio")
    private Integer hcFcPromedio;

    @SerializedName("hc_fc_maxima")
    private Integer hcFcMaxima;

    @SerializedName("hc_velocidad_promedio_ms")
    private Double hcVelocidadPromedioMs;

    @SerializedName("hc_elevacion_ganada_metros")
    private Double hcElevacionGanadaMetros;

    @SerializedName("hc_fuente_datos")
    private String hcFuenteDatos;      // "health_connect"

    public ProgresoHealthConnectRequest() {
        this.hcFuenteDatos = "health_connect";
    }

    // Getters y Setters
    public Integer getIdEntrenamiento() { return idEntrenamiento; }
    public void setIdEntrenamiento(Integer idEntrenamiento) { this.idEntrenamiento = idEntrenamiento; }

    public String getHcSesionId() { return hcSesionId; }
    public void setHcSesionId(String hcSesionId) { this.hcSesionId = hcSesionId; }

    public Integer getHcTipoEjercicio() { return hcTipoEjercicio; }
    public void setHcTipoEjercicio(Integer hcTipoEjercicio) { this.hcTipoEjercicio = hcTipoEjercicio; }

    public Integer getHcDuracionActivaMin() { return hcDuracionActivaMin; }
    public void setHcDuracionActivaMin(Integer hcDuracionActivaMin) { this.hcDuracionActivaMin = hcDuracionActivaMin; }

    public Integer getHcCaloriasKcal() { return hcCaloriasKcal; }
    public void setHcCaloriasKcal(Integer hcCaloriasKcal) { this.hcCaloriasKcal = hcCaloriasKcal; }

    public Integer getHcPasos() { return hcPasos; }
    public void setHcPasos(Integer hcPasos) { this.hcPasos = hcPasos; }

    public Double getHcDistanciaMetros() { return hcDistanciaMetros; }
    public void setHcDistanciaMetros(Double hcDistanciaMetros) { this.hcDistanciaMetros = hcDistanciaMetros; }

    public Integer getHcFcPromedio() { return hcFcPromedio; }
    public void setHcFcPromedio(Integer hcFcPromedio) { this.hcFcPromedio = hcFcPromedio; }

    public Integer getHcFcMaxima() { return hcFcMaxima; }
    public void setHcFcMaxima(Integer hcFcMaxima) { this.hcFcMaxima = hcFcMaxima; }

    public Double getHcVelocidadPromedioMs() { return hcVelocidadPromedioMs; }
    public void setHcVelocidadPromedioMs(Double hcVelocidadPromedioMs) { this.hcVelocidadPromedioMs = hcVelocidadPromedioMs; }

    public Double getHcElevacionGanadaMetros() { return hcElevacionGanadaMetros; }
    public void setHcElevacionGanadaMetros(Double hcElevacionGanadaMetros) { this.hcElevacionGanadaMetros = hcElevacionGanadaMetros; }

    public String getHcFuenteDatos() { return hcFuenteDatos; }
    public void setHcFuenteDatos(String hcFuenteDatos) { this.hcFuenteDatos = hcFuenteDatos; }

    /**
     * Factory method que convierte un HcSesionEjercicio en este request.
     */
    public static ProgresoHealthConnectRequest desde(
            com.example.sportine.models.healthconnect.HcSesionEjercicio sesion,
            int idEntrenamiento) {

        ProgresoHealthConnectRequest req = new ProgresoHealthConnectRequest();
        req.setIdEntrenamiento(idEntrenamiento);
        req.setHcSesionId(sesion.getSesionId());
        req.setHcTipoEjercicio(sesion.getTipoEjercicio());
        req.setHcDuracionActivaMin(sesion.getDuracionActivaMin());
        req.setHcCaloriasKcal(sesion.getCaloriasKcal());
        req.setHcPasos(sesion.getPasos());
        req.setHcDistanciaMetros(sesion.getDistanciaMetros());
        req.setHcFcPromedio(sesion.getFcPromedio());
        req.setHcFcMaxima(sesion.getFcMaxima());
        req.setHcVelocidadPromedioMs(sesion.getVelocidadPromedioMs());
        req.setHcElevacionGanadaMetros(sesion.getElevacionGanadaMetros());
        return req;
    }
}