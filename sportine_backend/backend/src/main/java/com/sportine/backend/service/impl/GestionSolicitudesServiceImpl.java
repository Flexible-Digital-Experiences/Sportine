package com.sportine.backend.service.impl;

import com.sportine.backend.dto.RespuestaSolicitudRequestDTO;
import com.sportine.backend.dto.SolicitudEntrenadorDTO;
import com.sportine.backend.model.SolicitudEntrenamiento;
import com.sportine.backend.repository.AlumnoDeporteRepository;
import com.sportine.backend.repository.EntrenadorAlumnoRepository;
import com.sportine.backend.repository.SolicitudEntrenamientoRepository;
import com.sportine.backend.service.GestionSolicitudesService;
import com.sportine.backend.model.EntrenadorAlumno;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.math.BigInteger;
import java.sql.Date;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class GestionSolicitudesServiceImpl implements GestionSolicitudesService {

    private final SolicitudEntrenamientoRepository solicitudRepository;
    private final EntrenadorAlumnoRepository entrenadorAlumnoRepository;
    private final AlumnoDeporteRepository alumnoDeporteRepository;
    private final CarreraDeportivaService carreraDeportivaService; // ← NUEVO

    @Override
    public List<SolicitudEntrenadorDTO> obtenerSolicitudesEnRevision(String usuarioEntrenador) {
        List<Map<String, Object>> resultados = solicitudRepository
                .obtenerSolicitudesPorEstado(usuarioEntrenador, "En_revisión");
        return resultados.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public List<SolicitudEntrenadorDTO> obtenerSolicitudesAceptadas(String usuarioEntrenador) {
        List<Map<String, Object>> resultados = solicitudRepository
                .obtenerSolicitudesPorEstado(usuarioEntrenador, "Aprobada");
        return resultados.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void responderSolicitud(RespuestaSolicitudRequestDTO request, String usuarioEntrenador) {
        log.info("=== RESPONDER SOLICITUD ===");
        log.info("ID Solicitud: {}", request.getIdSolicitud());
        log.info("Usuario entrenador recibido: '{}'", usuarioEntrenador);
        log.info("Acción: {}", request.getAccion());

        SolicitudEntrenamiento solicitud = solicitudRepository.findById(request.getIdSolicitud())
                .orElseThrow(() -> new RuntimeException(
                        "Solicitud no encontrada con ID: " + request.getIdSolicitud()));

        log.info("Solicitud encontrada - Entrenador en BD: '{}', Alumno: '{}'",
                solicitud.getUsuarioEntrenador(), solicitud.getUsuarioAlumno());

        String entrenadorBD       = solicitud.getUsuarioEntrenador();
        String entrenadorRecibido = usuarioEntrenador;

        log.info("Comparación: BD='{}' Recibido='{}' equals={}",
                entrenadorBD, entrenadorRecibido, entrenadorBD.equals(entrenadorRecibido));

        if (!solicitud.getUsuarioEntrenador().trim().equalsIgnoreCase(usuarioEntrenador.trim())) {
            throw new RuntimeException("No tienes permiso para responder esta solicitud");
        }

        if (solicitud.getStatusSolicitud() != SolicitudEntrenamiento.StatusSolicitud.En_revisión) {
            throw new RuntimeException("La solicitud no está en estado 'En revisión'. Estado actual: "
                    + solicitud.getStatusSolicitud());
        }

        if ("aceptar".equalsIgnoreCase(request.getAccion())) {
            aceptarSolicitud(solicitud);
        } else if ("rechazar".equalsIgnoreCase(request.getAccion())) {
            rechazarSolicitud(solicitud);
        } else {
            throw new RuntimeException("Acción inválida: " + request.getAccion());
        }

        log.info("✓ Solicitud procesada exitosamente");
    }

    private void aceptarSolicitud(SolicitudEntrenamiento solicitud) {
        // 1. Cambiar estado de solicitud
        solicitud.setStatusSolicitud(SolicitudEntrenamiento.StatusSolicitud.Aprobada);
        solicitudRepository.save(solicitud);

        // 2. Registrar deporte del alumno si no lo tiene
        boolean tieneDeporte = alumnoDeporteRepository
                .findByUsuarioAndIdDeporte(solicitud.getUsuarioAlumno(), solicitud.getIdDeporte())
                .isPresent();

        if (!tieneDeporte) {
            alumnoDeporteRepository.insertarAlumnoDeporte(
                    solicitud.getUsuarioAlumno(),
                    solicitud.getIdDeporte(),
                    "Principiante",
                    LocalDate.now()
            );
        }

        // 3. Crear relación entrenador-alumno
        EntrenadorAlumno relacion = new EntrenadorAlumno();
        relacion.setUsuarioEntrenador(solicitud.getUsuarioEntrenador());
        relacion.setUsuarioAlumno(solicitud.getUsuarioAlumno());
        relacion.setIdDeporte(solicitud.getIdDeporte());
        relacion.setFechaInicio(LocalDate.now());
        relacion.setFinMensualidad(null);
        relacion.setStatusRelacion("pendiente");
        entrenadorAlumnoRepository.save(relacion);

        // 4. ✅ Inicializar carrera deportiva del alumno en este deporte
        try {
            carreraDeportivaService.inicializarCarrera(
                    solicitud.getUsuarioAlumno(),
                    solicitud.getIdDeporte()
            );
        } catch (Exception e) {
            // No bloqueamos la aceptación si falla la carrera
            log.warn("No se pudo inicializar carrera para {} en deporte {}: {}",
                    solicitud.getUsuarioAlumno(), solicitud.getIdDeporte(), e.getMessage());
        }

        log.info("✅ Solicitud aceptada: {} entrena {} con {}",
                solicitud.getUsuarioAlumno(),
                solicitud.getIdDeporte(),
                solicitud.getUsuarioEntrenador());
    }

    private void rechazarSolicitud(SolicitudEntrenamiento solicitud) {
        solicitud.setStatusSolicitud(SolicitudEntrenamiento.StatusSolicitud.Rechazada);
        solicitudRepository.save(solicitud);
    }

    private SolicitudEntrenadorDTO mapToDTO(Map<String, Object> map) {
        SolicitudEntrenadorDTO dto = new SolicitudEntrenadorDTO();
        dto.setIdSolicitud(convertToInteger(map.get("idSolicitud")));
        dto.setUsuarioAlumno((String) map.get("usuarioAlumno"));
        dto.setNombreAlumno((String) map.get("nombreAlumno"));
        dto.setFotoAlumno((String) map.get("fotoAlumno"));
        dto.setEdad(convertToInteger(map.get("edad")));
        dto.setNombreDeporte((String) map.get("nombreDeporte"));
        dto.setIdDeporte(convertToInteger(map.get("idDeporte")));
        dto.setMotivoSolicitud((String) map.get("motivoSolicitud"));
        Date fechaSqlDate = (Date) map.get("fechaSolicitud");
        LocalDate fechaSolicitud = fechaSqlDate.toLocalDate();
        dto.setFechaSolicitud(fechaSolicitud);
        dto.setTiempoTranscurrido(calcularTiempoTranscurrido(fechaSolicitud));
        return dto;
    }

    private Integer convertToInteger(Object value) {
        if (value == null) return null;
        if (value instanceof Integer) return (Integer) value;
        if (value instanceof BigInteger) return ((BigInteger) value).intValue();
        if (value instanceof Long) return ((Long) value).intValue();
        return Integer.parseInt(value.toString());
    }

    private String calcularTiempoTranscurrido(LocalDate fechaSolicitud) {
        long dias = ChronoUnit.DAYS.between(fechaSolicitud, LocalDate.now());
        if (dias == 0) return "Hoy";
        if (dias == 1) return "Hace 1 día";
        if (dias < 7)  return "Hace " + dias + " días";
        if (dias < 30) { long s = dias / 7;  return "Hace " + s + (s == 1 ? " semana" : " semanas"); }
        long m = dias / 30; return "Hace " + m + (m == 1 ? " mes" : " meses");
    }
}