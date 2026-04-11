package com.sportine.backend.service.impl;

import com.sportine.backend.dto.CarreraDeporteDTO;
import com.sportine.backend.dto.MetricasUltimosDTO;
import com.sportine.backend.repository.*;
import com.sportine.backend.service.EstadisticasDeporteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class EstadisticasDeporteServiceImpl implements EstadisticasDeporteService {

    private final EstadisticasCarreraUsuarioRepository carreraRepository;
    private final ResultadoMetricaManualRepository metricaManualRepository;
    private final PlantillaMetricasDeporteRepository plantillaRepository;
    private final DeporteRepository deporteRepository;
    private final EntrenamientoRepository entrenamientoRepository;

    // ── Etiquetas legibles por métrica ────────────────────────────────────────
    private static final Map<String, String> ETIQUETAS = Map.ofEntries(
            Map.entry("tiros_dentro_area_anotados",   "Goles dentro del área"),
            Map.entry("tiros_fuera_area_anotados",    "Goles fuera del área"),
            Map.entry("remates_cabeza_anotados",      "Remates de cabeza"),
            Map.entry("penales_anotados",             "Penales anotados"),
            Map.entry("tiros_libres_anotados",        "Tiros libres anotados"),
            Map.entry("regates_exitosos",             "Regates exitosos"),
            Map.entry("pases_cortos_completados",     "Pases cortos completados"),
            Map.entry("pases_largos_completados",     "Pases largos completados"),
            Map.entry("centros_completados",          "Centros completados"),
            Map.entry("asistencias",                  "Asistencias de gol"),
            Map.entry("entradas_ganadas",             "Entradas ganadas"),
            Map.entry("intercepciones",               "Intercepciones"),
            Map.entry("despejes",                     "Despejes"),
            Map.entry("puntos_totales",               "Puntos totales"),
            Map.entry("tiros_2pts_zona_anotados",     "T2 zona anotados"),
            Map.entry("tiros_2pts_media_anotados",    "T2 media distancia anotados"),
            Map.entry("tiros_2pts_bandeja_anotados",  "Bandejas anotadas"),
            Map.entry("tiros_3pts_esquina_anotados",  "Triples esquina anotados"),
            Map.entry("tiros_3pts_arco_anotados",     "Triples arco anotados"),
            Map.entry("rebotes_ofensivos",            "Rebotes ofensivos"),
            Map.entry("rebotes_defensivos",           "Rebotes defensivos"),
            Map.entry("robos",                        "Robos de balón"),
            Map.entry("tapones",                      "Tapones"),
            Map.entry("perdidas",                     "Pérdidas de balón"),
            Map.entry("vueltas_completadas",          "Vueltas completadas"),
            Map.entry("distancia",                    "Distancia"),
            Map.entry("tiempo_promedio_largo_seg",    "Tiempo prom. por largo"),
            Map.entry("tiempo_mejor_largo_seg",       "Mejor tiempo por largo"),
            Map.entry("brazadas_por_largo",           "Brazadas por largo"),
            Map.entry("largos_crol",                  "Largos en crol"),
            Map.entry("largos_espalda",               "Largos en espalda"),
            Map.entry("largos_pecho",                 "Largos en pecho"),
            Map.entry("largos_mariposa",              "Largos en mariposa"),
            Map.entry("indice_eficiencia",            "Índice SWOLF"),
            Map.entry("intervalos_completados",       "Intervalos completados"),
            Map.entry("ritmo_promedio_min_km",        "Ritmo promedio"),
            Map.entry("ritmo_mejor_km",               "Mejor ritmo por km"),
            Map.entry("velocidad_maxima_ms",          "Velocidad máxima"),
            Map.entry("cadencia_promedio",            "Cadencia promedio"),
            Map.entry("golpes_totales_conectados",    "Golpes conectados"),
            Map.entry("jabs_conectados",              "Jabs conectados"),
            Map.entry("directos_conectados",          "Directos conectados"),
            Map.entry("ganchos_conectados",           "Ganchos conectados"),
            Map.entry("uppercuts_conectados",         "Uppercuts conectados"),
            Map.entry("golpes_cuerpo_conectados",     "Golpes al cuerpo"),
            Map.entry("golpes_cabeza_conectados",     "Golpes a la cabeza"),
            Map.entry("rounds_completados",           "Rounds completados"),
            Map.entry("winners_totales",              "Winners totales"),
            Map.entry("winners_derecha",              "Winners de derecha"),
            Map.entry("winners_reves",                "Winners de revés"),
            Map.entry("aces",                         "Aces"),
            Map.entry("dobles_faltas",                "Dobles faltas"),
            Map.entry("primeros_saques_dentro",       "Primeros saques dentro"),
            Map.entry("segundos_saques_dentro",       "Segundos saques dentro"),
            Map.entry("puntos_ganados_1er_saque",     "Pts con 1er saque"),
            Map.entry("puntos_ganados_2do_saque",     "Pts con 2do saque"),
            Map.entry("sets_ganados",                 "Sets ganados"),
            Map.entry("games_ganados",                "Games ganados"),
            Map.entry("subidas_a_red",                "Subidas a la red"),
            Map.entry("puntos_ganados_en_red",        "Pts ganados en red"),
            Map.entry("errores_no_forzados_total",    "Errores no forzados"),
            Map.entry("volumen_total_kg",             "Volumen total"),
            Map.entry("repeticiones_totales",         "Repeticiones totales"),
            Map.entry("series_completadas",           "Series completadas"),
            Map.entry("series_parciales",             "Series parciales"),
            Map.entry("volumen_pecho",                "Volumen pecho"),
            Map.entry("volumen_espalda",              "Volumen espalda"),
            Map.entry("volumen_piernas",              "Volumen piernas"),
            Map.entry("volumen_hombros",              "Volumen hombros"),
            Map.entry("volumen_brazos",               "Volumen brazos"),
            Map.entry("peso_maximo_levantado",        "Peso máximo levantado"),
            Map.entry("rpe_promedio",                 "RPE promedio"),
            Map.entry("velocidad_maxima",             "Velocidad máxima"),
            Map.entry("cadencia_promedio_ciclismo",   "Cadencia promedio"),
            Map.entry("tiempo_zona_aerobica_min",     "Tiempo zona aeróbica"),
            Map.entry("tiempo_zona_umbral_min",       "Tiempo zona umbral"),
            Map.entry("tiempo_zona_anaerobica_min",   "Tiempo zona anaeróbica"),
            Map.entry("subidas_completadas",          "Subidas completadas"),
            Map.entry("desnivel_positivo",            "Desnivel positivo"),
            Map.entry("hits",                         "Hits"),
            Map.entry("sencillos",                    "Sencillos"),
            Map.entry("dobles",                       "Dobles"),
            Map.entry("triples",                      "Triples"),
            Map.entry("home_runs",                    "Home runs"),
            Map.entry("carreras_anotadas",            "Carreras anotadas"),
            Map.entry("carreras_impulsadas",          "Carreras impulsadas"),
            Map.entry("bases_por_bolas",              "Bases por bolas"),
            Map.entry("ponches_bateando",             "Ponches bateando"),
            Map.entry("ponches_lanzando",             "Ponches lanzando"),
            Map.entry("lanzamientos_totales",         "Lanzamientos totales"),
            Map.entry("strikes_lanzados",             "Strikes lanzados"),
            Map.entry("hits_permitidos",              "Hits permitidos"),
            Map.entry("carreras_permitidas",          "Carreras permitidas"),
            Map.entry("outs_defensivos",              "Outs defensivos"),
            Map.entry("errores_cometidos",            "Errores cometidos"),
            Map.entry("doble_plays",                  "Doble plays"),
            Map.entry("bases_robadas",                "Bases robadas")
    );

    private static final Map<String, String> EMOJIS = Map.ofEntries(
            Map.entry("tiros_dentro_area_anotados",  "⚽"),
            Map.entry("tiros_fuera_area_anotados",   "🎯"),
            Map.entry("remates_cabeza_anotados",     "🏹"),
            Map.entry("penales_anotados",            "⚽"),
            Map.entry("tiros_libres_anotados",       "🎯"),
            Map.entry("regates_exitosos",            "💨"),
            Map.entry("pases_cortos_completados",    "🤝"),
            Map.entry("pases_largos_completados",    "🎯"),
            Map.entry("centros_completados",         "📐"),
            Map.entry("asistencias",                 "🅰️"),
            Map.entry("entradas_ganadas",            "🛡️"),
            Map.entry("intercepciones",              "✋"),
            Map.entry("despejes",                    "👟"),
            Map.entry("puntos_totales",              "🏀"),
            Map.entry("tiros_2pts_zona_anotados",    "🏀"),
            Map.entry("tiros_2pts_media_anotados",   "🏀"),
            Map.entry("tiros_2pts_bandeja_anotados", "🏀"),
            Map.entry("tiros_3pts_esquina_anotados", "🔥"),
            Map.entry("tiros_3pts_arco_anotados",    "🔥"),
            Map.entry("rebotes_ofensivos",           "💪"),
            Map.entry("rebotes_defensivos",          "🛡️"),
            Map.entry("robos",                       "🤏"),
            Map.entry("tapones",                     "🚫"),
            Map.entry("perdidas",                    "❌"),
            Map.entry("vueltas_completadas",         "🏊"),
            Map.entry("distancia",                   "📍"),
            Map.entry("tiempo_promedio_largo_seg",   "⏱️"),
            Map.entry("tiempo_mejor_largo_seg",      "🥇"),
            Map.entry("brazadas_por_largo",          "🏊"),
            Map.entry("largos_crol",                 "🏊"),
            Map.entry("largos_espalda",              "🏊"),
            Map.entry("largos_pecho",                "🏊"),
            Map.entry("largos_mariposa",             "🦋"),
            Map.entry("indice_eficiencia",           "📊"),
            Map.entry("intervalos_completados",      "⚡"),
            Map.entry("ritmo_promedio_min_km",       "⏱️"),
            Map.entry("ritmo_mejor_km",              "🥇"),
            Map.entry("velocidad_maxima_ms",         "💨"),
            Map.entry("cadencia_promedio",           "🔄"),
            Map.entry("golpes_totales_conectados",   "🥊"),
            Map.entry("jabs_conectados",             "👊"),
            Map.entry("directos_conectados",         "👊"),
            Map.entry("ganchos_conectados",          "🥊"),
            Map.entry("uppercuts_conectados",        "⬆️"),
            Map.entry("golpes_cuerpo_conectados",    "🥊"),
            Map.entry("golpes_cabeza_conectados",    "🥊"),
            Map.entry("rounds_completados",          "🔔"),
            Map.entry("winners_totales",             "🎾"),
            Map.entry("winners_derecha",             "🎾"),
            Map.entry("winners_reves",               "🎾"),
            Map.entry("aces",                        "🚀"),
            Map.entry("dobles_faltas",               "❌"),
            Map.entry("primeros_saques_dentro",      "🎾"),
            Map.entry("segundos_saques_dentro",      "🎾"),
            Map.entry("puntos_ganados_1er_saque",    "✅"),
            Map.entry("puntos_ganados_2do_saque",    "✅"),
            Map.entry("sets_ganados",                "🏆"),
            Map.entry("games_ganados",               "🎯"),
            Map.entry("subidas_a_red",               "🕸️"),
            Map.entry("puntos_ganados_en_red",       "✅"),
            Map.entry("errores_no_forzados_total",   "❌"),
            Map.entry("volumen_total_kg",            "🏋️"),
            Map.entry("repeticiones_totales",        "💪"),
            Map.entry("series_completadas",          "✅"),
            Map.entry("series_parciales",            "⚠️"),
            Map.entry("volumen_pecho",               "🏋️"),
            Map.entry("volumen_espalda",             "🏋️"),
            Map.entry("volumen_piernas",             "🦵"),
            Map.entry("volumen_hombros",             "💪"),
            Map.entry("volumen_brazos",              "💪"),
            Map.entry("peso_maximo_levantado",       "🏆"),
            Map.entry("rpe_promedio",                "😤"),
            Map.entry("velocidad_maxima",            "💨"),
            Map.entry("tiempo_zona_aerobica_min",    "🟢"),
            Map.entry("tiempo_zona_umbral_min",      "🟡"),
            Map.entry("tiempo_zona_anaerobica_min",  "🔴"),
            Map.entry("subidas_completadas",         "⛰️"),
            Map.entry("desnivel_positivo",           "📈"),
            Map.entry("hits",                        "⚾"),
            Map.entry("sencillos",                   "1️⃣"),
            Map.entry("dobles",                      "2️⃣"),
            Map.entry("triples",                     "3️⃣"),
            Map.entry("home_runs",                   "🏠"),
            Map.entry("carreras_anotadas",           "🏃"),
            Map.entry("carreras_impulsadas",         "💥"),
            Map.entry("bases_por_bolas",             "🚶"),
            Map.entry("ponches_bateando",            "❌"),
            Map.entry("ponches_lanzando",            "🔥"),
            Map.entry("lanzamientos_totales",        "⚾"),
            Map.entry("strikes_lanzados",            "✅"),
            Map.entry("hits_permitidos",             "⚠️"),
            Map.entry("carreras_permitidas",         "⚠️"),
            Map.entry("outs_defensivos",             "🧤"),
            Map.entry("errores_cometidos",           "❌"),
            Map.entry("doble_plays",                 "💫"),
            Map.entry("bases_robadas",               "💨")
    );

    private static final Map<String, String> UNIDADES = Map.ofEntries(
            Map.entry("tiros_dentro_area_anotados",  "goles"),
            Map.entry("tiros_fuera_area_anotados",   "goles"),
            Map.entry("remates_cabeza_anotados",     "goles"),
            Map.entry("penales_anotados",            "goles"),
            Map.entry("tiros_libres_anotados",       "goles"),
            Map.entry("regates_exitosos",            "regates"),
            Map.entry("pases_cortos_completados",    "pases"),
            Map.entry("pases_largos_completados",    "pases"),
            Map.entry("centros_completados",         "cent."),
            Map.entry("asistencias",                 "asist."),
            Map.entry("entradas_ganadas",            "ent."),
            Map.entry("intercepciones",              "int."),
            Map.entry("despejes",                    "desp."),
            Map.entry("puntos_totales",              "pts"),
            Map.entry("tiros_2pts_zona_anotados",    "tiros"),
            Map.entry("tiros_2pts_media_anotados",   "tiros"),
            Map.entry("tiros_2pts_bandeja_anotados", "tiros"),
            Map.entry("tiros_3pts_esquina_anotados", "triples"),
            Map.entry("tiros_3pts_arco_anotados",    "triples"),
            Map.entry("rebotes_ofensivos",           "reb"),
            Map.entry("rebotes_defensivos",          "reb"),
            Map.entry("robos",                       "rob"),
            Map.entry("tapones",                     "tap"),
            Map.entry("perdidas",                    "perd"),
            Map.entry("vueltas_completadas",         "vueltas"),
            Map.entry("distancia",                   "m"),
            Map.entry("tiempo_promedio_largo_seg",   "seg"),
            Map.entry("tiempo_mejor_largo_seg",      "seg"),
            Map.entry("brazadas_por_largo",          "braz"),
            Map.entry("largos_crol",                 "vtas"),
            Map.entry("largos_espalda",              "vtas"),
            Map.entry("largos_pecho",                "vtas"),
            Map.entry("largos_mariposa",             "vtas"),
            Map.entry("indice_eficiencia",           "pts"),
            Map.entry("intervalos_completados",      "int."),
            Map.entry("ritmo_promedio_min_km",       "min/km"),
            Map.entry("ritmo_mejor_km",              "min/km"),
            Map.entry("velocidad_maxima_ms",         "m/s"),
            Map.entry("cadencia_promedio",           "pasos/m"),
            Map.entry("golpes_totales_conectados",   "golpes"),
            Map.entry("jabs_conectados",             "jabs"),
            Map.entry("directos_conectados",         "golpes"),
            Map.entry("ganchos_conectados",          "golpes"),
            Map.entry("uppercuts_conectados",        "golpes"),
            Map.entry("golpes_cuerpo_conectados",    "golpes"),
            Map.entry("golpes_cabeza_conectados",    "golpes"),
            Map.entry("rounds_completados",          "rounds"),
            Map.entry("winners_totales",             "pts"),
            Map.entry("winners_derecha",             "pts"),
            Map.entry("winners_reves",               "pts"),
            Map.entry("aces",                        "pts"),
            Map.entry("dobles_faltas",               "pts"),
            Map.entry("primeros_saques_dentro",      "saques"),
            Map.entry("segundos_saques_dentro",      "saques"),
            Map.entry("puntos_ganados_1er_saque",    "pts"),
            Map.entry("puntos_ganados_2do_saque",    "pts"),
            Map.entry("sets_ganados",                "sets"),
            Map.entry("games_ganados",               "games"),
            Map.entry("subidas_a_red",               "vec."),
            Map.entry("puntos_ganados_en_red",       "pts"),
            Map.entry("errores_no_forzados_total",   "pts"),
            Map.entry("volumen_total_kg",            "kg"),
            Map.entry("repeticiones_totales",        "reps"),
            Map.entry("series_completadas",          "ser."),
            Map.entry("series_parciales",            "ser."),
            Map.entry("volumen_pecho",               "kg"),
            Map.entry("volumen_espalda",             "kg"),
            Map.entry("volumen_piernas",             "kg"),
            Map.entry("volumen_hombros",             "kg"),
            Map.entry("volumen_brazos",              "kg"),
            Map.entry("peso_maximo_levantado",       "kg"),
            Map.entry("rpe_promedio",                "/10"),
            Map.entry("velocidad_maxima",            "m/s"),
            Map.entry("tiempo_zona_aerobica_min",    "min"),
            Map.entry("tiempo_zona_umbral_min",      "min"),
            Map.entry("tiempo_zona_anaerobica_min",  "min"),
            Map.entry("subidas_completadas",         "sub."),
            Map.entry("desnivel_positivo",           "m"),
            Map.entry("hits",                        "hits"),
            Map.entry("sencillos",                   "hits"),
            Map.entry("dobles",                      "hits"),
            Map.entry("triples",                     "hits"),
            Map.entry("home_runs",                   "HR"),
            Map.entry("carreras_anotadas",           "R"),
            Map.entry("carreras_impulsadas",         "RBI"),
            Map.entry("bases_por_bolas",             "BB"),
            Map.entry("ponches_bateando",            "K"),
            Map.entry("ponches_lanzando",            "K"),
            Map.entry("lanzamientos_totales",        "lanz."),
            Map.entry("strikes_lanzados",            "str."),
            Map.entry("hits_permitidos",             "hits"),
            Map.entry("carreras_permitidas",         "R"),
            Map.entry("outs_defensivos",             "outs"),
            Map.entry("errores_cometidos",           "err."),
            Map.entry("doble_plays",                 "DP"),
            Map.entry("bases_robadas",               "SB")
    );

    // Métricas clave por deporte para las 3 cards
    private static final Map<Integer, List<String>> METRICAS_CARDS = Map.of(
            1, List.of("tiros_dentro_area_anotados", "regates_exitosos", "pases_cortos_completados"),
            2, List.of("puntos_totales", "tiros_2pts_zona_anotados", "tiros_3pts_esquina_anotados"),
            3, List.of("vueltas_completadas", "distancia", "tiempo_promedio_largo_seg"),
            4, List.of("distancia", "intervalos_completados", "ritmo_promedio_min_km"),
            5, List.of("golpes_totales_conectados", "jabs_conectados", "rounds_completados"),
            6, List.of("winners_totales", "aces", "primeros_saques_dentro"),
            7, List.of("volumen_total_kg", "repeticiones_totales", "peso_maximo_levantado"),
            8, List.of("distancia", "intervalos_completados", "velocidad_maxima"),
            9, List.of("hits", "ponches_lanzando", "outs_defensivos")
    );

    // Pares: métrica principal → su comparado (intentados/esperados)
    private static final Map<String, String> PARES_COMPARATIVOS = Map.ofEntries(
            Map.entry("tiros_dentro_area_anotados",  "tiros_dentro_area_intentados"),
            Map.entry("tiros_fuera_area_anotados",   "tiros_fuera_area_intentados"),
            Map.entry("remates_cabeza_anotados",     "remates_cabeza_intentados"),
            Map.entry("penales_anotados",            "penales_intentados"),
            Map.entry("tiros_libres_anotados",       "tiros_libres_intentados"),
            Map.entry("pases_cortos_completados",    "pases_cortos_intentados"),
            Map.entry("regates_exitosos",            "regates_intentados"),
            Map.entry("pases_largos_completados",    "pases_largos_intentados"),
            Map.entry("centros_completados",         "centros_intentados"),
            Map.entry("tiros_2pts_zona_anotados",    "tiros_2pts_zona_intentados"),
            Map.entry("tiros_2pts_media_anotados",   "tiros_2pts_media_intentados"),
            Map.entry("tiros_2pts_bandeja_anotados", "tiros_2pts_bandeja_intentados"),
            Map.entry("tiros_3pts_esquina_anotados", "tiros_3pts_esquina_intentados"),
            Map.entry("tiros_3pts_arco_anotados",    "tiros_3pts_arco_intentados"),
            Map.entry("jabs_conectados",             "jabs_intentados"),
            Map.entry("directos_conectados",         "directos_intentados"),
            Map.entry("ganchos_conectados",          "ganchos_intentados"),
            Map.entry("uppercuts_conectados",        "uppercuts_intentados"),
            Map.entry("golpes_cuerpo_conectados",    "golpes_cuerpo_intentados"),
            Map.entry("golpes_cabeza_conectados",    "golpes_cabeza_intentados"),
            Map.entry("golpes_totales_conectados",   "golpes_totales_intentados"),
            Map.entry("primeros_saques_dentro",      "primeros_saques_intentados"),
            Map.entry("segundos_saques_dentro",      "segundos_saques_intentados")
    );

    // ═══════════════════════════════════════════════════════════════════════
    // CARRERA POR DEPORTE (3 cards)
    // ═══════════════════════════════════════════════════════════════════════

    @Override
    public CarreraDeporteDTO obtenerCarreraDeporte(String usuario, Integer idDeporte) {
        log.info("Obteniendo carrera de {} en deporte {}", usuario, idDeporte);

        String nombreDeporte = deporteRepository.findById(idDeporte)
                .map(d -> d.getNombreDeporte())
                .orElse("Deporte");

        List<String> metricasCards = METRICAS_CARDS.getOrDefault(idDeporte, List.of());
        List<CarreraDeporteDTO.CardCarreraDTO> cards = new ArrayList<>();

        for (String nombreMetrica : metricasCards) {
            carreraRepository.findByUsuarioAndIdDeporteAndNombreMetrica(
                            usuario, idDeporte, nombreMetrica)
                    .ifPresent(fila -> {
                        CarreraDeporteDTO.CardCarreraDTO card = new CarreraDeporteDTO.CardCarreraDTO();
                        card.setNombreMetrica(nombreMetrica);
                        card.setEtiqueta(ETIQUETAS.getOrDefault(nombreMetrica, nombreMetrica));
                        card.setEmoji(EMOJIS.getOrDefault(nombreMetrica, "🏆"));
                        card.setValorTotal(fila.getValorTotal());
                        card.setMejorSesion(fila.getMejorSesion());
                        card.setTotalEntrenamientos(fila.getTotalEntrenamientos());
                        card.setUnidad(UNIDADES.getOrDefault(nombreMetrica, ""));
                        cards.add(card);
                    });

            // Si no tiene datos, agregar card en 0
            if (cards.stream().noneMatch(c -> c.getNombreMetrica().equals(nombreMetrica))) {
                CarreraDeporteDTO.CardCarreraDTO card = new CarreraDeporteDTO.CardCarreraDTO();
                card.setNombreMetrica(nombreMetrica);
                card.setEtiqueta(ETIQUETAS.getOrDefault(nombreMetrica, nombreMetrica));
                card.setEmoji(EMOJIS.getOrDefault(nombreMetrica, "🏆"));
                card.setValorTotal(0.0);
                card.setMejorSesion(0.0);
                card.setTotalEntrenamientos(0);
                card.setUnidad(UNIDADES.getOrDefault(nombreMetrica, ""));
                cards.add(card);
            }
        }

        log.info("Cards generadas para deporte {}: {}", idDeporte, cards.size());
        return new CarreraDeporteDTO(idDeporte, nombreDeporte, cards);
    }

    // ═══════════════════════════════════════════════════════════════════════
    // MÉTRICAS ÚLTIMOS N ENTRENAMIENTOS (gráficas)
    // ═══════════════════════════════════════════════════════════════════════

    @Override
    public MetricasUltimosDTO obtenerMetricasUltimos(String usuario, Integer idDeporte, int limite) {
        log.info("Obteniendo métricas de últimos {} entrenamientos de {} en deporte {}",
                limite, usuario, idDeporte);

        String nombreDeporte = deporteRepository.findById(idDeporte)
                .map(d -> d.getNombreDeporte())
                .orElse("Deporte");

        List<Integer> idsEntrenamientos = entrenamientoRepository
                .findUltimosFinalizadosByUsuarioAndDeporte(usuario, idDeporte, limite);

        if (idsEntrenamientos.isEmpty()) {
            return new MetricasUltimosDTO(idDeporte, nombreDeporte, List.of());
        }

        // ✅ Métricas que son el "comparado" de otra → NO se muestran solas
        Set<String> sonComparados = new HashSet<>(PARES_COMPARATIVOS.values());

        // Todas las métricas calculadas del deporte, excluyendo las que son solo comparados
        List<String> metricasGrafica = plantillaRepository
                .findByIdDeporteAndFuente(idDeporte, "calculada")
                .stream()
                .map(p -> p.getNombreMetrica())
                .filter(nombre -> !sonComparados.contains(nombre))
                .collect(Collectors.toList());

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd MMM",
                new java.util.Locale("es", "MX"));

        List<MetricasUltimosDTO.GraficaMetricaDTO> graficas = new ArrayList<>();

        for (String nombreMetrica : metricasGrafica) {
            var plantillaOpt = plantillaRepository
                    .findByIdDeporteAndNombreMetrica(idDeporte, nombreMetrica);
            if (plantillaOpt.isEmpty()) continue;

            Integer idPlantilla = plantillaOpt.get().getIdPlantilla();

            // ¿Tiene par comparativo?
            String metricaComparada = PARES_COMPARATIVOS.get(nombreMetrica);
            Integer idPlantillaComparada = null;
            if (metricaComparada != null) {
                idPlantillaComparada = plantillaRepository
                        .findByIdDeporteAndNombreMetrica(idDeporte, metricaComparada)
                        .map(p -> p.getIdPlantilla())
                        .orElse(null);
            }

            final Integer idPlantillaCompFinal = idPlantillaComparada;
            List<MetricasUltimosDTO.GraficaMetricaDTO.PuntoDTO> puntos = new ArrayList<>();

            for (Integer idEntrenamiento : idsEntrenamientos) {
                String fecha = entrenamientoRepository.findById(idEntrenamiento)
                        .map(e -> e.getFechaEntrenamiento() != null
                                ? e.getFechaEntrenamiento().format(fmt) : "?")
                        .orElse("?");

                Double valor = metricaManualRepository
                        .findByIdEntrenamientoAndIdPlantillaAndUsuarioAndNumeroSerie(
                                idEntrenamiento, idPlantilla, usuario, null)
                        .map(r -> r.getValorNumerico().doubleValue())
                        .orElse(null);

                Double valorComparado = null;
                if (idPlantillaCompFinal != null) {
                    valorComparado = metricaManualRepository
                            .findByIdEntrenamientoAndIdPlantillaAndUsuarioAndNumeroSerie(
                                    idEntrenamiento, idPlantillaCompFinal, usuario, null)
                            .map(r -> r.getValorNumerico().doubleValue())
                            .orElse(null);
                }

                if (valor != null && valor != 0) {
                    puntos.add(new MetricasUltimosDTO.GraficaMetricaDTO.PuntoDTO(
                            idEntrenamiento, fecha, valor, valorComparado));
                }
            }

            // Solo agregar si tiene al menos 1 punto con datos
            if (!puntos.isEmpty()) {
                graficas.add(new MetricasUltimosDTO.GraficaMetricaDTO(
                        nombreMetrica,
                        ETIQUETAS.getOrDefault(nombreMetrica, nombreMetrica),
                        UNIDADES.getOrDefault(nombreMetrica, ""),
                        puntos
                ));
            }
        }

        log.info("Gráficas generadas: {} para deporte {}", graficas.size(), idDeporte);
        return new MetricasUltimosDTO(idDeporte, nombreDeporte, graficas);
    }
}