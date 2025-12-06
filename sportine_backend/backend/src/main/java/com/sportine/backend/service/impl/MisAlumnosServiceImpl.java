package com.sportine.backend.service.impl;

import com.sportine.backend.dto.AlumnoEntrenadorDTO;
import com.sportine.backend.repository.EntrenadorAlumnoRepository;
import com.sportine.backend.service.MisAlumnosService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MisAlumnosServiceImpl implements MisAlumnosService {

    private final EntrenadorAlumnoRepository entrenadorAlumnoRepository;

    @Override
    public List<AlumnoEntrenadorDTO> obtenerMisAlumnos(String usuarioEntrenador) {
        log.info("Obteniendo alumnos del entrenador: {}", usuarioEntrenador);

        List<Map<String, Object>> resultados = entrenadorAlumnoRepository
                .obtenerAlumnosPorEntrenador(usuarioEntrenador);

        return resultados.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<AlumnoEntrenadorDTO> obtenerAlumnosPendientes(String usuarioEntrenador) {
        log.info("Obteniendo alumnos pendientes del entrenador: {}", usuarioEntrenador);

        List<Map<String, Object>> resultados = entrenadorAlumnoRepository
                .obtenerAlumnosPorEntrenador(usuarioEntrenador);

        return resultados.stream()
                .map(this::mapToDTO)
                .filter(alumno -> "pendiente".equalsIgnoreCase(alumno.getStatusRelacion()))
                .collect(Collectors.toList());
    }

    private AlumnoEntrenadorDTO mapToDTO(Map<String, Object> map) {
        AlumnoEntrenadorDTO dto = new AlumnoEntrenadorDTO();

        dto.setUsuarioAlumno((String) map.get("usuarioAlumno"));
        dto.setNombreCompleto((String) map.get("nombreCompleto"));
        dto.setFotoPerfil((String) map.get("fotoPerfil"));

        // Edad
        Object edadObj = map.get("edad");
        if (edadObj != null) {
            dto.setEdad(((Number) edadObj).intValue());
        }

        dto.setDeportes((String) map.get("deportes"));
        dto.setStatusRelacion((String) map.get("statusRelacion"));

        // Fecha de inicio
        Date fechaSqlDate = (Date) map.get("fechaInicio");
        if (fechaSqlDate != null) {
            LocalDate fechaInicio = fechaSqlDate.toLocalDate();
            dto.setFechaInicio(calcularTiempoDesde(fechaInicio));
        }

        return dto;
    }

    private String calcularTiempoDesde(LocalDate fechaInicio) {
        LocalDate ahora = LocalDate.now();
        long meses = ChronoUnit.MONTHS.between(fechaInicio, ahora);

        if (meses == 0) {
            long dias = ChronoUnit.DAYS.between(fechaInicio, ahora);
            if (dias == 0) return "Hoy";
            if (dias == 1) return "Hace 1 día";
            return "Hace " + dias + " días";
        } else if (meses == 1) {
            return "Hace 1 mes";
        } else if (meses < 12) {
            return "Hace " + meses + " meses";
        } else {
            long años = meses / 12;
            return "Hace " + años + (años == 1 ? " año" : " años");
        }
    }
}