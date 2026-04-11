package com.sportine.backend.service.impl;

import com.sportine.backend.dto.AsignarEjercicioDTO;
import com.sportine.backend.dto.CrearEntrenamientoRequestDTO;
import com.sportine.backend.exception.AccesoNoAutorizadoException;
import com.sportine.backend.exception.RecursoNoEncontradoException;
import com.sportine.backend.model.EjerciciosAsignados;
import com.sportine.backend.model.Entrenamiento;
import com.sportine.backend.model.ResultadoSeriesEjercicio;
import com.sportine.backend.model.Usuario;
import com.sportine.backend.repository.*;
import com.sportine.backend.service.AsignarEntrenamientoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AsignarEntrenamientoServiceImpl implements AsignarEntrenamientoService {

    private final EntrenamientoRepository entrenamientoRepository;
    private final EjerciciosAsignadosRepository ejerciciosAsignadosRepository;
    private final UsuarioRepository usuarioRepository;
    private final ResultadoSeriesEjercicioRepository seriesRepository;
    private final EntrenadorAlumnoRepository entrenadorAlumnoRepository;

    // ── CAMBIOS EN AsignarEntrenamientoServiceImpl.java ──────────────────────────
//
// 1. Agregar el repository en las dependencias de la clase:
//    private final EntrenadorAlumnoRepository entrenadorAlumnoRepository;
//
// 2. Reemplazar SOLO el método crearEntrenamiento() con este:

    @Override
    @Transactional
    public Integer crearEntrenamiento(CrearEntrenamientoRequestDTO request, String usernameEntrenador) {
        usuarioRepository.findByUsuario(request.getUsuarioAlumno())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Alumno no encontrado: " + request.getUsuarioAlumno()));

        Integer idDeporte = entrenadorAlumnoRepository
                .findByUsuarioEntrenadorAndUsuarioAlumnoAndStatusRelacion(
                        usernameEntrenador,
                        request.getUsuarioAlumno(),
                        "activo")
                .stream()
                .findFirst()
                .map(rel -> rel.getIdDeporte())
                .orElse(null);

        log.info("Creando entrenamiento - entrenador: {}, alumno: {}, idDeporte: {}",
                usernameEntrenador, request.getUsuarioAlumno(), idDeporte);

        Entrenamiento entrenamiento = new Entrenamiento();
        entrenamiento.setUsuario(request.getUsuarioAlumno());
        entrenamiento.setUsuarioEntrenador(usernameEntrenador);
        entrenamiento.setIdDeporte(idDeporte); // ✅ NUEVO
        entrenamiento.setTituloEntrenamiento(request.getTituloEntrenamiento());
        entrenamiento.setObjetivo(request.getObjetivo());
        entrenamiento.setDificultad(request.getDificultad());
        entrenamiento.setEstadoEntrenamiento(Entrenamiento.EstadoEntrenamiento.pendiente);
        entrenamiento.setFechaEntrenamiento(request.getFechaEntrenamiento());
        entrenamiento.setHoraEntrenamiento(request.getHoraEntrenamiento());

        Entrenamiento entrenamientoGuardado = entrenamientoRepository.save(entrenamiento);
        guardarEjercicios(request.getEjercicios(),
                entrenamientoGuardado.getIdEntrenamiento(),
                request.getUsuarioAlumno());

        return entrenamientoGuardado.getIdEntrenamiento();
    }

    @Override
    @Transactional
    public void actualizarEntrenamiento(Integer idEntrenamiento,
                                        CrearEntrenamientoRequestDTO request,
                                        String usernameEntrenador) {
        Entrenamiento entrenamiento = entrenamientoRepository.findById(idEntrenamiento)
                .orElseThrow(() -> new RecursoNoEncontradoException("Entrenamiento no encontrado"));

        if (!entrenamiento.getUsuarioEntrenador().equals(usernameEntrenador)) {
            throw new AccesoNoAutorizadoException(
                    "No tienes permiso para editar este entrenamiento");
        }

        entrenamiento.setTituloEntrenamiento(request.getTituloEntrenamiento());
        entrenamiento.setObjetivo(request.getObjetivo());
        entrenamiento.setDificultad(request.getDificultad());
        entrenamiento.setFechaEntrenamiento(request.getFechaEntrenamiento());
        entrenamiento.setHoraEntrenamiento(request.getHoraEntrenamiento());
        entrenamientoRepository.save(entrenamiento);

        ejerciciosAsignadosRepository.deleteByIdEntrenamiento(idEntrenamiento);
        guardarEjercicios(request.getEjercicios(), idEntrenamiento, entrenamiento.getUsuario());
    }

    @Override
    @Transactional
    public void eliminarEntrenamiento(Integer idEntrenamiento, String usernameEntrenador) {
        Entrenamiento entrenamiento = entrenamientoRepository.findById(idEntrenamiento)
                .orElseThrow(() -> new RecursoNoEncontradoException("Entrenamiento no encontrado"));

        if (!entrenamiento.getUsuarioEntrenador().equals(usernameEntrenador)) {
            throw new AccesoNoAutorizadoException(
                    "No tienes permiso para eliminar este entrenamiento");
        }

        ejerciciosAsignadosRepository.deleteByIdEntrenamiento(idEntrenamiento);
        entrenamientoRepository.delete(entrenamiento);
    }

    private void guardarEjercicios(List<AsignarEjercicioDTO> ejercicios,
                                   Integer idEntrenamiento,
                                   String usuarioAlumno) {
        if (ejercicios == null || ejercicios.isEmpty()) return;

        for (AsignarEjercicioDTO dto : ejercicios) {
            EjerciciosAsignados ejercicio = new EjerciciosAsignados();
            ejercicio.setIdEntrenamiento(idEntrenamiento);
            ejercicio.setUsuario(usuarioAlumno);
            ejercicio.setNombreEjercicio(dto.getNombreEjercicio());
            ejercicio.setStatusEjercicio(EjerciciosAsignados.StatusEjercicio.pendiente);
            ejercicio.setSeries(dto.getSeries());
            ejercicio.setRepeticiones(dto.getRepeticiones());
            ejercicio.setPeso(dto.getPeso());
            ejercicio.setDistancia(dto.getDistancia());
            ejercicio.setDuracion(dto.getDuracion());
            // ✅ Propagar el flag de exitosos definido por el entrenador
            ejercicio.setTieneExitosos(
                    dto.getTieneExitosos() != null && dto.getTieneExitosos());

            EjerciciosAsignados guardado = ejerciciosAsignadosRepository.save(ejercicio);

            if (dto.getSeries() != null && dto.getSeries() > 0) {
                for (int numSerie = 1; numSerie <= dto.getSeries(); numSerie++) {
                    ResultadoSeriesEjercicio serie = new ResultadoSeriesEjercicio();
                    serie.setIdAsignado(guardado.getIdAsignado());
                    serie.setNumeroSerie(numSerie);
                    serie.setRepsEsperadas(dto.getRepeticiones());
                    serie.setPesoEsperado(dto.getPeso());
                    if (dto.getDuracion() != null) {
                        serie.setDuracionEsperadaSeg(dto.getDuracion() * 60);
                    }
                    if (dto.getDistancia() != null) {
                        serie.setDistanciaEsperadaMetros(dto.getDistancia());
                    }
                    serie.setStatus(ResultadoSeriesEjercicio.StatusSerie.pendiente);
                    seriesRepository.save(serie);
                }
                log.info("✅ Pre-generadas {} series para ejercicio '{}' (exitosos: {})",
                        dto.getSeries(), dto.getNombreEjercicio(),
                        ejercicio.getTieneExitosos());
            }
        }
    }
}