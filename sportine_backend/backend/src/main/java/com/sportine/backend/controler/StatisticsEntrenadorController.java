package com.sportine.backend.controler;

import com.sportine.backend.dto.*;
import com.sportine.backend.service.StatisticsEntrenadorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.sportine.backend.dto.CarreraDeporteDTO;
import com.sportine.backend.dto.DeporteAlumnoDTO;
import com.sportine.backend.dto.HistorialEntrenamientoDTO;
import com.sportine.backend.dto.MetricasUltimosDTO;

import java.util.List;

/**
 * Controller REST para los endpoints de estadísticas del entrenador.
 * Permite al entrenador ver estadísticas de sus alumnos.
 *
 * Endpoints disponibles:
 * - GET /api/entrenador/estadisticas/alumnos - Lista de alumnos con métricas
 * - GET /api/entrenador/estadisticas/alumno/{usuario} - Detalle de un alumno
 * - GET /api/entrenador/estadisticas/alumno/{usuario}/frequency - Frecuencia del alumno
 * - GET /api/entrenador/estadisticas/alumno/{usuario}/sports - Deportes del alumno
 * - GET /api/entrenador/estadisticas/alumno/{usuario}/feedback - Feedback del alumno
 */
@RestController
@RequestMapping("/api/entrenador/estadisticas")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class StatisticsEntrenadorController {

    private final StatisticsEntrenadorService statisticsEntrenadorService;

    /**
     * Obtiene lista resumida de todos los alumnos del entrenador con sus métricas.
     *
     * GET /api/entrenador/estadisticas/alumnos
     *
     * @param authentication Usuario autenticado (entrenador)
     * @return Lista de AlumnoCardStatsDTO
     */
    @GetMapping("/alumnos")
    public ResponseEntity<?> obtenerResumenAlumnos(Authentication authentication) {
        try {
            String usuarioEntrenador = authentication.getName();
            log.info("GET /api/entrenador/estadisticas/alumnos - Entrenador: {}", usuarioEntrenador);

            List<AlumnoCardStatsDTO> alumnos = statisticsEntrenadorService
                    .obtenerResumenAlumnos(usuarioEntrenador);

            return ResponseEntity.ok(alumnos);

        } catch (RuntimeException e) {
            log.error("Error al obtener resumen de alumnos: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            log.error("Error inesperado: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error interno del servidor"));
        }
    }

    /**
     * Obtiene las estadísticas detalladas de un alumno específico.
     *
     * GET /api/entrenador/estadisticas/alumno/{usuarioAlumno}
     *
     * @param authentication Usuario autenticado (entrenador)
     * @param usuarioAlumno Username del alumno
     * @return DetalleEstadisticasAlumnoDTO con información completa
     */
    @GetMapping("/alumno/{usuarioAlumno}")
    public ResponseEntity<?> obtenerDetalleAlumno(
            Authentication authentication,
            @PathVariable String usuarioAlumno) {
        try {
            String usuarioEntrenador = authentication.getName();
            log.info("GET /api/entrenador/estadisticas/alumno/{} - Entrenador: {}",
                    usuarioAlumno, usuarioEntrenador);

            DetalleEstadisticasAlumnoDTO detalle = statisticsEntrenadorService
                    .obtenerDetalleEstadisticasAlumno(usuarioEntrenador, usuarioAlumno);

            return ResponseEntity.ok(detalle);

        } catch (RuntimeException e) {
            log.error("Error al obtener detalle del alumno: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            log.error("Error inesperado: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error interno del servidor"));
        }
    }

    /**
     * Obtiene la frecuencia de entrenamientos de un alumno.
     *
     * GET /api/entrenador/estadisticas/alumno/{usuarioAlumno}/frequency?period=WEEK|MONTH|YEAR
     *
     * @param authentication Usuario autenticado (entrenador)
     * @param usuarioAlumno Username del alumno
     * @param period Período (default: MONTH)
     * @return TrainingFrequencyDTO
     */
    @GetMapping("/alumno/{usuarioAlumno}/frequency")
    public ResponseEntity<?> obtenerFrecuenciaAlumno(
            Authentication authentication,
            @PathVariable String usuarioAlumno,
            @RequestParam(defaultValue = "MONTH") String period) {
        try {
            String usuarioEntrenador = authentication.getName();
            log.info("GET /api/entrenador/estadisticas/alumno/{}/frequency - Entrenador: {}, Período: {}",
                    usuarioAlumno, usuarioEntrenador, period);

            TrainingFrequencyDTO frecuencia = statisticsEntrenadorService
                    .obtenerFrecuenciaAlumno(usuarioEntrenador, usuarioAlumno, period);

            return ResponseEntity.ok(frecuencia);

        } catch (IllegalArgumentException e) {
            log.error("Período inválido: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Período inválido. Use: WEEK, MONTH o YEAR"));
        } catch (RuntimeException e) {
            log.error("Error al obtener frecuencia: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            log.error("Error inesperado: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error interno del servidor"));
        }
    }

    /**
     * Obtiene la distribución de deportes de un alumno.
     *
     * GET /api/entrenador/estadisticas/alumno/{usuarioAlumno}/sports
     *
     * @param authentication Usuario autenticado (entrenador)
     * @param usuarioAlumno Username del alumno
     * @return SportsDistributionDTO
     */
    @GetMapping("/alumno/{usuarioAlumno}/sports")
    public ResponseEntity<?> obtenerDistribucionDeportesAlumno(
            Authentication authentication,
            @PathVariable String usuarioAlumno) {
        try {
            String usuarioEntrenador = authentication.getName();
            log.info("GET /api/entrenador/estadisticas/alumno/{}/sports - Entrenador: {}",
                    usuarioAlumno, usuarioEntrenador);

            SportsDistributionDTO distribucion = statisticsEntrenadorService
                    .obtenerDistribucionDeportesAlumno(usuarioEntrenador, usuarioAlumno);

            return ResponseEntity.ok(distribucion);

        } catch (RuntimeException e) {
            log.error("Error al obtener distribución de deportes: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            log.error("Error inesperado: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error interno del servidor"));
        }
    }

    /**
     * Obtiene el feedback promedio de un alumno.
     *
     * GET /api/entrenador/estadisticas/alumno/{usuarioAlumno}/feedback
     *
     * @param authentication Usuario autenticado (entrenador)
     * @param usuarioAlumno Username del alumno
     * @return FeedbackPromedioDTO
     */
    @GetMapping("/alumno/{usuarioAlumno}/feedback")
    public ResponseEntity<?> obtenerFeedbackAlumno(
            Authentication authentication,
            @PathVariable String usuarioAlumno) {
        try {
            String usuarioEntrenador = authentication.getName();
            log.info("GET /api/entrenador/estadisticas/alumno/{}/feedback - Entrenador: {}",
                    usuarioAlumno, usuarioEntrenador);

            FeedbackPromedioDTO feedback = statisticsEntrenadorService
                    .obtenerFeedbackAlumno(usuarioEntrenador, usuarioAlumno);

            return ResponseEntity.ok(feedback);

        } catch (RuntimeException e) {
            log.error("Error al obtener feedback: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            log.error("Error inesperado: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error interno del servidor"));
        }
    }
    /**
     * Deportes que el entrenador imparte a un alumno específico.
     * Filtra por Entrenador_Alumno para que el entrenador solo vea
     * los deportes que él le da a ese alumno.
     *
     * GET /api/entrenador/estadisticas/alumno/{usuarioAlumno}/deportes
     */
    @GetMapping("/alumno/{usuarioAlumno}/deportes")
    public ResponseEntity<?> obtenerDeportesParaAlumno(
            Authentication authentication,
            @PathVariable String usuarioAlumno) {
        try {
            String usuarioEntrenador = authentication.getName();
            log.info("GET /alumno/{}/deportes - Entrenador: {}", usuarioAlumno, usuarioEntrenador);

            List<DeporteAlumnoDTO> deportes = statisticsEntrenadorService
                    .obtenerDeportesEntrenadorParaAlumno(usuarioEntrenador, usuarioAlumno);

            return ResponseEntity.ok(deportes);
        } catch (RuntimeException e) {
            log.error("Error al obtener deportes para alumno: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            log.error("Error inesperado: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error interno del servidor"));
        }
    }

    /**
     * Carrera (3 cards) de un alumno en un deporte específico.
     * El entrenador solo puede ver deportes que él imparte.
     *
     * GET /api/entrenador/estadisticas/alumno/{usuarioAlumno}/carrera/{idDeporte}
     */
    @GetMapping("/alumno/{usuarioAlumno}/carrera/{idDeporte}")
    public ResponseEntity<?> obtenerCarreraAlumno(
            Authentication authentication,
            @PathVariable String usuarioAlumno,
            @PathVariable Integer idDeporte) {
        try {
            String usuarioEntrenador = authentication.getName();
            log.info("GET /alumno/{}/carrera/{} - Entrenador: {}", usuarioAlumno, idDeporte, usuarioEntrenador);

            CarreraDeporteDTO carrera = statisticsEntrenadorService
                    .obtenerCarreraAlumno(usuarioEntrenador, usuarioAlumno, idDeporte);

            return ResponseEntity.ok(carrera);
        } catch (RuntimeException e) {
            log.error("Error al obtener carrera alumno: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            log.error("Error inesperado: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error interno del servidor"));
        }
    }

    /**
     * Métricas (gráficas) de un alumno en un deporte específico.
     *
     * GET /api/entrenador/estadisticas/alumno/{usuarioAlumno}/metricas?idDeporte=&limite=
     */
    @GetMapping("/alumno/{usuarioAlumno}/metricas")
    public ResponseEntity<?> obtenerMetricasAlumno(
            Authentication authentication,
            @PathVariable String usuarioAlumno,
            @RequestParam Integer idDeporte,
            @RequestParam(defaultValue = "5") int limite) {
        try {
            String usuarioEntrenador = authentication.getName();
            log.info("GET /alumno/{}/metricas?idDeporte={} - Entrenador: {}", usuarioAlumno, idDeporte, usuarioEntrenador);

            MetricasUltimosDTO metricas = statisticsEntrenadorService
                    .obtenerMetricasAlumno(usuarioEntrenador, usuarioAlumno, idDeporte, limite);

            return ResponseEntity.ok(metricas);
        } catch (RuntimeException e) {
            log.error("Error al obtener métricas alumno: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            log.error("Error inesperado: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error interno del servidor"));
        }
    }

    /**
     * Historial de últimos entrenamientos de un alumno en un deporte.
     *
     * GET /api/entrenador/estadisticas/alumno/{usuarioAlumno}/historial/{idDeporte}?limite=5
     */
    @GetMapping("/alumno/{usuarioAlumno}/historial/{idDeporte}")
    public ResponseEntity<?> obtenerHistorialAlumno(
            Authentication authentication,
            @PathVariable String usuarioAlumno,
            @PathVariable Integer idDeporte,
            @RequestParam(defaultValue = "5") int limite) {
        try {
            String usuarioEntrenador = authentication.getName();
            log.info("GET /alumno/{}/historial/{} - Entrenador: {}", usuarioAlumno, idDeporte, usuarioEntrenador);

            List<HistorialEntrenamientoDTO> historial = statisticsEntrenadorService
                    .obtenerHistorialAlumno(usuarioEntrenador, usuarioAlumno, idDeporte, limite);

            return ResponseEntity.ok(historial);
        } catch (RuntimeException e) {
            log.error("Error al obtener historial alumno: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            log.error("Error inesperado: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error interno del servidor"));
        }
    }

    /**
     * Clase interna para respuestas de error
     */
    private record ErrorResponse(String error) {}
}