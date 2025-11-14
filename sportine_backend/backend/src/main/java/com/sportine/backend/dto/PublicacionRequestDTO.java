package com.sportine.backend.dto;

import lombok.Data;

/**
 * DTO (Paquete) para RECIBIR una nueva publicación desde el Frontend (Android).
 */
@Data
public class PublicacionRequestDTO {

    // El username del usuario que está creando el post
    private String usuario;

    private String descripcion;
    private String imagen; // La URL de la imagen
}