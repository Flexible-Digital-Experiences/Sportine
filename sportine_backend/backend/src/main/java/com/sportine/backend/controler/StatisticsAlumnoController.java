package com.sportine.backend.controler;

import com.sportine.backend.dto.*;
import com.sportine.backend.service.StatisticsAlumnoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Controller REST para los endpoints de estadísticas del alumno.
 *
 * Endpoints disponibles:
 * - GET /api/alumno/estadisticas/overview - Resumen general
 * - GET /api/alumno/estadisticas/frequency - Frecuencia de entrenamientos
 * - GET /api/alumno/estadisticas/sports-distribution - Distribución por deporte
 * - GET /api/alumno/estadisticas/streak - Información de racha
 * - GET /api/alumno/estadisticas/feedback - Feedback promedio
 */
@RestController
@RequestMapping("/api/alumno/estadisticas")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class StatisticsAlumnoController {

    private final StatisticsAlumnoService statisticsAlumnoService;

    /**
     * Obtiene el resumen general de estadísticas del alumno.
     *
     * GET /api/alumno/estadisticas/overview
     *
     * @param authentication Usuario autenticado
     * @return StatisticsOverviewDTO con métricas principales
     */
    @GetMapping("/overview")
    public ResponseEntity<?> obtenerResumenGeneral(Authentication authentication) {
        try {
            String username = authentication.getName();
            log.info("GET /api/alumno/estadisticas/overview - Usuario: {}", username);

            StatisticsOverviewDTO resumen = statisticsAlumnoService.obtenerResumenGeneral(username);
            return ResponseEntity.ok(resumen);

        } catch (RuntimeException e) {
            log.error("Error al obtener resumen de estadísticas: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            log.error("Error inesperado al obtener estadísticas: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error interno del servidor"));
        }
    }

    /**
     * Obtiene datos de frecuencia de entrenamientos para gráficas.
     *
     * GET /api/alumno/estadisticas/frequency?period=WEEK|MONTH|YEAR
     *
     * @param authentication Usuario autenticado
     * @param period Período: "WEEK", "MONTH", o "YEAR" (default: MONTH)
     * @return TrainingFrequencyDTO con datos para gráfica
     */
    @GetMapping("/frequency")
    public ResponseEntity<?> obtenerFrecuencia(
            Authentication authentication,
            @RequestParam(defaultValue = "MONTH") String period) {
        try {
            String username = authentication.getName();
            log.info("GET /api/alumno/estadisticas/frequency - Usuario: {}, Período: {}", username, period);

            TrainingFrequencyDTO frecuencia = statisticsAlumnoService
                    .obtenerFrecuenciaEntrenamientos(username, period);
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
     * Obtiene la distribución de deportes del alumno.
     *
     * GET /api/alumno/estadisticas/sports-distribution
     *
     * @param authentication Usuario autenticado
     * @return SportsDistributionDTO con datos para gráfica de pastel
     */
    @GetMapping("/sports-distribution")
    public ResponseEntity<?> obtenerDistribucionDeportes(Authentication authentication) {
        try {
            String username = authentication.getName();
            log.info("GET /api/alumno/estadisticas/sports-distribution - Usuario: {}", username);

            SportsDistributionDTO distribucion = statisticsAlumnoService
                    .obtenerDistribucionDeportes(username);
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
     * Obtiene información detallada de la racha del alumno.
     *
     * GET /api/alumno/estadisticas/streak
     *
     * @param authentication Usuario autenticado
     * @return StreakInfoDTO con datos de racha
     */
    @GetMapping("/streak")
    public ResponseEntity<?> obtenerInformacionRacha(Authentication authentication) {
        try {
            String username = authentication.getName();
            log.info("GET /api/alumno/estadisticas/streak - Usuario: {}", username);

            StreakInfoDTO streak = statisticsAlumnoService.obtenerInformacionRacha(username);
            return ResponseEntity.ok(streak);

        } catch (RuntimeException e) {
            log.error("Error al obtener información de racha: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            log.error("Error inesperado: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error interno del servidor"));
        }
    }

    /**
     * Obtiene el feedback promedio del alumno.
     *
     * GET /api/alumno/estadisticas/feedback
     *
     * @param authentication Usuario autenticado
     * @return FeedbackPromedioDTO con promedios
     */
    @GetMapping("/feedback")
    public ResponseEntity<?> obtenerFeedbackPromedio(Authentication authentication) {
        try {
            String username = authentication.getName();
            log.info("GET /api/alumno/estadisticas/feedback - Usuario: {}", username);

            FeedbackPromedioDTO feedback = statisticsAlumnoService.obtenerFeedbackPromedio(username);
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