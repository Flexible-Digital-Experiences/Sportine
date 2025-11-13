package com.sportine.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioResponseDTO {
    private String usuario;
    private String nombre;
    private String apellidos;
    private String rol;
    private String mensaje;
}