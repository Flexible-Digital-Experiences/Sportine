package com.sportine.backend.service.impl;

import com.sportine.backend.dto.ProgresoCompletoDTO;
import com.sportine.backend.exception.RecursoNoEncontradoException;
import com.sportine.backend.model.*;
import com.sportine.backend.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProgresoCompletoServiceImpl {

    private final EntrenamientoRepository entrenamientoRepository;
    private final ProgresoEntrenamientoRepository progresoRepository;
    private final EjerciciosAsignadosRepository ejerciciosRepository;
    private final ResultadoSeriesEjercicioRepository seriesRepository;
    private final ResultadoMetricaManualRepository metricaRepository;
    private final PlantillaMetricasDeporteRepository plantillaRepository;
    private final DeporteRepository deporteRepository;

    @Transactional(readOnly = true)
    public ProgresoCompletoDTO obtenerProgresoCompleto(Integer idEntrenamiento, String usuario) {
        log.info("Obteniendo progreso completo del entrenamiento {} para {}",
                idEntrenamiento, usuario);

        Entrenamiento entrenamiento = entrenamientoRepository
                .findById(idEntrenamiento)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Entrenamiento no encontrado: " + idEntrenamiento));

        ProgresoCompletoDTO dto = new ProgresoCompletoDTO();
        dto.setIdEntrenamiento(idEntrenamiento);
        dto.setTitulo(entrenamiento.getTituloEntrenamiento());
        dto.setFechaEntrenamiento(entrenamiento.getFechaEntrenamiento().toString());
        dto.setEstadoEntrenamiento(entrenamiento.getEstadoEntrenamiento().name());

        // Nombre del deporte
        if (entrenamiento.getIdDeporte() != null) {
            deporteRepository.findById(entrenamiento.getIdDeporte())
                    .ifPresent(d -> dto.setNombreDeporte(d.getNombreDeporte()));
        }

        // Datos de progreso y Health Connect
        progresoRepository.findByIdEntrenamientoAndUsuario(idEntrenamiento, usuario)
                .ifPresent(progreso -> {
                    dto.setCompletado(progreso.getCompletado());
                    dto.setFechaFinalizacion(progreso.getFechaFinalizacion());
                    dto.setTieneDatosHc(progreso.getHcSesionId() != null);
                    dto.setHcTipoEjercicio(progreso.getHcTipoEjercicio());
                    dto.setHcDuracionActivaMin(progreso.getHcDuracionActivaMin());
                    dto.setHcCaloriasKcal(progreso.getHcCaloriasKcal());
                    dto.setHcPasos(progreso.getHcPasos());
                    dto.setHcDistanciaMetros(progreso.getHcDistanciaMetros());
                    dto.setHcVelocidadPromedioMs(progreso.getHcVelocidadPromedioMs());
                    dto.setHcFuenteDatos(progreso.getHcFuenteDatos());
                    dto.setHcSincronizadoEn(progreso.getHcSincronizadoEn());
                });

        // Ejercicios con sus series
        List<EjerciciosAsignados> ejercicios =
                ejerciciosRepository.findByIdEntrenamientoOrderByIdAsignadoAsc(idEntrenamiento);

        long completadosOParciales = ejercicios.stream()
                .filter(e -> e.getStatusEjercicio() == EjerciciosAsignados.StatusEjercicio.completado
                        || e.getStatusEjercicio() == EjerciciosAsignados.StatusEjercicio.parcial)
                .count();

        dto.setPorcentajeCompletado(ejercicios.isEmpty() ? 0.0
                : (completadosOParciales * 100.0) / ejercicios.size());

        List<ProgresoCompletoDTO.EjercicioConSeriesDTO> ejerciciosDTO = ejercicios.stream()
                .map(e -> {
                    ProgresoCompletoDTO.EjercicioConSeriesDTO ejDTO =
                            new ProgresoCompletoDTO.EjercicioConSeriesDTO();
                    ejDTO.setIdAsignado(e.getIdAsignado());
                    ejDTO.setNombreEjercicio(e.getNombreEjercicio());
                    ejDTO.setSeriesEsperadas(e.getSeries());
                    ejDTO.setRepsEsperadas(e.getRepeticiones());
                    ejDTO.setPesoEsperado(e.getPeso());
                    ejDTO.setDuracionEsperadaMin(e.getDuracion());
                    ejDTO.setDistanciaEsperadaMetros(e.getDistancia());
                    ejDTO.setStatusEjercicio(e.getStatusEjercicio().name());

                    // Series pre-generadas con resultados del alumno
                    List<ResultadoSeriesEjercicio> series =
                            seriesRepository.findByIdAsignadoOrderByNumeroSerieAsc(
                                    e.getIdAsignado());

                    List<ProgresoCompletoDTO.SerieResultadoDTO> seriesDTO = series.stream()
                            .map(s -> new ProgresoCompletoDTO.SerieResultadoDTO(
                                    s.getIdResultado(), s.getNumeroSerie(),
                                    s.getRepsEsperadas(), s.getRepsCompletadas(),
                                    s.getPesoEsperado(), s.getPesoUsado(),
                                    s.getDuracionEsperadaSeg(), s.getDuracionCompletadaSeg(),
                                    s.getDistanciaEsperadaMetros(),
                                    s.getDistanciaCompletadaMetros(),
                                    s.getStatus().name(),  s.getRegistradoEn()
                            ))
                            .collect(Collectors.toList());

                    ejDTO.setSeries(seriesDTO);
                    return ejDTO;
                })
                .collect(Collectors.toList());

        dto.setEjercicios(ejerciciosDTO);

        // Métricas manuales del deporte
        List<ResultadoMetricaManual> metricasGuardadas =
                metricaRepository.findByIdEntrenamientoAndUsuario(idEntrenamiento, usuario);

        List<ProgresoCompletoDTO.MetricaResultadoDTO> metricasDTO = metricasGuardadas.stream()
                .map(m -> {
                    PlantillaMetricasDeporte plantilla =
                            plantillaRepository.findById(m.getIdPlantilla()).orElse(null);
                    return new ProgresoCompletoDTO.MetricaResultadoDTO(
                            m.getIdPlantilla(),
                            plantilla != null ? plantilla.getNombreMetrica() : "",
                            plantilla != null ? plantilla.getEtiquetaDisplay() : "",
                            m.getValorNumerico(),
                            plantilla != null ? plantilla.getUnidad() : "",
                            plantilla != null ? plantilla.getFuente().name() : "",
                            m.getNumeroSerie()
                    );
                })
                .collect(Collectors.toList());

        dto.setMetricasDeporte(metricasDTO);
        return dto;
    }
}