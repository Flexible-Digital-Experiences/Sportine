package com.sportine.backend.dto;

import lombok.Data;

@Data
public class PublicacionRequestDTO {

    // El 'usuario' se quita. Lo obtendremos del Token JWT.

    private String descripcion;

    // TODO: Esto eventualmente será un sistema de subida de archivos,
    // pero por ahora aceptamos una URL de imagen (puede ser null).
    private String imagen;
}