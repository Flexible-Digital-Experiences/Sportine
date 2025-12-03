package com.sportine.backend.controler;

import com.sportine.backend.dto.*;
import com.sportine.backend.service.StatisticsEntrenadorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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
     * Clase interna para respuestas de error
     */
    private record ErrorResponse(String error) {}
}