package com.sportine.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * DTO para la distribución de deportes en los entrenamientos.
 * Se usa para alimentar gráficas de pastel/dona.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SportsDistributionDTO {

    // Datos para la gráfica
    private List<SportData> deportes;

    // Total de entrenamientos considerados
    private Integer totalEntrenamientos;

    // Deporte más frecuente
    private String deportePrincipal;

    /**
     * Representa los datos de un deporte específico
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SportData {
        private Integer idDeporte;
        private String nombreDeporte;       // "Fútbol", "Gimnasio", etc.
        private Integer cantidadEntrenamientos;
        private Double porcentaje;          // % del total
        private String color;               // Color sugerido para la gráfica (hex)
    }
}