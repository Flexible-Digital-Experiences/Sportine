package com.sportine.backend.scheduler;

import com.sportine.backend.service.SuscripcionRecurrenteService;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Component
public class PagosRecurrentesScheduler{

    private static final Logger logger = LoggerFactory.getLogger(PagosRecurrentesScheduler.class);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private SuscripcionRecurrenteService suscripcionService;

    @PostConstruct
    public void init() {
        logger.info("========================================");
        logger.info("PagosRecurrentesScheduler inicializado");
        logger.info("Zona horaria: {}", ZoneId.systemDefault());
        logger.info("Hora actual: {}", LocalDateTime.now().format(formatter));
        logger.info("========================================");
    }

    /**
     * Se ejecuta todos los días a las 2:00 AM (México)
     * Procesa pagos recurrentes usando PayPal Vault
     */
    @Scheduled(cron = "0 0 2 * * *", zone = "America/Mexico_City")
    public void procesarPagosRecurrentes() {
        String horaEjecucion = LocalDateTime.now().format(formatter);
        LocalDate hoy = LocalDate.now();

        logger.info("========================================");
        logger.info("INICIO - Procesamiento de pagos recurrentes");
        logger.info("Fecha: {}", hoy);
        logger.info("Hora de ejecución: {}", horaEjecucion);
        logger.info("========================================");

        try {
            // Procesar suscripciones que deben cobrarse HOY
            var resultado = suscripcionService.procesarPagosPendientes(hoy);

            logger.info("========================================");
            logger.info("RESUMEN DE PROCESAMIENTO:");
            logger.info("Suscripciones procesadas: {}", resultado.getTotalProcesadas());
            logger.info("Pagos exitosos: {}", resultado.getPagosExitosos());
            logger.info("Pagos fallidos: {}", resultado.getPagosFallidos());
            logger.info("Suscripciones canceladas: {}", resultado.getSuscripcionesCanceladas());
            logger.info("========================================");

        } catch (Exception e) {
            logger.error("========================================");
            logger.error("ERROR - Fallo en procesamiento de pagos");
            logger.error("Mensaje: {}", e.getMessage());
            logger.error("========================================", e);
        }
    }

    /**
     * Se ejecuta cada 6 horas para reintentar pagos fallidos
     */
    @Scheduled(cron = "0 0 */6 * * *", zone = "America/Mexico_City")
    public void reintentarPagosFallidos() {
        logger.info("========================================");
        logger.info("INICIO - Reintento de pagos fallidos");
        logger.info("========================================");

        try {
            var resultado = suscripcionService.reintentarPagosFallidos();

            logger.info("Reintentos exitosos: {}", resultado.getReintentosExitosos());
            logger.info("Reintentos fallidos: {}", resultado.getReintentosFallidos());
            logger.info("========================================");

        } catch (Exception e) {
            logger.error("ERROR - Fallo en reintentos: {}", e.getMessage(), e);
        }
    }

    /**
     * Se ejecuta diariamente para verificar estado de suscripciones
     */
    @Scheduled(cron = "0 30 2 * * *", zone = "America/Mexico_City")
    public void verificarEstadoSuscripciones() {
        logger.info("========================================");
        logger.info("INICIO - Verificación de estado de suscripciones");
        logger.info("========================================");

        try {
            // Cancelar suscripciones con 3 pagos fallidos consecutivos
            int canceladas = suscripcionService.cancelarSuscripcionesConFallosContinuos();

            // Marcar como expiradas las que ya pasaron su fecha de fin
            int expiradas = suscripcionService.marcarSuscripcionesExpiradas();

            logger.info("Suscripciones canceladas por fallos: {}", canceladas);
            logger.info("Suscripciones marcadas como expiradas: {}", expiradas);
            logger.info("========================================");

        } catch (Exception e) {
            logger.error("ERROR - Fallo en verificación: {}", e.getMessage(), e);
        }
    }
}