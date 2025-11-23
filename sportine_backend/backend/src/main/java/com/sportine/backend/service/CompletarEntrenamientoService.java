package com.sportine.backend.service;

import com.sportine.backend.dto.CompletarEntrenamientoRequestDTO;

/**
 * Interface para el servicio de completar entrenamientos.
 * Define las operaciones para marcar entrenamientos como finalizados.
 */
public interface CompletarEntrenamientoService {

    /**
     * Marca un entrenamiento como completado
     *
     * @param request DTO con el ID del entrenamiento y feedback opcional
     * @param username Usuario del alumno
     * @return Mensaje de éxito
     */
    String completarEntrenamiento(CompletarEntrenamientoRequestDTO request, String username);
}