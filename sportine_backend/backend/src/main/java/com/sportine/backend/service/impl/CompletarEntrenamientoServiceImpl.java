package com.sportine.backend.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sportine.backend.dto.CompletarEntrenamientoRequestDTO;
import com.sportine.backend.exception.DatosInvalidosException;
import com.sportine.backend.exception.RecursoNoEncontradoException;
import com.sportine.backend.exception.AccesoNoAutorizadoException;
import com.sportine.backend.model.*;
import com.sportine.backend.repository.*;
import com.sportine.backend.service.CompletarEntrenamientoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompletarEntrenamientoServiceImpl implements CompletarEntrenamientoService {

    private final EntrenamientoRepository entrenamientoRepository;
    private final ProgresoEntrenamientoRepository progresoEntrenamientoRepository;
    private final FeedbackEntrenamientoRepository feedbackEntrenamientoRepository;
    private final PublicacionRepository publicacionRepository;
    private final EjerciciosAsignadosRepository ejerciciosRepository;
    private final ResultadoSeriesEjercicioRepository seriesRepository;
    private final ResultadoMetricaManualRepository metricaManualRepository;
    private final PlantillaMetricasDeporteRepository plantillaRepository;
    private final DeporteRepository deporteRepository;
    private final CarreraDeportivaService carreraDeportivaService;
    private final LogroDesbloqueadoRepository logroRepository;

    @Value("${n8n.webhook.url:http://localhost:5678/webhook/metricasAI}")
    private String n8nWebhookUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    @Transactional
    public String completarEntrenamiento(CompletarEntrenamientoRequestDTO request, String username) {
        log.info("Usuario {} marcando entrenamiento {} como completado",
                username, request.getIdEntrenamiento());

        Entrenamiento entrenamiento = entrenamientoRepository.findById(request.getIdEntrenamiento())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Entrenamiento no encontrado con id: " + request.getIdEntrenamiento()));

        if (!entrenamiento.getUsuario().equals(username)) {
            throw new AccesoNoAutorizadoException("Este entrenamiento no te pertenece");
        }

        if (entrenamiento.getEstadoEntrenamiento() == Entrenamiento.EstadoEntrenamiento.finalizado) {
            throw new DatosInvalidosException("Este entrenamiento ya está completado");
        }

        entrenamiento.setEstadoEntrenamiento(Entrenamiento.EstadoEntrenamiento.finalizado);
        entrenamientoRepository.save(entrenamiento);

        registrarProgreso(request.getIdEntrenamiento(), username);

        if (request.getComentarios() != null || request.getNivelCansancio() != null) {
            guardarFeedback(request, username);
        }

        if (request.isPublicarLogro()) {
            generarPublicacionAutomatica(entrenamiento, username);
        }

        try {
            dispararWebhookMetricas(entrenamiento, username);
        } catch (Exception e) {
            log.warn("No se pudo disparar webhook de métricas para entrenamiento {}: {}",
                    request.getIdEntrenamiento(), e.getMessage());
        }

        log.info("Entrenamiento {} completado exitosamente por {}",
                request.getIdEntrenamiento(), username);
        return "Entrenamiento completado exitosamente";
    }

    // ── n8n webhook ───────────────────────────────────────────────────────────

    private void dispararWebhookMetricas(Entrenamiento entrenamiento, String usuario) {
        Integer idEntrenamiento = entrenamiento.getIdEntrenamiento();
        Integer idDeporte = entrenamiento.getIdDeporte();

        String nombreDeporte = deporteRepository.findById(idDeporte)
                .map(Deporte::getNombreDeporte)
                .orElse("General");

        List<String> metricasEsperadas = plantillaRepository
                .findByIdDeporteOrderByOrdenDisplayAsc(idDeporte)
                .stream()
                .filter(p -> p.getFuente() == PlantillaMetricasDeporte.Fuente.calculada)
                .map(PlantillaMetricasDeporte::getNombreMetrica)
                .collect(Collectors.toList());

        if (metricasEsperadas.isEmpty()) {
            log.info("Deporte {} no tiene métricas calculadas, omitiendo webhook n8n", nombreDeporte);
            return;
        }

        List<EjerciciosAsignados> ejercicios =
                ejerciciosRepository.findByIdEntrenamientoOrderByIdAsignadoAsc(idEntrenamiento);

        List<Map<String, Object>> ejerciciosJson = ejercicios.stream()
                .filter(e -> e.getStatusEjercicio() != EjerciciosAsignados.StatusEjercicio.pendiente)
                .map(e -> {
                    List<ResultadoSeriesEjercicio> series =
                            seriesRepository.findByIdAsignadoOrderByNumeroSerieAsc(e.getIdAsignado());
                    List<Map<String, Object>> seriesJson = series.stream()
                            .filter(s -> s.getStatus() != ResultadoSeriesEjercicio.StatusSerie.pendiente)
                            .map(s -> {
                                Map<String, Object> sm = new HashMap<>();
                                sm.put("numeroSerie", s.getNumeroSerie());
                                sm.put("repsCompletadas", s.getRepsCompletadas());
                                sm.put("exitosos", s.getExitosos());
                                sm.put("status", s.getStatus().name());
                                return sm;
                            })
                            .collect(Collectors.toList());
                    Map<String, Object> em = new HashMap<>();
                    em.put("nombre", e.getNombreEjercicio());
                    em.put("series", seriesJson);
                    return em;
                })
                .filter(e -> !((List<?>) e.get("series")).isEmpty())
                .collect(Collectors.toList());

        Map<String, Object> payload = new HashMap<>();
        payload.put("idEntrenamiento", idEntrenamiento);
        payload.put("usuario", usuario);
        payload.put("deporte", nombreDeporte);
        payload.put("idDeporte", idDeporte);
        payload.put("metricasEsperadas", metricasEsperadas);
        payload.put("ejercicios", ejerciciosJson);

        log.info("Mandando webhook a n8n para entrenamiento {} - {} ejercicios",
                idEntrenamiento, ejerciciosJson.size());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(payload, headers);

        ResponseEntity<List> response = restTemplate.exchange(
                n8nWebhookUrl, HttpMethod.POST, httpEntity, List.class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            List<Map<String, Object>> responseBody = response.getBody();
            if (!responseBody.isEmpty()) {
                guardarMetricasDeAgente(responseBody.get(0), idEntrenamiento, idDeporte, usuario);
            }
        } else {
            log.warn("n8n respondió con status: {}", response.getStatusCode());
        }
    }

    // ── Guardar métricas + actualizar carrera + logros ────────────────────────

    @SuppressWarnings("unchecked")
    private void guardarMetricasDeAgente(Map<String, Object> resultado,
                                         Integer idEntrenamiento,
                                         Integer idDeporte,
                                         String usuario) {
        List<Map<String, Object>> metricas = (List<Map<String, Object>>) resultado.get("metricas");
        if (metricas == null || metricas.isEmpty()) {
            log.warn("El agente no regresó métricas para entrenamiento {}", idEntrenamiento);
            return;
        }

        int guardadas = 0;
        for (Map<String, Object> metrica : metricas) {
            String nombreMetrica = (String) metrica.get("nombre_metrica");
            Number valorNum      = (Number) metrica.get("valor");
            if (nombreMetrica == null || valorNum == null) continue;

            // ✅ Fix: final para que el lambda pueda capturarlo sin problemas
            final double valor = valorNum.doubleValue();

            // 1. Guardar en Resultado_Metrica_Manual
            plantillaRepository.findByIdDeporteAndNombreMetrica(idDeporte, nombreMetrica)
                    .ifPresent(plantilla -> {
                        ResultadoMetricaManual existente = metricaManualRepository
                                .findByIdEntrenamientoAndIdPlantillaAndUsuarioAndNumeroSerie(
                                        idEntrenamiento, plantilla.getIdPlantilla(), usuario, null)
                                .orElseGet(() -> {
                                    ResultadoMetricaManual nuevo = new ResultadoMetricaManual();
                                    nuevo.setIdEntrenamiento(idEntrenamiento);
                                    nuevo.setIdPlantilla(plantilla.getIdPlantilla());
                                    nuevo.setUsuario(usuario);
                                    nuevo.setNumeroSerie(null);
                                    return nuevo;
                                });
                        existente.setValorNumerico((float)valor);
                        existente.setRegistradoEn(LocalDateTime.now());
                        existente.setNotas("Calculado por agente n8n");
                        metricaManualRepository.save(existente);
                    });

            // 2. Actualizar carrera histórica y detectar logros
            try {
                List<String> logrosDesbloqueados = carreraDeportivaService
                        .actualizarCarrera(usuario, idDeporte, nombreMetrica, valor, idEntrenamiento);

                // 3. Guardar cada logro en Logro_Desbloqueado
                for (String mensajeLogro : logrosDesbloqueados) {
                    LogroDesbloqueado logro = new LogroDesbloqueado();
                    logro.setUsuario(usuario);
                    logro.setIdDeporte(idDeporte);
                    logro.setIdEntrenamiento(idEntrenamiento);
                    logro.setNombreMetrica(nombreMetrica);
                    logro.setMensaje(mensajeLogro);
                    logro.setPublicado(false);
                    logroRepository.save(logro);
                    log.info("🏆 Logro guardado para {}: {}", usuario, mensajeLogro);
                }
            } catch (Exception e) {
                log.warn("Error actualizando carrera para métrica {}: {}",
                        nombreMetrica, e.getMessage());
            }

            guardadas++;
        }

        log.info("✅ {} métricas guardadas para entrenamiento {} por agente n8n",
                guardadas, idEntrenamiento);
    }

    // ── Métodos existentes ────────────────────────────────────────────────────

    private void registrarProgreso(Integer idEntrenamiento, String username) {
        ProgresoEntrenamiento progreso = progresoEntrenamientoRepository
                .findByIdEntrenamientoAndUsuario(idEntrenamiento, username)
                .orElse(new ProgresoEntrenamiento());

        if (progreso.getIdProgreso() == null) {
            progreso.setIdEntrenamiento(idEntrenamiento);
            progreso.setUsuario(username);
        }

        progreso.setFechaFinalizacion(LocalDateTime.now());
        progreso.setCompletado(true);
        progresoEntrenamientoRepository.save(progreso);
        log.info("Progreso registrado para entrenamiento {}", idEntrenamiento);
    }

    private void guardarFeedback(CompletarEntrenamientoRequestDTO request, String username) {
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

    private void generarPublicacionAutomatica(Entrenamiento entrenamiento, String username) {
        String dificultad = entrenamiento.getDificultad();
        String titulo     = entrenamiento.getTituloEntrenamiento();
        String objetivo   = entrenamiento.getObjetivo();

        int tipoPublicacion;
        String mensaje;

        if (dificultad != null && (dificultad.equalsIgnoreCase("Dificil")
                || dificultad.equalsIgnoreCase("Avanzado"))) {
            tipoPublicacion = 2;
            mensaje = "¡Nivel Experto Desbloqueado! 🔥\nCompletó '" + titulo
                    + "' en modo DIFÍCIL.\n🎯 Objetivo cumplido: " + objetivo;
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