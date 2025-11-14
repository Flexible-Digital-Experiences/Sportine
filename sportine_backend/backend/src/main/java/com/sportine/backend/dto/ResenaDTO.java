package com.sportine.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResenaDTO {
    private String nombreAlumno;
    private String fotoAlumno;
    private Integer ratingDado;
    private String comentario;
}
