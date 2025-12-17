package com.sportine.backend.service;

import com.sportine.backend.model.HistorialPagosAlumnoEntrenador;

import java.time.LocalDate;
import java.util.List;

/**
 * Servicio para gestionar el historial de pagos entre estudiantes y entrenadores
 */
public interface HistorialPagosService {

    /**
     * Registrar un pago exitoso
     *
     * @param idSuscripcion ID de la suscripción
     * @param orderId ID de la orden en PayPal
     * @param captureId ID de la captura en PayPal
     * @param fechaPago fecha del pago
     */
    void registrarPagoExitoso(Integer idSuscripcion, String orderId,
                              String captureId, LocalDate fechaPago);

    /**
     * Registrar un pago fallido
     *
     * @param idSuscripcion ID de la suscripción
     * @param motivoFallo razón del fallo
     * @param fechaIntento fecha del intento
     */
    void registrarPagoFallido(Integer idSuscripcion, String motivoFallo, LocalDate fechaIntento);

    /**
     * Obtener historial de pagos de una suscripción
     *
     * @param idSuscripcion ID de la suscripción
     * @return lista de pagos
     */
    List<HistorialPagosAlumnoEntrenador> obtenerHistorialPorSuscripcion(Integer idSuscripcion);

    /**
     * Verificar si existe un pago con un transaction ID específico
     *
     * @param transactionId ID de transacción de PayPal
     * @return true si existe
     */
    boolean existePagoConTransactionId(String transactionId);

    /**
     * Registrar comisión de Sportine
     *
     * @param idPago ID del pago
     */
    void registrarComisionSportine(Integer idPago);
}