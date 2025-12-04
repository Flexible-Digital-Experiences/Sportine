package com.example.sportine.models;

import com.google.gson.annotations.SerializedName;
import java.util.Date;

public class PublicacionFeedDTO {

    @SerializedName("idPublicacion")
    private Integer idPublicacion;

    @SerializedName("descripcion")
    private String descripcion;

    @SerializedName("imagen")
    private String imagen;

    @SerializedName("fechaPublicacion")
    private Date fechaPublicacion;

    @SerializedName("autorUsername")
    private String autorUsername;

    @SerializedName("autorNombreCompleto")
    private String autorNombreCompleto;

    @SerializedName("autorFotoPerfil")
    private String autorFotoPerfil;

    @SerializedName("totalLikes")
    private Integer totalLikes;

    @SerializedName("likedByMe")
    private boolean isLikedByMe;

    @SerializedName("mine")
    private boolean isMine;

    // --- ¡ESTE ES EL QUE FALTABA! ---
    @SerializedName("tipo")
    private Integer tipo; // 1 = Normal, 2 = Logro

    // --- GETTERS Y SETTERS ---

    public Integer getIdPublicacion() { return idPublicacion; }
    public void setIdPublicacion(Integer idPublicacion) { this.idPublicacion = idPublicacion; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getImagen() { return imagen; }
    public void setImagen(String imagen) { this.imagen = imagen; }

    public Date getFechaPublicacion() { return fechaPublicacion; }
    public void setFechaPublicacion(Date fechaPublicacion) { this.fechaPublicacion = fechaPublicacion; }

    public String getAutorUsername() { return autorUsername; }
    public void setAutorUsername(String autorUsername) { this.autorUsername = autorUsername; }

    public String getAutorNombreCompleto() { return autorNombreCompleto; }
    public void setAutorNombreCompleto(String autorNombreCompleto) { this.autorNombreCompleto = autorNombreCompleto; }

    public String getAutorFotoPerfil() { return autorFotoPerfil; }
    public void setAutorFotoPerfil(String autorFotoPerfil) { this.autorFotoPerfil = autorFotoPerfil; }

    public Integer getTotalLikes() { return totalLikes; }
    public void setTotalLikes(Integer totalLikes) { this.totalLikes = totalLikes; }

    public boolean isLikedByMe() { return isLikedByMe; }
    public void setLikedByMe(boolean likedByMe) { isLikedByMe = likedByMe; }

    public boolean isMine() { return isMine; }
    public void setMine(boolean mine) { isMine = mine; }

    // Getters y Setters para TIPO
    public Integer getTipo() { return tipo; }
    public void setTipo(Integer tipo) { this.tipo = tipo; }
}