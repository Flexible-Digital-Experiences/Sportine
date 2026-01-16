package com.sportine.backend.service.impl;

import com.sportine.backend.dto.*;
import com.sportine.backend.exception.RecursoNoEncontradoException;
import com.sportine.backend.model.*;
import com.sportine.backend.repository.*;
import com.sportine.backend.service.StatisticsAlumnoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.DayOfWeek;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class StatisticsAlumnoServiceImpl implements StatisticsAlumnoService {

    private final ProgresoEntrenamientoRepository progresoRepository;
    private final EntrenamientoRepository entrenamientoRepository;
    private final FeedbackEntrenamientoRepository feedbackRepository;
    private final DeporteRepository deporteRepository;
    private final UsuarioRepository usuarioRepository;

    // Colores para las gráficas de deportes
    private static final Map<String, String> COLORES_DEPORTES = Map.of(
            "Fútbol", "#4CAF50",
            "Basketball", "#FF9800",
            "Natación", "#2196F3",
            "Running", "#F44336",
            "Boxeo", "#9C27B0",
            "Tenis", "#FFEB3B",
            "Gimnasio", "#607D8B",
            "Ciclismo", "#00BCD4",
            "Béisbol", "#795548"
    );

    @Override
    public StatisticsOverviewDTO obtenerResumenGeneral(String username) {
        log.info("Calculando resumen general de estadísticas para {}", username);

        // Verificar que el usuario existe
        usuarioRepository.findByUsuario(username)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));

        StatisticsOverviewDTO dto = new StatisticsOverviewDTO();

        // 1. Total de entrenamientos completados
        Long totalEntrenamientos = progresoRepository.countCompletadosByUsuario(username);
        dto.setTotalEntrenamientos(totalEntrenamientos.intValue());

        // 2. Calcular racha actual y mejor racha
        Map<String, Integer> rachas = calcularRachas(username);
        dto.setRachaActual(rachas.get("actual"));
        dto.setMejorRacha(rachas.get("mejor"));

        // 3. Entrenamientos del mes actual
        LocalDateTime inicioMes = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LocalDateTime finMes = LocalDate.now().plusDays(1).atStartOfDay().minusSeconds(1);
        Long entrenamientosMes = progresoRepository.contarEntrenamientosCompletadosEnRango(
                username, inicioMes, finMes
        );
        dto.setEntrenamientosMesActual(entrenamientosMes.intValue());

        // 4. Entrenamientos de la semana actual
        LocalDate hoy = LocalDate.now();
        LocalDate inicioSemana = hoy.with(DayOfWeek.MONDAY);
        LocalDateTime inicioSemanaTime = inicioSemana.atStartOfDay();
        LocalDateTime finSemanaTime = hoy.plusDays(1).atStartOfDay().minusSeconds(1);
        Long entrenamientosSemana = progresoRepository.contarEntrenamientosCompletadosEnRango(
                username, inicioSemanaTime, finSemanaTime
        );
        dto.setEntrenamientosSemanaActual(entrenamientosSemana.intValue());

        // 5. Deportes practicados
        List<Integer> deportesIds = entrenamientoRepository.findDistinctDeportesByUsuario(username);
        dto.setDeportesPracticados(deportesIds.size());

        // 6. Calcular tendencia (comparar con mes anterior)
        LocalDateTime inicioMesAnterior = inicioMes.minusMonths(1);
        LocalDateTime finMesAnterior = inicioMes.minusSeconds(1);
        Long entrenamientosMesAnterior = progresoRepository.contarEntrenamientosCompletadosEnRango(
                username, inicioMesAnterior, finMesAnterior
        );

        if (entrenamientosMesAnterior > 0) {
            double cambio = ((double) (entrenamientosMes - entrenamientosMesAnterior) / entrenamientosMesAnterior) * 100;
            dto.setPorcentajeCambio(cambio);

            if (cambio > 10) {
                dto.setTendencia("mejorando");
            } else if (cambio < -10) {
                dto.setTendencia("decreciendo");
            } else {
                dto.setTendencia("estable");
            }
        } else {
            dto.setTendencia("mejorando");
            dto.setPorcentajeCambio(100.0);
        }

        // 7. Feedback promedio
        Double cansancioPromedio = feedbackRepository.calcularPromedioNivelCansancio(username);
        Double dificultadPromedio = feedbackRepository.calcularPromedioDificultadPercibida(username);
        dto.setNivelCansancioPromedio(cansancioPromedio != null ? cansancioPromedio : 0.0);
        dto.setDificultadPercibidaPromedio(dificultadPromedio != null ? dificultadPromedio : 0.0);

        // 8. Porcentaje de completado (total completados vs total asignados)
        Long totalAsignados = entrenamientoRepository.countByUsuario(username);
        if (totalAsignados > 0) {
            double porcentaje = ((double) totalEntrenamientos / totalAsignados) * 100;
            dto.setPorcentajeCompletado(porcentaje);
        } else {
            dto.setPorcentajeCompletado(0.0);
        }

        // 9. Tiempo total (esto requeriría duración en los entrenamientos, por ahora estimado)
        // Asumimos 60 minutos por entrenamiento promedio
        int tiempoTotalMinutos = totalEntrenamientos.intValue() * 60;
        dto.setTiempoTotalMinutos(tiempoTotalMinutos);
        dto.setTiempoTotalFormateado(formatearTiempo(tiempoTotalMinutos));

        log.info("Resumen general calculado: {} entrenamientos, racha {}", totalEntrenamientos, rachas.get("actual"));
        return dto;
    }

    @Override
    public TrainingFrequencyDTO obtenerFrecuenciaEntrenamientos(String username, String periodo) {
        log.info("Calculando frecuencia de entrenamientos para {} - Período: {}", username, periodo);

        TrainingFrequencyDTO dto = new TrainingFrequencyDTO();
        dto.setPeriodo(periodo);

        List<TrainingFrequencyDTO.DataPoint> dataPoints = new ArrayList<>();
        LocalDate hoy = LocalDate.now();
        int totalEntrenamientos = 0;

        switch (periodo.toUpperCase()) {
            case "WEEK":
                // Últimas 8 semanas
                for (int i = 7; i >= 0; i--) {
                    LocalDate inicioSemana = hoy.minusWeeks(i).with(DayOfWeek.MONDAY);
                    LocalDate finSemana = inicioSemana.plusDays(6);

                    Long count = progresoRepository.contarEntrenamientosCompletadosEnRango(
                            username,
                            inicioSemana.atStartOfDay(),
                            finSemana.plusDays(1).atStartOfDay().minusSeconds(1)
                    );

                    String etiqueta = i == 0 ? "Esta sem." : "Sem " + (8 - i);
                    dataPoints.add(new TrainingFrequencyDTO.DataPoint(
                            etiqueta,
                            count.intValue(),
                            inicioSemana.toString(),
                            i == 0
                    ));
                    totalEntrenamientos += count.intValue();
                }
                dto.setPromedioPorPeriodo(totalEntrenamientos / 8.0);
                break;

            case "MONTH":
                // Últimos 6 meses
                for (int i = 5; i >= 0; i--) {
                    LocalDate inicioMes = hoy.minusMonths(i).withDayOfMonth(1);
                    LocalDate finMes = inicioMes.withDayOfMonth(inicioMes.lengthOfMonth());

                    Long count = progresoRepository.contarEntrenamientosCompletadosEnRango(
                            username,
                            inicioMes.atStartOfDay(),
                            finMes.plusDays(1).atStartOfDay().minusSeconds(1)
                    );

                    String mes = inicioMes.getMonth().toString().substring(0, 3);
                    dataPoints.add(new TrainingFrequencyDTO.DataPoint(
                            mes,
                            count.intValue(),
                            inicioMes.toString(),
                            i == 0
                    ));
                    totalEntrenamientos += count.intValue();
                }
                dto.setPromedioPorPeriodo(totalEntrenamientos / 6.0);
                break;

            case "YEAR":
                // Últimos 12 meses
                for (int i = 11; i >= 0; i--) {
                    LocalDate inicioMes = hoy.minusMonths(i).withDayOfMonth(1);
                    LocalDate finMes = inicioMes.withDayOfMonth(inicioMes.lengthOfMonth());

                    Long count = progresoRepository.contarEntrenamientosCompletadosEnRango(
                            username,
                            inicioMes.atStartOfDay(),
                            finMes.plusDays(1).atStartOfDay().minusSeconds(1)
                    );

                    String mes = inicioMes.getMonth().toString().substring(0, 3);
                    dataPoints.add(new TrainingFrequencyDTO.DataPoint(
                            mes,
                            count.intValue(),
                            inicioMes.toString(),
                            i == 0
                    ));
                    totalEntrenamientos += count.intValue();
                }
                dto.setPromedioPorPeriodo(totalEntrenamientos / 12.0);
                break;

            default:
                throw new IllegalArgumentException("Período no válido: " + periodo);
        }

        dto.setDataPoints(dataPoints);
        dto.setTotalEntrenamientos(totalEntrenamientos);

        log.info("Frecuencia calculada: {} entrenamientos en {} períodos", totalEntrenamientos, dataPoints.size());
        return dto;
    }

    @Override
    public SportsDistributionDTO obtenerDistribucionDeportes(String username) {
        log.info("Calculando distribución de deportes para {}", username);

        SportsDistributionDTO dto = new SportsDistributionDTO();
        List<SportsDistributionDTO.SportData> deportesData = new ArrayList<>();

        // Obtener todos los deportes únicos que ha entrenado
        List<Integer> deportesIds = entrenamientoRepository.findDistinctDeportesByUsuario(username);
        Long totalEntrenamientos = entrenamientoRepository.countCompletadosByUsuario(username);

        dto.setTotalEntrenamientos(totalEntrenamientos.intValue());

        String deportePrincipal = "";
        int maxEntrenamientos = 0;

        for (Integer idDeporte : deportesIds) {
            Deporte deporte = deporteRepository.findById(idDeporte).orElse(null);
            if (deporte != null) {
                Long count = entrenamientoRepository.countByUsuarioAndIdDeporte(username, idDeporte);
                double porcentaje = (count.doubleValue() / totalEntrenamientos) * 100;

                String color = COLORES_DEPORTES.getOrDefault(deporte.getNombreDeporte(), "#757575");

                deportesData.add(new SportsDistributionDTO.SportData(
                        idDeporte,
                        deporte.getNombreDeporte(),
                        count.intValue(),
                        porcentaje,
                        color
                ));

                if (count > maxEntrenamientos) {
                    maxEntrenamientos = count.intValue();
                    deportePrincipal = deporte.getNombreDeporte();
                }
            }
        }

        // Ordenar por cantidad descendente
        deportesData.sort((a, b) -> b.getCantidadEntrenamientos().compareTo(a.getCantidadEntrenamientos()));

        dto.setDeportes(deportesData);
        dto.setDeportePrincipal(deportePrincipal);

        log.info("Distribución calculada: {} deportes, principal: {}", deportesData.size(), deportePrincipal);
        return dto;
    }

    @Override
    public StreakInfoDTO obtenerInformacionRacha(String username) {
        log.info("Calculando información de racha para {}", username);

        StreakInfoDTO dto = new StreakInfoDTO();

        // Calcular rachas
        Map<String, Integer> rachas = calcularRachas(username);
        dto.setRachaActual(rachas.get("actual"));
        dto.setMejorRacha(rachas.get("mejor"));

        // Verificar si entrenó hoy
        LocalDate hoy = LocalDate.now();
        boolean entrenoHoy = progresoRepository.existsCompletadoEnFecha(username, hoy);
        dto.setEntrenoHoy(entrenoHoy);

        // Fecha de inicio de la racha actual
        if (dto.getRachaActual() > 0) {
            dto.setFechaInicioRacha(hoy.minusDays(dto.getRachaActual() - 1));
        }

        // Milestones
        int[] milestones = {7, 10, 14, 21, 30, 60, 100};
        Integer proximoMilestone = null;
        for (int milestone : milestones) {
            if (dto.getRachaActual() < milestone) {
                proximoMilestone = milestone;
                break;
            }
        }

        if (proximoMilestone != null) {
            dto.setProximoMilestone(proximoMilestone);
            dto.setDiasParaProximoMilestone(proximoMilestone - dto.getRachaActual());
        }

        // Mensaje motivacional
        String mensaje = generarMensajeMotivacional(dto.getRachaActual(), entrenoHoy);
        dto.setMensaje(mensaje);

        // Estadísticas de consistencia
        Long diasEntrenados = progresoRepository.contarDiasUnicos(username);
        dto.setDiasEntrenados(diasEntrenados.intValue());

        // Días totales desde el primer entrenamiento
        List<ProgresoEntrenamiento> progresos = progresoRepository.findCompletadosOrderByFecha(username);
        if (!progresos.isEmpty()) {
            LocalDate primerEntrenamiento = progresos.get(progresos.size() - 1)
                    .getFechaFinalizacion().toLocalDate();
            long diasTotales = ChronoUnit.DAYS.between(primerEntrenamiento, hoy) + 1;
            dto.setDiasTotales((int) diasTotales);

            double porcentaje = (diasEntrenados.doubleValue() / diasTotales) * 100;
            dto.setPorcentajeConsistencia(porcentaje);
        }

        log.info("Racha calculada: actual={}, mejor={}", dto.getRachaActual(), dto.getMejorRacha());
        return dto;
    }

    @Override
    public FeedbackPromedioDTO obtenerFeedbackPromedio(String username) {
        log.info("Calculando feedback promedio para {}", username);

        FeedbackPromedioDTO dto = new FeedbackPromedioDTO();

        // Promedios
        Double cansancio = feedbackRepository.calcularPromedioNivelCansancio(username);
        Double dificultad = feedbackRepository.calcularPromedioDificultadPercibida(username);

        dto.setNivelCansancioPromedio(cansancio != null ? cansancio : 0.0);
        dto.setDificultadPercibidaPromedio(dificultad != null ? dificultad : 0.0);

        // Total de feedbacks
        List<FeedbackEntrenamiento> feedbacks = feedbackRepository.findByUsuario(username);
        dto.setTotalFeedbacksRecibidos(feedbacks.size());

        // Estado de ánimo predominante
        if (!feedbacks.isEmpty()) {
            Map<String, Long> estadosAnimo = feedbacks.stream()
                    .filter(f -> f.getEstadoAnimo() != null)
                    .collect(Collectors.groupingBy(
                            FeedbackEntrenamiento::getEstadoAnimo,
                            Collectors.counting()
                    ));

            if (!estadosAnimo.isEmpty()) {
                String predominante = estadosAnimo.entrySet().stream()
                        .max(Map.Entry.comparingByValue())
                        .get().getKey();
                dto.setEstadoAnimoPredominante(predominante);
            }
        }

        // Tendencias (comparar últimos 5 con anteriores)
        if (feedbacks.size() >= 10) {
            List<FeedbackEntrenamiento> ordenados = feedbackRepository
                    .findByUsuarioOrderByFechaFeedbackDesc(username);

            double cansancioReciente = ordenados.stream().limit(5)
                    .mapToInt(FeedbackEntrenamiento::getNivelCansancio)
                    .average().orElse(0);

            double cansancioAnterior = ordenados.stream().skip(5).limit(5)
                    .mapToInt(FeedbackEntrenamiento::getNivelCansancio)
                    .average().orElse(0);

            if (cansancioReciente > cansancioAnterior + 1) {
                dto.setTendenciaCansancio("aumentando");
            } else if (cansancioReciente < cansancioAnterior - 1) {
                dto.setTendenciaCansancio("disminuyendo");
            } else {
                dto.setTendenciaCansancio("estable");
            }
        } else {
            dto.setTendenciaCansancio("estable");
        }

        // Recomendación
        String recomendacion = generarRecomendacion(dto);
        dto.setRecomendacion(recomendacion);

        log.info("Feedback calculado: cansancio={}, dificultad={}", cansancio, dificultad);
        return dto;
    }

    // ==========================================
    // MÉTODOS AUXILIARES PRIVADOS
    // ==========================================

    private Map<String, Integer> calcularRachas(String username) {
        List<ProgresoEntrenamiento> progresos = progresoRepository.findCompletadosOrderByFecha(username);

        if (progresos.isEmpty()) {
            return Map.of("actual", 0, "mejor", 0);
        }

        // 1. Ordenar de forma DESCENDENTE (más reciente primero)
        List<LocalDate> fechas = progresos.stream()
                .map(p -> p.getFechaFinalizacion().toLocalDate())
                .distinct() // Eliminar duplicados del mismo día
                .sorted(Collections.reverseOrder()) // DESCENDENTE
                .collect(Collectors.toList());

        LocalDate hoy = LocalDate.now();
        int rachaActual = 0;
        int mejorRacha = 0;

        // 2. Validar si la racha está "viva" (el entrenamiento más reciente es hoy o ayer)
        LocalDate fechaMasReciente = fechas.get(0);
        long diasDesdeUltimo = ChronoUnit.DAYS.between(fechaMasReciente, hoy);
        boolean rachaViva = (diasDesdeUltimo <= 1);

        // 3. Calcular la racha actual (si está viva)
        if (rachaViva) {
            rachaActual = 1; // Contar el primer día

            for (int i = 1; i < fechas.size(); i++) {
                LocalDate fechaActual = fechas.get(i);
                LocalDate fechaAnterior = fechas.get(i - 1);

                long diferencia = ChronoUnit.DAYS.between(fechaActual, fechaAnterior);

                if (diferencia == 1) {
                    rachaActual++;
                } else {
                    break; // Se rompió la racha
                }
            }
        }

        // 4. Calcular la mejor racha histórica
        int rachaTemp = 1;
        mejorRacha = 1;

        for (int i = 1; i < fechas.size(); i++) {
            LocalDate fechaActual = fechas.get(i);
            LocalDate fechaAnterior = fechas.get(i - 1);

            long diferencia = ChronoUnit.DAYS.between(fechaActual, fechaAnterior);

            if (diferencia == 1) {
                rachaTemp++;
                mejorRacha = Math.max(mejorRacha, rachaTemp);
            } else {
                rachaTemp = 1;
            }
        }

        // La racha actual también puede ser la mejor racha
        mejorRacha = Math.max(mejorRacha, rachaActual);

        return Map.of("actual", rachaActual, "mejor", mejorRacha);
    }

    private String formatearTiempo(int minutos) {
        int horas = minutos / 60;
        int mins = minutos % 60;
        return horas + "h " + mins + "m";
    }

    private String generarMensajeMotivacional(int racha, boolean entrenoHoy) {
        if (racha == 0) {
            return "¡Comienza tu racha hoy! Cada entrenamiento cuenta.";
        } else if (racha < 7) {
            return "¡Vas " + racha + " días consecutivos! Sigue así.";
        } else if (racha < 14) {
            return "¡Increíble! " + racha + " días de constancia. ¡Eres imparable!";
        } else if (racha < 30) {
            return "¡Wow! " + racha + " días consecutivos. ¡Eres una inspiración!";
        } else {
            return "¡LEYENDA! " + racha + " días seguidos entrenando. ¡Nada te detiene!";
        }
    }

    private String generarRecomendacion(FeedbackPromedioDTO dto) {
        double cansancio = dto.getNivelCansancioPromedio();
        double dificultad = dto.getDificultadPercibidaPromedio();

        if (cansancio > 8 && dificultad > 8) {
            return "Considera tomar un día de descanso. Tu cuerpo necesita recuperación.";
        } else if (cansancio < 4 && dificultad < 4) {
            return "¡Excelente! Podrías aumentar la intensidad de tus entrenamientos.";
        } else if (dto.getTendenciaCansancio().equals("aumentando")) {
            return "Tu nivel de cansancio está aumentando. Asegúrate de descansar bien.";
        } else {
            return "Mantén el buen trabajo. Tu progreso es constante y saludable.";
        }
    }
}