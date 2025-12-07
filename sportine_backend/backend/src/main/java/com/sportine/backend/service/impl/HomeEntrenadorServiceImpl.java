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

@Service
@RequiredArgsConstructor
@Slf4j
public class HomeEntrenadorServiceImpl implements HomeEntrenadorService {

    private final UsuarioRepository usuarioRepository;
    private final EntrenadorAlumnoRepository entrenadorAlumnoRepository;
    private final EntrenamientoRepository entrenamientoRepository;
    private final InformacionAlumnoRepository informacionAlumnoRepository;
    private final ProgresoEntrenamientoRepository progresoEntrenamientoRepository;

    // --- NUEVO: Inyectamos el repositorio para buscar nombres de deportes ---
    private final DeporteRepository deporteRepository;

    @Override
    @Transactional(readOnly = true)
    public HomeEntrenadorDTO obtenerHomeEntrenador(String username) {
        log.info("Obteniendo home del entrenador {}", username);

        Usuario entrenador = usuarioRepository.findByUsuario(username)
                .orElseThrow(() -> new RecursoNoEncontradoException("Entrenador no encontrado: " + username));

        // 1. Obtenemos las relaciones. GRACIAS a tu corrección en la BD,
        // ahora cada relación ya trae el id_deporte específico.
        List<EntrenadorAlumno> relaciones = entrenadorAlumnoRepository
                .findByUsuarioEntrenadorAndStatusRelacion(username, "activo");

        // 2. Mapeamos a DTO
        List<AlumnoProgresoDTO> alumnosDTO = relaciones.stream()
                .map(relacion -> {
                    // Generamos el DTO base con la info del alumno
                    AlumnoProgresoDTO dto = obtenerProgresoAlumno(relacion.getUsuarioAlumno());

                    if (dto != null) {
                        // --- LÓGICA DE TRADUCCIÓN ID -> NOMBRE ---
                        // Usamos el ID que está en la tabla Entrenador_Alumno
                        Integer idDeporte = relacion.getIdDeporte();
                        String nombreDeporte = "Sin asignar";

                        if (idDeporte != null) {
                            nombreDeporte = deporteRepository.findById(idDeporte)
                                    .map(Deporte::getNombreDeporte) // Obtenemos el nombre de la entidad Deporte
                                    .orElse("Desconocido");
                        }

                        // Metemos el nombre en el DTO para que Android lo lea fácil
                        dto.setDeporte(nombreDeporte);
                    }
                    return dto;
                })
                .collect(Collectors.toList());

        // 3. Estadísticas
        long alumnosActivos = alumnosDTO.stream()
                .filter(dto -> dto != null && dto.getActivo())
                .count();

        HomeEntrenadorDTO dto = new HomeEntrenadorDTO();
        dto.setSaludo("Hola de nuevo, " + entrenador.getNombre());
        dto.setFecha(formatearFecha(LocalDate.now()));
        dto.setMensajeDinamico(generarMensajeDinamico(relaciones.size(), (int) alumnosActivos));
        dto.setAlumnos(alumnosDTO);
        dto.setTotalAlumnos(relaciones.size());
        dto.setAlumnosActivos((int) alumnosActivos);

        return dto;
    }

    private AlumnoProgresoDTO obtenerProgresoAlumno(String usuarioAlumno) {
        // ... (Esta parte se mantiene igual, obteniendo info personal y estadísticas) ...
        Usuario alumno = usuarioRepository.findByUsuario(usuarioAlumno).orElse(null);
        if (alumno == null) return null;

        InformacionAlumno infoAlumno = informacionAlumnoRepository
                .findByUsuario(usuarioAlumno)
                .orElse(null);

        LocalDateTime hace7Dias = LocalDateTime.now().minusDays(7);
        Long completadosSemanaLong = progresoEntrenamientoRepository
                .contarEntrenamientosCompletadosEnRango(usuarioAlumno, hace7Dias, LocalDateTime.now());
        int completadosSemana = completadosSemanaLong.intValue();

        List<Entrenamiento> entrenamientos = entrenamientoRepository.findByUsuario(usuarioAlumno);

        long pendientes = entrenamientos.stream()
                .filter(e -> e.getEstadoEntrenamiento() == Entrenamiento.EstadoEntrenamiento.pendiente)
                .count();

        LocalDate ultimaActividad = entrenamientos.stream()
                .filter(e -> e.getEstadoEntrenamiento() == Entrenamiento.EstadoEntrenamiento.finalizado)
                .map(Entrenamiento::getFechaEntrenamiento)
                .max(LocalDate::compareTo)
                .orElse(null);

        boolean activo = ultimaActividad != null && ultimaActividad.isAfter(LocalDate.now().minusDays(3));
        String descripcionActividad = generarDescripcionActividad(ultimaActividad, completadosSemana);

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

        // Nota: No seteamos el deporte aquí dentro porque el deporte depende
        // de la relación con el entrenador, lo cual manejamos en el loop principal arriba.

        return dto;
    }

    // ... (Métodos auxiliares formatearFecha, generarMensajeDinamico, etc. iguales) ...
    private String generarDescripcionActividad(LocalDate ultimaActividad, int completados) {
        if (ultimaActividad == null) return "Sin actividad reciente";
        LocalDate hoy = LocalDate.now();
        if (ultimaActividad.equals(hoy)) return "Completó entrenamiento hoy";
        else if (ultimaActividad.equals(hoy.minusDays(1))) return "Completó entrenamiento ayer";
        else {
            long diasAtras = hoy.toEpochDay() - ultimaActividad.toEpochDay();
            return "Última actividad hace " + diasAtras + " días";
        }
    }

    private String formatearFecha(LocalDate fecha) {
        String diaSemana = fecha.getDayOfWeek().getDisplayName(TextStyle.FULL, new Locale("es", "ES"));
        String diaCapitalizado = diaSemana.substring(0, 1).toUpperCase() + diaSemana.substring(1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d 'de' MMMM 'de' yyyy", new Locale("es", "ES"));
        return diaCapitalizado + ", " + fecha.format(formatter);
    }

    private String generarMensajeDinamico(int totalAlumnos, int alumnosActivos) {
        if (totalAlumnos == 0) return "Aún no tienes alumnos asignados";
        else if (totalAlumnos == 1) return "Tienes 1 alumno";
        else return String.format("Tienes %d alumnos, %d activos esta semana", totalAlumnos, alumnosActivos);
    }
}