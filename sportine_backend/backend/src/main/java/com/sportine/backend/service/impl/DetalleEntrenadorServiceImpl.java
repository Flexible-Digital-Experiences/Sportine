package com.sportine.backend.service.impl;

import com.sportine.backend.dto.CalificacionDTO;
import com.sportine.backend.dto.EstadoRelacionDTO;
import com.sportine.backend.dto.PerfilEntrenadorDTO;
import com.sportine.backend.dto.ResenaDTO;
import com.sportine.backend.repository.DetalleEntrenadorRepository;
import com.sportine.backend.repository.EstudianteSuscripcionEntrenadorRepository; // ✅ NUEVO
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
    private final EstudianteSuscripcionEntrenadorRepository suscripcionRepository; // ✅ NUEVO

    @Override
    public PerfilEntrenadorDTO obtenerPerfilEntrenador(String usuarioEntrenador, String usuarioAlumno) {
        log.info("Obteniendo perfil completo del entrenador: {} para alumno: {}",
                usuarioEntrenador, usuarioAlumno);

        Map<String, Object> datosEntrenador = detalleEntrenadorRepository
                .obtenerDatosEntrenador(usuarioEntrenador)
                .orElseThrow(() -> new RuntimeException("Entrenador no encontrado: " + usuarioEntrenador));

        CalificacionDTO calificacion = obtenerCalificacionDTO(usuarioEntrenador);
        List<String> especialidades = detalleEntrenadorRepository.obtenerEspecialidades(usuarioEntrenador);
        List<ResenaDTO> resenas = obtenerResenasDTO(usuarioEntrenador);
        EstadoRelacionDTO estadoRelacion = obtenerEstadoRelacionDTO(usuarioEntrenador, usuarioAlumno);

        PerfilEntrenadorDTO perfil = new PerfilEntrenadorDTO();
        perfil.setUsuario((String) datosEntrenador.get("usuario"));
        perfil.setFotoPerfil((String) datosEntrenador.get("fotoPerfil"));

        String nombre = (String) datosEntrenador.get("nombre");
        String apellidos = (String) datosEntrenador.get("apellidos");
        perfil.setNombreCompleto(nombre + " " + apellidos);

        String ciudad = (String) datosEntrenador.get("ciudad");
        String estado = (String) datosEntrenador.get("estado");
        perfil.setUbicacion(ciudad + ", " + estado);

        perfil.setAcercaDeMi((String) datosEntrenador.get("descripcionPerfil"));

        Object costoObj = datosEntrenador.get("costoMensualidad");
        Integer costo = costoObj != null ? ((Number) costoObj).intValue() : 0;
        perfil.setCostoMensual(costo);

        Object limiteObj = datosEntrenador.get("limiteAlumnos");
        Integer limite = limiteObj != null ? ((Number) limiteObj).intValue() : 0;
        perfil.setLimiteAlumnos(limite);

        // ✅ FIX: conteo dinámico igual que BuscarEntrenadorServiceImpl
        int actuales = suscripcionRepository.contarAlumnosActivos(usuarioEntrenador);
        perfil.setAlumnosActuales(actuales);

        perfil.setCalificacion(calificacion);
        perfil.setEspecialidades(especialidades != null ? especialidades : new ArrayList<>());
        perfil.setResenas(resenas);
        perfil.setEstadoRelacion(estadoRelacion);

        log.info("Perfil del entrenador {} obtenido. Alumnos: {}/{}", usuarioEntrenador, actuales, limite);
        return perfil;
    }

    private CalificacionDTO obtenerCalificacionDTO(String usuario) {
        Map<String, Object> calificacionData = detalleEntrenadorRepository
                .obtenerCalificaciones(usuario)
                .orElse(Map.of("ratingPromedio", 0.0, "totalResenas", 0));

        Double ratingPromedio = 0.0;
        Object ratingObj = calificacionData.get("ratingPromedio");
        if (ratingObj instanceof Number) {
            ratingPromedio = Math.round(((Number) ratingObj).doubleValue() * 10.0) / 10.0;
        }

        Integer totalResenas = 0;
        Object totalObj = calificacionData.get("totalResenas");
        if (totalObj instanceof Number) {
            totalResenas = ((Number) totalObj).intValue();
        }

        return new CalificacionDTO(ratingPromedio, totalResenas);
    }

    private List<ResenaDTO> obtenerResenasDTO(String usuario) {
        List<Map<String, Object>> resenasData = detalleEntrenadorRepository.obtenerResenas(usuario);
        List<ResenaDTO> resenas = new ArrayList<>();

        for (Map<String, Object> data : resenasData) {
            Integer ratingDado = 0;
            Object ratingObj = data.get("ratingDado");
            if (ratingObj instanceof Number) {
                ratingDado = ((Number) ratingObj).intValue();
            }
            resenas.add(new ResenaDTO(
                    (String) data.get("nombreAlumno"),
                    (String) data.get("fotoAlumno"),
                    ratingDado,
                    (String) data.get("comentario")
            ));
        }
        return resenas;
    }

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
            if (finObj instanceof LocalDate) {
                finMensualidad = (LocalDate) finObj;
            } else if (finObj instanceof java.sql.Date) {
                finMensualidad = ((java.sql.Date) finObj).toLocalDate();
            }

            listaRelaciones.add(new EstadoRelacionDTO.RelacionDeporteDTO(
                    idRelacion, idDeporte, nombreDeporte, status, finMensualidad, statusSuscripcion));
        }

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

        log.info("Relación encontrada: Alumno {} - Entrenador {} | Estado: {} | Deportes: {}",
                usuarioAlumno, usuarioEntrenador, statusPrincipal, listaRelaciones.size());

        return new EstadoRelacionDTO(true, statusPrincipal, idDeportePrincipal,
                nombreDeportePrincipal, yaCalificado, finPrincipal, listaRelaciones);

    }
}