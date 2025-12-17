package com.sportine.backend.service.impl;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sportine.backend.model.InformacionEntrenador;
import com.sportine.backend.repository.InformacionEntrenadorRepository;
import com.sportine.backend.service.PayPalPlatformPartnerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class PayPalPlatformPartnerServiceImpl implements PayPalPlatformPartnerService {

    private final InformacionEntrenadorRepository entrenadorRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    private final Gson gson = new Gson();

    @Value("${paypal.client-id}")
    private String clientId;

    @Value("${paypal.client-secret}")
    private String clientSecret;

    @Value("${paypal.mode}")
    private String mode;

    @Value("${paypal.partner-merchant-id}")
    private String partnerMerchantId;

    @Value("${sportine.base-url}")
    private String sportineBaseUrl;

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
            log.error("Error obteniendo token de PayPal: {}", e.getMessage());
            throw new RuntimeException("Error de autenticación con PayPal", e);
        }
    }

    @Override
    public Map<String, String> iniciarOnboarding(String usuario) {
        try {
            log.info("Iniciando onboarding para entrenador: {}", usuario);

            // Verificar que el entrenador existe
            InformacionEntrenador entrenador = entrenadorRepository.findByUsuario(usuario)
                    .orElseThrow(() -> new RuntimeException("Entrenador no encontrado"));

            // Verificar que no esté ya onboarded
            if (entrenador.getOnboardingStatus() == InformacionEntrenador.OnboardingStatus.completed) {
                throw new RuntimeException("El entrenador ya completó el onboarding");
            }

            // Generar tracking ID único
            String trackingId = "SPORTINE-" + usuario + "-" + System.currentTimeMillis();

            String accessToken = getAccessToken();

            // Crear request body para Partner Referrals API
            JsonObject requestBody = new JsonObject();
            requestBody.addProperty("tracking_id", trackingId);

            // Operations
            JsonObject apiIntegrationPreference = new JsonObject();
            JsonObject restApiIntegration = new JsonObject();
            restApiIntegration.addProperty("integration_method", "PAYPAL");
            restApiIntegration.addProperty("integration_type", "THIRD_PARTY");

            JsonObject thirdPartyDetails = new JsonObject();
            thirdPartyDetails.add("features", gson.toJsonTree(new String[]{"PAYMENT", "REFUND", "PARTNER_FEE"}));
            restApiIntegration.add("third_party_details", thirdPartyDetails);

            apiIntegrationPreference.add("rest_api_integration", restApiIntegration);

            JsonObject operation = new JsonObject();
            operation.addProperty("operation", "API_INTEGRATION");
            operation.add("api_integration_preference", apiIntegrationPreference);

            requestBody.add("operations", gson.toJsonTree(new Object[]{gson.fromJson(operation, Object.class)}));

            // Products
            requestBody.add("products", gson.toJsonTree(new String[]{"EXPRESS_CHECKOUT"}));

            // Legal consents
            JsonObject legalConsent = new JsonObject();
            legalConsent.addProperty("type", "SHARE_DATA_CONSENT");
            legalConsent.addProperty("granted", true);
            requestBody.add("legal_consents", gson.toJsonTree(new Object[]{gson.fromJson(legalConsent, Object.class)}));

            // Partner config override (URLs de retorno)
            JsonObject partnerConfig = new JsonObject();
            partnerConfig.addProperty("return_url", sportineBaseUrl + "/api/v2/entrenador/paypal/onboarding/success");
            partnerConfig.addProperty("return_url_description", "Regresa a Sportine después de conectar PayPal");
            partnerConfig.addProperty("action_renewal_url", sportineBaseUrl + "/api/v2/entrenador/paypal/onboarding/action-required");
            requestBody.add("partner_config_override", partnerConfig);

            // Hacer llamada a PayPal
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + accessToken);

            HttpEntity<String> request = new HttpEntity<>(gson.toJson(requestBody), headers);

            log.info("Llamando a PayPal Partner Referrals API...");

            ResponseEntity<String> response = restTemplate.postForEntity(
                    getBaseUrl() + "/v2/customer/partner-referrals",
                    request,
                    String.class
            );

            JsonObject jsonResponse = gson.fromJson(response.getBody(), JsonObject.class);

            // Extraer action_url (link de onboarding)
            String onboardingUrl = null;
            var links = jsonResponse.getAsJsonArray("links");
            for (var link : links) {
                JsonObject linkObj = link.getAsJsonObject();
                if ("action_url".equals(linkObj.get("rel").getAsString())) {
                    onboardingUrl = linkObj.get("href").getAsString();
                    break;
                }
            }

            if (onboardingUrl == null) {
                throw new RuntimeException("No se pudo obtener el link de onboarding");
            }

            // Guardar en BD
            entrenador.setTrackingId(trackingId);
            entrenador.setOnboardingLink(onboardingUrl);
            entrenador.setOnboardingStatus(InformacionEntrenador.OnboardingStatus.pending);
            entrenadorRepository.save(entrenador);

            log.info("✅ Onboarding iniciado exitosamente");
            log.info("Tracking ID: {}", trackingId);
            log.info("Onboarding URL: {}", onboardingUrl);

            Map<String, String> result = new HashMap<>();
            result.put("tracking_id", trackingId);
            result.put("onboarding_url", onboardingUrl);

            return result;

        } catch (Exception e) {
            log.error("Error iniciando onboarding: {}", e.getMessage(), e);
            throw new RuntimeException("Error al iniciar onboarding con PayPal", e);
        }
    }

    @Override
    public Map<String, Object> verificarOnboarding(String merchantId, String trackingId) {
        try {
            log.info("Verificando onboarding - Merchant ID: {}, Tracking ID: {}", merchantId, trackingId);

            String accessToken = getAccessToken();

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + accessToken);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> request = new HttpEntity<>(headers);

            // Llamar a Merchant Integrations API
            String url = getBaseUrl() + "/v1/customer/partners/" + partnerMerchantId +
                    "/merchant-integrations/" + merchantId;

            log.info("Verificando en: {}", url);

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    request,
                    String.class
            );

            JsonObject merchantDetails = gson.fromJson(response.getBody(), JsonObject.class);

            Map<String, Object> result = new HashMap<>();
            result.put("merchant_id", merchantDetails.get("merchant_id").getAsString());
            result.put("tracking_id", merchantDetails.get("tracking_id").getAsString());
            result.put("payments_receivable", merchantDetails.get("payments_receivable").getAsBoolean());
            result.put("primary_email_confirmed", merchantDetails.get("primary_email_confirmed").getAsBoolean());

            log.info("✅ Onboarding verificado exitosamente");

            return result;

        } catch (Exception e) {
            log.error("Error verificando onboarding: {}", e.getMessage(), e);
            throw new RuntimeException("Error al verificar onboarding", e);
        }
    }

    @Override
    public void completarOnboarding(String merchantId, String merchantIdInPaypal,
                                    String trackingId, boolean isEmailConfirmed) {
        try {
            log.info("Completando onboarding para tracking ID: {}", trackingId);

            // Buscar entrenador por tracking_id
            InformacionEntrenador entrenador = entrenadorRepository.findByTrackingId(trackingId)
                    .orElseThrow(() -> new RuntimeException("No se encontró entrenador con ese tracking ID"));

            // Actualizar información
            entrenador.setMerchantId(merchantId);
            entrenador.setMerchantIdInPaypal(merchantIdInPaypal);
            entrenador.setPaypalEmailConfirmed(isEmailConfirmed ? "true" : "false");
            entrenador.setOnboardingStatus(InformacionEntrenador.OnboardingStatus.completed);
            entrenador.setFechaOnboarding(LocalDate.now());

            // Guardar permisos (simplificado)
            String permissions = "{\"PAYMENT\": true, \"REFUND\": true, \"PARTNER_FEE\": true}";
            entrenador.setPermissionsGranted(permissions);

            entrenadorRepository.save(entrenador);

            log.info("✅ Onboarding completado para entrenador: {}", entrenador.getUsuario());

        } catch (Exception e) {
            log.error("Error completando onboarding: {}", e.getMessage(), e);
            throw new RuntimeException("Error al completar onboarding", e);
        }
    }

    @Override
    public boolean puedeRecibirPagos(String usuario) {
        try {
            InformacionEntrenador entrenador = entrenadorRepository.findByUsuario(usuario)
                    .orElseThrow(() -> new RuntimeException("Entrenador no encontrado"));

            return entrenador.getOnboardingStatus() == InformacionEntrenador.OnboardingStatus.completed
                    && entrenador.getMerchantId() != null
                    && "true".equals(entrenador.getPaypalEmailConfirmed());

        } catch (Exception e) {
            log.error("Error verificando si puede recibir pagos: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public Map<String, Object> obtenerDetallesMerchant(String merchantId) {
        try {
            String accessToken = getAccessToken();

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + accessToken);

            HttpEntity<String> request = new HttpEntity<>(headers);

            String url = getBaseUrl() + "/v1/customer/partners/" + partnerMerchantId +
                    "/merchant-integrations/" + merchantId;

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    request,
                    String.class
            );

            JsonObject details = gson.fromJson(response.getBody(), JsonObject.class);

            Map<String, Object> result = new HashMap<>();
            result.put("merchant_id", details.get("merchant_id").getAsString());
            result.put("payments_receivable", details.get("payments_receivable").getAsBoolean());
            result.put("primary_email_confirmed", details.get("primary_email_confirmed").getAsBoolean());

            return result;

        } catch (Exception e) {
            log.error("Error obteniendo detalles del merchant: {}", e.getMessage(), e);
            throw new RuntimeException("Error al obtener detalles del merchant", e);
        }
    }
}