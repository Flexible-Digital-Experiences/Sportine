package com.sportine.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EntrenamientoDelDiaDTO {

    private Integer idEntrenamiento;
    private String titulo;
    private String dificultad;
    private String estado;
    private String objetivo;
}
