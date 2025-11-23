package com.sportine.backend.service;

import com.sportine.backend.dto.DetalleEntrenamientoDTO;

/**
 * Interface para el servicio de detalle de entrenamientos.
 * Define las operaciones para obtener información completa de un entrenamiento.
 */
public interface DetalleEntrenamientoService {

    /**
     * Obtiene el detalle completo de un entrenamiento para mostrarlo al alumno
     *
     * @param idEntrenamiento ID del entrenamiento
     * @param username Usuario del alumno (para validación)
     * @return DTO con toda la información del entrenamiento
     */
    DetalleEntrenamientoDTO obtenerDetalleEntrenamiento(Integer idEntrenamiento, String username);
}