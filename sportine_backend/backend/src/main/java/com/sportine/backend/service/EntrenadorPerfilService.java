package com.sportine.backend.service;

import com.sportine.backend.dto.ActualizarPerfilEntrenadorDTO;
import com.sportine.backend.dto.PerfilEntrenadorResponseDTO;

public interface EntrenadorPerfilService {

    PerfilEntrenadorResponseDTO obtenerPerfilEntrenador(String usuario);

    // ✅ NUEVO MÉTODO
    PerfilEntrenadorResponseDTO actualizarPerfilEntrenador(
            String usuario,
            ActualizarPerfilEntrenadorDTO datos
    );
}