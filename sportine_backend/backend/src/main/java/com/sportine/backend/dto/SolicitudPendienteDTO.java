package com.sportine.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SolicitudPendienteDTO {
    private Boolean tieneSolicitudPendiente;
    private List<SolicitudDetalleDTO> solicitudes;
}