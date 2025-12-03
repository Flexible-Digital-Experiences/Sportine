package com.example.sportine.models;

import java.util.List;

/**
 * DTO para la distribución de deportes en los entrenamientos.
 * Se usa para alimentar gráficas de pastel/dona.
 */
public class SportsDistributionDTO {

    private List<SportData> deportes;
    private Integer totalEntrenamientos;
    private String deportePrincipal;

    // Constructor vacío
    public SportsDistributionDTO() {
    }

    // Getters y Setters
    public List<SportData> getDeportes() {
        return deportes;
    }

    public void setDeportes(List<SportData> deportes) {
        this.deportes = deportes;
    }

    public Integer getTotalEntrenamientos() {
        return totalEntrenamientos;
    }

    public void setTotalEntrenamientos(Integer totalEntrenamientos) {
        this.totalEntrenamientos = totalEntrenamientos;
    }

    public String getDeportePrincipal() {
        return deportePrincipal;
    }

    public void setDeportePrincipal(String deportePrincipal) {
        this.deportePrincipal = deportePrincipal;
    }

    /**
     * Clase interna que representa los datos de un deporte específico
     */
    public static class SportData {
        private Integer idDeporte;
        private String nombreDeporte;       // "Fútbol", "Gimnasio", etc.
        private Integer cantidadEntrenamientos;
        private Double porcentaje;          // % del total
        private String color;               // Color sugerido para la gráfica (hex)

        // Constructor vacío
        public SportData() {
        }

        // Getters y Setters
        public Integer getIdDeporte() {
            return idDeporte;
        }

        public void setIdDeporte(Integer idDeporte) {
            this.idDeporte = idDeporte;
        }

        public String getNombreDeporte() {
            return nombreDeporte;
        }

        public void setNombreDeporte(String nombreDeporte) {
            this.nombreDeporte = nombreDeporte;
        }

        public Integer getCantidadEntrenamientos() {
            return cantidadEntrenamientos;
        }

        public void setCantidadEntrenamientos(Integer cantidadEntrenamientos) {
            this.cantidadEntrenamientos = cantidadEntrenamientos;
        }

        public Double getPorcentaje() {
            return porcentaje;
        }

        public void setPorcentaje(Double porcentaje) {
            this.porcentaje = porcentaje;
        }

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }
    }
}
