package com.sportine.backend.service;

import com.sportine.backend.dto.EntrenadorCardDTO;
import com.sportine.backend.dto.UsuarioDetalleDTO;

import java.util.List;

public interface BuscarEntrenadorService {
    List<EntrenadorCardDTO> buscarEntrenadores(String searchQuery, String usuarioAlumno);
}
