package com.sportine.backend.service.impl;

import com.sportine.backend.dto.ProgresoHealthConnectDTO;
import com.sportine.backend.exception.RecursoNoEncontradoException;
import com.sportine.backend.model.Entrenamiento;
import com.sportine.backend.model.ProgresoEntrenamiento;
import com.sportine.backend.repository.EntrenamientoRepository;
import com.sportine.backend.repository.ProgresoEntrenamientoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class HealthConnectServiceImpl {

    private final ProgresoEntrenamientoRepository progresoRepository;
    private final EntrenamientoRepository entrenamientoRepository;

    @Transactional
    public void sincronizarHealthConnect(ProgresoHealthConnectDTO dto, String usuario) {
        log.info("Sincronizando HC para entrenamiento {} del usuario {}",
                dto.getIdEntrenamiento(), usuario);

        Entrenamiento entrenamiento = entrenamientoRepository
                .findById(dto.getIdEntrenamiento())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Entrenamiento no encontrado: " + dto.getIdEntrenamiento()));

        if (!entrenamiento.getUsuario().equals(usuario)) {
            throw new RuntimeException("Este entrenamiento no te pertenece");
        }

        // Buscar progreso existente o crear uno nuevo
        ProgresoEntrenamiento progreso = progresoRepository
                .findByIdEntrenamientoAndUsuario(dto.getIdEntrenamiento(), usuario)
                .orElseGet(() -> {
                    ProgresoEntrenamiento nuevo = new ProgresoEntrenamiento();
                    nuevo.setIdEntrenamiento(dto.getIdEntrenamiento());
                    nuevo.setUsuario(usuario);
                    return nuevo;
                });

        // Mapear todos los campos de Health Connect
        progreso.setHcSesionId(dto.getHcSesionId());
        progreso.setHcTipoEjercicio(dto.getHcTipoEjercicio());
        progreso.setHcDuracionActivaMin(dto.getHcDuracionActivaMin());
        progreso.setHcCaloriasKcal(dto.getHcCaloriasKcal());
        progreso.setHcPasos(dto.getHcPasos());
        progreso.setHcDistanciaMetros(dto.getHcDistanciaMetros());
        progreso.setHcVelocidadPromedioMs(dto.getHcVelocidadPromedioMs());
        progreso.setHcFuenteDatos(dto.getHcFuenteDatos() != null
                ? dto.getHcFuenteDatos() : "health_connect");
        progreso.setHcSincronizadoEn(LocalDateTime.now());

        progresoRepository.save(progreso);
        log.info("✅ Datos de HC guardados para entrenamiento {}", dto.getIdEntrenamiento());
    }
}