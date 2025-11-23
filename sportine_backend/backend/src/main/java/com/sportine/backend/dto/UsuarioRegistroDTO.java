package com.sportine.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioRegistroDTO {

    private String nombre;
    private String apellidos;
    private String sexo;
    private String usuario;
    private String contrasena;
    private String rol;
    private Integer idEstado;
    private String ciudad;
}
