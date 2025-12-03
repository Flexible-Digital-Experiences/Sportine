package com.example.sportine.models;

import java.io.Serializable;

public class FeedbackResumenDTO implements Serializable {
    private Integer idFeedback;
    private String nombreAlumno;
    private String fotoAlumno;
    private String tituloEntrenamiento;
    private Integer nivelCansancio;
    private Integer dificultad;
    private String estadoAnimo;
    private String comentarios;
    private String fecha; // Recibimos la fecha como String para evitar errores

    // Getters
    public Integer getIdFeedback() { return idFeedback; }
    public String getNombreAlumno() { return nombreAlumno; }
    public String getFotoAlumno() { return fotoAlumno; }
    public String getTituloEntrenamiento() { return tituloEntrenamiento; }
    public Integer getNivelCansancio() { return nivelCansancio; }
    public Integer getDificultad() { return dificultad; }
    public String getEstadoAnimo() { return estadoAnimo; }
    public String getComentarios() { return comentarios; }
    public String getFecha() { return fecha; }
}