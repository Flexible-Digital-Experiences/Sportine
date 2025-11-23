package com.sportine.backend.service.impl;

import com.sportine.backend.dto.AlumnoProgresoDTO;
import com.sportine.backend.dto.HomeEntrenadorDTO;
import com.sportine.backend.exception.RecursoNoEncontradoException;
import com.sportine.backend.model.*;
import com.sportine.backend.repository.*;
import com.sportine.backend.service.HomeEntrenadorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Implementación del servicio para el home del entrenador.
 * Muestra lista de alumnos con su progreso semanal.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class HomeEntrenadorServiceImpl implements HomeEntrenadorService {

    private final UsuarioRepository usuarioRepository;
    private final EntrenadorAlumnoRepository entrenadorAlumnoRepository;
    private final EntrenamientoRepository entrenamientoRepository;
    private final InformacionAlumnoRepository informacionAlumnoRepository;
    private final ProgresoEntrenamientoRepository progresoEntrenamientoRepository;

    @Override
    @Transactional(readOnly = true)
    public HomeEntrenadorDTO obtenerHomeEntrenador(String username) {
        log.info("Obteniendo home del entrenador {}", username);

        // 1. Obtener datos del entrenador
        Usuario entrenador = usuarioRepository.findByUsuario(username)
                .orElseThrow(() -> new RecursoNoEncontradoException("Entrenador no encontrado: " + username));

        // 2. Obtener alumnos activos del entrenador
        List<EntrenadorAlumno> relaciones = entrenadorAlumnoRepository
                .findByUsuarioEntrenadorAndStatusRelacion(username, "activo");

        // 3. Mapear alumnos a DTOs con progreso
        List<AlumnoProgresoDTO> alumnosDTO = relaciones.stream()
                .map(relacion -> obtenerProgresoAlumno(relacion.getUsuarioAlumno()))
                .collect(Collectors.toList());

        // 4. Calcular estadísticas
        long alumnosActivos = alumnosDTO.stream()
                .filter(AlumnoProgresoDTO::getActivo)
                .count();

        // 5. Construir DTO de respuesta
        HomeEntrenadorDTO dto = new HomeEntrenadorDTO();
        dto.setSaludo("Hola de nuevo, " + entrenador.getNombre());
        dto.setFecha(formatearFecha(LocalDate.now()));
        dto.setMensajeDinamico(generarMensajeDinamico(relaciones.size(), (int) alumnosActivos));
        dto.setAlumnos(alumnosDTO);
        dto.setTotalAlumnos(relaciones.size());
        dto.setAlumnosActivos((int) alumnosActivos);

        log.info("Home del entrenador {} obtenido exitosamente con {} alumnos", username, relaciones.size());
        return dto;
    }

    /**
     * Obtiene el progreso de un alumno específico
     */
    private AlumnoProgresoDTO obtenerProgresoAlumno(String usuarioAlumno) {
        // Obtener datos del alumno
        Usuario alumno = usuarioRepository.findByUsuario(usuarioAlumno)
                .orElse(null);

        if (alumno == null) {
            return null;
        }

        InformacionAlumno infoAlumno = informacionAlumnoRepository
                .findByUsuario(usuarioAlumno)
                .orElse(null);

        // Calcular entrenamientos completados en la última semana
        LocalDateTime hace7Dias = LocalDateTime.now().minusDays(7);
        int completadosSemana = progresoEntrenamientoRepository
                .contarEntrenamientosCompletadosEnRango(
                        usuarioAlumno,
                        hace7Dias,
                        LocalDateTime.now()
                );

        // Contar entrenamientos pendientes
        List<Entrenamiento> entrenamientos = entrenamientoRepository
                .findByUsuario(usuarioAlumno);

        long pendientes = entrenamientos.stream()
                .filter(e -> e.getEstadoEntrenamiento() == Entrenamiento.EstadoEntrenamiento.pendiente)
                .count();

        // Obtener última actividad
        LocalDate ultimaActividad = entrenamientos.stream()
                .filter(e -> e.getEstadoEntrenamiento() == Entrenamiento.EstadoEntrenamiento.finalizado)
                .map(Entrenamiento::getFechaEntrenamiento)
                .max(LocalDate::compareTo)
                .orElse(null);

        // Determinar si está activo (actividad en los últimos 3 días)
        boolean activo = ultimaActividad != null &&
                ultimaActividad.isAfter(LocalDate.now().minusDays(3));

        // Generar descripción de actividad
        String descripcionActividad = generarDescripcionActividad(ultimaActividad, completadosSemana);

        // Construir DTO
        AlumnoProgresoDTO dto = new AlumnoProgresoDTO();
        dto.setUsuario(usuarioAlumno);
        dto.setNombre(alumno.getNombre());
        dto.setApellidos(alumno.getApellidos());
        dto.setFotoPerfil(infoAlumno != null ? infoAlumno.getFotoPerfil() : null);
        dto.setEntrenamientosCompletadosSemana(completadosSemana);
        dto.setEntrenamientosPendientes((int) pendientes);
        dto.setUltimaActividad(ultimaActividad);
        dto.setDescripcionActividad(descripcionActividad);
        dto.setActivo(activo);

        return dto;
    }

    /**
     * Genera la descripción de la última actividad del alumno
     */
    private String generarDescripcionActividad(LocalDate ultimaActividad, int completados) {
        if (ultimaActividad == null) {
            return "Sin actividad reciente";
        }

        LocalDate hoy = LocalDate.now();
        if (ultimaActividad.equals(hoy)) {
            return "Completó entrenamiento hoy";
        } else if (ultimaActividad.equals(hoy.minusDays(1))) {
            return "Completó entrenamiento ayer";
        } else {
            long diasAtras = hoy.toEpochDay() - ultimaActividad.toEpochDay();
            return "Última actividad hace " + diasAtras + " días";
        }
    }

    /**
     * Formatea la fecha para mostrarla bonita
     */
    private String formatearFecha(LocalDate fecha) {
        String diaSemana = fecha.getDayOfWeek()
                .getDisplayName(TextStyle.FULL, new Locale("es", "ES"));

        String diaCapitalizado = diaSemana.substring(0, 1).toUpperCase() +
                diaSemana.substring(1);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d 'de' MMMM 'de' yyyy",
                new Locale("es", "ES"));
        return diaCapitalizado + ", " + fecha.format(formatter);
    }

    /**
     * Genera mensaje dinámico según cantidad de alumnos
     */
    private String generarMensajeDinamico(int totalAlumnos, int alumnosActivos) {
        if (totalAlumnos == 0) {
            return "Aún no tienes alumnos asignados";
        } else if (totalAlumnos == 1) {
            return "Tienes 1 alumno";
        } else {
            return String.format("Tienes %d alumnos, %d activos esta semana",
                    totalAlumnos, alumnosActivos);
        }
    }
}