package com.sportine.backend.service;

import com.sportine.backend.dto.EntrenadorCardDTO;

import java.util.List;

public interface BuscarEntrenadorService {

    List<EntrenadorCardDTO> buscarEntrenadores(String searchQuery);
}
