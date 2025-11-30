package com.sportine.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstadoRelacionDTO {
    private Boolean tieneRelacion;
    private String estadoRelacion;
    private Integer idDeporte;
    private String nombreDeporte;
    private Boolean yaCalificado;
}
