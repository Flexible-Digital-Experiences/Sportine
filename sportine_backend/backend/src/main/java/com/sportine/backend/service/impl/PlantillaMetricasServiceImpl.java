package com.sportine.backend.service.impl;

import com.sportine.backend.dto.PlantillaMetricasDTO;
import com.sportine.backend.exception.RecursoNoEncontradoException;
import com.sportine.backend.model.Deporte;
import com.sportine.backend.model.PlantillaMetricasDeporte;
import com.sportine.backend.repository.DeporteRepository;
import com.sportine.backend.repository.PlantillaMetricasDeporteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class PlantillaMetricasServiceImpl {

    private final PlantillaMetricasDeporteRepository plantillaRepository;
    private final DeporteRepository deporteRepository;

    @Transactional(readOnly = true)
    public PlantillaMetricasDTO obtenerPlantillaPorDeporte(Integer idDeporte) {
        Deporte deporte = deporteRepository.findById(idDeporte)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Deporte no encontrado: " + idDeporte));

        List<PlantillaMetricasDeporte> todas =
                plantillaRepository.findByIdDeporteOrderByOrdenDisplayAsc(idDeporte);

        PlantillaMetricasDTO dto = new PlantillaMetricasDTO();
        dto.setIdDeporte(idDeporte);
        dto.setNombreDeporte(deporte.getNombreDeporte());
        dto.setMetricasHealthConnect(
                filtrarYMapear(todas, PlantillaMetricasDeporte.Fuente.health_connect));
        dto.setMetricasManuales(
                filtrarYMapear(todas, PlantillaMetricasDeporte.Fuente.manual));
        dto.setMetricasCalculadas(
                filtrarYMapear(todas, PlantillaMetricasDeporte.Fuente.calculada));

        return dto;
    }

    @Transactional(readOnly = true)
    public List<PlantillaMetricasDTO> obtenerTodasLasPlantillas() {
        return deporteRepository.findAll().stream()
                .map(d -> obtenerPlantillaPorDeporte(d.getIdDeporte()))
                .collect(Collectors.toList());
    }

    private List<PlantillaMetricasDTO.MetricaConfigDTO> filtrarYMapear(
            List<PlantillaMetricasDeporte> todas,
            PlantillaMetricasDeporte.Fuente fuente) {
        return todas.stream()
                .filter(p -> p.getFuente() == fuente)
                .map(p -> new PlantillaMetricasDTO.MetricaConfigDTO(
                        p.getIdPlantilla(),
                        p.getNombreMetrica(),
                        p.getEtiquetaDisplay(),
                        p.getUnidad(),
                        p.getFuente().name(),
                        p.getEsPorSerie(),
                        p.getOrdenDisplay()))
                .collect(Collectors.toList());
    }
}