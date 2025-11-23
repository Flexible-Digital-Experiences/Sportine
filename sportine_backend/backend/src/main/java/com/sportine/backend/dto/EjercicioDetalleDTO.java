package com.sportine.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO para mostrar el detalle de un ejercicio dentro del entrenamiento.
 * Incluye las métricas (repeticiones, series, peso, etc.) y el estado.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EjercicioDetalleDTO {

    private Integer idAsignado;
    private Integer ordenEjercicio;

    // Del catálogo
    private String nombreEjercicio;
    private String descripcion;
    private String tipoMedida;

    // Métricas asignadas
    private Integer repeticiones;
    private Integer series;
    private Integer duracion; // minutos
    private Float distancia; // km
    private Float peso; // kg

    // Instrucciones del entrenador
    private String notas;

    // Estado del ejercicio
    private String statusEjercicio; // "pendiente", "completado", "omitido"
}