package com.sportine.backend.service.impl;

import com.sportine.backend.model.AlumnoSuscripcionEntrenador;
import com.sportine.backend.repository.AlumnoSuscripcionRepository;
import com.sportine.backend.service.SuscripcionRecurrenteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class SuscripcionRecurrenteServiceImpl implements SuscripcionRecurrenteService {

    private final AlumnoSuscripcionRepository suscripcionRepository;

    @Override
    @Transactional
    public int marcarSuscripcionesExpiradas() {
        log.info("Marcando suscripciones expiradas...");
        int cantidad = suscripcionRepository.marcarComoExpiradas(LocalDate.now());
        log.info("Suscripciones marcadas como expiradas: {}", cantidad);
        return cantidad;
    }

    @Override
    @Transactional
    public int procesarCancelacionesPendientes() {
        log.info("Procesando cancelaciones pendientes...");

        // Buscar suscripciones canceladas cuyo periodo ya terminó
        List<AlumnoSuscripcionEntrenador> canceladas = suscripcionRepository
                .findCanceladasConPeriodoVencido(LocalDate.now());

        log.info("Cancelaciones a procesar: {}", canceladas.size());

        for (AlumnoSuscripcionEntrenador suscripcion : canceladas) {
            suscripcion.setStatusSuscripcion(AlumnoSuscripcionEntrenador.StatusSuscripcion.expired);
            suscripcion.setFechaCancelacion(LocalDate.now());
            suscripcionRepository.save(suscripcion);

            log.info("✅ Suscripción {} expirada - Estudiante: {} / Entrenador: {}",
                    suscripcion.getIdSuscripcion(),
                    suscripcion.getUsuarioEstudiante(),
                    suscripcion.getUsuarioEntrenador());
        }

        return canceladas.size();
    }
}