package com.sportine.backend.service.impl;

import com.sportine.backend.dto.ResultadoSerieDTO;
import com.sportine.backend.exception.RecursoNoEncontradoException;
import com.sportine.backend.model.EjerciciosAsignados;
import com.sportine.backend.model.ResultadoSeriesEjercicio;
import com.sportine.backend.repository.EjerciciosAsignadosRepository;
import com.sportine.backend.repository.ResultadoSeriesEjercicioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ResultadoSeriesServiceImpl {

    private final ResultadoSeriesEjercicioRepository seriesRepository;
    private final EjerciciosAsignadosRepository ejerciciosRepository;

    @Transactional
    public ResultadoSeriesEjercicio guardarResultadoSerie(ResultadoSerieDTO dto, String usuario) {
        log.info("Guardando resultado serie {} del ejercicio {}", dto.getNumeroSerie(), dto.getIdAsignado());

        EjerciciosAsignados ejercicio = ejerciciosRepository
                .findById(dto.getIdAsignado())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Ejercicio no encontrado: " + dto.getIdAsignado()));

        if (!ejercicio.getUsuario().equals(usuario)) {
            throw new RuntimeException("Este ejercicio no te pertenece");
        }

        ResultadoSeriesEjercicio serie = seriesRepository
                .findByIdAsignadoAndNumeroSerie(dto.getIdAsignado(), dto.getNumeroSerie())
                .orElseGet(() -> {
                    ResultadoSeriesEjercicio nueva = new ResultadoSeriesEjercicio();
                    nueva.setIdAsignado(dto.getIdAsignado());
                    nueva.setNumeroSerie(dto.getNumeroSerie());
                    return nueva;
                });

        // Omitido → todo en 0
        if ("omitido".equals(dto.getStatus())) {
            serie.setRepsCompletadas(0);
            serie.setPesoUsado(0f);
            serie.setDuracionCompletadaSeg(0);
            serie.setDistanciaCompletadaMetros(0f);
            serie.setExitosos(0);
        } else {
            serie.setRepsCompletadas(dto.getRepsCompletadas() != null ? dto.getRepsCompletadas() : 0);
            serie.setPesoUsado(dto.getPesoUsado() != null ? dto.getPesoUsado() : 0f);
            serie.setDuracionCompletadaSeg(dto.getDuracionCompletadaSeg() != null ? dto.getDuracionCompletadaSeg() : 0);
            serie.setDistanciaCompletadaMetros(dto.getDistanciaCompletadaMetros() != null ? dto.getDistanciaCompletadaMetros() : 0f);
            serie.setExitosos(dto.getExitosos()); // null si no aplica
        }

        serie.setStatus(ResultadoSeriesEjercicio.StatusSerie.valueOf(dto.getStatus()));
        serie.setNotas(dto.getNotas());
        serie.setRegistradoEn(LocalDateTime.now());

        ResultadoSeriesEjercicio saved = seriesRepository.save(serie);
        actualizarStatusEjercicioPadre(ejercicio);

        return saved;
    }

    private void actualizarStatusEjercicioPadre(EjerciciosAsignados ejercicio) {
        List<ResultadoSeriesEjercicio> todas =
                seriesRepository.findByIdAsignadoOrderByNumeroSerieAsc(ejercicio.getIdAsignado());

        if (todas.isEmpty()) return;

        long completadas = todas.stream().filter(s -> s.getStatus() == ResultadoSeriesEjercicio.StatusSerie.completado).count();
        long parciales   = todas.stream().filter(s -> s.getStatus() == ResultadoSeriesEjercicio.StatusSerie.parcial).count();
        long omitidas    = todas.stream().filter(s -> s.getStatus() == ResultadoSeriesEjercicio.StatusSerie.omitido).count();
        long total       = todas.size();

        EjerciciosAsignados.StatusEjercicio nuevoStatus;
        if      (completadas == total)               nuevoStatus = EjerciciosAsignados.StatusEjercicio.completado;
        else if (omitidas == total)                  nuevoStatus = EjerciciosAsignados.StatusEjercicio.omitido;
        else if (completadas > 0 || parciales > 0)   nuevoStatus = EjerciciosAsignados.StatusEjercicio.parcial;
        else                                         nuevoStatus = EjerciciosAsignados.StatusEjercicio.pendiente;

        ejercicio.setStatusEjercicio(nuevoStatus);
        ejerciciosRepository.save(ejercicio);
        log.info("Status ejercicio {} → {}", ejercicio.getIdAsignado(), nuevoStatus);
    }

    @Transactional(readOnly = true)
    public List<ResultadoSeriesEjercicio> obtenerSeriesDeEjercicio(Integer idAsignado) {
        return seriesRepository.findByIdAsignadoOrderByNumeroSerieAsc(idAsignado);
    }
}