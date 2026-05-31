package com.sportine.backend.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "comentario")
public class Comentario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idComentario;

    @Column(name = "id_publicacion")
    private Integer idPublicacion; // ID del post al que pertenece

    @Column(name = "usuario")
    private String usuario; // QuiÃ©n comentÃ³

    @Column(name = "texto")
    private String texto;

    @Column(name = "fecha")
    @Temporal(TemporalType.TIMESTAMP) // Â¡Importante para la hora exacta!
    private Date fecha;

    // --- Constructores, Getters y Setters ---
    public Comentario() {}

    public Integer getIdComentario() { return idComentario; }
    public void setIdComentario(Integer idComentario) { this.idComentario = idComentario; }

    public Integer getIdPublicacion() { return idPublicacion; }
    public void setIdPublicacion(Integer idPublicacion) { this.idPublicacion = idPublicacion; }

    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }

    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }

    public Date getFecha() { return fecha; }
    public void setFecha(Date fecha) { this.fecha = fecha; }
}
