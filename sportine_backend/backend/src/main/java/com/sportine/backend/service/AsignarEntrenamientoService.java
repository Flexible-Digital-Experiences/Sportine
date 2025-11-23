package com.sportine.backend.service;

import com.sportine.backend.dto.CrearEntrenamientoRequestDTO;

/**
 * Interface para el servicio de asignación de entrenamientos.
 * Define las operaciones para crear, editar y eliminar entrenamientos.
 */
public interface AsignarEntrenamientoService {

    /**
     * Crea y asigna un entrenamiento completo a un alumno
     *
     * @param request DTO con los datos del entrenamiento y ejercicios
     * @param usernameEntrenador Usuario del entrenador que crea el entrenamiento
     * @return ID del entrenamiento creado
     */
    Integer crearEntrenamiento(CrearEntrenamientoRequestDTO request, String usernameEntrenador);

    /**
     * Actualiza un entrenamiento existente
     *
     * @param idEntrenamiento ID del entrenamiento a actualizar
     * @param request Nuevos datos
     * @param usernameEntrenador Usuario del entrenador
     */
    void actualizarEntrenamiento(Integer idEntrenamiento,
                                 CrearEntrenamientoRequestDTO request,
                                 String usernameEntrenador);

    /**
     * Elimina un entrenamiento
     *
     * @param idEntrenamiento ID del entrenamiento
     * @param usernameEntrenador Usuario del entrenador
     */
    void eliminarEntrenamiento(Integer idEntrenamiento, String usernameEntrenador);
}