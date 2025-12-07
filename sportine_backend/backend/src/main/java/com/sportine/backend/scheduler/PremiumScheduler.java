package com.sportine.backend.scheduler;

import com.sportine.backend.service.PremiumEntrenadorService;
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
public class PremiumScheduler {

    private static final Logger logger = LoggerFactory.getLogger(PremiumScheduler.class);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private PremiumEntrenadorService premiumEntrenadorService;

    @PostConstruct
    public void init() {
        logger.info("========================================");
        logger.info("PremiumScheduler inicializado correctamente");
        logger.info("Zona horaria del sistema: {}", ZoneId.systemDefault());
        logger.info("Hora actual del sistema: {}", LocalDateTime.now().format(formatter));
        logger.info("========================================");
    }

    /**
     * Se ejecuta todos los días a las 3:00 AM hora de México
     * Verifica coaches con premium expirado y los downgrade a gratis
     */
    @Scheduled(cron = "0 0 0 * * *", zone = "America/Mexico_City")
    public void verificarPremiumExpirados() {
        String horaEjecucion = LocalDateTime.now().format(formatter);
        logger.info("========================================");
        logger.info("INICIO - Verificación de premium expirados");
        logger.info("Hora de ejecución: {}", horaEjecucion);
        logger.info("========================================");

        try {
            int coachesActualizados = premiumEntrenadorService.downgradearCoachesPremiumExpirados();

            logger.info("========================================");
            logger.info("FIN - Verificación completada exitosamente");
            logger.info("Total de coaches downgradeados: {}", coachesActualizados);
            logger.info("========================================");

        } catch (Exception e) {
            logger.error("========================================");
            logger.error("ERROR - Fallo en verificación de premium");
            logger.error("Mensaje: {}", e.getMessage());
            logger.error("========================================", e);
        }
    }
}