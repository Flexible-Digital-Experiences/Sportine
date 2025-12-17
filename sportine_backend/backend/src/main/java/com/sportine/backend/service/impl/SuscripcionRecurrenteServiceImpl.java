package com.sportine.backend.service.impl;

import com.sportine.backend.dto.ResultadoProcesamientoDTO;
import com.sportine.backend.model.AlumnoSuscripcionEntrenador;
import com.sportine.backend.repository.AlumnoSuscripcionRepository;
import com.sportine.backend.service.HistorialPagosService;
import com.sportine.backend.service.PayPalOrderService;
import com.sportine.backend.service.SuscripcionRecurrenteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class SuscripcionRecurrenteServiceImpl implements SuscripcionRecurrenteService {

    private final AlumnoSuscripcionRepository suscripcionRepository;
    private final PayPalOrderService paypalOrderService;
    private final HistorialPagosService historialPagosService;

    @Override
    @Transactional
    public ResultadoProcesamientoDTO procesarPagosPendientes(LocalDate fecha) {
        log.info("========================================");
        log.info("Procesando pagos pendientes para fecha: {}", fecha);
        log.info("========================================");

        ResultadoProcesamientoDTO resultado = new ResultadoProcesamientoDTO();

        try {
            // Buscar suscripciones activas cuya fecha_proximo_pago es HOY
            List<AlumnoSuscripcionEntrenador> suscripciones =
                    suscripcionRepository.findByStatusAndFechaProximoPago(
                            AlumnoSuscripcionEntrenador.StatusSuscripcion.active,
                            fecha
                    );

            log.info("Suscripciones encontradas para procesar: {}", suscripciones.size());
            resultado.setTotalProcesadas(suscripciones.size());

            for (AlumnoSuscripcionEntrenador suscripcion : suscripciones) {
                try {
                    log.info("→ Procesando suscripción ID: {} (Estudiante: {} -> Entrenador: {})",
                            suscripcion.getIdSuscripcion(),
                            suscripcion.getUsuarioEstudiante(),
                            suscripcion.getUsuarioEntrenador());

                    // Crear orden con Vault
                    String orderId = paypalOrderService.crearOrdenConVault(suscripcion.getIdSuscripcion());

                    // Capturar orden inmediatamente
                    Map<String, Object> captureDetails = paypalOrderService.capturarOrden(orderId);
                    String captureId = (String) captureDetails.get("capture_id");
                    String captureStatus = (String) captureDetails.get("capture_status");

                    if ("COMPLETED".equals(captureStatus)) {
                        // Registrar pago exitoso
                        historialPagosService.registrarPagoExitoso(
                                suscripcion.getIdSuscripcion(),
                                orderId,
                                captureId,
                                fecha
                        );

                        // Actualizar suscripción
                        suscripcion.setFechaProximoPago(fecha.plusMonths(1));
                        suscripcion.setIntentosFallidos(0);
                        suscripcionRepository.save(suscripcion);

                        resultado.incrementarExitosos();
                        log.info("   ✅ Pago exitoso - Capture ID: {}", captureId);
                    } else {
                        throw new RuntimeException("Captura no completada: " + captureStatus);
                    }

                } catch (Exception e) {
                    log.error("   ❌ Error procesando suscripción {}: {}",
                            suscripcion.getIdSuscripcion(), e.getMessage());

                    // Incrementar contador de fallos
                    int intentosFallidos = suscripcion.getIntentosFallidos() != null
                            ? suscripcion.getIntentosFallidos() + 1
                            : 1;
                    suscripcion.setIntentosFallidos(intentosFallidos);

                    // Registrar pago fallido
                    historialPagosService.registrarPagoFallido(
                            suscripcion.getIdSuscripcion(),
                            e.getMessage(),
                            fecha
                    );

                    // Si ya tiene 3 fallos, cancelar suscripción
                    if (intentosFallidos >= 3) {
                        suscripcion.setStatusSuscripcion(AlumnoSuscripcionEntrenador.StatusSuscripcion.cancelled);
                        suscripcion.setFechaCancelacion(LocalDate.now());
                        suscripcion.setMotivoCancelacion("Cancelada automáticamente por 3 pagos fallidos consecutivos");
                        resultado.incrementarCanceladas();
                        log.warn("   ⚠️ Suscripción {} cancelada por 3 fallos consecutivos",
                                suscripcion.getIdSuscripcion());
                    }

                    suscripcionRepository.save(suscripcion);
                    resultado.incrementarFallidos();
                }
            }

            log.info("========================================");
            log.info("RESUMEN:");
            log.info("Total procesadas: {}", resultado.getTotalProcesadas());
            log.info("Exitosos: {}", resultado.getPagosExitosos());
            log.info("Fallidos: {}", resultado.getPagosFallidos());
            log.info("Canceladas: {}", resultado.getSuscripcionesCanceladas());
            log.info("========================================");

        } catch (Exception e) {
            log.error("Error general en procesamiento de pagos: {}", e.getMessage(), e);
        }

        return resultado;
    }

    @Override
    @Transactional
    public ResultadoProcesamientoDTO reintentarPagosFallidos() {
        log.info("========================================");
        log.info("Reintentando pagos fallidos recientes");
        log.info("========================================");

        ResultadoProcesamientoDTO resultado = new ResultadoProcesamientoDTO();
        LocalDate hace48Horas = LocalDate.now().minusDays(2);

        try {
            List<AlumnoSuscripcionEntrenador> suscripciones =
                    suscripcionRepository.findActivasConFallosRecientes(hace48Horas);

            log.info("Suscripciones con fallos a reintentar: {}", suscripciones.size());

            for (AlumnoSuscripcionEntrenador suscripcion : suscripciones) {
                try {
                    log.info("→ Reintentando suscripción ID: {}", suscripcion.getIdSuscripcion());

                    String orderId = paypalOrderService.crearOrdenConVault(suscripcion.getIdSuscripcion());
                    Map<String, Object> captureDetails = paypalOrderService.capturarOrden(orderId);
                    String captureId = (String) captureDetails.get("capture_id");

                    historialPagosService.registrarPagoExitoso(
                            suscripcion.getIdSuscripcion(),
                            orderId,
                            captureId,
                            LocalDate.now()
                    );

                    suscripcion.setIntentosFallidos(0);
                    suscripcion.setFechaProximoPago(LocalDate.now().plusMonths(1));
                    suscripcionRepository.save(suscripcion);

                    resultado.incrementarReintentosExitosos();
                    log.info("   ✅ Reintento exitoso");

                } catch (Exception e) {
                    resultado.incrementarReintentosFallidos();
                    log.error("   ❌ Reintento fallido: {}", e.getMessage());
                }
            }

            log.info("========================================");
            log.info("Reintentos exitosos: {}", resultado.getReintentosExitosos());
            log.info("Reintentos fallidos: {}", resultado.getReintentosFallidos());
            log.info("========================================");

        } catch (Exception e) {
            log.error("Error en reintentos: {}", e.getMessage(), e);
        }

        return resultado;
    }

    @Override
    @Transactional
    public int cancelarSuscripcionesConFallosContinuos() {
        log.info("Cancelando suscripciones con fallos continuos...");

        List<AlumnoSuscripcionEntrenador> suscripciones =
                suscripcionRepository.findActivasConFallosContinuos();

        for (AlumnoSuscripcionEntrenador suscripcion : suscripciones) {
            suscripcion.setStatusSuscripcion(AlumnoSuscripcionEntrenador.StatusSuscripcion.cancelled);
            suscripcion.setFechaCancelacion(LocalDate.now());
            suscripcion.setMotivoCancelacion("Cancelada automáticamente por fallos continuos en el pago");
            suscripcionRepository.save(suscripcion);

            log.warn("Suscripción {} cancelada por fallos continuos", suscripcion.getIdSuscripcion());
        }

        return suscripciones.size();
    }

    @Override
    @Transactional
    public int marcarSuscripcionesExpiradas() {
        log.info("Marcando suscripciones expiradas...");
        return suscripcionRepository.marcarComoExpiradas(LocalDate.now());
    }

    @Override
    @Transactional
    public boolean procesarPagoIndividual(Integer idSuscripcion) {
        try {
            log.info("Procesando pago individual para suscripción: {}", idSuscripcion);

            AlumnoSuscripcionEntrenador suscripcion = suscripcionRepository.findById(idSuscripcion)
                    .orElseThrow(() -> new RuntimeException("Suscripción no encontrada"));

            String orderId = paypalOrderService.crearOrdenConVault(idSuscripcion);
            Map<String, Object> captureDetails = paypalOrderService.capturarOrden(orderId);
            String captureId = (String) captureDetails.get("capture_id");

            historialPagosService.registrarPagoExitoso(
                    idSuscripcion,
                    orderId,
                    captureId,
                    LocalDate.now()
            );

            suscripcion.setIntentosFallidos(0);
            suscripcion.setFechaProximoPago(LocalDate.now().plusMonths(1));
            suscripcionRepository.save(suscripcion);

            log.info("✅ Pago individual procesado exitosamente");
            return true;

        } catch (Exception e) {
            log.error("❌ Error procesando pago individual: {}", e.getMessage(), e);
            return false;
        }
    }
}