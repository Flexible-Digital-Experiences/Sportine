package com.sportine.backend.service.impl;

import com.sportine.backend.model.EstadisticasCarreraUsuario;
import com.sportine.backend.repository.EstadisticasCarreraUsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CarreraDeportivaService {

    private final EstadisticasCarreraUsuarioRepository carreraRepository;

    private static final Map<Integer, List<String>> METRICAS_CARRERA = Map.of(
            1, List.of(
                    "tiros_dentro_area_anotados",
                    "tiros_fuera_area_anotados",
                    "regates_exitosos",
                    "pases_cortos_completados"
            ),
            2, List.of(
                    "puntos_totales",
                    "tiros_2pts_zona_anotados",
                    "tiros_2pts_media_anotados",
                    "tiros_2pts_bandeja_anotados",
                    "tiros_3pts_esquina_anotados",
                    "tiros_3pts_arco_anotados"
            ),
            3, List.of(
                    "vueltas_completadas",
                    "distancia",
                    "tiempo_promedio_largo_seg"
            ),
            4, List.of(
                    "distancia",
                    "intervalos_completados",
                    "ritmo_promedio_min_km"
            ),
            5, List.of(
                    "golpes_totales_conectados",
                    "jabs_conectados",
                    "rounds_completados"
            ),
            6, List.of(
                    "winners_totales",
                    "aces",
                    "primeros_saques_dentro"
            ),
            7, List.of(
                    "volumen_total_kg",
                    "repeticiones_totales",
                    "peso_maximo_levantado"
            ),
            8, List.of(
                    "distancia",
                    "intervalos_completados",
                    "velocidad_maxima"
            ),
            9, List.of(
                    "hits",
                    "ponches_lanzando",
                    "outs_defensivos"
            )
    );

    private static final Map<String, List<Double>> UMBRALES_LOGROS = Map.ofEntries(
            Map.entry("tiros_dentro_area_anotados",  List.of(10.0, 25.0, 50.0, 100.0, 250.0)),
            Map.entry("tiros_fuera_area_anotados",   List.of(5.0,  15.0, 30.0,  75.0, 150.0)),
            Map.entry("regates_exitosos",            List.of(50.0, 100.0, 250.0, 500.0, 1000.0)),
            Map.entry("pases_cortos_completados",    List.of(100.0, 250.0, 500.0, 1000.0, 2500.0)),
            Map.entry("puntos_totales",              List.of(50.0, 100.0, 250.0, 500.0, 1000.0)),
            Map.entry("tiros_2pts_zona_anotados",    List.of(20.0, 50.0, 100.0, 250.0)),
            Map.entry("tiros_2pts_media_anotados",   List.of(20.0, 50.0, 100.0, 250.0)),
            Map.entry("tiros_2pts_bandeja_anotados", List.of(20.0, 50.0, 100.0, 250.0)),
            Map.entry("tiros_3pts_esquina_anotados", List.of(10.0, 25.0,  50.0, 100.0)),
            Map.entry("tiros_3pts_arco_anotados",    List.of(10.0, 25.0,  50.0, 100.0)),
            Map.entry("vueltas_completadas",         List.of(50.0, 100.0, 250.0, 500.0, 1000.0)),
            Map.entry("distancia",                   List.of(1000.0, 5000.0, 10000.0, 42195.0, 100000.0)),
            Map.entry("intervalos_completados",      List.of(10.0, 25.0, 50.0, 100.0)),
            Map.entry("golpes_totales_conectados",   List.of(100.0, 250.0, 500.0, 1000.0)),
            Map.entry("jabs_conectados",             List.of(50.0, 150.0, 300.0, 600.0)),
            Map.entry("rounds_completados",          List.of(10.0, 25.0, 50.0, 100.0)),
            Map.entry("winners_totales",             List.of(25.0, 50.0, 100.0, 250.0)),
            Map.entry("aces",                        List.of(10.0, 25.0,  50.0, 100.0)),
            Map.entry("primeros_saques_dentro",      List.of(25.0, 75.0, 150.0, 300.0)),
            Map.entry("volumen_total_kg",            List.of(1000.0, 5000.0, 10000.0, 50000.0)),
            Map.entry("repeticiones_totales",        List.of(500.0, 1000.0, 2500.0, 5000.0)),
            Map.entry("peso_maximo_levantado",       List.of(50.0, 80.0, 100.0, 120.0, 150.0)),
            Map.entry("hits",                        List.of(10.0, 25.0, 50.0, 100.0)),
            Map.entry("ponches_lanzando",            List.of(10.0, 25.0, 50.0, 100.0)),
            Map.entry("outs_defensivos",             List.of(10.0, 25.0, 50.0, 100.0))
    );

    private static final Map<String, String> EMOJI_METRICA = Map.ofEntries(
            Map.entry("tiros_dentro_area_anotados",  "⚽"),
            Map.entry("tiros_fuera_area_anotados",   "🎯"),
            Map.entry("regates_exitosos",            "💨"),
            Map.entry("pases_cortos_completados",    "🤝"),
            Map.entry("puntos_totales",              "🏀"),
            Map.entry("tiros_2pts_zona_anotados",    "🏀"),
            Map.entry("tiros_2pts_media_anotados",   "🏀"),
            Map.entry("tiros_2pts_bandeja_anotados", "🏀"),
            Map.entry("tiros_3pts_esquina_anotados", "🔥"),
            Map.entry("tiros_3pts_arco_anotados",    "🔥"),
            Map.entry("vueltas_completadas",         "🏊"),
            Map.entry("distancia",                   "📍"),
            Map.entry("intervalos_completados",      "⚡"),
            Map.entry("golpes_totales_conectados",   "🥊"),
            Map.entry("jabs_conectados",             "👊"),
            Map.entry("rounds_completados",          "🔔"),
            Map.entry("winners_totales",             "🎾"),
            Map.entry("aces",                        "🚀"),
            Map.entry("primeros_saques_dentro",      "🎾"),
            Map.entry("volumen_total_kg",            "🏋️"),
            Map.entry("repeticiones_totales",        "💪"),
            Map.entry("peso_maximo_levantado",       "🏆"),
            Map.entry("hits",                        "⚾"),
            Map.entry("ponches_lanzando",            "🔥"),
            Map.entry("outs_defensivos",             "🧤")
    );

    // ═════════════════════════════════════════════════════════════════════════
    // INICIALIZAR CARRERA AL ACEPTAR SOLICITUD
    // ═════════════════════════════════════════════════════════════════════════

    @Transactional
    public void inicializarCarrera(String usuario, Integer idDeporte) {
        List<String> metricas = METRICAS_CARRERA.get(idDeporte);
        if (metricas == null) {
            log.warn("No hay métricas de carrera definidas para deporte {}", idDeporte);
            return;
        }

        for (String nombreMetrica : metricas) {
            boolean existe = carreraRepository
                    .findByUsuarioAndIdDeporteAndNombreMetrica(usuario, idDeporte, nombreMetrica)
                    .isPresent();
            if (!existe) {
                EstadisticasCarreraUsuario fila = new EstadisticasCarreraUsuario();
                fila.setUsuario(usuario);
                fila.setIdDeporte(idDeporte);
                fila.setNombreMetrica(nombreMetrica);
                fila.setValorTotal(0.0);
                fila.setMejorSesion(0.0);
                fila.setTotalEntrenamientos(0);
                fila.setUltimaActualizacion(LocalDateTime.now());
                carreraRepository.save(fila);
            }
        }
        log.info("✅ Carrera inicializada para {} en deporte {}: {} métricas",
                usuario, idDeporte, metricas.size());
    }

    // ═════════════════════════════════════════════════════════════════════════
    // ACTUALIZAR CARRERA AL GUARDAR MÉTRICAS DE N8N
    // ═════════════════════════════════════════════════════════════════════════

    @Transactional
    public List<String> actualizarCarrera(String usuario, Integer idDeporte,
                                          String nombreMetrica, Double valorSesion,
                                          Integer idEntrenamiento) {
        if (valorSesion == null) return List.of();
        if (valorSesion <= 0.0) return List.of();

        List<String> metricasDeporte = METRICAS_CARRERA.get(idDeporte);
        if (metricasDeporte == null || !metricasDeporte.contains(nombreMetrica)) {
            return List.of();
        }

        EstadisticasCarreraUsuario fila = carreraRepository
                .findByUsuarioAndIdDeporteAndNombreMetrica(usuario, idDeporte, nombreMetrica)
                .orElseGet(() -> {
                    EstadisticasCarreraUsuario nueva = new EstadisticasCarreraUsuario();
                    nueva.setUsuario(usuario);
                    nueva.setIdDeporte(idDeporte);
                    nueva.setNombreMetrica(nombreMetrica);
                    nueva.setValorTotal(0.0);
                    nueva.setMejorSesion(0.0);
                    nueva.setTotalEntrenamientos(0);
                    return nueva;
                });

        double anteriorTotal  = fila.getValorTotal();
        double nuevoTotal     = anteriorTotal + valorSesion;
        int entrenamientosAnteriores = fila.getTotalEntrenamientos();

        // ✅ Detectar PB ANTES de actualizar mejorSesion
        boolean esPB = entrenamientosAnteriores > 0 && valorSesion > fila.getMejorSesion();

        fila.setValorTotal(nuevoTotal);
        fila.setTotalEntrenamientos(entrenamientosAnteriores + 1);
        fila.setUltimaActualizacion(LocalDateTime.now());

        if (valorSesion > fila.getMejorSesion()) {
            fila.setMejorSesion(valorSesion);
            fila.setFechaMejorSesion(LocalDate.now());
        }

        carreraRepository.save(fila);

        // Detectar logros por umbral acumulado
        List<String> logros = new ArrayList<>(detectarLogros(nombreMetrica, anteriorTotal, nuevoTotal));

        // ✅ Agregar logro de PB si aplica
        if (esPB) {
            String emoji = EMOJI_METRICA.getOrDefault(nombreMetrica, "🏆");
            String etiqueta = nombreMetrica.replace("_", " ");
            String mensajePB = emoji + " ¡Nuevo récord personal! "
                    + valorSesion.intValue() + " " + etiqueta + " en una sola sesión 🔥";
            logros.add(mensajePB);
            log.info("🔥 PB DETECTADO: {} → {} para {}", nombreMetrica, valorSesion, usuario);
        }

        return logros;
    }

    // ═════════════════════════════════════════════════════════════════════════
    // DETECCIÓN DE LOGROS POR UMBRAL
    // ═════════════════════════════════════════════════════════════════════════

    private List<String> detectarLogros(String nombreMetrica, double anterior, double nuevo) {
        List<Double> umbrales = UMBRALES_LOGROS.get(nombreMetrica);
        if (umbrales == null) return List.of();

        String emoji = EMOJI_METRICA.getOrDefault(nombreMetrica, "🏆");
        List<String> logros = new ArrayList<>();

        for (Double umbral : umbrales) {
            if (anterior < umbral && nuevo >= umbral) {
                logros.add(generarMensajeLogro(nombreMetrica, umbral, emoji));
                log.info("🏆 LOGRO DESBLOQUEADO: {} → umbral {}", nombreMetrica, umbral);
            }
        }
        return logros;
    }

    private String generarMensajeLogro(String nombreMetrica, Double umbral, String emoji) {
        String etiqueta = nombreMetrica
                .replace("_", " ")
                .replace("anotados", "anotados acumulados")
                .replace("completados", "completados en carrera")
                .replace("conectados", "conectados en carrera");

        int valor = umbral.intValue();
        return emoji + " ¡Logro desbloqueado! Llegaste a "
                + valor + " " + etiqueta + " en tu carrera en Sportine.";
    }
}