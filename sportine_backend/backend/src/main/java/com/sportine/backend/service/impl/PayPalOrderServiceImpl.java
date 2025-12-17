package com.sportine.backend.service.impl;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sportine.backend.model.AlumnoSuscripcionEntrenador;
import com.sportine.backend.model.InformacionEntrenador;
import com.sportine.backend.repository.AlumnoSuscripcionRepository;
import com.sportine.backend.repository.InformacionEntrenadorRepository;
import com.sportine.backend.repository.UsuarioRepository;
import com.sportine.backend.service.PayPalOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class PayPalOrderServiceImpl implements PayPalOrderService {

    private final InformacionEntrenadorRepository entrenadorRepository;
    private final UsuarioRepository usuarioRepository;
    private final AlumnoSuscripcionRepository suscripcionRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    private final Gson gson = new Gson();

    @Value("${paypal.client-id}")
    private String clientId;

    @Value("${paypal.client-secret}")
    private String clientSecret;

    @Value("${paypal.mode}")
    private String mode;

    @Value("${paypal.partner-merchant-id}")
    private String sportineMerchantId;

    @Value("${sportine.base-url}")
    private String sportineBaseUrl;

    @Value("${sportine.comision-porcentaje:10.00}")
    private Double comisionPorcentaje;

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
            throw new RuntimeException("Error de autenticación con PayPal", e);
        }
    }

    @Override
    public Map<String, String> crearOrdenMultiparty(String usuarioEstudiante, String usuarioEntrenador,
                                                    Integer idDeporte, Double montoTotal) {
        try {
            log.info("Creando orden multiparty: Estudiante={}, Entrenador={}, Monto={}",
                    usuarioEstudiante, usuarioEntrenador, montoTotal);

            // Verificar que el entrenador esté onboarded
            InformacionEntrenador entrenador = entrenadorRepository.findByUsuario(usuarioEntrenador)
                    .orElseThrow(() -> new RuntimeException("Entrenador no encontrado"));

            if (entrenador.getMerchantId() == null ||
                    entrenador.getOnboardingStatus() != InformacionEntrenador.OnboardingStatus.completed) {
                throw new RuntimeException("El entrenador no ha completado el onboarding de PayPal");
            }

            // Calcular montos
            BigDecimal total = new BigDecimal(montoTotal).setScale(2, RoundingMode.HALF_UP);
            BigDecimal comision = total.multiply(new BigDecimal(comisionPorcentaje))
                    .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
            BigDecimal montoEntrenador = total.subtract(comision);

            log.info("Cálculo de montos: Total={}, Comisión Sportine={}, Entrenador={}",
                    total, comision, montoEntrenador);

            String accessToken = getAccessToken();

            // Construir request body
            JsonObject requestBody = new JsonObject();
            requestBody.addProperty("intent", "CAPTURE");

            // Purchase units
            JsonObject purchaseUnit = new JsonObject();
            purchaseUnit.addProperty("reference_id", "SPORTINE-TRAINING-" + System.currentTimeMillis());
            purchaseUnit.addProperty("description", "Mensualidad de entrenamiento - " + usuarioEntrenador);

            // Amount
            JsonObject amount = new JsonObject();
            amount.addProperty("currency_code", "MXN");
            amount.addProperty("value", total.toString());

            JsonObject breakdown = new JsonObject();
            JsonObject itemTotal = new JsonObject();
            itemTotal.addProperty("currency_code", "MXN");
            itemTotal.addProperty("value", total.toString());
            breakdown.add("item_total", itemTotal);
            amount.add("breakdown", breakdown);

            purchaseUnit.add("amount", amount);

            // Payee (entrenador)
            JsonObject payee = new JsonObject();
            payee.addProperty("merchant_id", entrenador.getMerchantId());
            purchaseUnit.add("payee", payee);

            // Payment instruction con platform fee
            JsonObject paymentInstruction = new JsonObject();
            paymentInstruction.addProperty("disbursement_mode", "INSTANT");

            JsonObject platformFee = new JsonObject();
            JsonObject feeAmount = new JsonObject();
            feeAmount.addProperty("currency_code", "MXN");
            feeAmount.addProperty("value", comision.toString());
            platformFee.add("amount", feeAmount);

            JsonObject feePayee = new JsonObject();
            feePayee.addProperty("merchant_id", sportineMerchantId);
            platformFee.add("payee", feePayee);

            paymentInstruction.add("platform_fees", gson.toJsonTree(new Object[]{gson.fromJson(platformFee, Object.class)}));
            purchaseUnit.add("payment_instruction", paymentInstruction);

            requestBody.add("purchase_units", gson.toJsonTree(new Object[]{gson.fromJson(purchaseUnit, Object.class)}));

            // Application context
            JsonObject appContext = new JsonObject();
            appContext.addProperty("brand_name", "Sportine");
            appContext.addProperty("locale", "es-MX");
            appContext.addProperty("landing_page", "BILLING");
            appContext.addProperty("shipping_preference", "NO_SHIPPING");
            appContext.addProperty("user_action", "PAY_NOW");
            appContext.addProperty("return_url", sportineBaseUrl + "/api/v2/estudiante/pago/success");
            appContext.addProperty("cancel_url", sportineBaseUrl + "/api/v2/estudiante/pago/cancel");
            requestBody.add("application_context", appContext);

            // Hacer llamada a PayPal
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + accessToken);

            HttpEntity<String> request = new HttpEntity<>(gson.toJson(requestBody), headers);

            log.info("Enviando orden a PayPal...");

            ResponseEntity<String> response = restTemplate.postForEntity(
                    getBaseUrl() + "/v2/checkout/orders",
                    request,
                    String.class
            );

            JsonObject jsonResponse = gson.fromJson(response.getBody(), JsonObject.class);
            String orderId = jsonResponse.get("id").getAsString();

            // Extraer approval URL
            String approvalUrl = null;
            var links = jsonResponse.getAsJsonArray("links");
            for (var link : links) {
                JsonObject linkObj = link.getAsJsonObject();
                if ("approve".equals(linkObj.get("rel").getAsString())) {
                    approvalUrl = linkObj.get("href").getAsString();
                    break;
                }
            }

            log.info("✅ Orden creada exitosamente");
            log.info("Order ID: {}", orderId);
            log.info("Approval URL: {}", approvalUrl);

            Map<String, String> result = new HashMap<>();
            result.put("order_id", orderId);
            result.put("approval_url", approvalUrl);

            return result;

        } catch (Exception e) {
            log.error("Error creando orden: {}", e.getMessage(), e);
            throw new RuntimeException("Error al crear orden de pago", e);
        }
    }

    @Override
    public Map<String, Object> capturarOrden(String orderId) {
        try {
            log.info("Capturando orden: {}", orderId);

            String accessToken = getAccessToken();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + accessToken);

            HttpEntity<String> request = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.postForEntity(
                    getBaseUrl() + "/v2/checkout/orders/" + orderId + "/capture",
                    request,
                    String.class
            );

            JsonObject jsonResponse = gson.fromJson(response.getBody(), JsonObject.class);

            Map<String, Object> result = new HashMap<>();
            result.put("id", jsonResponse.get("id").getAsString());
            result.put("status", jsonResponse.get("status").getAsString());

            // Extraer capture ID
            var purchaseUnits = jsonResponse.getAsJsonArray("purchase_units");
            if (purchaseUnits.size() > 0) {
                var firstUnit = purchaseUnits.get(0).getAsJsonObject();
                var payments = firstUnit.getAsJsonObject("payments");
                var captures = payments.getAsJsonArray("captures");
                if (captures.size() > 0) {
                    var firstCapture = captures.get(0).getAsJsonObject();
                    result.put("capture_id", firstCapture.get("id").getAsString());
                    result.put("capture_status", firstCapture.get("status").getAsString());
                }
            }

            log.info("✅ Orden capturada exitosamente: {}", result);

            return result;

        } catch (Exception e) {
            log.error("Error capturando orden: {}", e.getMessage(), e);
            throw new RuntimeException("Error al capturar orden", e);
        }
    }

    @Override
    public Map<String, Object> obtenerDetallesOrden(String orderId) {
        try {
            String accessToken = getAccessToken();

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + accessToken);

            HttpEntity<String> request = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    getBaseUrl() + "/v2/checkout/orders/" + orderId,
                    HttpMethod.GET,
                    request,
                    String.class
            );

            JsonObject details = gson.fromJson(response.getBody(), JsonObject.class);

            Map<String, Object> result = new HashMap<>();
            result.put("id", details.get("id").getAsString());
            result.put("status", details.get("status").getAsString());

            return result;

        } catch (Exception e) {
            log.error("Error obteniendo detalles de orden: {}", e.getMessage(), e);
            throw new RuntimeException("Error al obtener detalles de orden", e);
        }
    }

    @Override
    public String crearOrdenConVault(Integer idSuscripcion) {
        try {
            log.info("Creando orden con Vault para suscripción: {}", idSuscripcion);

            // Obtener la suscripción
            AlumnoSuscripcionEntrenador suscripcion = suscripcionRepository.findById(idSuscripcion)
                    .orElseThrow(() -> new RuntimeException("Suscripción no encontrada"));

            // Verificar que tenga vault_id
            if (suscripcion.getVaultId() == null) {
                throw new RuntimeException("La suscripción no tiene un payment token guardado");
            }

            // Obtener merchant del entrenador
            InformacionEntrenador entrenador = entrenadorRepository.findByUsuario(suscripcion.getUsuarioEntrenador())
                    .orElseThrow(() -> new RuntimeException("Entrenador no encontrado"));

            String accessToken = getAccessToken();

            // Construir orden similar a crearOrdenMultiparty pero con vault
            JsonObject requestBody = new JsonObject();
            requestBody.addProperty("intent", "CAPTURE");

            // Purchase units (igual que antes)
            JsonObject purchaseUnit = new JsonObject();
            purchaseUnit.addProperty("reference_id", "SPORTINE-RECURRENT-" + idSuscripcion);
            purchaseUnit.addProperty("description", "Pago mensual recurrente");

            JsonObject amount = new JsonObject();
            amount.addProperty("currency_code", suscripcion.getMoneda());
            amount.addProperty("value", suscripcion.getMontoTotal().toString());
            purchaseUnit.add("amount", amount);

            JsonObject payee = new JsonObject();
            payee.addProperty("merchant_id", entrenador.getMerchantId());
            purchaseUnit.add("payee", payee);

            // Platform fee
            JsonObject paymentInstruction = new JsonObject();
            paymentInstruction.addProperty("disbursement_mode", "INSTANT");

            JsonObject platformFee = new JsonObject();
            JsonObject feeAmount = new JsonObject();
            feeAmount.addProperty("currency_code", suscripcion.getMoneda());
            feeAmount.addProperty("value", suscripcion.getMontoComisionSportine().toString());
            platformFee.add("amount", feeAmount);

            JsonObject feePayee = new JsonObject();
            feePayee.addProperty("merchant_id", sportineMerchantId);
            platformFee.add("payee", feePayee);

            paymentInstruction.add("platform_fees", gson.toJsonTree(new Object[]{gson.fromJson(platformFee, Object.class)}));
            purchaseUnit.add("payment_instruction", paymentInstruction);

            requestBody.add("purchase_units", gson.toJsonTree(new Object[]{gson.fromJson(purchaseUnit, Object.class)}));

            // Payment source con vault
            JsonObject paymentSource = new JsonObject();
            JsonObject paypalPayment = new JsonObject();
            JsonObject vaultId = new JsonObject();
            vaultId.addProperty("id", suscripcion.getVaultId());
            paypalPayment.add("vault_id", vaultId);
            paymentSource.add("paypal", paypalPayment);
            requestBody.add("payment_source", paymentSource);

            // Hacer llamada
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + accessToken);

            HttpEntity<String> request = new HttpEntity<>(gson.toJson(requestBody), headers);

            ResponseEntity<String> response = restTemplate.postForEntity(
                    getBaseUrl() + "/v2/checkout/orders",
                    request,
                    String.class
            );

            JsonObject jsonResponse = gson.fromJson(response.getBody(), JsonObject.class);
            String orderId = jsonResponse.get("id").getAsString();

            log.info("✅ Orden con Vault creada: {}", orderId);

            return orderId;

        } catch (Exception e) {
            log.error("Error creando orden con Vault: {}", e.getMessage(), e);
            throw new RuntimeException("Error al crear orden con Vault", e);
        }
    }
}