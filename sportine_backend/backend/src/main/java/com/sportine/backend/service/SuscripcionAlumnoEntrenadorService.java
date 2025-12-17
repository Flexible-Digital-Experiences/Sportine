package com.sportine.backend.service;

import com.sportine.backend.model.AlumnoSuscripcionEntrenador;

import java.util.List;
import java.util.Map;

/**
 * Servicio para gestionar suscripciones entre estudiantes y entrenadores
 */
public interface SuscripcionAlumnoEntrenadorService {

    /**
     * Crear una nueva suscripción (con primer pago)
     *
     * @param usuarioEstudiante usuario del estudiante
     * @param usuarioEntrenador usuario del entrenador
     * @param idDeporte ID del deporte
     * @return Map con order_id y approval_url
     */
    Map<String, String> crearSuscripcion(String usuarioEstudiante, String usuarioEntrenador, Integer idDeporte);

    /**
     * Confirmar suscripción después de que el estudiante apruebe el pago
     *
     * @param orderId ID de la orden de PayPal
     * @param vaultId ID del payment token guardado (opcional)
     * @return suscripción activada
     */
    AlumnoSuscripcionEntrenador confirmarSuscripcion(String orderId, String vaultId);

    /**
     * Cancelar una suscripción
     *
     * @param idSuscripcion ID de la suscripción
     * @param motivo motivo de cancelación
     */
    void cancelarSuscripcion(Integer idSuscripcion, String motivo);

    /**
     * Obtener suscripciones de un estudiante
     *
     * @param usuarioEstudiante usuario del estudiante
     * @return lista de suscripciones
     */
    List<AlumnoSuscripcionEntrenador> obtenerSuscripcionesEstudiante(String usuarioEstudiante);

    /**
     * Obtener suscripciones de un entrenador
     *
     * @param usuarioEntrenador usuario del entrenador
     * @return lista de suscripciones
     */
    List<AlumnoSuscripcionEntrenador> obtenerSuscripcionesEntrenador(String usuarioEntrenador);

    /**
     * Verificar si un estudiante tiene suscripción activa con un entrenador
     *
     * @param usuarioEstudiante usuario del estudiante
     * @param usuarioEntrenador usuario del entrenador
     * @param idDeporte ID del deporte
     * @return true si tiene suscripción activa
     */
    boolean tieneSuscripcionActiva(String usuarioEstudiante, String usuarioEntrenador, Integer idDeporte);
}