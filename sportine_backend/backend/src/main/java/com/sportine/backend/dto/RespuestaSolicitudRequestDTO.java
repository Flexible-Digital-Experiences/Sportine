package com.sportine.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RespuestaSolicitudRequestDTO {
    private Integer idSolicitud;
    private String accion; // "aceptar" o "rechazar"
}