package com.sportine.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActualizarPerfilEntrenadorDTO {

    private Integer costoMensualidad;    // Puede ser null si no se actualiza
    private String descripcionPerfil;    // Puede ser null si no se actualiza
    private Integer limiteAlumnos;       // Solo si es premium

    // Lista de IDs de deportes (ej: [1, 2, 7])
    private java.util.List<Integer> deportes;  // Puede ser null si no se actualiza
}