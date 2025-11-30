package com.sportine.backend.service.impl;

import com.sportine.backend.dto.DeporteDisponibleDTO;
import com.sportine.backend.dto.FormularioSolicitudDTO;
import com.sportine.backend.dto.InfoDeporteAlumnoDTO;
import com.sportine.backend.repository.SolicitudEntrenadorRepository;
import com.sportine.backend.repository.UsuarioRepository;
import com.sportine.backend.service.SolicitudEntrenadorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class SolicitudEntrenadorServiceImpl implements SolicitudEntrenadorService {

    private final SolicitudEntrenadorRepository solicitudRepository;
    private final UsuarioRepository usuarioRepository;

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
                // MySQL devuelve 1 o 0 como Long
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

}