package com.sportine.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CalificacionRequestDTO {
    private String usuarioEntrenador;
    private Integer calificacion;
    private String comentario;
}