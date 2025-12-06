package com.sportine.backend.service;

import com.sportine.backend.dto.AlumnoDetalleEntrenadorDTO;

public interface AlumnoDetalleService {
    AlumnoDetalleEntrenadorDTO obtenerDetalleAlumno(String usuarioEntrenador, String usuarioAlumno);
    void actualizarNivelAlumno(String usuarioEntrenador, String usuarioAlumno, Integer idDeporte, Integer nuevoNivel);
    void actualizarEstadoRelacion(String usuarioEntrenador, String usuarioAlumno, Integer idDeporte, String nuevoEstado);
}