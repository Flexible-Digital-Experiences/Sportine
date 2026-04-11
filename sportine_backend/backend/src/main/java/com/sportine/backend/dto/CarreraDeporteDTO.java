// ── CarreraDeporteDTO.java ────────────────────────────────────────────────────
package com.sportine.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * Estadísticas históricas acumuladas del alumno en un deporte específico.
 * Se arma desde Estadisticas_Carrera_Usuario.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarreraDeporteDTO {

    @JsonProperty("id_deporte")
    private Integer idDeporte;

    @JsonProperty("nombre_deporte")
    private String nombreDeporte;

    @JsonProperty("cards")
    private List<CardCarreraDTO> cards; // Las 3 cards principales del deporte

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CardCarreraDTO {

        @JsonProperty("nombre_metrica")
        private String nombreMetrica;

        @JsonProperty("etiqueta")
        private String etiqueta;        // "Goles totales", "Regates exitosos", etc.

        @JsonProperty("emoji")
        private String emoji;

        @JsonProperty("valor_total")
        private Double valorTotal;      // acumulado histórico

        @JsonProperty("mejor_sesion")
        private Double mejorSesion;

        @JsonProperty("total_entrenamientos")
        private Integer totalEntrenamientos;

        @JsonProperty("unidad")
        private String unidad;
    }
}