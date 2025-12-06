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

        // ✅ NUEVOS CAMPOS: Correo y Teléfono
        perfil.setCorreo((String) datosEntrenador.get("correo"));
        perfil.setTelefono((String) datosEntrenador.get("telefono"));

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
        // Buscar si existe alguna relación
        var relacionOpt = detalleEntrenadorRepository.obtenerEstadoRelacion(
                usuarioEntrenador, usuarioAlumno);

        log.info("Verificando relación - isPresent: {}", relacionOpt.isPresent());

        if (!relacionOpt.isPresent()) {
            // NO HAY RELACIÓN
            log.info("No existe relación entre Alumno {} y Entrenador {}",
                    usuarioAlumno, usuarioEntrenador);
            return new EstadoRelacionDTO(false, null, null, null, false);
        }

        // SÍ HAY RELACIÓN - Extraer datos
        Map<String, Object> relacionData = relacionOpt.get();
        log.info("Datos de relación encontrados: {}", relacionData);

        String statusRelacion = (String) relacionData.get("statusRelacion");

        // Verificar si el status es null (significa que la query devolvió fila vacía)
        if (statusRelacion == null) {
            log.info("Status de relación es NULL - No hay relación real entre {} y {}",
                    usuarioAlumno, usuarioEntrenador);
            return new EstadoRelacionDTO(false, null, null, null, false);
        }

        Integer idDeporte = null;
        Object idDeporteObj = relacionData.get("idDeporte");
        if (idDeporteObj != null) {
            if (idDeporteObj instanceof Integer) {
                idDeporte = (Integer) idDeporteObj;
            } else if (idDeporteObj instanceof Number) {
                idDeporte = ((Number) idDeporteObj).intValue();
            }
        }

        String nombreDeporte = (String) relacionData.get("nombreDeporte");

        // Verificar si ya calificó (convertir Long a Boolean)
        Long countCalificacion = detalleEntrenadorRepository.verificarSiYaCalifico(
                usuarioAlumno, usuarioEntrenador);
        Boolean yaCalificado = countCalificacion != null && countCalificacion > 0;

        log.info("Relación ACTIVA encontrada: Alumno {} - Entrenador {} | Estado: {} | Deporte: {} | Ya calificó: {}",
                usuarioAlumno, usuarioEntrenador, statusRelacion, nombreDeporte, yaCalificado);

        return new EstadoRelacionDTO(true, statusRelacion, idDeporte, nombreDeporte, yaCalificado);
    }
}