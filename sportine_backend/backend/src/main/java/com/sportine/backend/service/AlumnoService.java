package com.sportine.backend.service;

import com.sportine.backend.dto.HomeAlumnoDTO;

public interface AlumnoService {

    HomeAlumnoDTO obtenerHomeAlumno(String usuario);
}