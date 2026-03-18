package com.sportine.backend.controler;

import com.sportine.backend.model.AlumnoSuscripcionEntrenador;
import com.sportine.backend.model.HistorialPagosAlumnoEntrenador;
import com.sportine.backend.service.HistorialPagosService;
import com.sportine.backend.service.PayPalOrderService;
import com.sportine.backend.service.SuscripcionAlumnoEntrenadorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v2/estudiante/suscripcion")
@RequiredArgsConstructor
@Slf4j
public class AlumnoSuscripcionController {

    private final SuscripcionAlumnoEntrenadorService suscripcionService;
    private final PayPalOrderService paypalOrderService;
    private final HistorialPagosService historialPagosService;

    @Value("${sportine.base-url}")
    private String sportineBaseUrl;

    @PostMapping("/crear")
    public ResponseEntity<Map<String, Object>> crearSuscripcion(
            @RequestParam String usuarioEstudiante,
            @RequestParam String usuarioEntrenador,
            @RequestParam Integer idDeporte) {

        String returnUrl = sportineBaseUrl + "/api/v2/estudiante/suscripcion/pago/success";
        String cancelUrl = sportineBaseUrl + "/api/v2/estudiante/suscripcion/pago/cancel";

        try {
            log.info("Creando suscripción: Estudiante={}, Entrenador={}, Deporte={}",
                    usuarioEstudiante, usuarioEntrenador, idDeporte);

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
            response.put("message", "Suscripción creada. Redirigir a PayPal para aprobar el pago.");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error creando suscripción: {}", e.getMessage(), e);

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error al crear suscripción: " + e.getMessage());

            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @PostMapping("/confirmar")
    public ResponseEntity<Map<String, Object>> confirmarSuscripcion(
            @RequestParam String orderId,
            @RequestParam(required = false) String vaultId) {

        try {
            log.info("Confirmando suscripción - Order ID: {}", orderId);

            Map<String, Object> captureDetails = paypalOrderService.capturarOrden(orderId);
            String captureStatus = (String) captureDetails.get("capture_status");

            if (!"COMPLETED".equals(captureStatus)) {
                throw new RuntimeException("El pago no fue completado. Estado: " + captureStatus);
            }

            AlumnoSuscripcionEntrenador suscripcion =
                    suscripcionService.confirmarSuscripcion(orderId, vaultId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "¡Suscripción activada exitosamente!");
            response.put("id_suscripcion", suscripcion.getIdSuscripcion());
            response.put("fecha_inicio", suscripcion.getFechaInicioSuscripcion());
            response.put("fecha_proximo_pago", suscripcion.getFechaProximoPago());
            response.put("monto_mensual", suscripcion.getMontoTotal());
            response.put("status", suscripcion.getStatusSuscripcion());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error confirmando suscripción: {}", e.getMessage(), e);

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error al confirmar suscripción: " + e.getMessage());

            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @PostMapping("/cancelar")
    public ResponseEntity<Map<String, Object>> cancelarSuscripcion(
            @RequestParam Integer idSuscripcion,
            @RequestParam(required = false) String motivo) {

        try {
            log.info("Cancelando suscripción ID: {}", idSuscripcion);

            String motivoCancelacion = motivo != null ? motivo : "Cancelada por el estudiante";
            suscripcionService.cancelarSuscripcion(idSuscripcion, motivoCancelacion);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Suscripción cancelada exitosamente");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error cancelando suscripción: {}", e.getMessage(), e);

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error al cancelar suscripción: " + e.getMessage());

            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @GetMapping("/mis-suscripciones")
    public ResponseEntity<Map<String, Object>> obtenerMisSuscripciones(
            @RequestParam String usuarioEstudiante) {

        try {
            List<AlumnoSuscripcionEntrenador> suscripciones =
                    suscripcionService.obtenerSuscripcionesEstudiante(usuarioEstudiante);

            List<Map<String, Object>> suscripcionesDTO = suscripciones.stream()
                    .map(s -> {
                        Map<String, Object> dto = new HashMap<>();
                        dto.put("id_suscripcion", s.getIdSuscripcion());
                        dto.put("usuario_entrenador", s.getUsuarioEntrenador());
                        dto.put("id_deporte", s.getIdDeporte());
                        dto.put("monto_total", s.getMontoTotal());
                        dto.put("status", s.getStatusSuscripcion());
                        dto.put("fecha_inicio", s.getFechaInicioSuscripcion());
                        dto.put("fecha_proximo_pago", s.getFechaProximoPago());
                        return dto;
                    })
                    .collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("suscripciones", suscripcionesDTO);
            response.put("total", suscripcionesDTO.size());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error obteniendo suscripciones: {}", e.getMessage());

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error al obtener suscripciones");

            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @GetMapping("/estudiantes-suscritos")
    public ResponseEntity<Map<String, Object>> obtenerEstudiantesSuscritos(
            @RequestParam String usuarioEntrenador) {

        try {
            List<AlumnoSuscripcionEntrenador> suscripciones =
                    suscripcionService.obtenerSuscripcionesEntrenador(usuarioEntrenador);

            List<Map<String, Object>> estudiantesDTO = suscripciones.stream()
                    .filter(s -> s.getStatusSuscripcion() == AlumnoSuscripcionEntrenador.StatusSuscripcion.active)
                    .map(s -> {
                        Map<String, Object> dto = new HashMap<>();
                        dto.put("id_suscripcion", s.getIdSuscripcion());
                        dto.put("usuario_estudiante", s.getUsuarioEstudiante());
                        dto.put("id_deporte", s.getIdDeporte());
                        dto.put("monto_mensual", s.getMontoTotal());
                        dto.put("fecha_inicio", s.getFechaInicioSuscripcion());
                        dto.put("fecha_proximo_pago", s.getFechaProximoPago());
                        return dto;
                    })
                    .collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("estudiantes", estudiantesDTO);
            response.put("total", estudiantesDTO.size());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error obteniendo estudiantes: {}", e.getMessage());

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error al obtener estudiantes");

            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @GetMapping("/historial-pagos")
    public ResponseEntity<Map<String, Object>> obtenerHistorialPagos(
            @RequestParam Integer idSuscripcion) {

        try {
            List<HistorialPagosAlumnoEntrenador> historial =
                    historialPagosService.obtenerHistorialPorSuscripcion(idSuscripcion);

            List<Map<String, Object>> historialDTO = historial.stream()
                    .map(p -> {
                        Map<String, Object> dto = new HashMap<>();
                        dto.put("id_pago", p.getIdPago());
                        dto.put("monto_total", p.getMontoTotal());
                        dto.put("status", p.getStatusPago());
                        dto.put("fecha_pago", p.getFechaPago());
                        dto.put("transaction_id", p.getPaypalTransactionId());
                        return dto;
                    })
                    .collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("historial", historialDTO);
            response.put("total_pagos", historialDTO.size());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error obteniendo historial: {}", e.getMessage());

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error al obtener historial");

            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @GetMapping("/estado")
    public ResponseEntity<Map<String, Object>> verificarEstado(
            @RequestParam Integer idSuscripcion) {

        try {
            AlumnoSuscripcionEntrenador suscripcion =
                    suscripcionService.obtenerSuscripcionesEstudiante("")
                            .stream()
                            .filter(s -> s.getIdSuscripcion().equals(idSuscripcion))
                            .findFirst()
                            .orElseThrow(() -> new RuntimeException("Suscripción no encontrada"));

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("id_suscripcion", suscripcion.getIdSuscripcion());
            response.put("status", suscripcion.getStatusSuscripcion());
            response.put("fecha_proximo_pago", suscripcion.getFechaProximoPago());
            response.put("monto_mensual", suscripcion.getMontoTotal());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error verificando estado: {}", e.getMessage());

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error al verificar estado");

            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @GetMapping("/pago/success")
    public ResponseEntity<Void> pagoSuccess(
            @RequestParam(required = false) String token,
            @RequestParam(required = false) String PayerID) {

        log.info("Callback de pago exitoso - Token: {}, PayerID: {}", token, PayerID);
        return ResponseEntity.status(302)
                .header("Location", "sportine://payment/success?token=" + token + "&PayerID=" + PayerID)
                .header("ngrok-skip-browser-warning", "true")
                .build();
    }

    @GetMapping("/pago/cancel")
    public ResponseEntity<Void> pagoCancel(
            @RequestParam(required = false) String token) {

        log.warn("Callback de pago cancelado - Token: {}", token);
        return ResponseEntity.status(302)
                .header("Location", "sportine://payment/cancel")
                .header("ngrok-skip-browser-warning", "true")
                .build();
    }

    @PostMapping("/cancelar-por-usuario")
    public ResponseEntity<Map<String, Object>> cancelarPorUsuario(
            @RequestParam String usuarioEstudiante,
            @RequestParam String usuarioEntrenador,
            @RequestParam Integer idDeporte,
            @RequestParam(required = false) String motivo) {

        try {
            log.info("Cancelando suscripción: Estudiante={}, Entrenador={}",
                    usuarioEstudiante, usuarioEntrenador);

            List<AlumnoSuscripcionEntrenador> suscripciones =
                    suscripcionService.obtenerSuscripcionesEstudiante(usuarioEstudiante);

            AlumnoSuscripcionEntrenador suscripcion = suscripciones.stream()
                    .filter(s -> s.getUsuarioEntrenador().equals(usuarioEntrenador)
                            && s.getIdDeporte().equals(idDeporte)
                            && s.getStatusSuscripcion() == AlumnoSuscripcionEntrenador.StatusSuscripcion.active)
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("No tienes una suscripción activa con este entrenador"));

            String motivoCancelacion = motivo != null ? motivo : "Cancelada por el estudiante";
            suscripcionService.cancelarSuscripcion(suscripcion.getIdSuscripcion(), motivoCancelacion);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Suscripción cancelada. Tu acceso continúa hasta " +
                    suscripcion.getFechaProximoPago());
            response.put("fecha_fin", suscripcion.getFechaProximoPago());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error cancelando suscripción: {}", e.getMessage(), e);

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
}