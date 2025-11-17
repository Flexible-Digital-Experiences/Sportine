package com.example.sportine.models;

import com.google.gson.annotations.SerializedName;
import java.util.Date;

public class PublicacionFeedDTO {

    // (Campos del post)
    @SerializedName("idPublicacion") private Integer idPublicacion;
    @SerializedName("descripcion") private String descripcion;
    @SerializedName("imagen") private String imagen;
    @SerializedName("fechaPublicacion") private Date fechaPublicacion;

    // (Campos del autor)
    @SerializedName("autorUsername") private String autorUsername;
    @SerializedName("autorNombreCompleto") private String autorNombreCompleto;
    @SerializedName("autorFotoPerfil") private String autorFotoPerfil;

    // --- ¡NUEVOS CAMPOS DE LIKES! ---
    @SerializedName("totalLikes") private int totalLikes;
    @SerializedName("likedByMe") private boolean isLikedByMe;

    // --- Getters ---
    public Integer getIdPublicacion() { return idPublicacion; }
    public String getDescripcion() { return descripcion; }
    public String getImagen() { return imagen; }
    public Date getFechaPublicacion() { return fechaPublicacion; }
    public String getAutorUsername() { return autorUsername; }
    public String getAutorNombreCompleto() { return autorNombreCompleto; }
    public String getAutorFotoPerfil() { return autorFotoPerfil; }
    public int getTotalLikes() { return totalLikes; }
    public boolean isLikedByMe() { return isLikedByMe; }

    // --- Setters (¡los vamos a necesitar!) ---
    public void setLikedByMe(boolean likedByMe) { this.isLikedByMe = likedByMe; }
    public void setTotalLikes(int totalLikes) { this.totalLikes = totalLikes; }
}