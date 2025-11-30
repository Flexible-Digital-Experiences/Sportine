package com.sportine.backend.service;

import com.sportine.backend.dto.FormularioSolicitudDTO;
import com.sportine.backend.dto.InfoDeporteAlumnoDTO;

public interface SolicitudEntrenadorService {
    FormularioSolicitudDTO obtenerFormularioSolicitud(String usuarioEntrenador, String usuarioAlumno);
    InfoDeporteAlumnoDTO obtenerInfoDeporte(Integer idDeporte, String usuarioAlumno);
}