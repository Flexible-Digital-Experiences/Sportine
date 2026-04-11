package com.sportine.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeporteAlumnoDTO {

    @JsonProperty("id_deporte")
    private Integer idDeporte;

    @JsonProperty("nombre_deporte")
    private String nombreDeporte;

    @JsonProperty("emoji")
    private String emoji;
}