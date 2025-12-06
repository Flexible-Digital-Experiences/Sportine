package com.sportine.backend.service;

import com.sportine.backend.dto.AlumnoEntrenadorDTO;
import java.util.List;

public interface MisAlumnosService {
    List<AlumnoEntrenadorDTO> obtenerMisAlumnos(String usuarioEntrenador);
    List<AlumnoEntrenadorDTO> obtenerAlumnosPendientes(String usuarioEntrenador);
}