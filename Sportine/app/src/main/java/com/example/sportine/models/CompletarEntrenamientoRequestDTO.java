package com.example.sportine.models;

public class CompletarEntrenamientoRequestDTO {
    private Integer idEntrenamiento;
    private String comentarios;
    private Integer nivelCansancio;      // 1 al 10
    private Integer dificultadPercibida; // 1 al 10
    private String estadoAnimo;          // "Motivado", "Cansado", etc.

    public CompletarEntrenamientoRequestDTO(Integer idEntrenamiento, String comentarios,
                                            Integer nivelCansancio, Integer dificultadPercibida,
                                            String estadoAnimo) {
        this.idEntrenamiento = idEntrenamiento;
        this.comentarios = comentarios;
        this.nivelCansancio = nivelCansancio;
        this.dificultadPercibida = dificultadPercibida;
        this.estadoAnimo = estadoAnimo;
    }
}