package com.sportine.backend.service;

import com.sportine.backend.dto.PerfilEntrenadorDTO;

public interface DetalleEntrenadorService {

    PerfilEntrenadorDTO obtenerPerfilEntrenador(String usuario);
}
