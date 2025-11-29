package com.sportine.backend.service.impl;

import com.sportine.backend.dto.*;
import com.sportine.backend.model.SolicitudEntrenamiento;
import com.sportine.backend.repository.*;
import com.sportine.backend.service.EnviarSolicitudEntrenadorService;
import com.sportine.backend.service.SolicitudEntrenadorService;
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
public class EnviarSolicitudEntrenadorServiceImpl implements EnviarSolicitudEntrenadorService {

    private final SolicitudEntrenadorRepository solicitudRepository;
    private final UsuarioRepository usuarioRepository;
    private final SolicitudEntrenamientoRepository solicitudEntrenamientoRepository;
    private final AlumnoDeporteRepository alumnoDeporteRepository;

    @Override
    public FormularioSolicitudDTO obtenerFormularioSolicitud(String usuarioEntrenador, String usuarioAlumno) {
        log.info("Obteniendo formulario de solicitud para alumno {} hacia entrenador {}",
                usuarioAlumno, usuarioEntrenador);

        // Verificar que el entrenador existe
        var entrenador = usuarioRepository.findById(usuarioEntrenador)
                .orElseThrow(() -> new RuntimeException("Entrenador no encontrado: " + usuarioEntrenador));

        String nombreEntrenador = entrenador.getNombre() + " " + entrenador.getApellidos();

        // Obtener deportes disponibles
        List<Map<String, Object>> deportesData = solicitudRepository.obtenerDeportesDisponibles(
                usuarioEntrenador,
                usuarioAlumno
        );

        List<DeporteDisponibleDTO> deportesDisponibles = new ArrayList<>();

        for (Map<String, Object> data : deportesData) {
            Integer idDeporte = ((Number) data.get("idDeporte")).intValue();
            String nombreDeporte = (String) data.get("nombreDeporte");

            DeporteDisponibleDTO deporte = new DeporteDisponibleDTO(
                    idDeporte,
                    nombreDeporte
            );

            deportesDisponibles.add(deporte);
        }

        FormularioSolicitudDTO formulario = new FormularioSolicitudDTO(
                usuarioEntrenador,
                nombreEntrenador,
                deportesDisponibles
        );

        log.info("Formulario generado: {} deportes disponibles", deportesDisponibles.size());

        return formulario;
    }

    @Override
    public InfoDeporteAlumnoDTO obtenerInfoDeporte(Integer idDeporte, String usuarioAlumno) {
        log.info("Obteniendo info del deporte {} para alumno {}", idDeporte, usuarioAlumno);

        Map<String, Object> infoData = solicitudRepository.obtenerInfoDeporteAlumno(idDeporte, usuarioAlumno)
                .orElseThrow(() -> new RuntimeException("Deporte no encontrado: " + idDeporte));

        String nombreDeporte = (String) infoData.get("nombreDeporte");

        Boolean tieneNivel = false;
        Object tieneNivelObj = infoData.get("tieneNivelRegistrado");

        if (tieneNivelObj != null) {
            if (tieneNivelObj instanceof Boolean) {
                tieneNivel = (Boolean) tieneNivelObj;
            } else if (tieneNivelObj instanceof Number) {
                tieneNivel = ((Number) tieneNivelObj).intValue() == 1;
            }
        }

        String nivelActual = (String) infoData.get("nivelActual");

        InfoDeporteAlumnoDTO info = new InfoDeporteAlumnoDTO(
                idDeporte,
                nombreDeporte,
                tieneNivel,
                nivelActual
        );

        log.info("Info deporte: {} - Tiene nivel: {} - Nivel actual: {}",
                nombreDeporte, tieneNivel, nivelActual);

        return info;
    }

    @Override
    @Transactional
    public SolicitudResponseDTO enviarSolicitud(SolicitudRequestDTO request, String usuarioAlumno) {
        log.info("Procesando solicitud de {} para entrenador {} en deporte {}",
                usuarioAlumno, request.getUsuarioEntrenador(), request.getIdDeporte());

        // 1. Verificar que no exista una solicitud pendiente o aprobada
        boolean existeSolicitud = solicitudEntrenamientoRepository
                .existeSolicitudActivaOPendiente(
                        usuarioAlumno,
                        request.getUsuarioEntrenador(),
                        request.getIdDeporte()
                );

        if (existeSolicitud) {
            log.warn("Ya existe una solicitud activa entre {} y {} para deporte {}",
                    usuarioAlumno, request.getUsuarioEntrenador(), request.getIdDeporte());
            throw new RuntimeException("Ya tienes una solicitud pendiente o aprobada con este entrenador para este deporte");
        }

        // 2. Si el alumno NO tiene nivel registrado en este deporte, crearlo
        var alumnoDeporteOpt = alumnoDeporteRepository
                .findByUsuarioAndIdDeporte(usuarioAlumno, request.getIdDeporte());

        if (alumnoDeporteOpt.isEmpty() && request.getNivel() != null) {
            log.info("Registrando nivel {} para alumno {} en deporte {}",
                    request.getNivel(), usuarioAlumno, request.getIdDeporte());

            // Insertar directamente con query nativo
            alumnoDeporteRepository.insertarAlumnoDeporte(
                    usuarioAlumno,
                    request.getIdDeporte(),
                    request.getNivel(),
                    LocalDate.now()
            );
        }

        // 3. Crear la solicitud
        SolicitudEntrenamiento solicitud = new SolicitudEntrenamiento();
        solicitud.setUsuarioAlumno(usuarioAlumno);
        solicitud.setUsuarioEntrenador(request.getUsuarioEntrenador());
        solicitud.setIdDeporte(request.getIdDeporte());
        solicitud.setDescripcionSolicitud(request.getMotivo());
        solicitud.setFechaSolicitud(LocalDate.now());
        solicitud.setStatusSolicitud(SolicitudEntrenamiento.StatusSolicitud.En_revisión);

        SolicitudEntrenamiento solicitudGuardada = solicitudEntrenamientoRepository.save(solicitud);

        log.info("✅ Solicitud {} creada exitosamente", solicitudGuardada.getIdSolicitud());

        return new SolicitudResponseDTO(
                solicitudGuardada.getIdSolicitud(),
                "Solicitud enviada exitosamente",
                "En_revisión",
                solicitudGuardada.getFechaSolicitud().toString()
        );
    }
}