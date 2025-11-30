package com.sportine.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InfoDeporteAlumnoDTO {
    private Integer idDeporte;
    private String nombreDeporte;
    private boolean tieneNivelRegistrado; // Si el alumno ya tiene nivel en este deporte
    private String nivelActual; // El nivel actual (si existe)
}