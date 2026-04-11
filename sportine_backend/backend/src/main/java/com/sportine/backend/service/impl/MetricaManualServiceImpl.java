package com.sportine.backend.service.impl;

import com.sportine.backend.dto.MetricaManualDTO;
import com.sportine.backend.exception.RecursoNoEncontradoException;
import com.sportine.backend.model.Entrenamiento;
import com.sportine.backend.model.ResultadoMetricaManual;
import com.sportine.backend.repository.EntrenamientoRepository;
import com.sportine.backend.repository.PlantillaMetricasDeporteRepository;
import com.sportine.backend.repository.ResultadoMetricaManualRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class MetricaManualServiceImpl {

    private final ResultadoMetricaManualRepository metricaRepository;
    private final EntrenamientoRepository entrenamientoRepository;
    private final PlantillaMetricasDeporteRepository plantillaRepository;

    @Transactional
    public List<ResultadoMetricaManual> guardarMetricas(MetricaManualDTO dto, String usuario) {
        log.info("Guardando {} métricas para entrenamiento {} del usuario {}",
                dto.getMetricas().size(), dto.getIdEntrenamiento(), usuario);

        Entrenamiento entrenamiento = entrenamientoRepository
                .findById(dto.getIdEntrenamiento())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Entrenamiento no encontrado: " + dto.getIdEntrenamiento()));

        if (!entrenamiento.getUsuario().equals(usuario)) {
            throw new RuntimeException("Este entrenamiento no te pertenece");
        }

        List<ResultadoMetricaManual> resultados = new ArrayList<>();

        for (MetricaManualDTO.MetricaItemDTO item : dto.getMetricas()) {
            // Validar que la plantilla de métrica existe
            plantillaRepository.findById(item.getIdPlantilla())
                    .orElseThrow(() -> new RecursoNoEncontradoException(
                            "Plantilla de métrica no encontrada: " + item.getIdPlantilla()));

            ResultadoMetricaManual metrica = new ResultadoMetricaManual();
            metrica.setIdEntrenamiento(dto.getIdEntrenamiento());
            metrica.setIdPlantilla(item.getIdPlantilla());
            metrica.setUsuario(usuario);
            metrica.setValorNumerico(item.getValorNumerico());
            metrica.setNumeroSerie(item.getNumeroSerie());
            metrica.setNotas(item.getNotas());

            resultados.add(metricaRepository.save(metrica));
        }

        log.info("✅ {} métricas guardadas para el entrenamiento {}",
                resultados.size(), dto.getIdEntrenamiento());
        return resultados;
    }

    @Transactional(readOnly = true)
    public List<ResultadoMetricaManual> obtenerMetricasDeEntrenamiento(
            Integer idEntrenamiento, String usuario) {
        return metricaRepository.findByIdEntrenamientoAndUsuario(idEntrenamiento, usuario);
    }
}