package com.sportine.backend.dto;

import lombok.Data;
import java.util.Date;

@Data
public class ComentarioResponseDTO {
    private Integer idComentario;
    private String texto;
    private Date fecha;


    private String autorUsername;
    private String autorNombre;
    private String autorFoto;

    private boolean isMine;
}