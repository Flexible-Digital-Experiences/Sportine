package com.sportine.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class CambiarPasswordDTO {
    private String passwordActual;
    private String passwordNueva;
    private String passwordNuevaConfirmar;
}
