package com.sportine.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDTO {

    private boolean success;
    private String mensaje;

    // Datos del usuario (solo si login exitoso)
    private String token;

    private String usuario;
    private String nombre;
    private String apellidos;
    private String rol;  // "alumno" o "entrenador"
    private String sexo;
    private String estado;
    private String ciudad;
}
