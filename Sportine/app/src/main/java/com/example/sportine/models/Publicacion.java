package com.example.sportine.models;

import com.google.gson.annotations.SerializedName;
import java.util.Date;

// Este es el "molde" para recibir un post.
// Es un espejo del 'Publicacion.java' del backend.
public class Publicacion {

    // Usamos @SerializedName para que GSON sepa
    // cómo mapear 'id_publicacion' del JSON a esta variable.
    @SerializedName("id_publicacion")
    private Integer idPublicacion;

    @SerializedName("usuario")
    private String usuario;

    @SerializedName("descripcion")
    private String descripcion;

    @SerializedName("fecha_publicacion")
    private Date fechaPublicacion;

    @SerializedName("imagen")
    private String imagen;

    // Getters (los setters no son tan necesarios si solo lees datos)
    public Integer getIdPublicacion() { return idPublicacion; }
    public String getUsuario() { return usuario; }
    public String getDescripcion() { return descripcion; }
    public Date getFechaPublicacion() { return fechaPublicacion; }
    public String getImagen() { return imagen; }
}