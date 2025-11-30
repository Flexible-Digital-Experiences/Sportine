package com.sportine.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SolicitudDetalleDTO {
    private Integer idSolicitud;
    private String nombreDeporte;
    private String fechaSolicitud;
    private String motivo;
}