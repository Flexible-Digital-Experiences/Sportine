package com.sportine.backend.service.impl;

import com.sportine.backend.dto.*;
import com.sportine.backend.exception.AccesoNoAutorizadoException;
import com.sportine.backend.exception.RecursoNoEncontradoException;
import com.sportine.backend.model.*;
import com.sportine.backend.repository.*;
import com.sportine.backend.service.StatisticsAlumnoService;
import com.sportine.backend.service.StatisticsEntrenadorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class StatisticsEntrenadorServiceImpl implements StatisticsEntrenadorService {

    private final EntrenadorAlumnoRepository entrenadorAlumnoRepository;
    private final ProgresoEntrenamientoRepository progresoRepository;
    private final EntrenamientoRepository entrenamientoRepository;
    private final FeedbackEntrenamientoRepository feedbackRepository;
    private final DeporteRepository deporteRepository;
    private final UsuarioRepository usuarioRepository;
    private final InformacionAlumnoRepository informacionAlumnoRepository;
    private final StatisticsAlumnoService statisticsAlumnoService;

    @Override
    public List<AlumnoCardStatsDTO> obtenerResumenAlumnos(String usuarioEntrenador) {
        log.info("Obteniendo resumen de alumnos para entrenador: {}", usuarioEntrenador);

        // Obtener alumnos activos
        List<EntrenadorAlumno> relaciones = entrenadorAlumnoRepository
                .findAlumnosActivosByEntrenador(usuarioEntrenador);

        List<AlumnoCardStatsDTO> alumnosStats = new ArrayList<>();

        for (EntrenadorAlumno relacion : relaciones) {
            AlumnoCardStatsDTO dto = construirCardAlumno(relacion, usuarioEntrenador);
            alumnosStats.add(dto);
        }

        // Ordenar por entrenamientos del mes (más activos primero)
        alumnosStats.sort((a, b) -> b.getEntrenamientosMesActual().compareTo(a.getEntrenamientosMesActual()));

        log.info("Resumen de {} alumnos generado", alumnosStats.size());
        return alumnosStats;
    }

    @Override
    public DetalleEstadisticasAlumnoDTO obtenerDetalleEstadisticasAlumno(
            String usuarioEntrenador,
            String usuarioAlumno) {

        log.info("Obteniendo detalle de estadísticas de {} para entrenador {}", usuarioAlumno, usuarioEntrenador);

        // 1. Validar relación activa
        validarRelacion(usuarioEntrenador, usuarioAlumno);

        // 2. Obtener información del alumno
        Usuario alumno = usuarioRepository.findByUsuario(usuarioAlumno)
                .orElseThrow(() -> new RecursoNoEncontradoException("Alumno no encontrado"));

        InformacionAlumno infoAlumno = informacionAlumnoRepository.findByUsuario(usuarioAlumno)
                .orElse(new InformacionAlumno());

        // 3. Construir DTO
        DetalleEstadisticasAlumnoDTO dto = new DetalleEstadisticasAlumnoDTO();
        dto.setUsuario(usuarioAlumno);
        dto.setNombreCompleto(alumno.getNombre() + " " + alumno.getApellidos());
        dto.setFotoPerfil(infoAlumno.getFotoPerfil());

        // 4. Obtener estadísticas usando el service del alumno
        dto.setResumenGeneral(statisticsAlumnoService.obtenerResumenGeneral(usuarioAlumno));
        dto.setFrecuenciaEntrenamientos(statisticsAlumnoService.obtenerFrecuenciaEntrenamientos(usuarioAlumno, "MONTH"));
        dto.setDistribucionDeportes(statisticsAlumnoService.obtenerDistribucionDeportes(usuarioAlumno));
        dto.setInfoRacha(statisticsAlumnoService.obtenerInformacionRacha(usuarioAlumno));
        dto.setFeedbackPromedio(statisticsAlumnoService.obtenerFeedbackPromedio(usuarioAlumno));

        // 5. Información específica de la relación con este entrenador
        EntrenadorAlumno relacion = entrenadorAlumnoRepository
                .findByUsuarioEntrenadorAndUsuarioAlumno(usuarioEntrenador, usuarioAlumno)
                .orElseThrow(() -> new RecursoNoEncontradoException("Relación no encontrada"));

        dto.setFechaInicioRelacion(relacion.getFechaInicio().toString());

        long diasJuntos = ChronoUnit.DAYS.between(relacion.getFechaInicio(), LocalDate.now());
        dto.setDiasJuntos((int) diasJuntos);

        Long entrenamientosJuntos = entrenamientoRepository.countByUsuarioAndUsuarioEntrenador(
                usuarioAlumno,
                usuarioEntrenador
        );
        dto.setEntrenamientosJuntos(entrenamientosJuntos.intValue());

        log.info("Detalle generado: {} entrenamientos juntos, {} días", entrenamientosJuntos, diasJuntos);
        return dto;
    }

    @Override
    public TrainingFrequencyDTO obtenerFrecuenciaAlumno(
            String usuarioEntrenador,
            String usuarioAlumno,
            String periodo) {

        log.info("Obteniendo frecuencia de {} para entrenador {} - período: {}",
                usuarioAlumno, usuarioEntrenador, periodo);

        // Validar relación
        validarRelacion(usuarioEntrenador, usuarioAlumno);

        // Delegar al service del alumno
        return statisticsAlumnoService.obtenerFrecuenciaEntrenamientos(usuarioAlumno, periodo);
    }

    @Override
    public SportsDistributionDTO obtenerDistribucionDeportesAlumno(
            String usuarioEntrenador,
            String usuarioAlumno) {

        log.info("Obteniendo distribución de deportes de {} para entrenador {}",
                usuarioAlumno, usuarioEntrenador);

        // Validar relación
        validarRelacion(usuarioEntrenador, usuarioAlumno);

        // Delegar al service del alumno
        return statisticsAlumnoService.obtenerDistribucionDeportes(usuarioAlumno);
    }

    @Override
    public FeedbackPromedioDTO obtenerFeedbackAlumno(
            String usuarioEntrenador,
            String usuarioAlumno) {

        log.info("Obteniendo feedback de {} para entrenador {}", usuarioAlumno, usuarioEntrenador);

        // Validar relación
        validarRelacion(usuarioEntrenador, usuarioAlumno);

        // Delegar al service del alumno
        return statisticsAlumnoService.obtenerFeedbackPromedio(usuarioAlumno);
    }

    // ==========================================
    // MÉTODOS AUXILIARES PRIVADOS
    // ==========================================

    private void validarRelacion(String usuarioEntrenador, String usuarioAlumno) {
        boolean tieneRelacion = entrenadorAlumnoRepository
                .existsByUsuarioEntrenadorAndUsuarioAlumnoAndStatusRelacion(
                        usuarioEntrenador,
                        usuarioAlumno,
                        "activo"  // ✅ Tercer parámetro: solo permitir si la relación está activa
                );

        if (!tieneRelacion) {
            throw new AccesoNoAutorizadoException(
                    "No tienes permiso para ver las estadísticas de este alumno"
            );
        }
    }

    private AlumnoCardStatsDTO construirCardAlumno(EntrenadorAlumno relacion, String usuarioEntrenador) {
        String usuarioAlumno = relacion.getUsuarioAlumno();

        AlumnoCardStatsDTO dto = new AlumnoCardStatsDTO();

        // Información del alumno
        Usuario alumno = usuarioRepository.findByUsuario(usuarioAlumno)
                .orElse(new Usuario());

        dto.setUsuario(usuarioAlumno);
        dto.setNombreCompleto(alumno.getNombre() + " " + alumno.getApellidos());

        InformacionAlumno infoAlumno = informacionAlumnoRepository.findByUsuario(usuarioAlumno)
                .orElse(new InformacionAlumno());
        dto.setFotoPerfil(infoAlumno.getFotoPerfil());

        // Deporte principal de la relación
        if (relacion.getIdDeporte() != null) {
            Deporte deporte = deporteRepository.findById(relacion.getIdDeporte())
                    .orElse(null);
            if (deporte != null) {
                dto.setDeportePrincipal(deporte.getNombreDeporte());
                dto.setIdDeportePrincipal(deporte.getIdDeporte());
            }
        }

        // Métricas
        Long totalEntrenamientos = progresoRepository.countCompletadosByUsuario(usuarioAlumno);
        dto.setTotalEntrenamientos(totalEntrenamientos.intValue());

        // Racha actual
        Map<String, Integer> rachas = calcularRachas(usuarioAlumno);
        dto.setRachaActual(rachas.get("actual"));

        // Entrenamientos del mes
        LocalDateTime inicioMes = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LocalDateTime finMes = LocalDate.now().atTime(23, 59, 59);
        Long entrenamientosMes = progresoRepository.contarEntrenamientosCompletadosEnRango(
                usuarioAlumno, inicioMes, finMes
        );
        dto.setEntrenamientosMesActual(entrenamientosMes.intValue());

        // Nivel de compromiso
        calcularNivelCompromiso(dto);

        // Última actividad
        List<ProgresoEntrenamiento> progresos = progresoRepository
                .findTopByUsuarioOrderByFechaFinalizacionDesc(usuarioAlumno);

        if (!progresos.isEmpty()) {
            LocalDateTime ultimaFecha = progresos.get(0).getFechaFinalizacion();
            long diasDesde = ChronoUnit.DAYS.between(ultimaFecha.toLocalDate(), LocalDate.now());

            if (diasDesde == 0) {
                dto.setUltimaActividad("Hoy");
                dto.setEntrenoHoy(true);
            } else if (diasDesde == 1) {
                dto.setUltimaActividad("Ayer");
                dto.setEntrenoHoy(false);
            } else {
                dto.setUltimaActividad("Hace " + diasDesde + " días");
                dto.setEntrenoHoy(false);
            }
        } else {
            dto.setUltimaActividad("Sin actividad");
            dto.setEntrenoHoy(false);
        }

        // Feedback promedio
        Double cansancio = feedbackRepository.calcularPromedioNivelCansancio(usuarioAlumno);
        Double dificultad = feedbackRepository.calcularPromedioDificultadPercibida(usuarioAlumno);
        dto.setNivelCansancioPromedio(cansancio != null ? cansancio : 0.0);
        dto.setDificultadPercibidaPromedio(dificultad != null ? dificultad : 0.0);

        return dto;
    }

    private void calcularNivelCompromiso(AlumnoCardStatsDTO dto) {
        int entrenamientosMes = dto.getEntrenamientosMesActual();
        int racha = dto.getRachaActual();

        // Lógica simple de compromiso
        if (entrenamientosMes >= 12 || racha >= 7) {
            dto.setNivelCompromiso("alto");
            dto.setColorCompromiso("#4CAF50"); // Verde
        } else if (entrenamientosMes >= 6 || racha >= 3) {
            dto.setNivelCompromiso("medio");
            dto.setColorCompromiso("#FF9800"); // Naranja
        } else {
            dto.setNivelCompromiso("bajo");
            dto.setColorCompromiso("#F44336"); // Rojo
        }
    }

    private Map<String, Integer> calcularRachas(String username) {
        List<ProgresoEntrenamiento> progresos = progresoRepository.findCompletadosOrderByFecha(username);

        int rachaActual = 0;
        int mejorRacha = 0;
        int rachaTemp = 0;

        if (progresos.isEmpty()) {
            return Map.of("actual", 0, "mejor", 0);
        }

        LocalDate fechaAnterior = null;
        LocalDate hoy = LocalDate.now();
        boolean rachaActiva = false;

        for (ProgresoEntrenamiento progreso : progresos) {
            LocalDate fecha = progreso.getFechaFinalizacion().toLocalDate();

            if (fechaAnterior == null) {
                rachaTemp = 1;
                if (fecha.equals(hoy) || fecha.equals(hoy.minusDays(1))) {
                    rachaActiva = true;
                }
            } else {
                long diasDiferencia = ChronoUnit.DAYS.between(fecha, fechaAnterior);

                if (diasDiferencia == 1) {
                    rachaTemp++;
                } else {
                    mejorRacha = Math.max(mejorRacha, rachaTemp);
                    rachaTemp = 1;
                    if (fecha.equals(hoy) || fecha.equals(hoy.minusDays(1))) {
                        rachaActiva = true;
                    } else {
                        rachaActiva = false;
                    }
                }
            }

            fechaAnterior = fecha;
        }

        mejorRacha = Math.max(mejorRacha, rachaTemp);

        if (rachaActiva && fechaAnterior != null) {
            long diasDesdeUltimo = ChronoUnit.DAYS.between(fechaAnterior, hoy);
            if (diasDesdeUltimo <= 1) {
                rachaActual = rachaTemp;
            }
        }

        return Map.of("actual", rachaActual, "mejor", mejorRacha);
    }
}