package com.sportine.backend.scheduler;

import com.sportine.backend.service.SuscripcionRecurrenteService;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Component
public class PagosRecurrentesScheduler {

    private static final Logger logger = LoggerFactory.getLogger(PagosRecurrentesScheduler.class);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private SuscripcionRecurrenteService suscripcionService;

    @PostConstruct
    public void init() {
        logger.info("PagosRecurrentesScheduler inicializado");
        logger.info("Zona horaria: {}", ZoneId.systemDefault());
        logger.info("Hora actual: {}", LocalDateTime.now().format(formatter));
    }

    /**
     * Se ejecuta diariamente a las 2:00 AM
     * Marca como expiradas las suscripciones cuya fecha_fin ya pasó
     * y finaliza las canceladas cuyo periodo de servicio ya terminó
     */
    @Scheduled(cron = "0 0 2 * * *", zone = "America/Mexico_City")
    public void verificarEstadoSuscripciones() {
        logger.info("INICIO - Verificación diaria de suscripciones [{}]",
                LocalDateTime.now().format(formatter));

        try {
            int expiradas = suscripcionService.marcarSuscripcionesExpiradas();
            int canceladas = suscripcionService.procesarCancelacionesPendientes();

            logger.info("Suscripciones expiradas: {}", expiradas);
            logger.info("Cancelaciones procesadas: {}", canceladas);

        } catch (Exception e) {
            logger.error("ERROR en verificación diaria: {}", e.getMessage(), e);
        }
    }
}