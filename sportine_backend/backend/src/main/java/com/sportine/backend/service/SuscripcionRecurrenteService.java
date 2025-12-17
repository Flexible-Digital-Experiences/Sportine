package com.sportine.backend.service;

import com.sportine.backend.dto.ResultadoProcesamientoDTO;

import java.time.LocalDate;

/**
 * Servicio para manejar pagos recurrentes de suscripciones estudiante-entrenador
 */
public interface SuscripcionRecurrenteService {

    /**
     * Procesar todos los pagos que deben ejecutarse en una fecha específica
     * Este método es llamado por el scheduler diariamente
     *
     * @param fecha fecha para la cual procesar pagos
     * @return resultado del procesamiento
     */
    ResultadoProcesamientoDTO procesarPagosPendientes(LocalDate fecha);

    /**
     * Reintentar pagos que fallaron recientemente
     *
     * @return resultado de los reintentos
     */
    ResultadoProcesamientoDTO reintentarPagosFallidos();

    /**
     * Cancelar suscripciones con 3 o más pagos fallidos consecutivos
     *
     * @return cantidad de suscripciones canceladas
     */
    int cancelarSuscripcionesConFallosContinuos();

    /**
     * Marcar como expiradas las suscripciones cuya fecha_fin ya pasó
     *
     * @return cantidad de suscripciones marcadas como expiradas
     */
    int marcarSuscripcionesExpiradas();

    /**
     * Procesar un pago individual de una suscripción
     *
     * @param idSuscripcion ID de la suscripción
     * @return true si el pago fue exitoso
     */
    boolean procesarPagoIndividual(Integer idSuscripcion);
}