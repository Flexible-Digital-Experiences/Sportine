// ── MetricasUltimosDTO.java ───────────────────────────────────────────────────
package com.sportine.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * Métricas de los últimos N entrenamientos de un deporte.
 * Se arma desde Resultado_Metrica_Manual para graficar evolución.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MetricasUltimosDTO {

    @JsonProperty("id_deporte")
    private Integer idDeporte;

    @JsonProperty("nombre_deporte")
    private String nombreDeporte;

    @JsonProperty("graficas")
    private List<GraficaMetricaDTO> graficas; // Una gráfica por métrica clave

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GraficaMetricaDTO {

        @JsonProperty("nombre_metrica")
        private String nombreMetrica;

        @JsonProperty("etiqueta")
        private String etiqueta;         // Label para mostrar en la UI

        @JsonProperty("unidad")
        private String unidad;

        @JsonProperty("puntos")
        private List<PuntoDTO> puntos;   // Un punto por entrenamiento

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class PuntoDTO {

            @JsonProperty("id_entrenamiento")
            private Integer idEntrenamiento;

            @JsonProperty("fecha")
            private String fecha;         // "31 Mar", "01 Abr", etc.

            @JsonProperty("valor")
            private Double valor;

            @JsonProperty("valor_comparado")
            private Double valorComparado; // ej. intentados vs anotados — null si no aplica
        }
    }
}