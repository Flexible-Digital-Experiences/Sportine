package com.sportine.backend.service;

import com.sportine.backend.model.EntrenadorAlumno;

import java.util.List;

public interface MensualidadService {

    /**
     * Actualiza todas las mensualidades vencidas a status 'pendiente'
     * @return número de relaciones actualizadas
     */
    int actualizarMensualidadesVencidas();

    /**
     * Verifica y actualiza una relación específica si está vencida
     */
    void verificarYActualizarMensualidad(Integer idRelacion);

    /**
     * Obtiene todas las relaciones que están por vencer (próximos N días)
     */
    List<EntrenadorAlumno> obtenerMensualidadesPorVencer(int dias);

    /**
     * Renueva una mensualidad (extiende 30 días desde hoy)
     */
    EntrenadorAlumno renovarMensualidad(Integer idRelacion, int diasExtension);
}
