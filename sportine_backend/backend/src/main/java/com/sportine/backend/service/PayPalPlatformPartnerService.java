package com.sportine.backend.service;

import java.util.Map;

/**
 * Servicio para manejar el onboarding de entrenadores a PayPal Platform Partner
 */
public interface PayPalPlatformPartnerService {

    /**
     * Iniciar proceso de onboarding para un entrenador
     * Genera el link que el entrenador debe seguir para conectar su cuenta PayPal
     *
     * @param usuario username del entrenador
     * @return Map con tracking_id y onboarding_url
     */
    Map<String, String> iniciarOnboarding(String usuario);

    /**
     * Verificar el estado del onboarding con PayPal
     * Se llama después de que el entrenador completa el proceso
     *
     * @param merchantId ID del merchant devuelto por PayPal
     * @param trackingId tracking ID generado por nosotros
     * @return detalles del merchant
     */
    Map<String, Object> verificarOnboarding(String merchantId, String trackingId);

    /**
     * Completar el proceso de onboarding y actualizar la BD
     *
     * @param merchantId merchant ID de PayPal
     * @param merchantIdInPaypal payer ID
     * @param trackingId tracking ID
     * @param isEmailConfirmed si el email está confirmado
     */
    void completarOnboarding(String merchantId, String merchantIdInPaypal,
                             String trackingId, boolean isEmailConfirmed);

    /**
     * Verificar si un entrenador puede recibir pagos
     *
     * @param usuario username del entrenador
     * @return true si está onboarded y puede recibir pagos
     */
    boolean puedeRecibirPagos(String usuario);

    /**
     * Obtener detalles del merchant desde PayPal
     *
     * @param merchantId merchant ID del entrenador
     * @return detalles del merchant
     */
    Map<String, Object> obtenerDetallesMerchant(String merchantId);
}