package com.sportine.backend.controler;

import com.sportine.backend.client.FastApiClient;
import com.sportine.backend.dto.EntrenadorCardDTO;
import com.sportine.backend.dto.EntrenadorRecomendadoDTO;
import com.sportine.backend.dto.PerfilEntrenadorDTO;
import com.sportine.backend.service.BuscarEntrenadorService;
import com.sportine.backend.service.DetalleEntrenadorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/buscar-entrenadores")
@RequiredArgsConstructor
@Slf4j
public class BuscarEntrenadorController {

    // ── Servicios existentes (sin cambios) ────────────────────
    private final DetalleEntrenadorService detalleEntrenadorService;
    private final BuscarEntrenadorService buscarEntrenadorService;

    // ── Nuevo: cliente FastAPI para el endpoint de recomendación ──
    private final FastApiClient fastApiClient;

    /**
     * Mapa de nombre de deporte → ID en la tabla Deporte de MySQL.
     * Coincide exactamente con los INSERTs del schema de sportine_db.
     * Se usa para convertir las especialidades (List<String>) al formato
     * que FastAPI necesita (List<Integer>).
     */
    private static final Map<String, Integer> DEPORTE_IDS = Map.of(
            "Fútbol",     1,
            "Basketball", 2,
            "Natación",   3,
            "Running",    4,
            "Boxeo",      5,
            "Tenis",      6,
            "Gimnasio",   7,
            "Ciclismo",   8,
            "Béisbol",    9
    );

    // ═════════════════════════════════════════════════════════════
    // ENDPOINTS EXISTENTES — no se modificó ni una línea
    // ═════════════════════════════════════════════════════════════

    @GetMapping
    public ResponseEntity<List<EntrenadorCardDTO>> buscarEntrenadores(
            @RequestParam(required = false) String query,
            Authentication authentication) {

        String usuarioAlumno = authentication.getName();
        log.info("Usuario {} buscando entrenadores con query: {}", usuarioAlumno, query);

        List<EntrenadorCardDTO> entrenadores = buscarEntrenadorService.buscarEntrenadores(
                query,
                usuarioAlumno
        );

        return ResponseEntity.ok(entrenadores);
    }

    @GetMapping("/ver/{usuario}")
    public ResponseEntity<PerfilEntrenadorDTO> verEntrenador(
            @PathVariable String usuario,
            Authentication authentication) {

        String usuarioAlumno = authentication.getName();
        log.info("Alumno {} viendo perfil del entrenador {}", usuarioAlumno, usuario);

        PerfilEntrenadorDTO response = detalleEntrenadorService.obtenerPerfilEntrenador(
                usuario, usuarioAlumno);

        return ResponseEntity.ok(response);
    }

    // ═════════════════════════════════════════════════════════════
    // NUEVO — Recomendación con IA
    // ═════════════════════════════════════════════════════════════

    /**
     * Rankea entrenadores por compatibilidad con el alumno usando FastAPI.
     *
     * POST /api/buscar-entrenadores/recomendar
     *
     * No recibe body — obtiene los candidatos internamente con el servicio
     * de búsqueda existente y los envía a FastAPI para que los rankee.
     *
     * Flujo interno:
     *   1. buscarEntrenadorService.buscarEntrenadores(null, alumno)
     *      → List<EntrenadorCardDTO> (todos los entrenadores disponibles)
     *   2. Mapeo EntrenadorCardDTO → formato FastAPI
     *      (especialidades: List<String> → List<Integer> vía DEPORTE_IDS)
     *   3. POST /recomendar-entrenadores a FastAPI
     *   4. Enriquecer cada card con su score y ordenar de mayor a menor
     *
     * Respuesta exitosa (200):
     * [
     *   {
     *     "usuario": "entrenador_a",
     *     "nombreCompleto": "...",
     *     "fotoPerfil": "...",
     *     "ratingPromedio": 4.5,
     *     "especialidades": ["Fútbol", "Basketball"],
     *     "limiteAlumnos": 10,
     *     "alumnosActuales": 2,
     *     "scoreCompatibilidad": 87.3
     *   },
     *   ...ordenados de mayor a menor score
     * ]
     *
     * Si FastAPI no está disponible → 503 con mensaje explicativo.
     * Si no hay candidatos disponibles → 200 con lista vacía [].
     */
    @PostMapping("/recomendar")
    public ResponseEntity<?> recomendarEntrenadores(Authentication authentication) {
        try {
            String usuarioAlumno = authentication.getName();
            log.info("POST /api/buscar-entrenadores/recomendar — Usuario: {}", usuarioAlumno);

            // ── Paso 1: obtener candidatos con el servicio existente ──────
            // query=null → devuelve todos los entrenadores disponibles en el
            // mismo estado del alumno (la misma lógica que el GET de búsqueda)
            List<EntrenadorCardDTO> candidatos = buscarEntrenadorService
                    .buscarEntrenadores(null, usuarioAlumno);

            if (candidatos.isEmpty()) {
                log.info("Sin candidatos disponibles para {}", usuarioAlumno);
                return ResponseEntity.ok(List.of());
            }

            // ── Paso 2: mapear al formato que espera FastAPI ──────────────
            // La conversión clave: especialidades (nombres) → deportes (IDs)
            // Los nombres que no estén en DEPORTE_IDS se ignoran (filter id > 0)
            List<Map<String, Object>> candidatosFastApi = candidatos.stream()
                    .map(e -> {
                        List<Integer> deporteIds = e.getEspecialidades() != null
                                ? e.getEspecialidades().stream()
                                .map(nombre -> DEPORTE_IDS.getOrDefault(nombre, 0))
                                .filter(id -> id > 0)
                                .collect(Collectors.toList())
                                : List.of();

                        Map<String, Object> c = new HashMap<>();
                        c.put("usuario",         e.getUsuario());
                        c.put("deportes",        deporteIds);
                        c.put("rating",          e.getRatingPromedio() != null ? e.getRatingPromedio() : 0.0);
                        // FastAPI valida espacios_libres >= 0 y limite_alumnos >= 1
                        c.put("espacios_libres", Math.max(0, e.getEspaciosDisponibles()));
                        c.put("limite_alumnos",  Math.max(1, e.getLimiteAlumnos() != null ? e.getLimiteAlumnos() : 1));
                        return c;
                    })
                    .collect(Collectors.toList());

            // ── Paso 3: construir body y llamar a FastAPI ─────────────────
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("usuario_alumno", usuarioAlumno);
            requestBody.put("candidatos",     candidatosFastApi);

            log.info("Enviando {} candidatos a FastAPI para ranking", candidatosFastApi.size());

            Map<?, ?> iaResponse = fastApiClient.post(
                    "/recomendar-entrenadores", requestBody, Map.class
            );

            // ── Paso 4: construir mapa score por usuario ──────────────────
            @SuppressWarnings("unchecked")
            List<Map<?, ?>> recomendaciones = (List<Map<?, ?>>) iaResponse.get("recomendaciones");

            Map<String, Double> scoreMap = recomendaciones.stream()
                    .collect(Collectors.toMap(
                            r -> (String)  r.get("usuario"),
                            r -> ((Number) r.get("score_compatibilidad")).doubleValue()
                    ));

            // ── Paso 5: enriquecer cards y ordenar de mayor a menor score ─
            List<EntrenadorRecomendadoDTO> resultado = candidatos.stream()
                    .map(e -> EntrenadorRecomendadoDTO.desde(
                            e,
                            scoreMap.getOrDefault(e.getUsuario(), 0.0)
                    ))
                    .sorted(Comparator
                            .comparingDouble(EntrenadorRecomendadoDTO::getScoreCompatibilidad)
                            .reversed())
                    .collect(Collectors.toList());

            log.info("{} entrenadores rankeados para {}", resultado.size(), usuarioAlumno);
            return ResponseEntity.ok(resultado);

        } catch (RuntimeException e) {
            log.error("Error en recomendación IA: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            log.error("Error inesperado en recomendación: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error interno del servidor"));
        }
    }

    // ─────────────────────────────────────────────────────────────
    private record ErrorResponse(String error) {}
}