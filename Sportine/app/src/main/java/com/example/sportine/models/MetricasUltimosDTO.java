// ── MetricasUltimosDTO.java (Android) ────────────────────────────────────────
package com.example.sportine.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class MetricasUltimosDTO {

    @SerializedName("id_deporte")     private Integer idDeporte;
    @SerializedName("nombre_deporte") private String nombreDeporte;
    @SerializedName("graficas")       private List<GraficaMetricaDTO> graficas;

    public Integer getIdDeporte() { return idDeporte; }
    public String getNombreDeporte() { return nombreDeporte; }
    public List<GraficaMetricaDTO> getGraficas() { return graficas; }

    public static class GraficaMetricaDTO {
        @SerializedName("nombre_metrica") private String nombreMetrica;
        @SerializedName("etiqueta")       private String etiqueta;
        @SerializedName("unidad")         private String unidad;
        @SerializedName("puntos")         private List<PuntoDTO> puntos;

        public String getNombreMetrica() { return nombreMetrica; }
        public String getEtiqueta() { return etiqueta; }
        public String getUnidad() { return unidad; }
        public List<PuntoDTO> getPuntos() { return puntos; }

        public static class PuntoDTO {
            @SerializedName("id_entrenamiento") private Integer idEntrenamiento;
            @SerializedName("fecha")            private String fecha;
            @SerializedName("valor")            private Double valor;
            @SerializedName("valor_comparado")  private Double valorComparado;

            public Integer getIdEntrenamiento() { return idEntrenamiento; }
            public String getFecha() { return fecha; }
            public Double getValor() { return valor != null ? valor : 0.0; }
            public Double getValorComparado() { return valorComparado; }
            public boolean tieneComparado() { return valorComparado != null; }
        }
    }
}