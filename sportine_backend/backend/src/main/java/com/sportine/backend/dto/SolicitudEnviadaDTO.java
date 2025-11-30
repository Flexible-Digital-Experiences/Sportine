package com.sportine.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SolicitudEnviadaDTO {
    private Integer idSolicitud;
    private String usuarioEntrenador;
    private String nombreEntrenador;
    private String fotoEntrenador;
    private String nombreDeporte;
    private String statusSolicitud; // "En_revisión", "Aprobada", "Rechazada"
    private String fechaSolicitud;
    private String motivo;
}