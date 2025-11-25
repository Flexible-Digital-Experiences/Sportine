package com.sportine.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EntrenadorCardDTO {
    private String usuario;
    private String nombreCompleto;
    private String fotoPerfil;
    private Double ratingPromedio;
    private List<String> especialidades;
    private Integer limiteAlumnos;
    private Integer alumnosActuales;

    // Constructor anterior para compatibilidad
    public EntrenadorCardDTO(String usuario, String nombreCompleto, String fotoPerfil,
                             Double ratingPromedio, List<String> especialidades) {
        this.usuario = usuario;
        this.nombreCompleto = nombreCompleto;
        this.fotoPerfil = fotoPerfil;
        this.ratingPromedio = ratingPromedio;
        this.especialidades = especialidades;
        this.limiteAlumnos = 0;
        this.alumnosActuales = 0;
    }

    // Métodos útiles
    public Integer getEspaciosDisponibles() {
        if (limiteAlumnos == null || alumnosActuales == null) {
            return 0;
        }
        return limiteAlumnos - alumnosActuales;
    }

    public Integer getPorcentajeOcupacion() {
        if (limiteAlumnos == null || limiteAlumnos == 0 || alumnosActuales == null) {
            return 0;
        }
        return (int) ((alumnosActuales * 100.0) / limiteAlumnos);
    }
}