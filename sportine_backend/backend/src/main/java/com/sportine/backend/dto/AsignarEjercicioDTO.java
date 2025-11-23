package com.sportine.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO para representar un ejercicio que el entrenador asigna.
 * Se usa dentro de CrearEntrenamientoRequestDTO.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AsignarEjercicioDTO {

    private Integer idCatalogo; // ID del ejercicio del catálogo
    private Integer ordenEjercicio; // Orden de ejecución (1, 2, 3...)

    // Métricas del ejercicio
    private Integer repeticiones;
    private Integer series;
    private Integer duracion; // minutos
    private Float distancia; // km
    private Float peso; // kg

    // Instrucciones opcionales del entrenador
    private String notas;
}