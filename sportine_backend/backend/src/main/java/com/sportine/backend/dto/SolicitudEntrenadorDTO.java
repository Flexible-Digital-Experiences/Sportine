package com.sportine.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SolicitudEntrenadorDTO {
    private Integer idSolicitud;
    private String usuarioAlumno;
    private String nombreAlumno;
    private String fotoAlumno;
    private Integer edad;
    private String nombreDeporte;
    private Integer idDeporte;
    private String motivoSolicitud;
    private LocalDate fechaSolicitud;
    private String tiempoTranscurrido; // "Hace 2 días", "Hace 3 horas"
}