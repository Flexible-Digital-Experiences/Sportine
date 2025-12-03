package com.example.sportine.models;
import java.util.List;

/**
 * DTO para los datos de frecuencia de entrenamientos.
 * Se usa para alimentar gráficas de barras.
 */
public class TrainingFrequencyDTO {

    private String periodo;  // "WEEK", "MONTH", "YEAR"
    private List<DataPoint> dataPoints;
    private Integer totalEntrenamientos;
    private Double promedioPorPeriodo;

    // Constructor vacío
    public TrainingFrequencyDTO() {
    }

    // Getters y Setters
    public String getPeriodo() {
        return periodo;
    }

    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    public List<DataPoint> getDataPoints() {
        return dataPoints;
    }

    public void setDataPoints(List<DataPoint> dataPoints) {
        this.dataPoints = dataPoints;
    }

    public Integer getTotalEntrenamientos() {
        return totalEntrenamientos;
    }

    public void setTotalEntrenamientos(Integer totalEntrenamientos) {
        this.totalEntrenamientos = totalEntrenamientos;
    }

    public Double getPromedioPorPeriodo() {
        return promedioPorPeriodo;
    }

    public void setPromedioPorPeriodo(Double promedioPorPeriodo) {
        this.promedioPorPeriodo = promedioPorPeriodo;
    }

    /**
     * Clase interna que representa un punto de dato en la gráfica
     */
    public static class DataPoint {
        private String etiqueta;        // Ej: "Sem 1", "Enero", "Lunes"
        private Integer valor;          // Número de entrenamientos
        private String fecha;           // Fecha de referencia (formato ISO)
        private Boolean esPeriodoActual; // true si es la semana/mes actual

        // Constructor vacío
        public DataPoint() {
        }

        // Getters y Setters
        public String getEtiqueta() {
            return etiqueta;
        }

        public void setEtiqueta(String etiqueta) {
            this.etiqueta = etiqueta;
        }

        public Integer getValor() {
            return valor;
        }

        public void setValor(Integer valor) {
            this.valor = valor;
        }

        public String getFecha() {
            return fecha;
        }

        public void setFecha(String fecha) {
            this.fecha = fecha;
        }

        public Boolean getEsPeriodoActual() {
            return esPeriodoActual;
        }

        public void setEsPeriodoActual(Boolean esPeriodoActual) {
            this.esPeriodoActual = esPeriodoActual;
        }
    }
}
