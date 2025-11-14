package com.sportine.backend.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "Likes")
public class Likes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idLike; // <-- CAMBIO #1

    // Esta es la variable en Java (camelCase)
    @Column(name = "id_publicacion") // Esta es la columna en MySQL (snake_case)
    private Integer idPublicacion; // <-- CAMBIO #2

    @Column(name = "usuario_like")
    private String usuarioLike; // <-- CAMBIO #3

    @Column(name = "fecha_like")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaLike = new Date();

    // --- Constructores, Getters y Setters ---
    public Likes() {}

    // (Los Getters y Setters también cambian de nombre)
    public Integer getIdLike() {
        return idLike;
    }
    public void setIdLike(Integer idLike) {
        this.idLike = idLike;
    }
    public Integer getIdPublicacion() {
        return idPublicacion;
    }
    public void setIdPublicacion(Integer idPublicacion) {
        this.idPublicacion = idPublicacion;
    }
    public String getUsuarioLike() {
        return usuarioLike;
    }
    public void setUsuarioLike(String usuarioLike) {
        this.usuarioLike = usuarioLike;
    }
    public Date getFechaLike() {
        return fechaLike;
    }
    public void setFechaLike(Date fechaLike) {
        this.fechaLike = fechaLike;
    }
}