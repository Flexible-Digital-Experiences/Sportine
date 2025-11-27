package com.example.sportine.models;

public class ResenaDTO {
    private String nombreAlumno;
    private String fotoAlumno;
    private Integer ratingDado;
    private String comentario;

    public ResenaDTO() {}

    public ResenaDTO(String nombreAlumno, String fotoAlumno, Integer ratingDado, String comentario) {
        this.nombreAlumno = nombreAlumno;
        this.fotoAlumno = fotoAlumno;
        this.ratingDado = ratingDado;
        this.comentario = comentario;
    }

    public String getNombreAlumno() { return nombreAlumno; }
    public void setNombreAlumno(String nombreAlumno) { this.nombreAlumno = nombreAlumno; }

    public String getFotoAlumno() { return fotoAlumno; }
    public void setFotoAlumno(String fotoAlumno) { this.fotoAlumno = fotoAlumno; }

    public Integer getRatingDado() { return ratingDado; }
    public void setRatingDado(Integer ratingDado) { this.ratingDado = ratingDado; }

    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }
}
