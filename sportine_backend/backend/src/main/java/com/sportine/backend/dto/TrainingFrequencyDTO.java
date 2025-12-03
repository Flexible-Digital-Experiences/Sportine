package com.sportine.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * DTO para los datos de frecuencia de entrenamientos.
 * Se usa para alimentar gráficas de barras mostrando entrenamientos por período.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrainingFrequencyDTO {

    // Período de los datos
    private String periodo;  // "WEEK", "MONTH", "YEAR"

    // Datos para la gráfica
    private List<DataPoint> dataPoints;

    // Estadísticas del período
    private Integer totalEntrenamientos;
    private Double promedioPorPeriodo;  // Ej: promedio de entrenamientos por semana

    /**
     * Representa un punto de dato en la gráfica
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DataPoint {
        private String etiqueta;        // Ej: "Sem 1", "Enero", "Lunes"
        private Integer valor;          // Número de entrenamientos
        private String fecha;           // Fecha de referencia (formato ISO)
        private Boolean esPeriodoActual; // true si es la semana/mes actual
    }
}