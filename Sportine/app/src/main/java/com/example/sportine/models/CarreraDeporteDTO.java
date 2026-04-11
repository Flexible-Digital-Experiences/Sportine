// ── CarreraDeporteDTO.java (Android) ─────────────────────────────────────────
package com.example.sportine.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class CarreraDeporteDTO {

    @SerializedName("id_deporte")   private Integer idDeporte;
    @SerializedName("nombre_deporte") private String nombreDeporte;
    @SerializedName("cards")        private List<CardCarreraDTO> cards;

    public Integer getIdDeporte() { return idDeporte; }
    public String getNombreDeporte() { return nombreDeporte; }
    public List<CardCarreraDTO> getCards() { return cards; }

    public static class CardCarreraDTO {
        @SerializedName("nombre_metrica")       private String nombreMetrica;
        @SerializedName("etiqueta")             private String etiqueta;
        @SerializedName("emoji")                private String emoji;
        @SerializedName("valor_total")          private Double valorTotal;
        @SerializedName("mejor_sesion")         private Double mejorSesion;
        @SerializedName("total_entrenamientos") private Integer totalEntrenamientos;
        @SerializedName("unidad")               private String unidad;

        public String getNombreMetrica() { return nombreMetrica; }
        public String getEtiqueta() { return etiqueta; }
        public String getEmoji() { return emoji; }
        public Double getValorTotal() { return valorTotal != null ? valorTotal : 0.0; }
        public Double getMejorSesion() { return mejorSesion != null ? mejorSesion : 0.0; }
        public Integer getTotalEntrenamientos() { return totalEntrenamientos != null ? totalEntrenamientos : 0; }
        public String getUnidad() { return unidad != null ? unidad : ""; }
    }
}