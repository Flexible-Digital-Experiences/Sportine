package com.sportine.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlumnoEntrenadorDTO {
    private String usuarioAlumno;
    private String nombreCompleto;
    private String fotoPerfil;
    private Integer edad;
    private String deportes; // Lista de deportes separados por comas
    private String fechaInicio;
    private String statusRelacion; // "activo", "pendiente", "vencido"
}