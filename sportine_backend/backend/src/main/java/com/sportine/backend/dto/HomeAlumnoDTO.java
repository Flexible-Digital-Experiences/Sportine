package com.sportine.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HomeAlumnoDTO {

    private String saludo;
    private String mensajeDinamico;
    private List<EntrenamientoDelDiaDTO> entrenamientosDelDia;
}