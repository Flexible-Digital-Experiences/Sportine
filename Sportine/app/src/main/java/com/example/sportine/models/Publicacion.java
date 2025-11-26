package com.example.sportine.models;

import com.google.gson.annotations.SerializedName;
import java.util.Date;

public class Publicacion {

    @SerializedName("id_publicacion")
    private Integer idPublicacion;

    @SerializedName("usuario")
    private String usuario;

    @SerializedName("descripcion")
    private String descripcion;

    @SerializedName("imagen")
    private String imagen;

    @SerializedName("fecha_publicacion")
    private Date fechaPublicacion;

    // --- CONSTRUCTOR VACÍO (Necesario) ---
    public Publicacion() {}

    // --- GETTERS Y SETTERS (Esto es lo que te falta) ---

    public Integer getIdPublicacion() { return idPublicacion; }
    public void setIdPublicacion(Integer idPublicacion) { this.idPublicacion = idPublicacion; }

    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getImagen() { return imagen; }
    public void setImagen(String imagen) { this.imagen = imagen; }

    public Date getFechaPublicacion() { return fechaPublicacion; }
    public void setFechaPublicacion(Date fechaPublicacion) { this.fechaPublicacion = fechaPublicacion; }
}