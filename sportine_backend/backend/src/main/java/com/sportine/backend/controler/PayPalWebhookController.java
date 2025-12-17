package com.sportine.backend.controler;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sportine.backend.service.WebhookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/webhooks/paypal")
@RequiredArgsConstructor
@Slf4j
public class PayPalWebhookController {

    private final WebhookService webhookService;
    private final Gson gson = new Gson();

    @Value("${paypal.webhook-id}")
    private String webhookId;

    /**
     * Endpoint para recibir webhooks de PayPal
     * PayPal enviará eventos aquí cuando ocurran transacciones
     */
    @PostMapping
    public ResponseEntity<String> recibirWebhook(
            @RequestBody String payload,
            @RequestHeader("PAYPAL-TRANSMISSION-ID") String transmissionId,
            @RequestHeader("PAYPAL-TRANSMISSION-TIME") String transmissionTime,
            @RequestHeader("PAYPAL-TRANSMISSION-SIG") String transmissionSig,
            @RequestHeader("PAYPAL-CERT-URL") String certUrl,
            @RequestHeader("PAYPAL-AUTH-ALGO") String authAlgo) {

        try {
            log.info("========================================");
            log.info("Webhook recibido de PayPal");
            log.info("Transmission ID: {}", transmissionId);
            log.info("Transmission Time: {}", transmissionTime);
            log.info("========================================");

            // Parsear el payload
            JsonObject webhookEvent = gson.fromJson(payload, JsonObject.class);
            String eventType = webhookEvent.get("event_type").getAsString();

            log.info("Tipo de evento: {}", eventType);

            // Verificar la firma del webhook (importante para seguridad)
            boolean isValid = webhookService.verificarFirmaWebhook(
                    payload, transmissionId, transmissionTime, transmissionSig, certUrl, authAlgo, webhookId
            );

            if (!isValid) {
                log.error("❌ Webhook con firma inválida - posible ataque");
                return ResponseEntity.status(401).body("Firma inválida");
            }

            log.info("✅ Webhook verificado correctamente");

            // Procesar el evento según su tipo
            webhookService.procesarEvento(eventType, webhookEvent);

            log.info("✅ Webhook procesado exitosamente");
            log.info("========================================");

            return ResponseEntity.ok("Webhook procesado");

        } catch (Exception e) {
            log.error("❌ Error procesando webhook: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("Error procesando webhook");
        }
    }

    /**
     * Endpoint de prueba para verificar que los webhooks están configurados
     */
    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> testWebhook() {
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Webhook endpoint está funcionando",
                "webhook_id", webhookId
        ));
    }
}