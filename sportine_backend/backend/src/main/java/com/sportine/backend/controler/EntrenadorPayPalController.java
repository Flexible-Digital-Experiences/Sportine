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

    /**
     * Endpoint 2: Callback después del onboarding
     * PayPal redirige aquí después de que el entrenador completa el proceso
     *
     * URL: /api/v2/entrenador/paypal/onboarding/success?merchantId=XXX&merchantIdInPayPal=YYY&...
     */
    @GetMapping("/onboarding/success")
    public ResponseEntity<Map<String, Object>> onboardingSuccess(
            @RequestParam(required = false) String merchantId,
            @RequestParam(required = false) String merchantIdInPayPal,
            @RequestParam(required = false) String permissionsGranted,
            @RequestParam(required = false) String consentStatus,
            @RequestParam(required = false) String productIntentId,
            @RequestParam(required = false) String isEmailConfirmed,
            @RequestParam(required = false) String accountStatus) {

        try {
            log.info("========================================");
            log.info("Callback de onboarding recibido");
            log.info("merchantId: {}", merchantId);
            log.info("merchantIdInPayPal: {}", merchantIdInPayPal);
            log.info("permissionsGranted: {}", permissionsGranted);
            log.info("isEmailConfirmed: {}", isEmailConfirmed);
            log.info("========================================");

            if (merchantId == null || merchantIdInPayPal == null) {
                throw new RuntimeException("Faltan parámetros requeridos del onboarding");
            }

            // Verificar onboarding con PayPal
            Map<String, Object> detalles = paypalPartnerService.verificarOnboarding(merchantId, null);
            String trackingId = (String) detalles.get("tracking_id");

            // Completar onboarding en nuestra BD
            boolean emailConfirmed = "true".equalsIgnoreCase(isEmailConfirmed);
            paypalPartnerService.completarOnboarding(merchantId, merchantIdInPayPal, trackingId, emailConfirmed);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "¡Onboarding completado exitosamente! Ya puedes recibir pagos de tus estudiantes.");
            response.put("merchant_id", merchantId);
            response.put("puede_recibir_pagos", detalles.get("payments_receivable"));

            // Aquí podrías redirigir al usuario a una página de éxito en tu app
            // return ResponseEntity.status(302).header("Location", "sportine://onboarding/success").build();

            return ResponseEntity.ok(response);

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
}