package com.sportine.backend.service.impl;

import com.sportine.backend.model.InformacionEntrenador;
import com.sportine.backend.repository.InformacionEntrenadorRepository;
import com.sportine.backend.service.PremiumEntrenadorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
public class PremiumEntrenadorServiceImpl implements PremiumEntrenadorService {

    private static final Logger logger = LoggerFactory.getLogger(PremiumEntrenadorServiceImpl.class);

    @Autowired
    private InformacionEntrenadorRepository entrenadorRepository;

    /**
     * Downgrade masivo de coaches con premium expirado
     * Este método es llamado por el scheduler diariamente
     *
     * @return cantidad de coaches actualizados
     */
    @Override
    @Transactional
    public int downgradearCoachesPremiumExpirados() {
        LocalDate hoy = LocalDate.now();

        logger.info("Buscando coaches con premium expirado (fecha_fin_suscripcion < {})", hoy);

        // Buscar todos los coaches con suscripción cancelada y fecha expirada
        List<InformacionEntrenador> coachesExpirados = entrenadorRepository.findAll().stream()
                .filter(coach ->
                        "cancelled".equals(coach.getSubscriptionStatus()) &&
                                coach.getFechaFinSuscripcion() != null &&
                                coach.getFechaFinSuscripcion().isBefore(hoy) &&
                                "premium".equals(coach.getTipoCuenta())
                )
                .toList();

        logger.info("Se encontraron {} coaches con premium expirado", coachesExpirados.size());

        int coachesActualizados = 0;

        for (InformacionEntrenador coach : coachesExpirados) {
            try {
                logger.info("Downgradeando coach: {} (expiró: {})",
                        coach.getUsuario(),
                        coach.getFechaFinSuscripcion()
                );

                entrenadorRepository.downgradearAGratis(coach.getUsuario());
                coachesActualizados++;

                logger.info("✓ Coach {} downgradeado exitosamente a gratis (límite: 3 alumnos)",
                        coach.getUsuario()
                );

            } catch (Exception e) {
                logger.error("✗ Error al downgradear coach {}: {}",
                        coach.getUsuario(),
                        e.getMessage()
                );
            }
        }

        logger.info("Total de coaches downgradeados: {}", coachesActualizados);
        return coachesActualizados;
    }

    /**
     * Downgrade individual de un coach específico
     *
     * @param usuario identificador del coach
     */
    @Override
    @Transactional
    public void downgradearCoachIndividual(String usuario) {
        logger.info("Iniciando downgrade individual para coach: {}", usuario);

        Optional<InformacionEntrenador> coachOpt = entrenadorRepository.findByUsuario(usuario);

        if (coachOpt.isEmpty()) {
            logger.warn("Coach {} no encontrado", usuario);
            throw new IllegalArgumentException("Coach no encontrado: " + usuario);
        }

        InformacionEntrenador coach = coachOpt.get();

        if (!"premium".equals(coach.getTipoCuenta())) {
            logger.warn("Coach {} no tiene cuenta premium, no se puede downgradear", usuario);
            return;
        }

        entrenadorRepository.downgradearAGratis(usuario);
        logger.info("✓ Coach {} downgradeado a gratis exitosamente", usuario);
    }

    /**
     * Verificar si un coach tiene premium activo o está en periodo de gracia
     *
     * @param usuario identificador del coach
     * @return true si tiene acceso premium, false si no
     */
    @Override
    public boolean tieneAccesoPremium(String usuario) {
        Optional<InformacionEntrenador> coachOpt = entrenadorRepository.findByUsuario(usuario);

        if (coachOpt.isEmpty()) {
            return false;
        }

        InformacionEntrenador coach = coachOpt.get();

        // Si no es premium, no tiene acceso
        if (!"premium".equals(coach.getTipoCuenta())) {
            return false;
        }

        // Si está activo, tiene acceso
        if ("active".equals(coach.getSubscriptionStatus())) {
            return true;
        }

        // Si está cancelado, verificar si aún está en periodo de gracia
        if ("cancelled".equals(coach.getSubscriptionStatus()) && coach.getFechaFinSuscripcion() != null) {
            LocalDate hoy = LocalDate.now();
            return !coach.getFechaFinSuscripcion().isBefore(hoy);
        }

        return false;
    }

    /**
     * Obtener días restantes de premium para un coach
     *
     * @param usuario identificador del coach
     * @return días restantes, 0 si ya expiró, -1 si no tiene premium
     */
    @Override
    public int obtenerDiasRestantesPremium(String usuario) {
        Optional<InformacionEntrenador> coachOpt = entrenadorRepository.findByUsuario(usuario);

        if (coachOpt.isEmpty()) {
            return -1;
        }

        InformacionEntrenador coach = coachOpt.get();

        // Si no es premium, retornar -1
        if (!"premium".equals(coach.getTipoCuenta())) {
            return -1;
        }

        // Si no tiene fecha de fin, es premium indefinido (activo)
        if (coach.getFechaFinSuscripcion() == null) {
            return Integer.MAX_VALUE; // Infinito
        }

        // Calcular días restantes
        LocalDate hoy = LocalDate.now();
        long diasRestantes = ChronoUnit.DAYS.between(hoy, coach.getFechaFinSuscripcion());

        return diasRestantes < 0 ? 0 : (int) diasRestantes;
    }
}