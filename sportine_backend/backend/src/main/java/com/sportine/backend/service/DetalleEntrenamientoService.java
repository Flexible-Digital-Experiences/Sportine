package com.sportine.backend.service;

import com.sportine.backend.dto.DetalleEntrenamientoDTO;

public interface DetalleEntrenamientoService {

    /**
     * Obtiene el detalle completo de un entrenamiento para mostrarlo al alumno
     */
    DetalleEntrenamientoDTO obtenerDetalleEntrenamiento(Integer idEntrenamiento, String username);

    /**
     * Cambia el estado de un ejercicio (completado/pendiente)
     * Se usa cuando el alumno marca el CheckBox
     * * @param idAsignado ID del ejercicio
     * @param completado true si se marcó, false si se desmarcó
     */
    void cambiarEstadoEjercicio(Integer idAsignado, boolean completado);
}