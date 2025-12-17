package com.sportine.backend.service;

import java.util.Map;

/**
 * Servicio para crear y capturar órdenes de pago con Platform Fees (multiparty)
 */
public interface PayPalOrderService {

    /**
     * Crear una orden de pago multiparty con platform fee
     *
     * @param usuarioEstudiante usuario del estudiante que paga
     * @param usuarioEntrenador usuario del entrenador que recibe
     * @param idDeporte ID del deporte
     * @param montoTotal monto total a pagar
     * @return Map con order_id y approval_url
     */
    Map<String, String> crearOrdenMultiparty(String usuarioEstudiante, String usuarioEntrenador,
                                             Integer idDeporte, Double montoTotal);

    /**
     * Capturar una orden ya aprobada por el usuario
     *
     * @param orderId ID de la orden
     * @return detalles de la captura
     */
    Map<String, Object> capturarOrden(String orderId);

    /**
     * Obtener detalles de una orden
     *
     * @param orderId ID de la orden
     * @return detalles de la orden
     */
    Map<String, Object> obtenerDetallesOrden(String orderId);

    /**
     * Crear orden con Vault (usando payment token guardado)
     *
     * @param idSuscripcion ID de la suscripción
     * @return order_id
     */
    String crearOrdenConVault(Integer idSuscripcion);
}