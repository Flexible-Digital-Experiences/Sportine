
package com.sportine.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ActualizarUsuarioDTO {

    @JsonProperty("nombre")
    private String nombre;

    @JsonProperty("apellidos")
    private String apellidos;

    @JsonProperty("sexo")
    private String sexo;

    @JsonProperty("estado")
    private String estado; // Nombre del estado, no ID

    @JsonProperty("ciudad")
    private String ciudad;

    @JsonProperty("password")
    private String password;

    @JsonProperty("correo")
    private String correo;
}