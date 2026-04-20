package com.sportine.backend.controler;

import com.sportine.backend.service.SuscripcionAlumnoEntrenadorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v2/estudiante/suscripcion")
@RequiredArgsConstructor
public class AlumnoSuscripcionController {

    private final SuscripcionAlumnoEntrenadorService suscripcionService;

    @Value("${sportine.base-url}")
    private String sportineBaseUrl;

    // Agrega en application.properties:  sportine.frontend-url=http://localhost:5500
    @Value("${sportine.frontend-url:http://localhost:5500}")
    private String frontendUrl;

    // ── 1. CREAR SUSCRIPCIÓN ─────────────────────────────────────────────────
    @PostMapping("/crear")
    public ResponseEntity<Map<String, Object>> crearSuscripcion(
            @RequestParam String usuarioEstudiante,
            @RequestParam String usuarioEntrenador,
            @RequestParam Integer idDeporte,
            @RequestParam(required = false, defaultValue = "android") String source) {

        String returnUrl;
        String cancelUrl;

        if ("web".equals(source)) {
            returnUrl = sportineBaseUrl + "/api/v2/estudiante/suscripcion/pago/success"
                    + "?source=web&entrenador=" + usuarioEntrenador;
            cancelUrl = sportineBaseUrl + "/api/v2/estudiante/suscripcion/pago/cancel"
                    + "?source=web&entrenador=" + usuarioEntrenador;
        } else {
            returnUrl = sportineBaseUrl + "/api/v2/estudiante/suscripcion/pago/success";
            cancelUrl = sportineBaseUrl + "/api/v2/estudiante/suscripcion/pago/cancel";
        }

        try {
            log.info("Creando suscripción: Estudiante={}, Entrenador={}, Deporte={}, Source={}",
                    usuarioEstudiante, usuarioEntrenador, idDeporte, source);

            if (suscripcionService.tieneSuscripcionActiva(usuarioEstudiante, usuarioEntrenador, idDeporte)) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Ya tienes una suscripción activa con este entrenador");
                return ResponseEntity.status(400).body(errorResponse);
            }

            Map<String, String> paypalResponse = suscripcionService.crearSuscripcion(
                    usuarioEstudiante, usuarioEntrenador, idDeporte, returnUrl, cancelUrl);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("order_id", paypalResponse.get("order_id"));
            response.put("approval_url", paypalResponse.get("approval_url"));
            response.put("message", "Suscripción creada.");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error creando suscripción: {}", e.getMessage(), e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error al crear suscripción: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    // ── 2. CALLBACK ÉXITO PAYPAL ─────────────────────────────────────────────
    @GetMapping("/pago/success")
    public ResponseEntity<Void> pagoSuccess(
            @RequestParam(required = false) String token,
            @RequestParam(required = false) String PayerID,
            @RequestParam(required = false, defaultValue = "android") String source,
            @RequestParam(required = false) String entrenador) {

        log.info("Callback pago exitoso - Token: {}, Source: {}, Entrenador: {}", token, source, entrenador);

        if ("web".equals(source)) {
            String location = frontendUrl + "/pages/alumno/ver-entrenador.html"
                    + "?usuario=" + (entrenador != null ? entrenador : "")
                    + "&token=" + (token != null ? token : "")
                    + "&PayerID=" + (PayerID != null ? PayerID : "");

            log.info("Redirigiendo a frontend: {}", location);

            return ResponseEntity.status(302)
                    .header("Location", location)
                    .build();
        } else {
            return ResponseEntity.status(302)
                    .header("Location", "sportine://payment/success?token=" + token + "&PayerID=" + PayerID)
                    .build();
        }
    }

    // ── 3. CALLBACK CANCELACIÓN PAYPAL ───────────────────────────────────────
    @GetMapping("/pago/cancel")
    public ResponseEntity<Void> pagoCancel(
            @RequestParam(required = false) String token,
            @RequestParam(required = false, defaultValue = "android") String source,
            @RequestParam(required = false) String entrenador) {

        log.warn("Callback pago cancelado - Token: {}, Source: {}", token, source);

        if ("web".equals(source)) {
            String location = frontendUrl + "/pages/alumno/ver-entrenador.html"
                    + "?usuario=" + (entrenador != null ? entrenador : "")
                    + "&payment_cancelled=true";

            return ResponseEntity.status(302)
                    .header("Location", location)
                    .build();
        } else {
            return ResponseEntity.status(302)
                    .header("Location", "sportine://payment/cancel")
                    .build();
        }
    }

    // ── 4. CONFIRMAR PAGO ────────────────────────────────────────────────────
    @PostMapping("/confirmar")
    public ResponseEntity<Map<String, Object>> confirmarSuscripcion(
            @RequestParam String token,
            @RequestParam(required = false) String payerId) {

        log.info("Confirmando suscripción - Token: {}, PayerID: {}", token, payerId);

        try {
            suscripcionService.confirmarSuscripcion(token, payerId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Suscripción confirmada y activada correctamente.");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error confirmando suscripción: {}", e.getMessage(), e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error al confirmar suscripción: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    // ── 5. CANCELAR SUSCRIPCIÓN ──────────────────────────────────────────────
    // El service firma es: cancelarSuscripcion(Integer idSuscripcion, String motivo)
    // El frontend manda el idSuscripcion directamente
    @PostMapping("/cancelar")
    public ResponseEntity<Map<String, Object>> cancelarSuscripcion(
            @RequestParam Integer idSuscripcion,
            @RequestParam(required = false, defaultValue = "Cancelada por el usuario") String motivo) {

        log.info("Cancelando suscripción id={}", idSuscripcion);

        try {
            suscripcionService.cancelarSuscripcion(idSuscripcion, motivo);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Suscripción cancelada correctamente.");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error cancelando suscripción: {}", e.getMessage(), e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error al cancelar suscripción: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
}