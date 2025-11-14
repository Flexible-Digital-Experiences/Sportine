package com.sportine.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActualizarUsuarioDTO {
    private String nombre;
    private String apellidos;
    private String sexo;
    private String estado;
    private String ciudad;
}
