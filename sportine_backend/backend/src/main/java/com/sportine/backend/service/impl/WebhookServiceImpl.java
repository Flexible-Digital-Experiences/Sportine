package com.sportine.backend.service.impl;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sportine.backend.model.InformacionEntrenador;
import com.sportine.backend.repository.AlumnoSuscripcionRepository;
import com.sportine.backend.repository.InformacionEntrenadorRepository;
import com.sportine.backend.service.HistorialPagosService;
import com.sportine.backend.service.WebhookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Base64;

@Service
@Slf4j
@RequiredArgsConstructor
public class WebhookServiceImpl implements WebhookService {

    private final AlumnoSuscripcionRepository suscripcionRepository;
    private final InformacionEntrenadorRepository entrenadorRepository;
    private final HistorialPagosService historialPagosService;
    private final RestTemplate restTemplate = new RestTemplate();
    private final Gson gson = new Gson();

    @Value("${paypal.client-id}")
    private String clientId;

    @Value("${paypal.client-secret}")
    private String clientSecret;

    @Value("${paypal.mode}")
    private String mode;

    private String getBaseUrl() {
        return "sandbox".equals(mode)
                ? "https://api-m.sandbox.paypal.com"
                : "https://api-m.paypal.com";
    }

    private String getAccessToken() {
        try {
            String auth = clientId + ":" + clientSecret;
            String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.set("Authorization", "Basic " + encodedAuth);

            String body = "grant_type=client_credentials";
            HttpEntity<String> request = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(
                    getBaseUrl() + "/v1/oauth2/token",
                    request,
                    String.class
            );

            JsonObject jsonResponse = gson.fromJson(response.getBody(), JsonObject.class);
            return jsonResponse.get("access_token").getAsString();

        } catch (Exception e) {
            log.error("Error obteniendo token: {}", e.getMessage());
            throw new RuntimeException("Error de autenticación", e);
        }
    }

    @Override
    public boolean verificarFirmaWebhook(String payload, String transmissionId, String transmissionTime,
                                         String transmissionSig, String certUrl, String authAlgo, String webhookId) {
        try {
            // Si el webhook_id está pendiente, saltamos la verificación en desarrollo
            if ("PENDIENTE".equals(webhookId)) {
                log.warn("⚠️ Webhook ID pendiente - Saltando verificación de firma (solo en desarrollo)");
                return true;
            }

            log.info("Verificando firma del webhook...");

            String accessToken = getAccessToken();

            // Construir request body para verificación
            JsonObject verifyRequest = new JsonObject();
            verifyRequest.addProperty("transmission_id", transmissionId);
            verifyRequest.addProperty("transmission_time", transmissionTime);
            verifyRequest.addProperty("cert_url", certUrl);
            verifyRequest.addProperty("auth_algo", authAlgo);
            verifyRequest.addProperty("transmission_sig", transmissionSig);
            verifyRequest.addProperty("webhook_id", webhookId);

            // El webhook_event debe ser el payload completo como JsonObject
            JsonObject webhookEvent = gson.fromJson(payload, JsonObject.class);
            verifyRequest.add("webhook_event", webhookEvent);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + accessToken);

            HttpEntity<String> request = new HttpEntity<>(gson.toJson(verifyRequest), headers);

            ResponseEntity<String> response = restTemplate.postForEntity(
                    getBaseUrl() + "/v1/notifications/verify-webhook-signature",
                    request,
                    String.class
            );

            JsonObject result = gson.fromJson(response.getBody(), JsonObject.class);
            String verificationStatus = result.get("verification_status").getAsString();

            boolean isValid = "SUCCESS".equals(verificationStatus);
            log.info("Resultado de verificación: {}", verificationStatus);

            return isValid;

        } catch (Exception e) {
            log.error("Error verificando firma del webhook: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public void procesarEvento(String eventType, JsonObject eventData) {
        log.info("Procesando evento: {}", eventType);

        switch (eventType) {
            // Eventos de pagos
            case "PAYMENT.CAPTURE.COMPLETED":
                procesarPagoCompletado(eventData);
                break;

            case "PAYMENT.CAPTURE.DENIED":
            case "PAYMENT.CAPTURE.DECLINED":
            case "PAYMENT.CAPTURE.FAILED":
                procesarPagoFallido(eventData);
                break;

            case "PAYMENT.CAPTURE.REFUNDED":
                procesarPagoReembolsado(eventData);
                break;

            // Eventos de onboarding
            case "MERCHANT.ONBOARDING.COMPLETED":
                procesarMerchantOnboardingCompletado(eventData);
                break;

            case "MERCHANT.PARTNER-CONSENT.REVOKED":
                procesarConsentimientoRevocado(eventData);
                break;

            // Eventos de suscripciones (si usas Subscriptions API nativo)
            case "BILLING.SUBSCRIPTION.ACTIVATED":
                log.info("Suscripción activada - no requiere acción (manejado por confirmación manual)");
                break;

            case "BILLING.SUBSCRIPTION.CANCELLED":
                log.info("Suscripción cancelada - actualizar BD");
                break;

            default:
                log.warn("Evento no manejado: {}", eventType);
        }
    }

    @Override
    @Transactional
    public void procesarPagoCompletado(JsonObject eventData) {
        try {
            log.info("Procesando pago completado...");

            JsonObject resource = eventData.getAsJsonObject("resource");
            String captureId = resource.get("id").getAsString();

            // Verificar si ya procesamos este pago
            if (historialPagosService.existePagoConTransactionId(captureId)) {
                log.warn("Pago {} ya fue procesado anteriormente - ignorando", captureId);
                return;
            }

            // Extraer información del pago
            JsonObject amount = resource.getAsJsonObject("amount");
            String currency = amount.get("currency_code").getAsString();
            String value = amount.get("value").getAsString();

            log.info("Capture ID: {}", captureId);
            log.info("Monto: {} {}", value, currency);

            // Aquí podrías buscar la suscripción asociada y actualizar el historial
            // Por ahora solo logeamos
            log.info("✅ Pago completado procesado correctamente");

        } catch (Exception e) {
            log.error("Error procesando pago completado: {}", e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public void procesarPagoFallido(JsonObject eventData) {
        try {
            log.warn("Procesando pago fallido...");

            JsonObject resource = eventData.getAsJsonObject("resource");
            String captureId = resource.get("id").getAsString();

            log.warn("Capture ID fallido: {}", captureId);
            log.warn("⚠️ Se debe notificar al estudiante sobre el fallo en el pago");

            // Aquí puedes:
            // 1. Buscar la suscripción afectada
            // 2. Incrementar contador de fallos
            // 3. Enviar notificación al estudiante
            // 4. Si tiene 3 fallos, cancelar suscripción

        } catch (Exception e) {
            log.error("Error procesando pago fallido: {}", e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public void procesarPagoReembolsado(JsonObject eventData) {
        try {
            log.warn("Procesando pago reembolsado...");

            JsonObject resource = eventData.getAsJsonObject("resource");
            String refundId = resource.get("id").getAsString();

            log.warn("Refund ID: {}", refundId);
            log.warn("⚠️ Actualizar historial de pagos con el reembolso");

            // Aquí puedes:
            // 1. Buscar el pago original
            // 2. Marcar como reembolsado
            // 3. Ajustar comisiones si es necesario
            // 4. Notificar a ambas partes

        } catch (Exception e) {
            log.error("Error procesando reembolso: {}", e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public void procesarMerchantOnboardingCompletado(JsonObject eventData) {
        try {
            log.info("Procesando onboarding completado...");

            JsonObject resource = eventData.getAsJsonObject("resource");
            String merchantId = resource.get("merchant_id").getAsString();

            log.info("Merchant ID onboarded: {}", merchantId);

            // Actualizar estado en BD
            InformacionEntrenador entrenador = entrenadorRepository.findByMerchantId(merchantId)
                    .orElse(null);

            if (entrenador != null) {
                entrenador.setOnboardingStatus(InformacionEntrenador.OnboardingStatus.completed);
                entrenador.setFechaOnboarding(LocalDate.now());
                entrenadorRepository.save(entrenador);

                log.info("✅ Entrenador {} onboarding completado", entrenador.getUsuario());
            } else {
                log.warn("⚠️ No se encontró entrenador con merchant_id: {}", merchantId);
            }

        } catch (Exception e) {
            log.error("Error procesando onboarding completado: {}", e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public void procesarConsentimientoRevocado(JsonObject eventData) {
        try {
            log.warn("Procesando consentimiento revocado...");

            JsonObject resource = eventData.getAsJsonObject("resource");
            String merchantId = resource.get("merchant_id").getAsString();

            log.warn("Merchant ID que revocó consentimiento: {}", merchantId);

            // Marcar al entrenador como que ya no puede recibir pagos
            InformacionEntrenador entrenador = entrenadorRepository.findByMerchantId(merchantId)
                    .orElse(null);

            if (entrenador != null) {
                entrenador.setOnboardingStatus(InformacionEntrenador.OnboardingStatus.failed);
                entrenador.setPermissionsGranted("{\"revoked\": true}");
                entrenadorRepository.save(entrenador);

                log.warn("⚠️ Entrenador {} ya no puede recibir pagos", entrenador.getUsuario());

                // Aquí deberías:
                // 1. Cancelar todas sus suscripciones activas
                // 2. Notificar a los estudiantes
                // 3. Notificar al entrenador
            }

        } catch (Exception e) {
            log.error("Error procesando consentimiento revocado: {}", e.getMessage(), e);
        }
    }
}