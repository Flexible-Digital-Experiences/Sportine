package com.sportine.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FormularioSolicitudDTO {
    private String usuarioEntrenador;
    private String nombreEntrenador;
    private List<DeporteDisponibleDTO> deportesDisponibles; // Solo lista de deportes
}