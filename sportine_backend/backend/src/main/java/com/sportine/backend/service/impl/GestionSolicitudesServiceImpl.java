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
import lombok.extern.slf4j.Slf4j; // ✅ CAMBIAR ESTE IMPORT
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
@Slf4j  // ✅ AGREGAR ESTA ANOTACIÓN
@RequiredArgsConstructor
public class GestionSolicitudesServiceImpl implements GestionSolicitudesService {

    private final SolicitudEntrenamientoRepository solicitudRepository;
    private final EntrenadorAlumnoRepository entrenadorAlumnoRepository;
    private final AlumnoDeporteRepository alumnoDeporteRepository;

    @Override
    public List<SolicitudEntrenadorDTO> obtenerSolicitudesEnRevision(String usuarioEntrenador) {
        List<Map<String, Object>> resultados = solicitudRepository
                .obtenerSolicitudesPorEstado(usuarioEntrenador, "En_revisión");

        return resultados.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<SolicitudEntrenadorDTO> obtenerSolicitudesAceptadas(String usuarioEntrenador) {
        List<Map<String, Object>> resultados = solicitudRepository
                .obtenerSolicitudesPorEstado(usuarioEntrenador, "Aprobada");

        return resultados.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void responderSolicitud(RespuestaSolicitudRequestDTO request, String usuarioEntrenador) {
        log.info("=== RESPONDER SOLICITUD ===");
        log.info("ID Solicitud: {}", request.getIdSolicitud());
        log.info("Usuario entrenador recibido: '{}'", usuarioEntrenador);
        log.info("Acción: {}", request.getAccion());

        // Buscar la solicitud
        SolicitudEntrenamiento solicitud = solicitudRepository.findById(request.getIdSolicitud())
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada con ID: " + request.getIdSolicitud()));

        log.info("Solicitud encontrada:");
        log.info("  - ID: {}", solicitud.getIdSolicitud());
        log.info("  - Entrenador en BD: '{}'", solicitud.getUsuarioEntrenador());
        log.info("  - Alumno: '{}'", solicitud.getUsuarioAlumno());
        log.info("  - Estado: {}", solicitud.getStatusSolicitud());

        // Comparación detallada
        String entrenadorBD = solicitud.getUsuarioEntrenador();
        String entrenadorRecibido = usuarioEntrenador;

        log.info("Comparación de usuarios:");
        log.info("  - BD length: {}, Recibido length: {}", entrenadorBD.length(), entrenadorRecibido.length());
        log.info("  - BD bytes: {}", Arrays.toString(entrenadorBD.getBytes()));
        log.info("  - Recibido bytes: {}", Arrays.toString(entrenadorRecibido.getBytes()));
        log.info("  - Son iguales (==): {}", entrenadorBD == entrenadorRecibido);
        log.info("  - Son iguales (equals): {}", entrenadorBD.equals(entrenadorRecibido));
        log.info("  - Son iguales (equalsIgnoreCase): {}", entrenadorBD.equalsIgnoreCase(entrenadorRecibido));

        // ✅ Validar que pertenece al entrenador
        if (!solicitud.getUsuarioEntrenador().trim().equalsIgnoreCase(usuarioEntrenador.trim())) {
            log.error("❌ VALIDACIÓN FALLIDA");
            log.error("  - Entrenador en solicitud: '{}'", solicitud.getUsuarioEntrenador());
            log.error("  - Usuario recibido: '{}'", usuarioEntrenador);
            throw new RuntimeException("No tienes permiso para responder esta solicitud");
        }

        log.info("✅ Validación de entrenador exitosa");

        // Validar estado usando el enum
        if (solicitud.getStatusSolicitud() != SolicitudEntrenamiento.StatusSolicitud.En_revisión) {
            throw new RuntimeException("La solicitud no está en estado 'En revisión'. Estado actual: " +
                    solicitud.getStatusSolicitud());
        }

        // Procesar según la acción
        if ("aceptar".equalsIgnoreCase(request.getAccion())) {
            log.info("Aceptando solicitud...");
            aceptarSolicitud(solicitud);
        } else if ("rechazar".equalsIgnoreCase(request.getAccion())) {
            log.info("Rechazando solicitud...");
            rechazarSolicitud(solicitud);
        } else {
            throw new RuntimeException("Acción inválida: " + request.getAccion());
        }

        log.info("✓ Solicitud procesada exitosamente");
    }

    private void aceptarSolicitud(SolicitudEntrenamiento solicitud) {
        // 1. Cambiar estado de solicitud a "Aprobada"
        solicitud.setStatusSolicitud(SolicitudEntrenamiento.StatusSolicitud.Aprobada);
        solicitudRepository.save(solicitud);

        // 2. Verificar si el alumno ya tiene el deporte registrado en Alumno_Deporte
        boolean tieneDeporte = alumnoDeporteRepository
                .findByUsuarioAndIdDeporte(solicitud.getUsuarioAlumno(), solicitud.getIdDeporte())
                .isPresent();

        if (!tieneDeporte) {
            // Si no tiene el deporte, insertarlo con nivel "Principiante"
            alumnoDeporteRepository.insertarAlumnoDeporte(
                    solicitud.getUsuarioAlumno(),
                    solicitud.getIdDeporte(),
                    "Principiante",
                    LocalDate.now()
            );
        }

        // 3. Crear relación en Entrenador_Alumno con estado "pendiente"
        EntrenadorAlumno relacion = new EntrenadorAlumno();
        relacion.setUsuarioEntrenador(solicitud.getUsuarioEntrenador());
        relacion.setUsuarioAlumno(solicitud.getUsuarioAlumno());
        relacion.setIdDeporte(solicitud.getIdDeporte());
        relacion.setFechaInicio(LocalDate.now());
        relacion.setFinMensualidad(null);
        relacion.setStatusRelacion("pendiente"); // El alumno debe pagar

        entrenadorAlumnoRepository.save(relacion);
    }

    private void rechazarSolicitud(SolicitudEntrenamiento solicitud) {
        solicitud.setStatusSolicitud(SolicitudEntrenamiento.StatusSolicitud.Rechazada);
        solicitudRepository.save(solicitud);
    }

    /**
     * Mapea un resultado de query nativa a DTO
     */
    private SolicitudEntrenadorDTO mapToDTO(Map<String, Object> map) {
        SolicitudEntrenadorDTO dto = new SolicitudEntrenadorDTO();

        // Usar método helper para conversión segura
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

    /**
     * Método helper para convertir Object a Integer de forma segura
     * Maneja tanto Integer como BigInteger
     */
    private Integer convertToInteger(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Integer) {
            return (Integer) value;
        }
        if (value instanceof BigInteger) {
            return ((BigInteger) value).intValue();
        }
        if (value instanceof Long) {
            return ((Long) value).intValue();
        }
        // Si es otro tipo, intentar convertir
        return Integer.parseInt(value.toString());
    }

    /**
     * Calcula el tiempo transcurrido desde la fecha de solicitud
     */
    private String calcularTiempoTranscurrido(LocalDate fechaSolicitud) {
        LocalDate ahora = LocalDate.now();
        long dias = ChronoUnit.DAYS.between(fechaSolicitud, ahora);

        if (dias == 0) {
            return "Hoy";
        } else if (dias == 1) {
            return "Hace 1 día";
        } else if (dias < 7) {
            return "Hace " + dias + " días";
        } else if (dias < 30) {
            long semanas = dias / 7;
            return "Hace " + semanas + (semanas == 1 ? " semana" : " semanas");
        } else {
            long meses = dias / 30;
            return "Hace " + meses + (meses == 1 ? " mes" : " meses");
        }
    }
}