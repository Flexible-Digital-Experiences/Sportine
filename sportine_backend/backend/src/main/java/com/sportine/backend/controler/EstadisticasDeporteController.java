// ── EstadisticasDeporteController.java ───────────────────────────────────────
package com.sportine.backend.controler;

import com.sportine.backend.dto.CarreraDeporteDTO;
import com.sportine.backend.dto.MetricasUltimosDTO;
import com.sportine.backend.service.EstadisticasDeporteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/alumno/estadisticas")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class EstadisticasDeporteController {

    private final EstadisticasDeporteService estadisticasDeporteService;

    /**
     * GET /api/alumno/estadisticas/carrera?idDeporte=1
     * Devuelve las 3 cards con estadísticas acumuladas de carrera para el deporte.
     */
    @GetMapping("/carrera")
    public ResponseEntity<?> obtenerCarrera(
            @RequestParam Integer idDeporte,
            Authentication auth) {
        try {
            String usuario = auth.getName();
            log.info("GET /api/alumno/estadisticas/carrera - Usuario: {}, Deporte: {}",
                    usuario, idDeporte);
            CarreraDeporteDTO dto = estadisticasDeporteService
                    .obtenerCarreraDeporte(usuario, idDeporte);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            log.error("Error al obtener carrera: {}", e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(new ErrorResponse("Error al obtener estadísticas de carrera"));
        }
    }

    /**
     * GET /api/alumno/estadisticas/metricas-deporte?idDeporte=1&limite=5
     * Devuelve las métricas de los últimos N entrenamientos para gráficas.
     */
    @GetMapping("/metricas-deporte")
    public ResponseEntity<?> obtenerMetricasUltimos(
            @RequestParam Integer idDeporte,
            @RequestParam(defaultValue = "5") int limite,
            Authentication auth) {
        try {
            String usuario = auth.getName();
            log.info("GET /api/alumno/estadisticas/metricas-deporte - Usuario: {}, Deporte: {}, Límite: {}",
                    usuario, idDeporte, limite);
            MetricasUltimosDTO dto = estadisticasDeporteService
                    .obtenerMetricasUltimos(usuario, idDeporte, limite);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            log.error("Error al obtener métricas: {}", e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(new ErrorResponse("Error al obtener métricas del deporte"));
        }
    }
    private record ErrorResponse(String error) {}
}