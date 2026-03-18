package com.sportine.backend.service.impl;

import com.sportine.backend.dto.CalificacionDTO;
import com.sportine.backend.dto.EstadoRelacionDTO;
import com.sportine.backend.dto.PerfilEntrenadorDTO;
import com.sportine.backend.dto.ResenaDTO;
import com.sportine.backend.repository.DetalleEntrenadorRepository;
import com.sportine.backend.service.DetalleEntrenadorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class DetalleEntrenadorServiceImpl implements DetalleEntrenadorService {

    private final DetalleEntrenadorRepository detalleEntrenadorRepository;

    @Override
    public PerfilEntrenadorDTO obtenerPerfilEntrenador(String usuarioEntrenador, String usuarioAlumno) {
        log.info("Obteniendo perfil completo del entrenador: {} para alumno: {}",
                usuarioEntrenador, usuarioAlumno);

        // 1. Obtener datos principales del entrenador
        Map<String, Object> datosEntrenador = detalleEntrenadorRepository
                .obtenerDatosEntrenador(usuarioEntrenador)
                .orElseThrow(() -> new RuntimeException("Entrenador no encontrado: " + usuarioEntrenador));

        // 2. Obtener calificaciones
        CalificacionDTO calificacion = obtenerCalificacionDTO(usuarioEntrenador);

        // 3. Obtener especialidades
        List<String> especialidades = detalleEntrenadorRepository
                .obtenerEspecialidades(usuarioEntrenador);

        // 4. Obtener reseñas
        List<ResenaDTO> resenas = obtenerResenasDTO(usuarioEntrenador);

        // 5. Obtener estado de relación con el alumno
        EstadoRelacionDTO estadoRelacion = obtenerEstadoRelacionDTO(usuarioEntrenador, usuarioAlumno);

        // 6. Construir DTO principal
        PerfilEntrenadorDTO perfil = new PerfilEntrenadorDTO();
        perfil.setUsuario((String) datosEntrenador.get("usuario"));
        perfil.setFotoPerfil((String) datosEntrenador.get("fotoPerfil"));

        // Construir nombre completo
        String nombre = (String) datosEntrenador.get("nombre");
        String apellidos = (String) datosEntrenador.get("apellidos");
        perfil.setNombreCompleto(nombre + " " + apellidos);

        // Construir ubicación
        String ciudad = (String) datosEntrenador.get("ciudad");
        String estado = (String) datosEntrenador.get("estado");
        perfil.setUbicacion(ciudad + ", " + estado);

        perfil.setAcercaDeMi((String) datosEntrenador.get("descripcionPerfil"));

        // Manejar costo mensual
        Object costoObj = datosEntrenador.get("costoMensualidad");
        Integer costo = 0;
        if (costoObj != null) {
            if (costoObj instanceof Integer) {
                costo = (Integer) costoObj;
            } else if (costoObj instanceof Number) {
                costo = ((Number) costoObj).intValue();
            }
        }
        perfil.setCostoMensual(costo);

        // Manejar límite de alumnos
        Object limiteObj = datosEntrenador.get("limiteAlumnos");
        Integer limite = 0;
        if (limiteObj != null) {
            if (limiteObj instanceof Integer) {
                limite = (Integer) limiteObj;
            } else if (limiteObj instanceof Number) {
                limite = ((Number) limiteObj).intValue();
            }
        }
        perfil.setLimiteAlumnos(limite);

        // Manejar alumnos actuales
        Object actualesObj = datosEntrenador.get("alumnosActuales");
        Integer actuales = 0;
        if (actualesObj != null) {
            if (actualesObj instanceof Long) {
                actuales = ((Long) actualesObj).intValue();
            } else if (actualesObj instanceof Integer) {
                actuales = (Integer) actualesObj;
            } else if (actualesObj instanceof Number) {
                actuales = ((Number) actualesObj).intValue();
            }
        }
        perfil.setAlumnosActuales(actuales);


        perfil.setCalificacion(calificacion);
        perfil.setEspecialidades(especialidades != null ? especialidades : new ArrayList<>());
        perfil.setResenas(resenas);
        perfil.setEstadoRelacion(estadoRelacion);

        log.info("Perfil del entrenador {} obtenido exitosamente. Estado relación: {}",
                usuarioEntrenador, estadoRelacion.getEstadoRelacion());
        return perfil;
    }

    /**
     * Método auxiliar para construir el DTO de calificación.
     */
    private CalificacionDTO obtenerCalificacionDTO(String usuario) {
        Map<String, Object> calificacionData = detalleEntrenadorRepository
                .obtenerCalificaciones(usuario)
                .orElse(Map.of("ratingPromedio", 0.0, "totalResenas", 0));

        Double ratingPromedio = 0.0;
        Object ratingObj = calificacionData.get("ratingPromedio");
        if (ratingObj != null) {
            if (ratingObj instanceof Double) {
                ratingPromedio = (Double) ratingObj;
            } else if (ratingObj instanceof Number) {
                ratingPromedio = ((Number) ratingObj).doubleValue();
            }
        }

        ratingPromedio = Math.round(ratingPromedio * 10.0) / 10.0;

        Integer totalResenas = 0;
        Object totalObj = calificacionData.get("totalResenas");
        if (totalObj != null) {
            if (totalObj instanceof Integer) {
                totalResenas = (Integer) totalObj;
            } else if (totalObj instanceof Number) {
                totalResenas = ((Number) totalObj).intValue();
            } else if (totalObj instanceof Long) {
                totalResenas = ((Long) totalObj).intValue();
            }
        }

        return new CalificacionDTO(ratingPromedio, totalResenas);
    }

    /**
     * Método auxiliar para construir la lista de DTOs de reseñas.
     */
    private List<ResenaDTO> obtenerResenasDTO(String usuario) {
        List<Map<String, Object>> resenasData = detalleEntrenadorRepository.obtenerResenas(usuario);
        List<ResenaDTO> resenas = new ArrayList<>();

        for (Map<String, Object> data : resenasData) {
            String nombreAlumno = (String) data.get("nombreAlumno");
            String fotoAlumno = (String) data.get("fotoAlumno");
            String comentario = (String) data.get("comentario");

            Integer ratingDado = 0;
            Object ratingObj = data.get("ratingDado");
            if (ratingObj != null) {
                if (ratingObj instanceof Integer) {
                    ratingDado = (Integer) ratingObj;
                } else if (ratingObj instanceof Number) {
                    ratingDado = ((Number) ratingObj).intValue();
                }
            }

            ResenaDTO resena = new ResenaDTO(nombreAlumno, fotoAlumno, ratingDado, comentario);
            resenas.add(resena);
        }

        return resenas;
    }

    /**
     * Método auxiliar para obtener el estado de relación entre alumno y entrenador.
     */
    private EstadoRelacionDTO obtenerEstadoRelacionDTO(String usuarioEntrenador, String usuarioAlumno) {
        List<Map<String, Object>> relaciones = detalleEntrenadorRepository
                .obtenerRelaciones(usuarioEntrenador, usuarioAlumno);

        log.info("Relaciones encontradas: {}", relaciones.size());

        if (relaciones.isEmpty()) {
            return new EstadoRelacionDTO(false, null, null, null, false, null, new ArrayList<>());
        }

        List<EstadoRelacionDTO.RelacionDeporteDTO> listaRelaciones = new ArrayList<>();
        for (Map<String, Object> rel : relaciones) {
            Integer idDeporte = rel.get("idDeporte") != null
                    ? ((Number) rel.get("idDeporte")).intValue() : null;
            Integer idRelacion = rel.get("idRelacion") != null
                    ? ((Number) rel.get("idRelacion")).intValue() : null;
            String nombreDeporte = (String) rel.get("nombreDeporte");
            String status = (String) rel.get("statusRelacion");
            String statusSuscripcion = rel.get("statusSuscripcion") != null
                    ? (String) rel.get("statusSuscripcion") : "";

            LocalDate finMensualidad = null;
            Object finObj = rel.get("finMensualidad");
            if (finObj != null) {
                if (finObj instanceof LocalDate) {
                    finMensualidad = (LocalDate) finObj;
                } else if (finObj instanceof java.sql.Date) {
                    finMensualidad = ((java.sql.Date) finObj).toLocalDate();
                }
            }

            EstadoRelacionDTO.RelacionDeporteDTO dto = new EstadoRelacionDTO.RelacionDeporteDTO(
                    idRelacion, idDeporte, nombreDeporte, status, finMensualidad, statusSuscripcion);
            listaRelaciones.add(dto);
        }

        // La relación más relevante es la primera (ORDER BY id_relacion DESC)
        Map<String, Object> primera = relaciones.get(0);
        String statusPrincipal = (String) primera.get("statusRelacion");
        Integer idDeportePrincipal = primera.get("idDeporte") != null
                ? ((Number) primera.get("idDeporte")).intValue() : null;
        String nombreDeportePrincipal = (String) primera.get("nombreDeporte");

        LocalDate finPrincipal = null;
        Object finObj = primera.get("finMensualidad");
        if (finObj instanceof java.sql.Date) {
            finPrincipal = ((java.sql.Date) finObj).toLocalDate();
        } else if (finObj instanceof LocalDate) {
            finPrincipal = (LocalDate) finObj;
        }

        Long countCalificacion = detalleEntrenadorRepository
                .verificarSiYaCalifico(usuarioAlumno, usuarioEntrenador);
        Boolean yaCalificado = countCalificacion != null && countCalificacion > 0;

        log.info("Relación ACTIVA encontrada: Alumno {} - Entrenador {} | Estado: {} | Deportes: {}",
                usuarioAlumno, usuarioEntrenador, statusPrincipal, listaRelaciones.size());

        return new EstadoRelacionDTO(true, statusPrincipal, idDeportePrincipal,
                nombreDeportePrincipal, yaCalificado, finPrincipal, listaRelaciones);
    }
}