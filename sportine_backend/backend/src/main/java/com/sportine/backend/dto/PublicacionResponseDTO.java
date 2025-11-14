package com.sportine.backend.dto;

import lombok.Data;
import java.util.Date;

/**
 * DTO (Paquete Limpio) para ENVIAR una publicación al Frontend (Android).
 * Contiene datos "enriquecidos" que la app necesita para mostrar el feed.
 */
@Data
public class PublicacionResponseDTO {

    // Datos del Post
    private Integer idPublicacion;
    private String descripcion;
    private Date fechaPublicacion;
    private String imagenUrl;

    // Datos "Enriquecidos" del Usuario que posteó
    private String username;
    private String nombreCompleto;
    private String avatarUrl;       // ej: "http://.../fotoperfil.png"

    // Datos "Calculados"
    private int totalLikes;
    private boolean usuarioDioLike; // Para saber si el corazón debe estar rojo
}