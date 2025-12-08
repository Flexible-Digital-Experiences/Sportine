package com.sportine.backend.service.impl;

import com.sportine.backend.dto.CompletarEntrenamientoRequestDTO;
import com.sportine.backend.exception.DatosInvalidosException;
import com.sportine.backend.exception.RecursoNoEncontradoException;
import com.sportine.backend.exception.AccesoNoAutorizadoException;
import com.sportine.backend.model.*;
import com.sportine.backend.repository.*;
import com.sportine.backend.service.CompletarEntrenamientoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date; // Import necesario para Publicacion

/**
 * Implementación del servicio para marcar entrenamientos como completados.
 * Actualiza el estado, registra el progreso y guarda feedback opcional.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CompletarEntrenamientoServiceImpl implements CompletarEntrenamientoService {

    private final EntrenamientoRepository entrenamientoRepository;
    private final ProgresoEntrenamientoRepository progresoEntrenamientoRepository;
    private final FeedbackEntrenamientoRepository feedbackEntrenamientoRepository;
    // Repositorio inyectado para la funcionalidad social
    private final PublicacionRepository publicacionRepository;

    @Override
    @Transactional
    public String completarEntrenamiento(CompletarEntrenamientoRequestDTO request, String username) {
        log.info("Usuario {} marcando entrenamiento {} como completado", username, request.getIdEntrenamiento());

        // 1. Buscar el entrenamiento
        Entrenamiento entrenamiento = entrenamientoRepository.findById(request.getIdEntrenamiento())
                .orElseThrow(() -> new RecursoNoEncontradoException("Entrenamiento no encontrado con id: " + request.getIdEntrenamiento()));

        // 2. Validar que pertenece al alumno
        if (!entrenamiento.getUsuario().equals(username)) {
            throw new AccesoNoAutorizadoException("Este entrenamiento no te pertenece");
        }

        // 3. Validar que no esté ya completado
        if (entrenamiento.getEstadoEntrenamiento() == Entrenamiento.EstadoEntrenamiento.finalizado) {
            throw new DatosInvalidosException("Este entrenamiento ya está completado");
        }

        // 4. Actualizar estado del entrenamiento a "finalizado"
        entrenamiento.setEstadoEntrenamiento(Entrenamiento.EstadoEntrenamiento.finalizado);
        entrenamientoRepository.save(entrenamiento);

        // 5. Registrar progreso
        registrarProgreso(request.getIdEntrenamiento(), username);

        // 6. Guardar feedback si viene en el request
        if (request.getComentarios() != null || request.getNivelCansancio() != null) {
            guardarFeedback(request, username);
        }

        // --- MODIFICACIÓN AQUÍ ---
        // Generación automática de la publicación de logro SOLO si el usuario lo pidió (Clic en la Copa)
        if (request.isPublicarLogro()) {
            generarPublicacionAutomatica(entrenamiento, username);
        }
        // -------------------------

        log.info("Entrenamiento {} completado exitosamente por {}", request.getIdEntrenamiento(), username);
        return "Entrenamiento completado exitosamente";
    }

    /**
     * Registra el progreso del entrenamiento
     */
    private void registrarProgreso(Integer idEntrenamiento, String username) {
        // Verificar si ya existe progreso
        ProgresoEntrenamiento progreso = progresoEntrenamientoRepository
                .findByIdEntrenamientoAndUsuario(idEntrenamiento, username)
                .orElse(new ProgresoEntrenamiento());

        // Si es nuevo progreso, setear fecha de inicio
        if (progreso.getIdProgreso() == null) {
            progreso.setIdEntrenamiento(idEntrenamiento);
            progreso.setUsuario(username);
            progreso.setFechaInicio(LocalDateTime.now());
        }

        // Actualizar fecha de finalización y marcar como completado
        progreso.setFechaFinalizacion(LocalDateTime.now());
        progreso.setCompletado(true);

        progresoEntrenamientoRepository.save(progreso);
        log.info("Progreso registrado para entrenamiento {}", idEntrenamiento);
    }

    /**
     * Guarda el feedback opcional del alumno
     */
    private void guardarFeedback(CompletarEntrenamientoRequestDTO request, String username) {
        // Verificar si ya existe feedback
        FeedbackEntrenamiento feedback = feedbackEntrenamientoRepository
                .findByIdEntrenamientoAndUsuario(request.getIdEntrenamiento(), username)
                .orElse(new FeedbackEntrenamiento());

        feedback.setIdEntrenamiento(request.getIdEntrenamiento());
        feedback.setUsuario(username);
        feedback.setComentarios(request.getComentarios());
        feedback.setNivelCansancio(request.getNivelCansancio());
        feedback.setDificultadPercibida(request.getDificultadPercibida());
        feedback.setEstadoAnimo(request.getEstadoAnimo());
        feedback.setFechaFeedback(LocalDateTime.now());

        feedbackEntrenamientoRepository.save(feedback);
        log.info("Feedback guardado para entrenamiento {}", request.getIdEntrenamiento());
    }

    /**
     * Genera una publicación automática basada en la dificultad del entrenamiento
     */
    private void generarPublicacionAutomatica(Entrenamiento entrenamiento, String username) {
        String dificultad = entrenamiento.getDificultad();
        String titulo = entrenamiento.getTituloEntrenamiento();
        String objetivo = entrenamiento.getObjetivo();

        int tipoPublicacion = 1; // 1: Normal, 2: Logro
        String mensaje;

        // Determina si es un logro basado en la dificultad
        if (dificultad != null && (dificultad.equalsIgnoreCase("Dificil") || dificultad.equalsIgnoreCase("Avanzado"))) {
            tipoPublicacion = 2;
            mensaje = "¡Nivel Experto Desbloqueado! 🔥\nCompletó '" + titulo + "' en modo DIFÍCIL.\n🎯 Objetivo cumplido: " + objetivo;
        } else {
            tipoPublicacion = 1;
            mensaje = "Entrenamiento '" + titulo + "' finalizado.\nEnfoque: " + objetivo;
        }

        Publicacion post = new Publicacion();
        post.setUsuario(username);
        post.setDescripcion(mensaje);
        post.setFechaPublicacion(new Date());
        post.setTipo(tipoPublicacion);
        post.setImagen(null);

        publicacionRepository.save(post);
    }
}