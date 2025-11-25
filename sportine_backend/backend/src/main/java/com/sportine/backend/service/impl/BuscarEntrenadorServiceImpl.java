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
        put("futb", "Fútbol");
        put("futbo", "Fútbol");
        put("fútb", "Fútbol");
        put("fútbo", "Fútbol");
        put("soccer", "Fútbol");
        put("football", "Fútbol");
        put("f", "Fútbol");

        // Basketball
        put("basketball", "Basketball");
        put("basquetbol", "Basketball");
        put("basquet", "Basketball");
        put("basket", "Basketball");
        put("bask", "Basketball");
        put("b", "Basketball");
        put("ba", "Basketball");
        put("bas", "Basketball");
        put("baske", "Basketball");
        put("basketb", "Basketball");
        put("basketba", "Basketball");
        put("basketbal", "Basketball");
        put("basquetball", "Basketball");

        // Natación
        put("natacion", "Natación");
        put("n", "Natación");
        put("na", "Natación");
        put("nat", "Natación");
        put("natac", "Natación");
        put("nataci", "Natación");
        put("natacio", "Natación");
        put("natació", "Natación");
        put("natación", "Natación");
        put("nata", "Natación");
        put("swimming", "Natación");
        put("swim", "Natación");
        put("alberca", "Natación");
        put("piscina", "Natación");

        // Running
        put("running", "Running");
        put("r", "Running");
        put("ru", "Running");
        put("runn", "Running");
        put("runni", "Running");
        put("runnin", "Running");
        put("correr", "Running");
        put("c", "Running");
        put("co", "Running");
        put("cor", "Running");
        put("corr", "Running");
        put("corre", "Running");
        put("run", "Running");
        put("atletismo", "Running");
        put("trote", "Running");
        put("jogging", "Running");
        put("carreras", "Running");

        // Boxeo
        put("boxeo", "Boxeo");
        put("box", "Boxeo");
        put("boxing", "Boxeo");
        put("bo", "Boxeo");
        put("pugilismo", "Boxeo");

        // Tenis
        put("tenis", "Tenis");
        put("teni", "Tenis");
        put("tennis", "Tenis");
        put("tenn", "Tenis");
        put("tenni", "Tenis");
        put("ten", "Tenis");
        put("te", "Tenis");
        put("t", "Tenis");

        // Gimnasio
        put("gimnasio", "Gimnasio");
        put("g", "Gimnasio");
        put("gi", "Gimnasio");
        put("gim", "Gimnasio");
        put("gimn", "Gimnasio");
        put("gimna", "Gimnasio");
        put("gimnas", "Gimnasio");
        put("gimnasi", "Gimnasio");
        put("gym", "Gimnasio");
        put("pesas", "Gimnasio");
        put("p", "Gimnasio");
        put("pe", "Gimnasio");
        put("pes", "Gimnasio");
        put("pesa", "Gimnasio");
        put("fitness", "Gimnasio");
        put("musculacion", "Gimnasio");
        put("musculación", "Gimnasio");
        put("fuerza", "Gimnasio");
        put("weights", "Gimnasio");
        put("levantamiento", "Gimnasio");

        // Ciclismo
        put("ciclismo", "Ciclismo");
        put("ci", "Ciclismo");
        put("cic", "Ciclismo");
        put("cicl", "Ciclismo");
        put("cicli", "Ciclismo");
        put("ciclis", "Ciclismo");
        put("ciclism", "Ciclismo");
        put("bici", "Ciclismo");
        put("bicicleta", "Ciclismo");
        put("cycling", "Ciclismo");
        put("bike", "Ciclismo");
        put("bicis", "Ciclismo");

        // Béisbol
        put("beisbol", "Béisbol");
        put("be", "Béisbol");
        put("bei", "Béisbol");
        put("beisb", "Béisbol");
        put("beisbo", "Béisbol");
        put("bé", "Béisbol");
        put("béi", "Béisbol");
        put("béis", "Béisbol");
        put("béisb", "Béisbol");
        put("béisbo", "Béisbol");
        put("béisbol", "Béisbol");
        put("baseball", "Béisbol");
        put("base", "Béisbol");
        put("baseb", "Béisbol");
        put("baseba", "Béisbol");
        put("basebal", "Béisbol");
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

            // NUEVO: Obtener información de disponibilidad
            Integer limiteAlumnos = 0;
            Integer alumnosActuales = 0;

            Object limiteObj = data.get("limiteAlumnos");
            if (limiteObj != null) {
                if (limiteObj instanceof Integer) {
                    limiteAlumnos = (Integer) limiteObj;
                } else if (limiteObj instanceof Number) {
                    limiteAlumnos = ((Number) limiteObj).intValue();
                }
            }

            Object actualesObj = data.get("alumnosActuales");
            if (actualesObj != null) {
                if (actualesObj instanceof Long) {
                    alumnosActuales = ((Long) actualesObj).intValue();
                } else if (actualesObj instanceof Integer) {
                    alumnosActuales = (Integer) actualesObj;
                } else if (actualesObj instanceof Number) {
                    alumnosActuales = ((Number) actualesObj).intValue();
                }
            }

            // Verificación adicional: solo agregar si tiene espacio disponible
            // La query ya filtra, pero agregamos validación por seguridad
            if (alumnosActuales < limiteAlumnos) {
                // Obtener especialidades del entrenador
                List<String> especialidades = entrenadorNombreRepository.obtenerEspecialidadesEntrenador(usuario);

                // Crear DTO con información de disponibilidad
                EntrenadorCardDTO dto = new EntrenadorCardDTO(
                        usuario,
                        nombreCompleto,
                        fotoPerfil,
                        ratingPromedio,
                        especialidades != null ? especialidades : new ArrayList<>(),
                        limiteAlumnos,
                        alumnosActuales
                );

                entrenadores.add(dto);

                log.debug("Entrenador {} agregado - Alumnos: {}/{}",
                        usuario, alumnosActuales, limiteAlumnos);
            } else {
                log.debug("Entrenador {} omitido - Cupo lleno: {}/{}",
                        usuario, alumnosActuales, limiteAlumnos);
            }
        }

        log.info("Se encontraron {} entrenadores con disponibilidad", entrenadores.size());
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