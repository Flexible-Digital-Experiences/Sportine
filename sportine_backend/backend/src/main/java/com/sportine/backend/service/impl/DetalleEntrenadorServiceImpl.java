package com.sportine.backend.service.impl;


import com.sportine.backend.dto.CalificacionDTO;
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
    public PerfilEntrenadorDTO obtenerPerfilEntrenador(String usuario) {
        log.info("Obteniendo perfil completo del entrenador: {}", usuario);

        // 1. Obtener datos principales del entrenador
        Map<String, Object> datosEntrenador = detalleEntrenadorRepository
                .obtenerDatosEntrenador(usuario)
                .orElseThrow(() -> new RuntimeException("Entrenador no encontrado: " + usuario));

        // 2. Obtener calificaciones
        CalificacionDTO calificacion = obtenerCalificacionDTO(usuario);

        // 3. Obtener especialidades
        List<String> especialidades = detalleEntrenadorRepository.obtenerEspecialidades(usuario);

        // 4. Obtener reseñas
        List<ResenaDTO> resenas = obtenerResenasDTO(usuario);

        // 5. Construir DTO principal
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

        perfil.setCalificacion(calificacion);
        perfil.setEspecialidades(especialidades != null ? especialidades : new ArrayList<>());
        perfil.setResenas(resenas);

        log.info("Perfil del entrenador {} obtenido exitosamente", usuario);
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

        // Redondear a 1 decimal
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
}
