package com.sportine.backend.model;

import jakarta.persistence.*; // O javax.persistence.*
import java.util.Date;

@Entity
@Table(name = "Publicacion")
public class Publicacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_publicacion;

    // Si 'usuario' es una relación @ManyToOne con la tabla Usuario,
    // esto se puede cambiar, pero por ahora un String funciona bien)
    @Column(name = "usuario")
    private String usuario;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "fecha_publicacion")
    @Temporal(TemporalType.TIMESTAMP) // <-- ¡ESTA ES LA CORRECCIÓN!
    private Date fecha_publicacion;

    @Column(name = "imagen")
    private String imagen;

    // --- Constructores, Getters y Setters (Spring los necesita) ---

    public Publicacion() {
        // Constructor vacío
    }

    // Getters y Setters
    public Integer getId_publicacion() {
        return id_publicacion;
    }

    public void setId_publicacion(Integer id_publicacion) {
        this.id_publicacion = id_publicacion;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Date getFecha_publicacion() {
        return fecha_publicacion;
    }

    public void setFecha_publicacion(Date fecha_publicacion) {
        this.fecha_publicacion = fecha_publicacion;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }
}