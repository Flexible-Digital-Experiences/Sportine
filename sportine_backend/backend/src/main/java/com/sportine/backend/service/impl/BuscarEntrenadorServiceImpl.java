package com.sportine.backend.service.impl;

import com.sportine.backend.dto.EntrenadorCardDTO;
import com.sportine.backend.repository.BuscarEntrenadorNombreRepository;
import com.sportine.backend.repository.BuscarEntrenadorDeporteRepository;
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

    // Mapa de aliases de deportes
    private static final Map<String, String> DEPORTE_ALIASES = new HashMap<String, String>() {{
        // Fútbol
        put("futbol", "Fútbol");
        put("fútbol", "Fútbol");
        put("fut", "Fútbol");
        put("soccer", "Fútbol");
        put("football", "Fútbol");

        // Basketball
        put("basketball", "Basketball");
        put("basquetbol", "Basketball");
        put("basquet", "Basketball");
        put("basket", "Basketball");
        put("bask", "Basketball");
        put("basquetball", "Basketball");

        // Natación
        put("natacion", "Natación");
        put("natación", "Natación");
        put("nata", "Natación");
        put("swimming", "Natación");
        put("swim", "Natación");
        put("alberca", "Natación");
        put("piscina", "Natación");

        // Running
        put("running", "Running");
        put("correr", "Running");
        put("run", "Running");
        put("atletismo", "Running");
        put("trote", "Running");
        put("jogging", "Running");
        put("carreras", "Running");

        // Boxeo
        put("boxeo", "Boxeo");
        put("box", "Boxeo");
        put("boxing", "Boxeo");
        put("pugilismo", "Boxeo");

        // Tenis
        put("tenis", "Tenis");
        put("tennis", "Tenis");
        put("ten", "Tenis");

        // Gimnasio
        put("gimnasio", "Gimnasio");
        put("gym", "Gimnasio");
        put("pesas", "Gimnasio");
        put("fitness", "Gimnasio");
        put("musculacion", "Gimnasio");
        put("musculación", "Gimnasio");
        put("fuerza", "Gimnasio");
        put("weights", "Gimnasio");
        put("levantamiento", "Gimnasio");

        // Ciclismo
        put("ciclismo", "Ciclismo");
        put("bici", "Ciclismo");
        put("bicicleta", "Ciclismo");
        put("cycling", "Ciclismo");
        put("bike", "Ciclismo");
        put("bicis", "Ciclismo");
        put("pedal", "Ciclismo");

        // Béisbol
        put("beisbol", "Béisbol");
        put("béisbol", "Béisbol");
        put("baseball", "Béisbol");
        put("beis", "Béisbol");
        put("pelota", "Béisbol");
    }};

    @Override
    public List<EntrenadorCardDTO> buscarEntrenadores(String searchQuery, String usuarioAlumno) {
        log.info("Buscando entrenadores con query: {} para alumno: {}", searchQuery, usuarioAlumno);

        // Obtener el estado del alumno
        Integer idEstadoAlumno = usuarioRepository.findById(usuarioAlumno)
                .map(usuario -> usuario.getIdEstado())
                .orElse(null);

        if (idEstadoAlumno == null) {
            log.warn("No se encontró el estado del alumno: {}", usuarioAlumno);
            return new ArrayList<>();
        }

        log.info("Estado del alumno {}: {}", usuarioAlumno, idEstadoAlumno);

        // Normalizar el query de búsqueda
        String normalizedQuery = (searchQuery != null && !searchQuery.trim().isEmpty())
                ? searchQuery.trim()
                : null;

        List<Map<String, Object>> entrenadoresData;

        if (normalizedQuery != null) {
            // HAY BÚSQUEDA - Verificar si es un deporte
            String deporteNormalizado = normalizarDeporte(normalizedQuery);

            if (deporteNormalizado != null) {
                // ES UN DEPORTE - Filtrar por deporte Y estado
                log.info("Búsqueda por deporte: {} en estado: {}", deporteNormalizado, idEstadoAlumno);
                entrenadoresData = entrenadorDeporteRepository.buscarEntrenadoresPorDeporteYEstado(
                        deporteNormalizado,
                        idEstadoAlumno
                );
            } else {
                // ES UN NOMBRE - Buscar por nombre SIN filtrar por estado
                log.info("Búsqueda por nombre: {} (sin filtro de estado)", normalizedQuery);
                entrenadoresData = entrenadorNombreRepository.buscarEntrenadores(normalizedQuery);
            }
        } else {
            // SIN BÚSQUEDA - Mostrar mejores entrenadores del mismo estado
            log.info("Carga inicial - Mostrar mejores entrenadores del estado: {}", idEstadoAlumno);
            entrenadoresData = entrenadorNombreRepository.buscarEntrenadoresPorEstado(idEstadoAlumno);
        }

        // Construir DTOs con especialidades
        List<EntrenadorCardDTO> entrenadores = new ArrayList<>();

        for (Map<String, Object> data : entrenadoresData) {
            String usuario = (String) data.get("usuario");
            String nombreCompleto = (String) data.get("nombreCompleto");
            String fotoPerfil = (String) data.get("fotoPerfil");

            // Manejar el rating promedio de forma segura
            Double ratingPromedio = 0.0;
            Object ratingObj = data.get("ratingPromedio");
            if (ratingObj != null) {
                if (ratingObj instanceof Double) {
                    ratingPromedio = (Double) ratingObj;
                } else if (ratingObj instanceof Number) {
                    ratingPromedio = ((Number) ratingObj).doubleValue();
                }
            }

            // Redondear a 1 decimal
            ratingPromedio = Math.round(ratingPromedio * 10.0) / 10.0;

            // Obtener especialidades del entrenador
            List<String> especialidades = entrenadorNombreRepository.obtenerEspecialidadesEntrenador(usuario);

            // Crear DTO
            EntrenadorCardDTO dto = new EntrenadorCardDTO(
                    usuario,
                    nombreCompleto,
                    fotoPerfil,
                    ratingPromedio,
                    especialidades != null ? especialidades : new ArrayList<>()
            );

            entrenadores.add(dto);
        }

        log.info("Se encontraron {} entrenadores", entrenadores.size());
        return entrenadores;
    }

    private String normalizarDeporte(String query) {
        if (query == null || query.trim().isEmpty()) {
            return null;
        }

        // Normalizar: minúsculas y sin acentos
        String normalized = query.toLowerCase().trim()
                .replace("á", "a")
                .replace("é", "e")
                .replace("í", "i")
                .replace("ó", "o")
                .replace("ú", "u");

        // Buscar en el mapa de aliases
        return DEPORTE_ALIASES.get(normalized);
    }
}