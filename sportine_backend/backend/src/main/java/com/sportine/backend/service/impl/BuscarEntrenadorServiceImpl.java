package com.sportine.backend.service.impl;

import com.sportine.backend.dto.EntrenadorCardDTO;
import com.sportine.backend.repository.BuscarEntrenadorNombreRepository;
import com.sportine.backend.repository.BuscarEntrenadorDeporteRepository;
import com.sportine.backend.repository.EstudianteSuscripcionEntrenadorRepository;
import com.sportine.backend.repository.UsuarioRepository;
import com.sportine.backend.service.BuscarEntrenadorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class BuscarEntrenadorServiceImpl implements BuscarEntrenadorService {

    private final BuscarEntrenadorNombreRepository entrenadorNombreRepository;
    private final BuscarEntrenadorDeporteRepository entrenadorDeporteRepository;
    private final UsuarioRepository usuarioRepository;
    private final EstudianteSuscripcionEntrenadorRepository suscripcionRepository; // ✅ NUEVO

    // ... (DEPORTE_ALIASES sin cambios) ...

    @Override
    public List<EntrenadorCardDTO> buscarEntrenadores(String searchQuery, String usuarioAlumno) {
        log.info("Buscando entrenadores con query: {} para alumno: {}", searchQuery, usuarioAlumno);

        Integer idEstadoAlumno = usuarioRepository.findById(usuarioAlumno)
                .map(usuario -> usuario.getIdEstado())
                .orElse(null);

        if (idEstadoAlumno == null) {
            log.warn("No se encontró el estado del alumno: {}", usuarioAlumno);
            return new ArrayList<>();
        }

        String normalizedQuery = (searchQuery != null && !searchQuery.trim().isEmpty())
                ? searchQuery.trim() : null;

        List<Map<String, Object>> entrenadoresData;

        if (normalizedQuery != null) {
            String deporteNormalizado = normalizarDeporte(normalizedQuery);
            if (deporteNormalizado != null) {
                entrenadoresData = entrenadorDeporteRepository.buscarEntrenadoresPorDeporteYEstado(
                        deporteNormalizado, idEstadoAlumno);
            } else {
                entrenadoresData = entrenadorNombreRepository.buscarEntrenadores(normalizedQuery);
            }
        } else {
            entrenadoresData = entrenadorNombreRepository.buscarEntrenadoresPorEstado(idEstadoAlumno);
        }

        List<EntrenadorCardDTO> entrenadores = new ArrayList<>();

        for (Map<String, Object> data : entrenadoresData) {
            String usuario = (String) data.get("usuario");

            Double ratingPromedio = 0.0;
            Object ratingObj = data.get("ratingPromedio");
            if (ratingObj instanceof Number) {
                ratingPromedio = Math.round(((Number) ratingObj).doubleValue() * 10.0) / 10.0;
            }

            Integer limiteAlumnos = 0;
            Object limiteObj = data.get("limiteAlumnos");
            if (limiteObj instanceof Number) {
                limiteAlumnos = ((Number) limiteObj).intValue();
            }

            // ✅ FIX: conteo dinámico desde la BD en lugar de leer del query
            int alumnosActuales = suscripcionRepository.contarAlumnosActivos(usuario);

            // ✅ FIX: si ya alcanzó el límite, no aparece en búsqueda
            if (limiteAlumnos > 0 && alumnosActuales >= limiteAlumnos) {
                log.debug("Entrenador {} omitido — cupo lleno ({}/{})", usuario, alumnosActuales, limiteAlumnos);
                continue;
            }

            List<String> especialidades = entrenadorNombreRepository.obtenerEspecialidadesEntrenador(usuario);

            EntrenadorCardDTO dto = new EntrenadorCardDTO(
                    usuario,
                    (String) data.get("nombreCompleto"),
                    (String) data.get("fotoPerfil"),
                    ratingPromedio,
                    especialidades != null ? especialidades : new ArrayList<>(),
                    limiteAlumnos,
                    alumnosActuales  // ✅ siempre actualizado
            );

            entrenadores.add(dto);
            log.debug("Entrenador {} - Alumnos: {}/{}", usuario, alumnosActuales, limiteAlumnos);
        }

        log.info("Se encontraron {} entrenadores", entrenadores.size());
        return entrenadores;
    }

    private String normalizarDeporte(String query) {
        if (query == null || query.trim().isEmpty()) return null;
        String normalized = query.toLowerCase().trim()
                .replace("á","a").replace("é","e")
                .replace("í","i").replace("ó","o").replace("ú","u");
        return DEPORTE_ALIASES.get(normalized);
    }

    // DEPORTE_ALIASES — sin cambios, copiar igual que antes
    private static final Map<String, String> DEPORTE_ALIASES = new HashMap<String, String>() {{
        put("futbol", "Fútbol"); put("fútbol", "Fútbol"); put("fut", "Fútbol");
        put("futb", "Fútbol"); put("futbo", "Fútbol"); put("fútb", "Fútbol");
        put("fútbo", "Fútbol"); put("soccer", "Fútbol"); put("football", "Fútbol");
        put("f", "Fútbol");
        put("basketball", "Basketball"); put("basquetbol", "Basketball");
        put("basquet", "Basketball"); put("basket", "Basketball");
        put("bask", "Basketball"); put("b", "Basketball"); put("ba", "Basketball");
        put("bas", "Basketball"); put("baske", "Basketball"); put("basketb", "Basketball");
        put("basketba", "Basketball"); put("basketbal", "Basketball");
        put("basquetball", "Basketball");
        put("natacion", "Natación"); put("n", "Natación"); put("na", "Natación");
        put("nat", "Natación"); put("natac", "Natación"); put("nataci", "Natación");
        put("natacio", "Natación"); put("natació", "Natación"); put("natación", "Natación");
        put("nata", "Natación"); put("swimming", "Natación"); put("swim", "Natación");
        put("alberca", "Natación"); put("piscina", "Natación");
        put("running", "Running"); put("r", "Running"); put("ru", "Running");
        put("runn", "Running"); put("runni", "Running"); put("runnin", "Running");
        put("correr", "Running"); put("c", "Running"); put("co", "Running");
        put("cor", "Running"); put("corr", "Running"); put("corre", "Running");
        put("run", "Running"); put("atletismo", "Running"); put("trote", "Running");
        put("jogging", "Running"); put("carreras", "Running");
        put("boxeo", "Boxeo"); put("box", "Boxeo"); put("boxing", "Boxeo");
        put("bo", "Boxeo"); put("pugilismo", "Boxeo");
        put("tenis", "Tenis"); put("teni", "Tenis"); put("tennis", "Tenis");
        put("tenn", "Tenis"); put("tenni", "Tenis"); put("ten", "Tenis");
        put("te", "Tenis"); put("t", "Tenis");
        put("gimnasio", "Gimnasio"); put("g", "Gimnasio"); put("gi", "Gimnasio");
        put("gim", "Gimnasio"); put("gimn", "Gimnasio"); put("gimna", "Gimnasio");
        put("gimnas", "Gimnasio"); put("gimnasi", "Gimnasio"); put("gym", "Gimnasio");
        put("pesas", "Gimnasio"); put("p", "Gimnasio"); put("pe", "Gimnasio");
        put("pes", "Gimnasio"); put("pesa", "Gimnasio"); put("fitness", "Gimnasio");
        put("musculacion", "Gimnasio"); put("musculación", "Gimnasio");
        put("fuerza", "Gimnasio"); put("weights", "Gimnasio");
        put("levantamiento", "Gimnasio");
        put("ciclismo", "Ciclismo"); put("ci", "Ciclismo"); put("cic", "Ciclismo");
        put("cicl", "Ciclismo"); put("cicli", "Ciclismo"); put("ciclis", "Ciclismo");
        put("ciclism", "Ciclismo"); put("bici", "Ciclismo"); put("bicicleta", "Ciclismo");
        put("cycling", "Ciclismo"); put("bike", "Ciclismo"); put("bicis", "Ciclismo");
        put("beisbol", "Béisbol"); put("be", "Béisbol"); put("bei", "Béisbol");
        put("beisb", "Béisbol"); put("beisbo", "Béisbol"); put("bé", "Béisbol");
        put("béi", "Béisbol"); put("béis", "Béisbol"); put("béisb", "Béisbol");
        put("béisbo", "Béisbol"); put("béisbol", "Béisbol"); put("baseball", "Béisbol");
        put("base", "Béisbol"); put("baseb", "Béisbol"); put("baseba", "Béisbol");
        put("basebal", "Béisbol"); put("beis", "Béisbol"); put("pelota", "Béisbol");
    }};
}