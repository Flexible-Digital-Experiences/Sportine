package com.sportine.backend.service;

import com.sportine.backend.dto.ActualizarPerfilEntrenadorDTO;
import com.sportine.backend.dto.PerfilEntrenadorResponseDTO;
import org.springframework.web.multipart.MultipartFile;

public interface EntrenadorPerfilService {

    PerfilEntrenadorResponseDTO obtenerPerfilEntrenador(String usuario);

    PerfilEntrenadorResponseDTO actualizarPerfilEntrenador(
            String usuario,
            ActualizarPerfilEntrenadorDTO datos
    );

    // ✅ NUEVO MÉTODO
    PerfilEntrenadorResponseDTO actualizarFotoPerfil(String usuario, MultipartFile file);
}