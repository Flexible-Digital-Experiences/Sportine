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
}
