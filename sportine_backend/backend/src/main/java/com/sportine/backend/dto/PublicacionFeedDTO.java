package com.sportine.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Data
@NoArgsConstructor
public class PublicacionFeedDTO {

    // --- Datos del Post ---
    private Integer idPublicacion;
    private String descripcion;
    private String imagen;
    private Date fechaPublicacion;

    // --- Datos del Autor ---
    private String autorUsername;
    private String autorNombreCompleto;
    private String autorFotoPerfil;

    // --- ¡NUEVOS CAMPOS DE LIKES! ---
    private int totalLikes;
    private boolean isLikedByMe; // true si el usuario del token ya le dio like

    private boolean isMine;
    private Integer tipo;

}