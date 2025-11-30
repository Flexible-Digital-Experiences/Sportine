package com.sportine.backend.service;

import com.sportine.backend.dto.*;

import java.util.List;

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

    /**
     * Verifica si existe una solicitud pendiente con el entrenador
     */
    SolicitudPendienteDTO verificarSolicitudPendiente(String usuarioEntrenador, String usuarioAlumno);

    /**
     * Obtiene todas las solicitudes enviadas por el alumno
     */
    List<SolicitudEnviadaDTO> obtenerSolicitudesEnviadas(String usuarioAlumno);

    /**
     * Elimina una solicitud enviada por el alumno
     */
    void eliminarSolicitud(Integer idSolicitud, String usuarioAlumno);
} // ← Ahora SÍ termina la interfaz aquí