package com.sportine.backend.service.impl;

import com.sportine.backend.model.ComisionesSportine;
import com.sportine.backend.model.AlumnoSuscripcionEntrenador;
import com.sportine.backend.model.HistorialPagosAlumnoEntrenador;
import com.sportine.backend.repository.ComisionesSportineRepository;
import com.sportine.backend.repository.AlumnoSuscripcionRepository;
import com.sportine.backend.repository.HistorialPagosAlumnoEntrenadorRepository;
import com.sportine.backend.service.HistorialPagosService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class HistorialPagosServiceImpl implements HistorialPagosService {

    private final HistorialPagosAlumnoEntrenadorRepository historialRepository;
    private final AlumnoSuscripcionRepository suscripcionRepository;
    private final ComisionesSportineRepository comisionesRepository;

    @Override
    @Transactional
    public void registrarPagoExitoso(Integer idSuscripcion, String orderId,
                                     String captureId, LocalDate fechaPago) {
        try {
            log.info("Registrando pago exitoso - Suscripción: {}, Order: {}, Capture: {}",
                    idSuscripcion, orderId, captureId);

            // Obtener suscripción
            AlumnoSuscripcionEntrenador suscripcion = suscripcionRepository.findById(idSuscripcion)
                    .orElseThrow(() -> new RuntimeException("Suscripción no encontrada"));

            // Crear registro de pago
            HistorialPagosAlumnoEntrenador pago = new HistorialPagosAlumnoEntrenador();
            pago.setIdSuscripcion(idSuscripcion);
            pago.setPaypalPaymentId(orderId);
            pago.setPaypalTransactionId(captureId);
            pago.setPaypalSaleId(captureId);

            pago.setMontoTotal(suscripcion.getMontoTotal());
            pago.setMontoEntrenador(suscripcion.getMontoEntrenador());
            pago.setMontoComisionSportine(suscripcion.getMontoComisionSportine());
            pago.setMoneda(suscripcion.getMoneda());

            pago.setStatusPago("COMPLETED");
            pago.setFechaPago(LocalDateTime.now());
            pago.setFechaEsperadaPago(fechaPago);
            pago.setTipoEvento("PAYMENT.CAPTURE.COMPLETED");

            HistorialPagosAlumnoEntrenador pagoGuardado = historialRepository.save(pago);

            log.info("✅ Pago registrado con ID: {}", pagoGuardado.getIdPago());

            // Registrar comisión de Sportine
            registrarComisionSportine(pagoGuardado.getIdPago());

        } catch (Exception e) {
            log.error("Error registrando pago exitoso: {}", e.getMessage(), e);
            throw new RuntimeException("Error al registrar pago", e);
        }
    }

    @Override
    @Transactional
    public void registrarPagoFallido(Integer idSuscripcion, String motivoFallo, LocalDate fechaIntento) {
        try {
            log.warn("Registrando pago fallido - Suscripción: {}, Motivo: {}",
                    idSuscripcion, motivoFallo);

            AlumnoSuscripcionEntrenador suscripcion = suscripcionRepository.findById(idSuscripcion)
                    .orElseThrow(() -> new RuntimeException("Suscripción no encontrada"));

            HistorialPagosAlumnoEntrenador pagoFallido = new HistorialPagosAlumnoEntrenador();
            pagoFallido.setIdSuscripcion(idSuscripcion);
            pagoFallido.setMontoTotal(suscripcion.getMontoTotal());
            pagoFallido.setMontoEntrenador(suscripcion.getMontoEntrenador());
            pagoFallido.setMontoComisionSportine(suscripcion.getMontoComisionSportine());
            pagoFallido.setMoneda(suscripcion.getMoneda());

            pagoFallido.setStatusPago("FAILED");
            pagoFallido.setFechaPago(LocalDateTime.now());
            pagoFallido.setFechaEsperadaPago(fechaIntento);
            pagoFallido.setTipoEvento("PAYMENT.CAPTURE.FAILED");
            pagoFallido.setEventoWebhook("{\"error\": \"" + motivoFallo + "\"}");

            historialRepository.save(pagoFallido);

            log.info("❌ Pago fallido registrado");

        } catch (Exception e) {
            log.error("Error registrando pago fallido: {}", e.getMessage(), e);
        }
    }

    @Override
    public List<HistorialPagosAlumnoEntrenador> obtenerHistorialPorSuscripcion(Integer idSuscripcion) {
        return historialRepository.findByIdSuscripcionOrderByFechaPagoDesc(idSuscripcion);
    }

    @Override
    public boolean existePagoConTransactionId(String transactionId) {
        return historialRepository.existsByPaypalTransactionId(transactionId);
    }

    @Override
    @Transactional
    public void registrarComisionSportine(Integer idPago) {
        try {
            log.info("Registrando comisión de Sportine para pago: {}", idPago);

            HistorialPagosAlumnoEntrenador pago = historialRepository.findById(idPago)
                    .orElseThrow(() -> new RuntimeException("Pago no encontrado"));

            // Obtener la suscripción primero
            AlumnoSuscripcionEntrenador suscripcion = suscripcionRepository.findById(pago.getIdSuscripcion())
                    .orElseThrow(() -> new RuntimeException("Suscripción no encontrada"));

            // Crear la comisión
            ComisionesSportine comision = new ComisionesSportine();
            comision.setIdPago(idPago);
            comision.setMontoComision(pago.getMontoComisionSportine());
            comision.setMoneda(pago.getMoneda());
            comision.setPorcentajeAplicado(suscripcion.getPorcentajeComision()); // ✅ Ahora sí funciona
            comision.setStatusDeposito(ComisionesSportine.StatusDeposito.pending);
            comision.setFechaDepositoEsperado(LocalDate.now().plusDays(1)); // PayPal deposita al día siguiente

            comisionesRepository.save(comision);

            log.info("✅ Comisión registrada");

        } catch (Exception e) {
            log.error("Error registrando comisión: {}", e.getMessage(), e);
        }
    }
}