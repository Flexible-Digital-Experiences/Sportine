package com.sportine.backend.controler;

import com.sportine.backend.client.FastApiClient;
import com.sportine.backend.dto.AjusteRutinaDTO;
import com.sportine.backend.dto.PatronesDTO;
import com.sportine.backend.dto.PrediccionProgresoDTO;
import com.sportine.backend.dto.SportineScoreDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Gateway REST que expone al frontend los resultados del microservicio FastAPI de IA.
 *
 * ┌─────────────┐   JWT + HTTPS    ┌──────────────────────┐   secreto interno   ┌─────────────┐
 * │  Navegador  │ ───────────────► │    Spring Boot        │ ──────────────────► │   FastAPI   │
 * │  (Frontend) │ ◄─────────────── │  (este controller)    │ ◄────────────────── │  :8001      │
 * └─────────────┘                  └──────────────────────┘                      └─────────────┘
 *
 * El frontend NUNCA llama a FastAPI directamente.
 * Spring Boot valida el JWT del usuario y luego agrega el secreto interno
 * automáticamente vía FastApiClient antes de llamar a FastAPI.
 *
 * Endpoints expuestos (en orden de prioridad del plan):
 *   GET /api/alumno/sportine-score                               → Módulo 1: Sportine Score
 *   GET /api/entrenador/estadisticas/ajuste-rutina/{usuario}     → Módulo 3: Ajuste de rutina
 *   GET /api/alumno/estadisticas/prediccion?idDeporte=&dias=     → Módulo 4: Predicción de progreso
 *   GET /api/alumno/estadisticas/patrones                        → Módulo 5: Análisis de patrones
 *
 * Nota: el Módulo 2 (Recomendación de entrenadores, POST) se implementará en BuscarController
 * cuando ese archivo esté disponible para revisión.
 *
 * Ubicación esperada:
 *   src/main/java/com/sportine/backend/controler/FastApiGatewayController.java
 *
 * REQUISITOS para correr:
 *   1. FastAPI corriendo en localhost:8001  →  cd sportine-ai && python main.py
 *   2. Spring Boot corriendo en localhost:8080
 *   3. application.properties con fastapi.url y fastapi.secret
 */
@RestController
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class FastApiGatewayController {

    private final FastApiClient fastApiClient;

    // ═════════════════════════════════════════════════════════════
    // MÓDULO 1 — Sportine Score
    //
    // El alumno consulta su propio score. Spring Boot extrae el
    // usuario del JWT, nunca del path (un alumno no puede ver el
    // score de otro alumno).
    // ═════════════════════════════════════════════════════════════

    /**
     * Calcula el Sportine Score del alumno autenticado.
     *
     * GET /api/alumno/sportine-score
     * → FastAPI: GET /sportine-score/{usuario}
     *
     * Respuesta exitosa (200):
     * {
     *   "usuario": "alumno_test",
     *   "sportine_score": 73.5,
     *   "desglose": {
     *     "constancia": 22.0, "completitud": 16.0, "esfuerzo": 15.0,
     *     "carrera": 14.5, "actividad_hc": 6.0
     *   },
     *   "nivel": "Avanzado"
     * }
     */
    @GetMapping("/api/alumno/sportine-score")
    public ResponseEntity<?> getSportineScore(Authentication authentication) {
        try {
            String usuario = authentication.getName();
            log.info("GET /api/alumno/sportine-score — Usuario: {}", usuario);

            SportineScoreDTO score = fastApiClient.get(
                    "/sportine-score/" + usuario,
                    SportineScoreDTO.class
            );

            return ResponseEntity.ok(score);

        } catch (RuntimeException e) {
            log.error("Error en Sportine Score: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            log.error("Error inesperado en Sportine Score: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error interno del servidor"));
        }
    }

    // ═════════════════════════════════════════════════════════════
    // MÓDULO 3 — Ajuste de Rutinas (vista del entrenador)
    //
    // El entrenador ve la recomendación de ajuste de carga para
    // uno de sus alumnos. El usuarioAlumno viene en el path.
    // El entrenador se autentica con JWT como en cualquier otro
    // endpoint, pero no necesitamos su username aquí.
    // ═════════════════════════════════════════════════════════════

    /**
     * Recomienda ajuste de intensidad para un alumno (vista del entrenador).
     *
     * GET /api/entrenador/estadisticas/ajuste-rutina/{usuarioAlumno}?nSesiones=5
     * → FastAPI: GET /ajuste-rutina/{usuarioAlumno}?n_sesiones=5
     *
     * @param usuarioAlumno Username del alumno a analizar (path variable)
     * @param nSesiones     Número de sesiones a analizar (default 5, mín 2, máx 20)
     *
     * Respuesta exitosa (200):
     * {
     *   "usuario": "alumno_test",
     *   "recomendacion": "revisar_motivacion",
     *   "mensaje": "Detectamos 4 sesiones con estado de ánimo negativo...",
     *   "metricas_base": {
     *     "prom_cansancio": 6.2, "prom_dificultad": 5.8,
     *     "prom_fc_max": null, "n_sesiones_analizadas": 5
     *   }
     * }
     */
    @GetMapping("/api/entrenador/estadisticas/ajuste-rutina/{usuarioAlumno}")
    public ResponseEntity<?> getAjusteRutina(
            @PathVariable String usuarioAlumno,
            @RequestParam(defaultValue = "5") Integer nSesiones
    ) {
        try {
            log.info("GET /api/entrenador/estadisticas/ajuste-rutina/{} — nSesiones: {}",
                    usuarioAlumno, nSesiones);

            // FastAPI espera n_sesiones (snake_case) como query param
            String path = "/ajuste-rutina/" + usuarioAlumno + "?n_sesiones=" + nSesiones;
            AjusteRutinaDTO ajuste = fastApiClient.get(path, AjusteRutinaDTO.class);

            return ResponseEntity.ok(ajuste);

        } catch (RuntimeException e) {
            log.error("Error en ajuste de rutina para {}: {}", usuarioAlumno, e.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            log.error("Error inesperado en ajuste de rutina: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error interno del servidor"));
        }
    }

    // ═════════════════════════════════════════════════════════════
    // MÓDULO 4 — Predicción de Progreso
    //
    // El alumno ve la proyección de sus métricas a N días para
    // un deporte específico. Usa regresión lineal de Scikit-learn.
    // ═════════════════════════════════════════════════════════════

    /**
     * Predice la evolución de las métricas del alumno en un deporte.
     *
     * GET /api/alumno/estadisticas/prediccion?idDeporte=1&dias=30
     * → FastAPI: GET /prediccion-progreso/{usuario}?id_deporte=1&dias=30
     *
     * @param idDeporte ID del deporte a proyectar (requerido)
     * @param dias      Días de proyección: mín 7, máx 90 (default 30)
     *
     * Respuesta exitosa (200):
     * {
     *   "usuario": "alumno_test",
     *   "id_deporte": 1,
     *   "predicciones": [
     *     {
     *       "nombre_metrica": "goles",
     *       "prediccion": {
     *         "tendencia_por_dia": 0.1257,
     *         "r2_confianza": 0.881,
     *         "proyeccion_30_dias": [ { "dia": 1, "valor_proyectado": 12.4 }, ... ]
     *       },
     *       "mejor_sesion_actual": 5.0,
     *       "dias_para_superar_record": 12,
     *       "mensaje": "Tendencia positiva con alta confianza."
     *     }
     *   ]
     * }
     */
    @GetMapping("/api/alumno/estadisticas/prediccion")
    public ResponseEntity<?> getPrediccionProgreso(
            Authentication authentication,
            @RequestParam Integer idDeporte,
            @RequestParam(defaultValue = "30") Integer dias
    ) {
        try {
            String usuario = authentication.getName();
            log.info("GET /api/alumno/estadisticas/prediccion — Usuario: {}, Deporte: {}, Días: {}",
                    usuario, idDeporte, dias);

            // FastAPI espera id_deporte y dias como query params en snake_case
            String path = "/prediccion-progreso/" + usuario
                    + "?id_deporte=" + idDeporte
                    + "&dias=" + dias;

            PrediccionProgresoDTO prediccion = fastApiClient.get(
                    path, PrediccionProgresoDTO.class
            );

            return ResponseEntity.ok(prediccion);

        } catch (RuntimeException e) {
            log.error("Error en predicción de progreso: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            log.error("Error inesperado en predicción de progreso: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error interno del servidor"));
        }
    }

    // ═════════════════════════════════════════════════════════════
    // MÓDULO 5 — Análisis de Patrones
    //
    // El alumno ve sus patrones de entrenamiento detectados por
    // Pandas: mejor día, consistencia, correlación ánimo-calorías.
    // ═════════════════════════════════════════════════════════════

    /**
     * Analiza los patrones de entrenamiento del alumno autenticado.
     *
     * GET /api/alumno/estadisticas/patrones
     * → FastAPI: GET /patrones/{usuario}
     *
     * Respuesta exitosa (200):
     * {
     *   "usuario": "alumno_test",
     *   "mejor_dia_semana": "sábado",
     *   "indice_consistencia_pct": 37.5,
     *   "correlacion_animo_calorias": -0.74,
     *   "frecuencia_promedio_dias": 2.8,
     *   "total_sesiones_analizadas": 15,
     *   "mensaje": null
     * }
     */
    @GetMapping("/api/alumno/estadisticas/patrones")
    public ResponseEntity<?> getPatrones(Authentication authentication) {
        try {
            String usuario = authentication.getName();
            log.info("GET /api/alumno/estadisticas/patrones — Usuario: {}", usuario);

            PatronesDTO patrones = fastApiClient.get(
                    "/patrones/" + usuario,
                    PatronesDTO.class
            );

            return ResponseEntity.ok(patrones);

        } catch (RuntimeException e) {
            log.error("Error en análisis de patrones: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            log.error("Error inesperado en análisis de patrones: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error interno del servidor"));
        }
    }

    // ─────────────────────────────────────────────────────────────
    // Record interno — mismo patrón que usa EstadisticasDeporteController
    // ─────────────────────────────────────────────────────────────
    private record ErrorResponse(String error) {}
}