package com.example.sportine.models;

import com.google.gson.annotations.SerializedName;
import java.util.Date;

public class PublicacionFeedDTO {

    @SerializedName("idPublicacion") private Integer idPublicacion;
    @SerializedName("descripcion") private String descripcion;
    @SerializedName("imagen") private String imagen;
    @SerializedName("fechaPublicacion") private Date fechaPublicacion;

    @SerializedName("autorUsername") private String autorUsername;
    @SerializedName("autorNombreCompleto") private String autorNombreCompleto;
    @SerializedName("autorFotoPerfil") private String autorFotoPerfil;

    @SerializedName("totalLikes") private int totalLikes;
    @SerializedName("likedByMe") private boolean isLikedByMe;

    @SerializedName("mine")
    private boolean isMine;

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
    public void setLikedByMe(boolean likedByMe) { this.isLikedByMe = likedByMe; }
    public void setTotalLikes(int totalLikes) { this.totalLikes = totalLikes; }
    public boolean isMine() { return isMine; }
}