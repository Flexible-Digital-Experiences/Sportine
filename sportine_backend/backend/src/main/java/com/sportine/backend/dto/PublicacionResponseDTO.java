package com.sportine.backend.dto;

import lombok.Data;
import java.util.Date;


@Data
public class PublicacionResponseDTO {


    private Integer idPublicacion;
    private String descripcion;
    private Date fechaPublicacion;
    private String imagenUrl;


    private String username;
    private String nombreCompleto;
    private String avatarUrl;


    private int totalLikes;
    private boolean usuarioDioLike; // Para saber si el corazón debe estar rojo
}