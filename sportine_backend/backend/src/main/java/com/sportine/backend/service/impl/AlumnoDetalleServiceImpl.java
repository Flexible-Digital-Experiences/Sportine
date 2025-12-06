package com.sportine.backend.service.impl;

import com.sportine.backend.dto.AlumnoDetalleEntrenadorDTO;
import com.sportine.backend.repository.AlumnoDeporteRepository;
import com.sportine.backend.repository.AlumnoDetalleRepository;
import com.sportine.backend.repository.EntrenadorAlumnoRepository;
import com.sportine.backend.service.AlumnoDetalleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class AlumnoDetalleServiceImpl implements AlumnoDetalleService {

    private final AlumnoDetalleRepository alumnoDetalleRepository;
    private final AlumnoDeporteRepository alumnoDeporteRepository;
    private final EntrenadorAlumnoRepository entrenadorAlumnoRepository;

    @Override
    public AlumnoDetalleEntrenadorDTO obtenerDetalleAlumno(String usuarioEntrenador, String usuarioAlumno) {
        log.info("Obteniendo detalle del alumno {} para el entrenador {}", usuarioAlumno, usuarioEntrenador);

        List<Map<String, Object>> resultados = alumnoDetalleRepository
                .obtenerDetalleAlumnoEntrenador(usuarioEntrenador, usuarioAlumno);

        if (resultados.isEmpty()) {
            log.warn("No se encontró relación entre entrenador {} y alumno {}", usuarioEntrenador, usuarioAlumno);
            return null;
        }

        // El primer registro tiene los datos básicos del alumno
        Map<String, Object> primerRegistro = resultados.get(0);

        AlumnoDetalleEntrenadorDTO dto = new AlumnoDetalleEntrenadorDTO();

        // Datos básicos
        dto.setUsuarioAlumno((String) primerRegistro.get("usuarioAlumno"));
        dto.setNombreCompleto((String) primerRegistro.get("nombreCompleto"));
        dto.setFotoPerfil((String) primerRegistro.get("fotoPerfil"));
        dto.setSexo((String) primerRegistro.get("sexo"));
        dto.setCiudad((String) primerRegistro.get("ciudad"));

        // Edad
        Object edadObj = primerRegistro.get("edad");
        if (edadObj != null) {
            dto.setEdad(((Number) edadObj).intValue());
        }

        // Datos físicos
        Object estaturaObj = primerRegistro.get("estatura");
        if (estaturaObj != null) {
            dto.setEstatura(((Number) estaturaObj).floatValue());
        }

        Object pesoObj = primerRegistro.get("peso");
        if (pesoObj != null) {
            dto.setPeso(((Number) pesoObj).floatValue());
        }

        // Datos de salud
        dto.setLesiones((String) primerRegistro.get("lesiones"));
        dto.setPadecimientos((String) primerRegistro.get("padecimientos"));

        // Procesar deportes (cada registro es un deporte)
        List<AlumnoDetalleEntrenadorDTO.DeporteConRelacionDTO> deportes = new ArrayList<>();

        for (Map<String, Object> registro : resultados) {
            AlumnoDetalleEntrenadorDTO.DeporteConRelacionDTO deporte =
                    new AlumnoDetalleEntrenadorDTO.DeporteConRelacionDTO();

            // ID Deporte
            Object idDeporteObj = registro.get("idDeporte");
            if (idDeporteObj != null) {
                deporte.setIdDeporte(((Number) idDeporteObj).intValue());
            }

            deporte.setNombreDeporte((String) registro.get("nombreDeporte"));
            deporte.setNivel((String) registro.get("nivel"));
            deporte.setEstadoRelacion((String) registro.get("estadoRelacion"));

            // Fecha de inicio
            Date fechaInicioSql = (Date) registro.get("fechaInicio");
            if (fechaInicioSql != null) {
                deporte.setFechaInicio(fechaInicioSql.toLocalDate());
            }

            // Fin mensualidad
            Date finMensualidadSql = (Date) registro.get("finMensualidad");
            if (finMensualidadSql != null) {
                deporte.setFinMensualidad(finMensualidadSql.toLocalDate());
            }

            deportes.add(deporte);
        }

        dto.setDeportes(deportes);

        log.info("Detalle del alumno {} obtenido exitosamente. Deportes: {}",
                usuarioAlumno, deportes.size());

        return dto;
    }

    @Override
    public void actualizarNivelAlumno(String usuarioEntrenador, String usuarioAlumno,
                                      Integer idDeporte, Integer idNivel) {  // ← Integer, no String

        log.info("Actualizando nivel del alumno {} en deporte {} a nivel ID {}",
                usuarioAlumno, idDeporte, idNivel);

        // Verificar que existe la relación
        boolean existeRelacion = entrenadorAlumnoRepository
                .existsByUsuarioEntrenadorAndUsuarioAlumnoAndIdDeporte(
                        usuarioEntrenador, usuarioAlumno, idDeporte);

        if (!existeRelacion) {
            throw new IllegalArgumentException(
                    "No existe relación entre el entrenador y el alumno para este deporte");
        }

        // Actualizar nivel en Alumno_Deporte
        alumnoDeporteRepository.actualizarNivel(usuarioAlumno, idDeporte, idNivel);

        log.info("Nivel actualizado exitosamente");
    }

    @Override
    @Transactional
    public void actualizarEstadoRelacion(String usuarioEntrenador, String usuarioAlumno,
                                         Integer idDeporte, String nuevoEstado) {
        log.info("Actualizando estado de relación del alumno {} en deporte {} a {}",
                usuarioAlumno, idDeporte, nuevoEstado);

        // Actualizar el estado en Entrenador_Alumno
        entrenadorAlumnoRepository.actualizarEstadoRelacion(
                usuarioEntrenador, usuarioAlumno, idDeporte, nuevoEstado);

        log.info("Estado de relación actualizado exitosamente");
    }
}