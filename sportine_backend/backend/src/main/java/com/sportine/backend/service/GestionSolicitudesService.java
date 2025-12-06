package com.sportine.backend.service;

import com.sportine.backend.dto.RespuestaSolicitudRequestDTO;
import com.sportine.backend.dto.SolicitudEntrenadorDTO;

import java.util.List;

public interface GestionSolicitudesService {
    List<SolicitudEntrenadorDTO> obtenerSolicitudesEnRevision(String usuarioEntrenador);
    List<SolicitudEntrenadorDTO> obtenerSolicitudesAceptadas(String usuarioEntrenador);
    void responderSolicitud(RespuestaSolicitudRequestDTO request, String usuarioEntrenador);
}