package com.sportine.backend.controler;

import com.google.gson.JsonObject;
import com.sportine.backend.service.PayPalSuscripcionService;
import com.sportine.backend.service.PremiumEntrenadorService;
import com.sportine.backend.repository.InformacionEntrenadorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/suscripciones")
@RequiredArgsConstructor
@Slf4j
public class SuscripcionController {

    private final PayPalSuscripcionService payPalService;
    private final InformacionEntrenadorRepository entrenadorRepository;
    private final PremiumEntrenadorService premiumEntrenadorService;

    /**
     * Endpoint 1: Crear suscripción
     * Android llama esto cuando el entrenador quiere upgrade a Premium
     */
    @PostMapping("/crear")
    public ResponseEntity<Map<String, Object>> crearSuscripcion(
            @RequestParam String usuario) {

        try {
            log.info("Creando suscripción para usuario: {}", usuario);

            // Crear suscripción en PayPal
            Map<String, String> paypalResponse = payPalService.crearSuscripcion(usuario);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("subscription_id", paypalResponse.get("subscription_id"));
            response.put("approval_url", paypalResponse.get("approval_url"));
            response.put("message", "Suscripción creada. Redirigir a PayPal.");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error creando suscripción: {}", e.getMessage());

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error al crear suscripción: " + e.getMessage());

            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    /**
     * Endpoint 2: Confirmar suscripción
     * Android llama esto después de que el usuario aprueba en PayPal
     */
    @PostMapping("/confirmar")
    public ResponseEntity<Map<String, Object>> confirmarSuscripcion(
            @RequestParam String subscriptionId,
            @RequestParam String usuario) {

        try {
            log.info("Confirmando suscripción {} para usuario {}", subscriptionId, usuario);

            // Verificar con PayPal que la suscripción existe y está activa
            JsonObject detalles = payPalService.obtenerDetallesSuscripcion(subscriptionId);
            String status = detalles.get("status").getAsString();

            if ("ACTIVE".equals(status) || "APPROVAL_PENDING".equals(status)) {
                // Actualizar base de datos
                entrenadorRepository.actualizarSuscripcion(
                        usuario,
                        subscriptionId,
                        "premium",
                        50, // Alumnos ilimitados
                        LocalDate.now() // Fecha inicio
                );

                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "¡Bienvenido a Premium! Ahora tienes alumnos ilimitados.");
                response.put("tipo_cuenta", "premium");
                response.put("limite_alumnos", 50);

                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "La suscripción no está activa. Estado: " + status);

                return ResponseEntity.status(400).body(errorResponse);
            }

        } catch (Exception e) {
            log.error("Error confirmando suscripción: {}", e.getMessage());

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error al confirmar suscripción");

            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    /**
     * Endpoint 3: Verificar estado de suscripción
     */
    @GetMapping("/estado")
    public ResponseEntity<Map<String, Object>> verificarEstado(
            @RequestParam String usuario) {

        try {
            var entrenador = entrenadorRepository.findByUsuario(usuario)
                    .orElseThrow(() -> new RuntimeException("Entrenador no encontrado"));

            Map<String, Object> response = new HashMap<>();
            response.put("tipo_cuenta", entrenador.getTipoCuenta());
            response.put("subscription_id", entrenador.getSubscriptionId());
            response.put("subscription_status", entrenador.getSubscriptionStatus());
            response.put("limite_alumnos", entrenador.getLimiteAlumnos());
            response.put("fecha_inicio", entrenador.getFechaInicioSuscripcion());
            response.put("fecha_fin", entrenador.getFechaFinSuscripcion());

            // Agregar información de acceso premium
            response.put("tiene_acceso_premium", premiumEntrenadorService.tieneAccesoPremium(usuario));
            response.put("dias_restantes", premiumEntrenadorService.obtenerDiasRestantesPremium(usuario));

            // Si tiene suscripción activa, verificar con PayPal
            if (entrenador.getSubscriptionId() != null) {
                JsonObject detalles = payPalService.obtenerDetallesSuscripcion(
                        entrenador.getSubscriptionId()
                );
                response.put("status_paypal", detalles.get("status").getAsString());
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error verificando estado: {}", e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * Endpoint 4: Cancelar suscripción
     */
    @PostMapping("/cancelar")
    public ResponseEntity<Map<String, Object>> cancelarSuscripcion(
            @RequestParam String usuario,
            @RequestParam(required = false) String razon) {

        try {
            var entrenador = entrenadorRepository.findByUsuario(usuario)
                    .orElseThrow(() -> new RuntimeException("Entrenador no encontrado"));

            if (entrenador.getSubscriptionId() == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "No tienes suscripción activa");
                return ResponseEntity.status(400).body(errorResponse);
            }

            // Cancelar en PayPal
            boolean cancelado = payPalService.cancelarSuscripcion(
                    entrenador.getSubscriptionId(),
                    razon != null ? razon : "Usuario solicitó cancelación"
            );

            if (cancelado) {
                // Actualizar BD (mantener premium hasta fin de ciclo - 30 días después)
                LocalDate fechaFin = LocalDate.now().plusMonths(1);
                entrenadorRepository.marcarSuscripcionCancelada(usuario, fechaFin);

                int diasRestantes = premiumEntrenadorService.obtenerDiasRestantesPremium(usuario);

                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "Suscripción cancelada. Tendrás acceso Premium hasta el fin del ciclo actual.");
                response.put("fecha_fin_acceso", fechaFin);
                response.put("dias_restantes", diasRestantes);

                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Error al cancelar suscripción");
                return ResponseEntity.status(500).body(errorResponse);
            }

        } catch (Exception e) {
            log.error("Error cancelando suscripción: {}", e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/approval-url")
    public ResponseEntity<Map<String, Object>> obtenerApprovalUrl(
            @RequestParam String subscriptionId) {

        try {
            log.info("Obteniendo approval URL para: {}", subscriptionId);

            JsonObject detalles = payPalService.obtenerDetallesSuscripcion(subscriptionId);

            String approvalUrl = null;
            var links = detalles.getAsJsonArray("links");
            for (var link : links) {
                JsonObject linkObj = link.getAsJsonObject();
                if ("approve".equals(linkObj.get("rel").getAsString())) {
                    approvalUrl = linkObj.get("href").getAsString();
                    break;
                }
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("approval_url", approvalUrl);
            response.put("status", detalles.get("status").getAsString());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error obteniendo approval URL: {}", e.getMessage());

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error obteniendo URL");

            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    // ============================================
    // NUEVOS ENDPOINTS - GESTIÓN DE PREMIUM
    // ============================================

    /**
     * Endpoint 5: Verificar acceso premium
     * Útil para validaciones en el frontend
     */
    @GetMapping("/tiene-acceso-premium")
    public ResponseEntity<Map<String, Object>> tieneAccesoPremium(
            @RequestParam String usuario) {

        try {
            boolean tieneAcceso = premiumEntrenadorService.tieneAccesoPremium(usuario);
            int diasRestantes = premiumEntrenadorService.obtenerDiasRestantesPremium(usuario);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("tiene_acceso_premium", tieneAcceso);
            response.put("dias_restantes", diasRestantes);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error verificando acceso premium: {}", e.getMessage());

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error verificando acceso premium");

            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    /**
     * Endpoint 6: Obtener días restantes de premium
     */
    @GetMapping("/dias-restantes")
    public ResponseEntity<Map<String, Object>> obtenerDiasRestantes(
            @RequestParam String usuario) {

        try {
            int diasRestantes = premiumEntrenadorService.obtenerDiasRestantesPremium(usuario);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("dias_restantes", diasRestantes);

            if (diasRestantes == -1) {
                response.put("message", "No tienes suscripción premium");
            } else if (diasRestantes == 0) {
                response.put("message", "Tu suscripción premium ha expirado");
            } else if (diasRestantes == Integer.MAX_VALUE) {
                response.put("message", "Suscripción premium activa");
            } else {
                response.put("message", "Te quedan " + diasRestantes + " días de premium");
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error obteniendo días restantes: {}", e.getMessage());

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error obteniendo días restantes");

            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    /**
     * Endpoint 7: Downgrade manual (solo para admins o testing)
     */
    @PostMapping("/downgrade-manual")
    public ResponseEntity<Map<String, Object>> downgradearManual(
            @RequestParam String usuario) {

        try {
            log.warn("Downgrade manual solicitado para usuario: {}", usuario);

            premiumEntrenadorService.downgradearCoachIndividual(usuario);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Coach downgradeado a plan gratis exitosamente");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error en downgrade manual: {}", e.getMessage());

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error en downgrade: " + e.getMessage());

            return ResponseEntity.status(500).body(errorResponse);
        }
    }
}