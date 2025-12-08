package com.sportine.backend.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sportine.backend.repository.InformacionEntrenadorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor  // ✅ Para inyectar el repository
public class PayPalSuscripcionService {

    // ✅ Inyectar el repository
    private final InformacionEntrenadorRepository entrenadorRepository;

    @Value("${paypal.client-id}")
    private String clientId;

    @Value("${paypal.client-secret}")
    private String clientSecret;

    @Value("${paypal.mode}")
    private String mode;

    @Value("${paypal.plan.id}")
    private String planPremiumId;

    private final RestTemplate restTemplate = new RestTemplate();
    private final Gson gson = new Gson();

    private String getBaseUrl() {
        return "sandbox".equals(mode)
                ? "https://api-m.sandbox.paypal.com"
                : "https://api-m.paypal.com";
    }

    private String getAccessToken() {
        try {
            log.info("=== INTENTANDO OBTENER TOKEN ===");
            log.info("Client ID: {}", clientId != null ? clientId.substring(0, 10) + "..." : "NULL");
            log.info("Client Secret: {}", clientSecret != null ? "***" + clientSecret.substring(clientSecret.length()-5) : "NULL");
            log.info("Mode: {}", mode);
            log.info("Base URL: {}", getBaseUrl());

            String auth = clientId + ":" + clientSecret;
            String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));

            log.info("Auth Base64 generado (primeros 40 chars): {}", encodedAuth.substring(0, Math.min(40, encodedAuth.length())) + "...");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.set("Authorization", "Basic " + encodedAuth);

            String body = "grant_type=client_credentials";

            HttpEntity<String> request = new HttpEntity<>(body, headers);

            log.info("Haciendo POST a: {}", getBaseUrl() + "/v1/oauth2/token");

            ResponseEntity<String> response = restTemplate.postForEntity(
                    getBaseUrl() + "/v1/oauth2/token",
                    request,
                    String.class
            );

            log.info("✅ Respuesta exitosa: status={}", response.getStatusCode());

            JsonObject jsonResponse = gson.fromJson(response.getBody(), JsonObject.class);
            String token = jsonResponse.get("access_token").getAsString();

            log.info("✅ Token obtenido: {}", token.substring(0, 20) + "...");

            return token;

        } catch (HttpClientErrorException e) {
            log.error("❌ HTTP Error: status={}", e.getStatusCode());
            log.error("❌ Response body: {}", e.getResponseBodyAsString());
            log.error("❌ Response headers: {}", e.getResponseHeaders());
            throw new RuntimeException("Error de autenticación con PayPal", e);
        } catch (Exception e) {
            log.error("❌ Error general: {}", e.getMessage());
            log.error("❌ Tipo: {}", e.getClass().getName());
            throw new RuntimeException("Error de autenticación con PayPal", e);
        }
    }

    public Map<String, String> crearSuscripcion(String usuarioEntrenador) {
        try {
            log.info("✅ Creando suscripción para usuario: {}", usuarioEntrenador);

            // ✅ OBTENER EL CORREO DESDE LA BASE DE DATOS
            var entrenador = entrenadorRepository.findByUsuario(usuarioEntrenador)
                    .orElseThrow(() -> new RuntimeException("Entrenador no encontrado: " + usuarioEntrenador));

            String correo = entrenador.getCorreo();

            // Validar que el entrenador tenga correo
            if (correo == null || correo.trim().isEmpty()) {
                log.error("❌ El entrenador {} no tiene correo registrado", usuarioEntrenador);
                throw new RuntimeException("El entrenador no tiene un correo registrado");
            }

            log.info("📧 Usando correo del entrenador: {}", correo);

            String accessToken = getAccessToken();

            // Crear el objeto principal de la petición usando Gson
            JsonObject requestBody = new JsonObject();
            requestBody.addProperty("plan_id", planPremiumId);

            // Crear el application_context con Deep Links
            JsonObject applicationContext = new JsonObject();
            applicationContext.addProperty("brand_name", "Sportine");
            applicationContext.addProperty("locale", "es-MX");
            applicationContext.addProperty("shipping_preference", "NO_SHIPPING");
            applicationContext.addProperty("user_action", "SUBSCRIBE_NOW");

            // URLs de Deep Link para Android
            applicationContext.addProperty("return_url", "sportine://payment/success");
            applicationContext.addProperty("cancel_url", "sportine://payment/cancel");

            requestBody.add("application_context", applicationContext);

            // ✅ Agregar subscriber con el CORREO REAL de la BD
            JsonObject subscriber = new JsonObject();
            subscriber.addProperty("email_address", correo);  // ✅ Correo real
            requestBody.add("subscriber", subscriber);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + accessToken);

            // Convertir JsonObject a String para enviar
            HttpEntity<String> request = new HttpEntity<>(gson.toJson(requestBody), headers);

            log.info("📤 Creando suscripción en PayPal...");
            log.info("📧 Email del subscriber: {}", correo);
            log.info("🔗 Return URL: sportine://payment/success");
            log.info("🔗 Cancel URL: sportine://payment/cancel");

            ResponseEntity<String> response = restTemplate.postForEntity(
                    getBaseUrl() + "/v1/billing/subscriptions",
                    request,
                    String.class
            );

            log.info("✅ Suscripción creada exitosamente");

            JsonObject jsonResponse = gson.fromJson(response.getBody(), JsonObject.class);
            String subscriptionId = jsonResponse.get("id").getAsString();

            String approvalUrl = null;
            var links = jsonResponse.getAsJsonArray("links");
            for (var link : links) {
                JsonObject linkObj = link.getAsJsonObject();
                if ("approve".equals(linkObj.get("rel").getAsString())) {
                    approvalUrl = linkObj.get("href").getAsString();
                    break;
                }
            }

            log.info("✅ Subscription ID: {}", subscriptionId);
            log.info("✅ Approval URL: {}", approvalUrl);

            Map<String, String> result = new HashMap<>();
            result.put("subscription_id", subscriptionId);
            result.put("approval_url", approvalUrl);

            return result;

        } catch (HttpClientErrorException e) {
            log.error("❌ Error HTTP de PayPal: status={}", e.getStatusCode());
            log.error("❌ Response body: {}", e.getResponseBodyAsString());
            throw new RuntimeException("Error creando suscripción en PayPal: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("❌ Error creando suscripción: {}", e.getMessage());
            throw new RuntimeException("Error creando suscripción", e);
        }
    }

    public JsonObject obtenerDetallesSuscripcion(String subscriptionId) {
        try {
            String accessToken = getAccessToken();

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + accessToken);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> request = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    getBaseUrl() + "/v1/billing/subscriptions/" + subscriptionId,
                    HttpMethod.GET,
                    request,
                    String.class
            );

            return gson.fromJson(response.getBody(), JsonObject.class);

        } catch (Exception e) {
            log.error("Error obteniendo detalles de suscripción: {}", e.getMessage());
            throw new RuntimeException("Error verificando suscripción", e);
        }
    }

    public boolean cancelarSuscripcion(String subscriptionId, String razon) {
        try {
            String accessToken = getAccessToken();

            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("reason", razon);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + accessToken);

            HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(
                    getBaseUrl() + "/v1/billing/subscriptions/" + subscriptionId + "/cancel",
                    request,
                    String.class
            );

            return response.getStatusCode() == HttpStatus.NO_CONTENT;

        } catch (Exception e) {
            log.error("Error cancelando suscripción: {}", e.getMessage());
            return false;
        }
    }
}