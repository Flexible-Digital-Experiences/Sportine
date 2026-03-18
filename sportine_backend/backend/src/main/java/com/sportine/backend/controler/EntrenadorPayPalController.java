package com.sportine.backend.controler;

import com.sportine.backend.service.PayPalPlatformPartnerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v2/entrenador/paypal")
@RequiredArgsConstructor
@Slf4j
public class EntrenadorPayPalController {

    private final PayPalPlatformPartnerService paypalPartnerService;

    /**
     * Endpoint 1: Iniciar onboarding
     * Android llama esto cuando el entrenador quiere conectar su cuenta PayPal
     */
    @PostMapping("/onboarding/iniciar")
    public ResponseEntity<Map<String, Object>> iniciarOnboarding(
            @RequestParam String usuario) {

        try {
            log.info("Iniciando onboarding para entrenador: {}", usuario);

            Map<String, String> resultado = paypalPartnerService.iniciarOnboarding(usuario);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("tracking_id", resultado.get("tracking_id"));
            response.put("onboarding_url", resultado.get("onboarding_url"));
            response.put("message", "Onboarding iniciado. Redirigir al entrenador a PayPal.");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error iniciando onboarding: {}", e.getMessage(), e);

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error al iniciar onboarding: " + e.getMessage());

            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @GetMapping("/onboarding/success")
    public ResponseEntity<Map<String, Object>> onboardingSuccess(
            @RequestParam(required = false) String merchantId,
            @RequestParam(required = false) String merchantIdInPayPal,
            @RequestParam(required = false) String permissionsGranted,
            @RequestParam(required = false) String consentStatus,
            @RequestParam(required = false) String isEmailConfirmed,
            @RequestParam(required = false) String accountStatus,
            @RequestHeader(value = "User-Agent", defaultValue = "") String userAgent) {

        try {
            log.info("Callback de onboarding recibido - merchantId: {}, userAgent: {}", merchantId, userAgent);

            if (merchantId == null || merchantIdInPayPal == null) {
                throw new RuntimeException("Faltan parámetros requeridos del onboarding");
            }

            // Verificar y completar onboarding
            Map<String, Object> detalles = paypalPartnerService.verificarOnboarding(merchantId, null);
            String trackingId = (String) detalles.get("tracking_id");

            boolean emailConfirmed = "true".equalsIgnoreCase(isEmailConfirmed);
            paypalPartnerService.completarOnboarding(merchantId, merchantIdInPayPal, trackingId, emailConfirmed);

            // Detectar si viene desde Android o web
            boolean esAndroid = userAgent.toLowerCase().contains("android");

            if (esAndroid) {
                // Redirigir a deep link de la app
                log.info("Redirigiendo a app Android via deep link");
                return ResponseEntity.status(302)
                        .header("Location", "sportine://onboarding/success")
                        .build();
            } else {
                // Redirigir a página web (cuando exista), por ahora JSON
                log.info("Redirigiendo a web");
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "¡Onboarding completado exitosamente!");
                response.put("merchant_id", merchantId);
                return ResponseEntity.ok(response);
            }

        } catch (Exception e) {
            log.error("Error en callback de onboarding: {}", e.getMessage(), e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error completando onboarding: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    /**
     * Endpoint 3: Verificar si el entrenador puede recibir pagos
     */
    @GetMapping("/puede-recibir-pagos")
    public ResponseEntity<Map<String, Object>> puedeRecibirPagos(
            @RequestParam String usuario) {

        try {
            boolean puedeRecibir = paypalPartnerService.puedeRecibirPagos(usuario);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("puede_recibir_pagos", puedeRecibir);

            if (!puedeRecibir) {
                response.put("message", "El entrenador debe completar el onboarding de PayPal primero");
            } else {
                response.put("message", "El entrenador está listo para recibir pagos");
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error verificando capacidad de recibir pagos: {}", e.getMessage());

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error verificando estado");

            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    /**
     * Endpoint 4: Obtener detalles del merchant
     */
    @GetMapping("/merchant-details")
    public ResponseEntity<Map<String, Object>> obtenerDetallesMerchant(
            @RequestParam String usuario) {

        try {
            // Primero necesitamos obtener el merchant_id del entrenador
            // (deberías tener un método en InformacionEntrenadorRepository para esto)

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Detalles obtenidos exitosamente");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error obteniendo detalles: {}", e.getMessage());

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error obteniendo detalles");

            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    /**
     * Endpoint 5: Callback para acción requerida
     * PayPal redirige aquí si necesita alguna acción adicional del usuario
     */
    @GetMapping("/onboarding/action-required")
    public ResponseEntity<Map<String, Object>> onboardingActionRequired(
            @RequestParam(required = false) String merchantId) {

        log.warn("Acción requerida para merchant: {}", merchantId);

        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", "Se requiere acción adicional. Por favor contacta a soporte.");
        response.put("merchant_id", merchantId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/verificar-onboarding")
    public ResponseEntity<Map<String, Object>> verificarOnboarding(
            @RequestParam String usuario) {

        try {
            log.info("Verificando onboarding manualmente para: {}", usuario);

            Map<String, Object> resultado = paypalPartnerService
                    .verificarYCompletarPorTrackingId(usuario);

            boolean completado = Boolean.TRUE.equals(resultado.get("completado"));

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("completado", completado);
            response.put("puede_recibir_pagos", completado);
            response.put("message", resultado.get("mensaje"));

            if (completado) {
                response.put("merchant_id", resultado.get("merchant_id"));
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error verificando onboarding: {}", e.getMessage());

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("completado", false);
            errorResponse.put("puede_recibir_pagos", false);
            errorResponse.put("message", "Error al verificar: " + e.getMessage());

            return ResponseEntity.status(500).body(errorResponse);
        }
    }
}