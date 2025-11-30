package com.sportine.backend.service;

import com.sportine.backend.dto.FormularioSolicitudDTO;
import com.sportine.backend.dto.InfoDeporteAlumnoDTO;
import com.sportine.backend.dto.SolicitudRequestDTO;
import com.sportine.backend.dto.SolicitudResponseDTO;

public interface EnviarSolicitudEntrenadorService {

    /**
     * Obtiene el formulario inicial con los deportes disponibles del entrenador
     */
    FormularioSolicitudDTO obtenerFormularioSolicitud(String usuarioEntrenador, String usuarioAlumno);

    /**
     * Obtiene información específica de un deporte para el alumno
     */
    InfoDeporteAlumnoDTO obtenerInfoDeporte(Integer idDeporte, String usuarioAlumno);

    /**
     * Procesa y guarda una nueva solicitud de entrenamiento
     */
    SolicitudResponseDTO enviarSolicitud(SolicitudRequestDTO request, String usuarioAlumno);
}
