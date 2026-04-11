package com.sportine.backend.controler;

import com.sportine.backend.dto.PlantillaMetricasDTO;
import com.sportine.backend.service.impl.PlantillaMetricasServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller público para consultar la configuración de métricas por deporte.
 * No requiere autenticación — son datos de configuración del sistema.
 *
 * La app Android lo usa al abrir la pantalla de un entrenamiento para saber
 * dinámicamente qué campos mostrar al alumno según el deporte, sin hardcodear
 * nada en el frontend.
 *
 * La web también puede consumirlo para renderizar los formularios correctos.
 *
 * Endpoints:
 * GET /api/plantillas/deporte/{idDeporte} → métricas configuradas para ese deporte
 * GET /api/plantillas/deporte             → métricas de todos los deportes (útil para la web)
 */
@RestController
@RequestMapping("/api/plantillas")
@RequiredArgsConstructor
@Slf4j
public class PlantillaMetricasController {

    private final PlantillaMetricasServiceImpl plantillaService;

    /**
     * Devuelve la configuración completa de métricas para un deporte.
     * Separadas en tres grupos: health_connect, manual y calculada.
     *
     * GET /api/plantillas/deporte/{idDeporte}
     *
     * Ejemplo de respuesta para Running (id 4):
     * {
     *   "id_deporte": 4,
     *   "nombre_deporte": "Running",
     *   "metricas_health_connect": [
     *     { "nombre_metrica": "pace_promedio", "etiqueta_display": "Pace promedio",
     *       "unidad": "min/km", "es_por_serie": false }
     *   ],
     *   "metricas_manuales": [],
     *   "metricas_calculadas": []
     * }
     */
    @GetMapping("/deporte/{idDeporte}")
    public ResponseEntity<?> obtenerPlantillaPorDeporte(@PathVariable Integer idDeporte) {
        try {
            log.info("GET /api/plantillas/deporte/{}", idDeporte);
            PlantillaMetricasDTO plantilla = plantillaService.obtenerPlantillaPorDeporte(idDeporte);
            return ResponseEntity.ok(plantilla);
        } catch (RuntimeException e) {
            log.error("Error obteniendo plantilla para deporte {}: {}", idDeporte, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("success", false, "mensaje", e.getMessage()));
        }
    }

    /**
     * Devuelve las plantillas de todos los deportes.
     * Útil para que la web precargue toda la configuración de una vez.
     *
     * GET /api/plantillas/deporte
     */
    @GetMapping("/deporte")
    public ResponseEntity<?> obtenerTodasLasPlantillas() {
        try {
            log.info("GET /api/plantillas/deporte (todas)");
            return ResponseEntity.ok(plantillaService.obtenerTodasLasPlantillas());
        } catch (Exception e) {
            log.error("Error obteniendo todas las plantillas: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "mensaje", e.getMessage()));
        }
    }
}