package com.sportine.backend.service;

import com.google.gson.JsonObject;

/**
 * Servicio para manejar webhooks de PayPal
 */
public interface WebhookService {

    /**
     * Verificar la firma de un webhook de PayPal
     *
     * @param payload cuerpo del webhook
     * @param transmissionId ID de transmisión
     * @param transmissionTime tiempo de transmisión
     * @param transmissionSig firma
     * @param certUrl URL del certificado
     * @param authAlgo algoritmo de autenticación
     * @param webhookId ID del webhook configurado
     * @return true si la firma es válida
     */
    boolean verificarFirmaWebhook(String payload, String transmissionId, String transmissionTime,
                                  String transmissionSig, String certUrl, String authAlgo, String webhookId);

    /**
     * Procesar un evento de webhook según su tipo
     *
     * @param eventType tipo de evento
     * @param eventData datos del evento
     */
    void procesarEvento(String eventType, JsonObject eventData);

    /**
     * Procesar evento de pago completado
     */
    void procesarPagoCompletado(JsonObject eventData);

    /**
     * Procesar evento de pago fallido
     */
    void procesarPagoFallido(JsonObject eventData);

    /**
     * Procesar evento de pago reembolsado
     */
    void procesarPagoReembolsado(JsonObject eventData);

    /**
     * Procesar evento de onboarding completado
     */
    void procesarMerchantOnboardingCompletado(JsonObject eventData);

    /**
     * Procesar evento de consentimiento revocado
     */
    void procesarConsentimientoRevocado(JsonObject eventData);
}