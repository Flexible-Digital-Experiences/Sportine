package com.sportine.backend.service.impl;

import com.sportine.backend.model.AlumnoSuscripcionEntrenador;
import com.sportine.backend.model.InformacionEntrenador;
import com.sportine.backend.repository.AlumnoSuscripcionRepository;
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

    @Value("${sportine.comision-porcentaje:10.00}")
    private Double comisionPorcentaje;

    @Override
    @Transactional
    public Map<String, String> crearSuscripcion(String usuarioEstudiante, String usuarioEntrenador, Integer idDeporte) {
        try {
            log.info("Creando suscripción: Estudiante={}, Entrenador={}, Deporte={}",
                    usuarioEstudiante, usuarioEntrenador, idDeporte);

            // Verificar que el entrenador existe y está onboarded
            InformacionEntrenador entrenador = entrenadorRepository.findByUsuario(usuarioEntrenador)
                    .orElseThrow(() -> new RuntimeException("Entrenador no encontrado"));

            if (entrenador.getOnboardingStatus() != InformacionEntrenador.OnboardingStatus.completed) {
                throw new RuntimeException("El entrenador no puede recibir pagos aún");
            }

            // Verificar que no existe ya una suscripción activa
            if (tieneSuscripcionActiva(usuarioEstudiante, usuarioEntrenador, idDeporte)) {
                throw new RuntimeException("Ya existe una suscripción activa con este entrenador");
            }

            // Obtener costo de mensualidad
            Double costoMensualidad = entrenador.getCostoMensualidad().doubleValue();

            // Calcular montos
            BigDecimal montoTotal = new BigDecimal(costoMensualidad).setScale(2, RoundingMode.HALF_UP);
            BigDecimal comision = montoTotal.multiply(new BigDecimal(comisionPorcentaje))
                    .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
            BigDecimal montoEntrenador = montoTotal.subtract(comision);

            // Crear suscripción en estado pending
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

            AlumnoSuscripcionEntrenador suscripcionGuardada = suscripcionRepository.save(suscripcion);

            log.info("✅ Suscripción creada con ID: {}", suscripcionGuardada.getIdSuscripcion());

            // Crear orden de pago en PayPal
            Map<String, String> paypalResponse = paypalOrderService.crearOrdenMultiparty(
                    usuarioEstudiante,
                    usuarioEntrenador,
                    idDeporte,
                    costoMensualidad
            );

            // Guardar order_id en la suscripción
            suscripcionGuardada.setSubscriptionId(paypalResponse.get("order_id"));
            suscripcionRepository.save(suscripcionGuardada);

            return paypalResponse;

        } catch (Exception e) {
            log.error("Error creando suscripción: {}", e.getMessage(), e);
            throw new RuntimeException("Error al crear suscripción", e);
        }
    }

    @Override
    @Transactional
    public AlumnoSuscripcionEntrenador confirmarSuscripcion(String orderId, String vaultId) {
        try {
            log.info("Confirmando suscripción - Order ID: {}", orderId);

            // Buscar suscripción por order_id
            AlumnoSuscripcionEntrenador suscripcion = suscripcionRepository.findBySubscriptionId(orderId)
                    .orElseThrow(() -> new RuntimeException("Suscripción no encontrada"));

            // Verificar que la orden fue capturada exitosamente
            Map<String, Object> orderDetails = paypalOrderService.obtenerDetallesOrden(orderId);
            String status = (String) orderDetails.get("status");

            if (!"COMPLETED".equals(status)) {
                throw new RuntimeException("La orden no está completada. Estado: " + status);
            }

            // Activar suscripción
            suscripcion.setStatusSuscripcion(AlumnoSuscripcionEntrenador.StatusSuscripcion.active);
            suscripcion.setFechaInicioSuscripcion(LocalDate.now());
            suscripcion.setFechaProximoPago(LocalDate.now().plusMonths(1));
            suscripcion.setIntentosFallidos(0);

            // Guardar vault_id si se proporcionó
            if (vaultId != null) {
                suscripcion.setVaultId(vaultId);
                suscripcion.setPaymentSourceType("PAYPAL");
            }

            AlumnoSuscripcionEntrenador suscripcionActivada = suscripcionRepository.save(suscripcion);

            log.info("✅ Suscripción activada - ID: {}", suscripcionActivada.getIdSuscripcion());

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
            suscripcion.setFechaFinSuscripcion(LocalDate.now());

            suscripcionRepository.save(suscripcion);

            log.info("✅ Suscripción cancelada");

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
                        && s.getStatusSuscripcion() == AlumnoSuscripcionEntrenador.StatusSuscripcion.active);
    }
}