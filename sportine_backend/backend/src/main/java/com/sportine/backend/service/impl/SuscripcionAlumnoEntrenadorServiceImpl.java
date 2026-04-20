package com.sportine.backend.service.impl;

import com.sportine.backend.model.AlumnoSuscripcionEntrenador;
import com.sportine.backend.model.InformacionEntrenador;
import com.sportine.backend.repository.AlumnoSuscripcionRepository;
import com.sportine.backend.repository.EntrenadorAlumnoRepository;
import com.sportine.backend.repository.InformacionEntrenadorRepository;
import com.sportine.backend.service.PayPalOrderService;
import com.sportine.backend.service.SuscripcionAlumnoEntrenadorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class SuscripcionAlumnoEntrenadorServiceImpl implements SuscripcionAlumnoEntrenadorService {

    private final AlumnoSuscripcionRepository suscripcionRepository;
    private final InformacionEntrenadorRepository entrenadorRepository;
    private final PayPalOrderService paypalOrderService;
    private final EntrenadorAlumnoRepository entrenadorAlumnoRepository;

    @Value("${sportine.comision-porcentaje:10.00}")
    private Double comisionPorcentaje;

    @Override
    @Transactional
    public Map<String, String> crearSuscripcion(String usuarioEstudiante, String usuarioEntrenador,
                                                Integer idDeporte, String returnUrl, String cancelUrl) {
        try {
            log.info("Creando suscripción: Estudiante={}, Entrenador={}, Deporte={}",
                    usuarioEstudiante, usuarioEntrenador, idDeporte);

            InformacionEntrenador entrenador = entrenadorRepository.findByUsuario(usuarioEntrenador)
                    .orElseThrow(() -> new RuntimeException("Entrenador no encontrado"));

            if (entrenador.getOnboardingStatus() != InformacionEntrenador.OnboardingStatus.completed) {
                throw new RuntimeException("El entrenador no puede recibir pagos aún");
            }

            if (tieneSuscripcionActiva(usuarioEstudiante, usuarioEntrenador, idDeporte)) {
                throw new RuntimeException("Ya existe una suscripción activa con este entrenador");
            }

            Double costoMensualidad = entrenador.getCostoMensualidad().doubleValue();

            BigDecimal montoTotal = new BigDecimal(costoMensualidad).setScale(2, RoundingMode.HALF_UP);
            BigDecimal comision = montoTotal.multiply(new BigDecimal(comisionPorcentaje))
                    .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
            BigDecimal montoEntrenador = montoTotal.subtract(comision);

            Map<String, String> paypalResponse = paypalOrderService.crearOrdenMultiparty(
                    usuarioEstudiante,
                    usuarioEntrenador,
                    idDeporte,
                    costoMensualidad,
                    returnUrl,
                    cancelUrl
            );

            String orderId = paypalResponse.get("order_id");
            log.info("✅ Orden PayPal creada: {}", orderId);

            AlumnoSuscripcionEntrenador suscripcion = new AlumnoSuscripcionEntrenador();
            suscripcion.setUsuarioEstudiante(usuarioEstudiante);
            suscripcion.setUsuarioEntrenador(usuarioEntrenador);
            suscripcion.setIdDeporte(idDeporte);
            suscripcion.setMontoTotal(montoTotal);
            suscripcion.setMontoEntrenador(montoEntrenador);
            suscripcion.setMontoComisionSportine(comision);
            suscripcion.setPorcentajeComision(new BigDecimal(comisionPorcentaje));
            suscripcion.setMoneda("MXN");
            suscripcion.setStatusSuscripcion(AlumnoSuscripcionEntrenador.StatusSuscripcion.pending);
            suscripcion.setSubscriptionId(orderId);

            suscripcionRepository.save(suscripcion);
            log.info("✅ Suscripción guardada en BD con order_id: {}", orderId);

            return paypalResponse;

        } catch (Exception e) {
            log.error("Error creando suscripción: {}", e.getMessage(), e);
            throw new RuntimeException("Error al crear suscripción", e);
        }
    }

    @Override
    @Transactional
    public AlumnoSuscripcionEntrenador confirmarSuscripcion(String orderId, String payerId) {
        try {
            log.info("Confirmando suscripción - Order ID: {}, PayerID: {}", orderId, payerId);

            // Buscar suscripción por order_id
            AlumnoSuscripcionEntrenador suscripcion = suscripcionRepository.findBySubscriptionId(orderId)
                    .orElseThrow(() -> new RuntimeException("Suscripción no encontrada para order_id: " + orderId));

            // ── PASO 1: Verificar estado actual ───────────────────────────
            Map<String, Object> orderDetails = paypalOrderService.obtenerDetallesOrden(orderId);
            String status = (String) orderDetails.get("status");
            log.info("Estado actual de la orden {}: {}", orderId, status);

            // ── PASO 2: Si está APPROVED, hacer CAPTURE ───────────────────
            // PayPal Orders v2: CREATE → usuario aprueba (APPROVED) → tú capturas (COMPLETED)
            // Sin el capture el dinero no se mueve y el estado queda en APPROVED.
            if ("APPROVED".equals(status)) {
                log.info("Orden APPROVED — ejecutando capture para order_id: {}", orderId);
                try {
                    Map<String, Object> captureResult = paypalOrderService.capturarOrden(orderId);
                    String nuevoStatus = (String) captureResult.get("status");
                    log.info("Capture ejecutado — nuevo estado: {}", nuevoStatus);
                    status = nuevoStatus;
                } catch (Exception captureEx) {
                    log.error("Error ejecutando capture: {}", captureEx.getMessage(), captureEx);
                    throw new RuntimeException("Error al capturar el pago en PayPal: " + captureEx.getMessage(), captureEx);
                }
            }

            // ── PASO 3: Verificar que quedó COMPLETED ─────────────────────
            if (!"COMPLETED".equals(status)) {
                throw new RuntimeException("La orden no pudo completarse. Estado final: " + status);
            }

            // ── PASO 4: Activar suscripción en BD ─────────────────────────
            suscripcion.setStatusSuscripcion(AlumnoSuscripcionEntrenador.StatusSuscripcion.active);
            suscripcion.setFechaInicioSuscripcion(LocalDate.now());
            suscripcion.setFechaProximoPago(LocalDate.now().plusMonths(1));

            AlumnoSuscripcionEntrenador suscripcionActivada = suscripcionRepository.save(suscripcion);
            log.info("✅ Suscripción activada - ID: {}", suscripcionActivada.getIdSuscripcion());

            // ── PASO 5: Actualizar relación a 'activo' ────────────────────
            entrenadorAlumnoRepository.actualizarEstadoRelacion(
                    suscripcion.getUsuarioEntrenador(),
                    suscripcion.getUsuarioEstudiante(),
                    suscripcion.getIdDeporte(),
                    "activo"
            );
            log.info("✅ Relación actualizada a activo: {} -> {}",
                    suscripcion.getUsuarioEstudiante(), suscripcion.getUsuarioEntrenador());

            return suscripcionActivada;

        } catch (Exception e) {
            log.error("Error confirmando suscripción: {}", e.getMessage(), e);
            throw new RuntimeException("Error al confirmar suscripción", e);
        }
    }

    @Override
    @Transactional
    public void cancelarSuscripcion(Integer idSuscripcion, String motivo) {
        try {
            log.info("Cancelando suscripción ID: {}", idSuscripcion);

            AlumnoSuscripcionEntrenador suscripcion = suscripcionRepository.findById(idSuscripcion)
                    .orElseThrow(() -> new RuntimeException("Suscripción no encontrada"));

            suscripcion.setStatusSuscripcion(AlumnoSuscripcionEntrenador.StatusSuscripcion.cancelled);
            suscripcion.setFechaCancelacion(LocalDate.now());
            suscripcion.setMotivoCancelacion(motivo);
            suscripcion.setFechaFinSuscripcion(suscripcion.getFechaProximoPago());

            suscripcionRepository.save(suscripcion);
            log.info("✅ Suscripción cancelada - servicio activo hasta: {}", suscripcion.getFechaProximoPago());

        } catch (Exception e) {
            log.error("Error cancelando suscripción: {}", e.getMessage(), e);
            throw new RuntimeException("Error al cancelar suscripción", e);
        }
    }

    @Override
    public List<AlumnoSuscripcionEntrenador> obtenerSuscripcionesEstudiante(String usuarioEstudiante) {
        return suscripcionRepository.findByUsuarioEstudiante(usuarioEstudiante);
    }

    @Override
    public List<AlumnoSuscripcionEntrenador> obtenerSuscripcionesEntrenador(String usuarioEntrenador) {
        return suscripcionRepository.findByUsuarioEntrenador(usuarioEntrenador);
    }

    @Override
    public boolean tieneSuscripcionActiva(String usuarioEstudiante, String usuarioEntrenador, Integer idDeporte) {
        List<AlumnoSuscripcionEntrenador> suscripciones =
                suscripcionRepository.findByUsuarioEstudiante(usuarioEstudiante);

        return suscripciones.stream()
                .anyMatch(s -> s.getUsuarioEntrenador().equals(usuarioEntrenador)
                        && s.getIdDeporte().equals(idDeporte)
                        && (s.getStatusSuscripcion() == AlumnoSuscripcionEntrenador.StatusSuscripcion.active
                        || s.getStatusSuscripcion() == AlumnoSuscripcionEntrenador.StatusSuscripcion.pending));
    }
}