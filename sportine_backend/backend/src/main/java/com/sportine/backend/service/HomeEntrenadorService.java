package com.sportine.backend.service;

import com.sportine.backend.dto.HomeEntrenadorDTO;

/**
 * Interface para el servicio del home del entrenador.
 * Define las operaciones para obtener la vista principal del entrenador.
 */
public interface HomeEntrenadorService {

    /**
     * Obtiene los datos del home del entrenador
     *
     * @param username Usuario del entrenador
     * @return DTO con saludo, fecha y lista de alumnos con progreso
     */
    HomeEntrenadorDTO obtenerHomeEntrenador(String username);
}