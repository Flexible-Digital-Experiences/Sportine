package com.sportine.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PerfilEntrenadorDTO {
    private String usuario;
    private String fotoPerfil;
    private String nombreCompleto;
    private String ubicacion;
    private String acercaDeMi;
    private Integer costoMensual;
    private Integer limiteAlumnos;
    private Integer alumnosActuales;
    private String correo;
    private CalificacionDTO calificacion;
    private List<String> especialidades;
    private List<ResenaDTO> resenas;
    private EstadoRelacionDTO estadoRelacion;
}