package com.sportine.backend.controler;

import com.sportine.backend.dto.*;
import com.sportine.backend.repository.EntrenamientoRepository;
import com.sportine.backend.repository.ProgresoEntrenamientoRepository;
import com.sportine.backend.service.impl.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.sportine.backend.repository.DeporteRepository;
import com.sportine.backend.repository.AlumnoDeporteRepository;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/alumno/actividad")
@RequiredArgsConstructor
@Slf4j
public class AlumnoActividadController {

    private final HealthConnectServiceImpl healthConnectService;
    private final ProgresoCompletoServiceImpl progresoCompletoService;
    private final ConexionApiServiceImpl conexionService;
    private final ResultadoSeriesServiceImpl seriesService;
    private final MetricaManualServiceImpl metricaService;
    private final EstadisticasDeporteServiceImpl estadisticasService;
    private final AlumnoDeporteRepository alumnoDeporteRepository;
    private final DeporteRepository deporteRepository;
    private final ProgresoEntrenamientoRepository progresoEntrenamientoRepository;
    private final EntrenamientoRepository entrenamientoRepository;

    // ══════════════════════════════════════════════════════════════════
    // HEALTH CONNECT
    // ══════════════════════════════════════════════════════════════════

    /**
     * Android llama esto después de leer una sesión de Health Connect.
     * Guarda las métricas en Progreso_Entrenamiento.
     *
     * POST /api/alumno/actividad/progreso/health-connect
     */
    @PostMapping("/progreso/health-connect")
    public ResponseEntity<?> sincronizarHealthConnect(
            @RequestBody ProgresoHealthConnectDTO dto,
            Authentication authentication) {
        try {
            String usuario = authentication.getName();
            log.info("POST /progreso/health-connect - Entrenamiento: {} - Usuario: {}",
                    dto.getIdEntrenamiento(), usuario);

            healthConnectService.sincronizarHealthConnect(dto, usuario);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "mensaje", "Datos de Health Connect sincronizados correctamente"
            ));
        } catch (RuntimeException e) {
            log.error("Error sincronizando HC: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "mensaje", e.getMessage()));
        }
    }

    /**
     * Obtiene el progreso completo de un entrenamiento:
     * métricas HC + ejercicios con sus series + métricas manuales del deporte.
     * Usado tanto por Android como por la web de Sportine.
     *
     * GET /api/alumno/actividad/progreso/{idEntrenamiento}
     */
    @GetMapping("/progreso/{idEntrenamiento}")
    public ResponseEntity<?> obtenerProgresoCompleto(
            @PathVariable Integer idEntrenamiento,
            Authentication authentication) {
        try {
            String usuario = authentication.getName();
            log.info("GET /progreso/{} - Usuario: {}", idEntrenamiento, usuario);

            ProgresoCompletoDTO progreso =
                    progresoCompletoService.obtenerProgresoCompleto(idEntrenamiento, usuario);

            return ResponseEntity.ok(progreso);
        } catch (RuntimeException e) {
            log.error("Error obteniendo progreso completo: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "mensaje", e.getMessage()));
        }
    }

    // ══════════════════════════════════════════════════════════════════
    // CONEXIONES CON APIs EXTERNAS
    // ══════════════════════════════════════════════════════════════════

    /**
     * Android llama esto cuando el usuario activa Health Connect en la app.
     * Health Connect no necesita OAuth — solo registramos que está activo.
     *
     * POST /api/alumno/actividad/conexiones/health-connect
     */
    @PostMapping("/conexiones/health-connect")
    public ResponseEntity<?> registrarHealthConnect(Authentication authentication) {
        try {
            String usuario = authentication.getName();
            log.info("POST /conexiones/health-connect - Usuario: {}", usuario);

            ConexionApiDTO dto = new ConexionApiDTO();
            dto.setProveedor("health_connect");
            dto.setEstaConectado(true);
            conexionService.registrarOActualizar(dto, usuario);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "mensaje", "Health Connect registrado correctamente"
            ));
        } catch (Exception e) {
            log.error("Error registrando HC: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "mensaje", e.getMessage()));
        }
    }

    /**
     * Devuelve el estado de todas las conexiones del usuario
     * (health_connect, strava, etc.) con su última sincronización.
     *
     * GET /api/alumno/actividad/conexiones/estado
     */
    @GetMapping("/conexiones/estado")
    public ResponseEntity<?> obtenerEstadoConexiones(Authentication authentication) {
        try {
            String usuario = authentication.getName();
            log.info("GET /conexiones/estado - Usuario: {}", usuario);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "conexiones", conexionService.obtenerConexiones(usuario)
            ));
        } catch (Exception e) {
            log.error("Error obteniendo conexiones: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "mensaje", e.getMessage()));
        }
    }

    /**
     * Desconecta una API externa del usuario.
     * El proveedor debe ser: health_connect, strava o garmin.
     *
     * DELETE /api/alumno/actividad/conexiones/{proveedor}
     */
    @DeleteMapping("/conexiones/{proveedor}")
    public ResponseEntity<?> desconectarApi(
            @PathVariable String proveedor,
            Authentication authentication) {
        try {
            String usuario = authentication.getName();
            log.info("DELETE /conexiones/{} - Usuario: {}", proveedor, usuario);

            ConexionApiDTO dto = new ConexionApiDTO();
            dto.setProveedor(proveedor);
            dto.setEstaConectado(false);
            conexionService.registrarOActualizar(dto, usuario);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "mensaje", proveedor + " desconectado correctamente"
            ));
        } catch (Exception e) {
            log.error("Error desconectando {}: {}", proveedor, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "mensaje", e.getMessage()));
        }
    }

    // ══════════════════════════════════════════════════════════════════
    // SERIES DE EJERCICIOS
    // ══════════════════════════════════════════════════════════════════

    /**
     * El alumno reporta el resultado de una serie específica.
     * Funciona en tiempo real (serie por serie) o al final del entrenamiento.
     * Actualiza automáticamente el status del ejercicio padre.
     *
     * POST /api/alumno/actividad/series/{idAsignado}
     * Body: { numero_serie, reps_completadas, peso_usado, status, notas, ... }
     */
    @PostMapping("/series/{idAsignado}")
    public ResponseEntity<?> guardarResultadoSerie(
            @PathVariable Integer idAsignado,
            @RequestBody ResultadoSerieDTO dto,
            Authentication authentication) {
        try {
            String usuario = authentication.getName();
            dto.setIdAsignado(idAsignado);
            log.info("POST /series/{} - Serie: {} - Usuario: {}",
                    idAsignado, dto.getNumeroSerie(), usuario);

            seriesService.guardarResultadoSerie(dto, usuario);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "mensaje", "Serie " + dto.getNumeroSerie() + " guardada correctamente"
            ));
        } catch (RuntimeException e) {
            log.error("Error guardando serie: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "mensaje", e.getMessage()));
        }
    }

    /**
     * Obtiene todas las series de un ejercicio con sus valores
     * esperados y los resultados que el alumno ya reportó.
     *
     * GET /api/alumno/actividad/series/{idAsignado}
     */
    @GetMapping("/series/{idAsignado}")
    public ResponseEntity<?> obtenerSeries(
            @PathVariable Integer idAsignado,
            Authentication authentication) {
        try {
            log.info("GET /series/{} - Usuario: {}", idAsignado, authentication.getName());
            return ResponseEntity.ok(seriesService.obtenerSeriesDeEjercicio(idAsignado));
        } catch (Exception e) {
            log.error("Error obteniendo series: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "mensaje", e.getMessage()));
        }
    }

    // ══════════════════════════════════════════════════════════════════
    // MÉTRICAS MANUALES DEL DEPORTE
    // ══════════════════════════════════════════════════════════════════

    /**
     * El alumno guarda sus métricas manuales del deporte:
     * goles, tiros libres, vueltas en natación, rounds de boxeo, etc.
     * Soporta múltiples métricas en una sola llamada para minimizar requests.
     *
     * POST /api/alumno/actividad/metricas/manual
     * Body: { id_entrenamiento, metricas: [{ id_plantilla, valor_numerico, numero_serie }] }
     */
    @PostMapping("/metricas/manual")
    public ResponseEntity<?> guardarMetricasManual(
            @RequestBody MetricaManualDTO dto,
            Authentication authentication) {
        try {
            String usuario = authentication.getName();
            log.info("POST /metricas/manual - Entrenamiento: {} - {} métricas - Usuario: {}",
                    dto.getIdEntrenamiento(), dto.getMetricas().size(), usuario);

            metricaService.guardarMetricas(dto, usuario);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "mensaje", "Métricas guardadas correctamente",
                    "total_guardadas", dto.getMetricas().size()
            ));
        } catch (RuntimeException e) {
            log.error("Error guardando métricas manuales: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "mensaje", e.getMessage()));
        }
    }

    /**
     * Obtiene todas las métricas manuales registradas
     * para un entrenamiento específico.
     *
     * GET /api/alumno/actividad/metricas/{idEntrenamiento}
     */
    @GetMapping("/metricas/{idEntrenamiento}")
    public ResponseEntity<?> obtenerMetricas(
            @PathVariable Integer idEntrenamiento,
            Authentication authentication) {
        try {
            String usuario = authentication.getName();
            log.info("GET /metricas/{} - Usuario: {}", idEntrenamiento, usuario);

            return ResponseEntity.ok(
                    metricaService.obtenerMetricasDeEntrenamiento(idEntrenamiento, usuario));
        } catch (Exception e) {
            log.error("Error obteniendo métricas: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "mensaje", e.getMessage()));
        }
    }

    // ══════════════════════════════════════════════════════════════════
    // ESTADÍSTICAS HISTÓRICAS
    // ══════════════════════════════════════════════════════════════════

    /**
     * Devuelve las estadísticas históricas del alumno en un deporte específico.
     * Incluye promedios de Health Connect y evolución temporal para gráficas.
     * Consumible tanto desde Android como desde la web de Sportine.
     *
     * GET /api/alumno/actividad/estadisticas/deporte/{idDeporte}
     */
    @GetMapping("/estadisticas/deporte/{idDeporte}")
    public ResponseEntity<?> obtenerEstadisticasPorDeporte(
            @PathVariable Integer idDeporte,
            Authentication authentication) {
        try {
            String usuario = authentication.getName();
            log.info("GET /estadisticas/deporte/{} - Usuario: {}", idDeporte, usuario);

            CarreraDeporteDTO estadisticas =
                    estadisticasService.obtenerCarreraDeporte(usuario, idDeporte);

            return ResponseEntity.ok(estadisticas);
        } catch (RuntimeException e) {
            log.error("Error obteniendo estadísticas deporte {}: {}", idDeporte, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "mensaje", e.getMessage()));
        }
    }

    /**
     * GET /api/alumno/deportes
     * Devuelve la lista de deportes que practica el alumno autenticado.
     * Se usa en Android para construir los chips de filtro en estadísticas.
     */
    @GetMapping("/alumno-deportes")
    public ResponseEntity<?> obtenerDeportesAlumno(Authentication authentication) {
        try {
            String usuario = authentication.getName();
            log.info("GET /api/alumno/deportes - Usuario: {}", usuario);


            List<Map<String, Object>> deportes = alumnoDeporteRepository
                    .findByUsuario(usuario)
                    .stream()
                    .map(ad -> {
                        Map<String, Object> dto = new HashMap<>();
                        dto.put("id_deporte", ad.getIdDeporte());

                        // Buscar nombre del deporte
                        deporteRepository.findById(ad.getIdDeporte())
                                .ifPresent(d -> dto.put("nombre_deporte", d.getNombreDeporte()));

                        return dto;
                    })
                    .filter(dto -> dto.containsKey("nombre_deporte")) // filtrar si no se encontró
                    .collect(Collectors.toList());

            return ResponseEntity.ok(deportes);

        } catch (Exception e) {
            log.error("Error al obtener deportes del alumno: {}", e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error al obtener deportes"));
        }
    }

    @GetMapping("/historial-deporte/{idDeporte}")
    public ResponseEntity<?> obtenerHistorialDeporte(
            @PathVariable Integer idDeporte,
            @RequestParam(defaultValue = "5") int limite,
            Authentication authentication) {
        try {
            String usuario = authentication.getName();
            log.info("GET /historial-deporte/{} - Usuario: {} - Limite: {}", idDeporte, usuario, limite);

            List<Integer> ids = entrenamientoRepository
                    .findUltimosFinalizadosByUsuarioAndDeporte(usuario, idDeporte, limite);

            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd MMM", new Locale("es", "MX"));

            List<HistorialEntrenamientoDTO> historial = new ArrayList<>();

            for (Integer idEntrenamiento : ids) {
                entrenamientoRepository.findById(idEntrenamiento).ifPresent(e -> {
                    progresoEntrenamientoRepository
                            .findByIdEntrenamientoAndUsuario(idEntrenamiento, usuario)
                            .stream().findFirst()
                            .ifPresent(p -> {
                                HistorialEntrenamientoDTO dto = new HistorialEntrenamientoDTO();
                                dto.setIdEntrenamiento(idEntrenamiento);
                                dto.setTitulo(e.getTituloEntrenamiento());
                                dto.setFecha(e.getFechaEntrenamiento() != null
                                        ? e.getFechaEntrenamiento().format(fmt) : "?");
                                dto.setDificultad(e.getDificultad());
                                dto.setDuracionMin(p.getHcDuracionActivaMin());
                                dto.setCaloriasKcal(p.getHcCaloriasKcal());
                                dto.setDistanciaMetros(p.getHcDistanciaMetros());
                                dto.setPasos(p.getHcPasos());
                                dto.setTieneHc(p.getHcFuenteDatos() != null);
                                historial.add(dto);
                            });
                });
            }

            return ResponseEntity.ok(historial);

        } catch (Exception e) {
            log.error("Error obteniendo historial deporte {}: {}", idDeporte, e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error al obtener historial"));
        }
    }
}