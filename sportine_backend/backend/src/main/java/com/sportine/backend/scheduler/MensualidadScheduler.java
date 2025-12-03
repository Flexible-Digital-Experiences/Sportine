package com.sportine.backend.scheduler;

import com.sportine.backend.service.MensualidadService;
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
public class MensualidadScheduler {

    private static final Logger logger = LoggerFactory.getLogger(MensualidadScheduler.class);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private MensualidadService mensualidadService;

    /**
     * Este método se ejecuta cuando Spring inicializa el bean
     * Para confirmar que el scheduler está siendo detectado
     */
    @PostConstruct
    public void init() {
        logger.info("========================================");
        logger.info("MensualidadScheduler inicializado correctamente");
        logger.info("Zona horaria del sistema: {}", ZoneId.systemDefault());
        logger.info("Hora actual del sistema: {}", LocalDateTime.now().format(formatter));
        logger.info("========================================");
    }

    /**
     * OPCIÓN 1: Expresión CRON con zona horaria de México
     * Se ejecuta todos los días a las 2:00 AM hora de México
     */
    @Scheduled(cron = "0 10 19 * * *", zone = "America/Mexico_City")
    public void verificarMensualidadesVencidas() {
        String horaEjecucion = LocalDateTime.now().format(formatter);
        logger.info("========================================");
        logger.info("INICIO - Verificación de mensualidades");
        logger.info("Hora de ejecución: {}", horaEjecucion);
        logger.info("========================================");

        try {
            int relacionesActualizadas = mensualidadService.actualizarMensualidadesVencidas();

            logger.info("========================================");
            logger.info("FIN - Verificación completada exitosamente");
            logger.info("Total de relaciones actualizadas: {}", relacionesActualizadas);
            logger.info("========================================");

        } catch (Exception e) {
            logger.error("========================================");
            logger.error("ERROR - Fallo en verificación de mensualidades");
            logger.error("Mensaje: {}", e.getMessage());
            logger.error("========================================", e);
        }
    }
}