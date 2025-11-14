package com.sportine.backend.model;

import jakarta.persistence.*;

/**
 * Entidad (Model) que representa la tabla 'Amistad' en la base de datos.
 * Define la relación entre dos usuarios.
 */
@Entity
@Table(name = "Amistad")
public class Amistad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_amistad;

    // Usuario que envía o inicia la relación
    @Column(name = "usuario_1")
    private String usuario_1;

    // Usuario que recibe o acepta la relación
    @Column(name = "usuario_2")
    private String usuario_2;

    // --- Getters y Setters ---
    public Amistad() {}

    public Integer getId_amistad() { return id_amistad; }
    public void setId_amistad(Integer id_amistad) { this.id_amistad = id_amistad; }
    public String getUsuario_1() { return usuario_1; }
    public void setUsuario_1(String usuario_1) { this.usuario_1 = usuario_1; }
    public String getUsuario_2() { return usuario_2; }
    public void setUsuario_2(String usuario_2) { this.usuario_2 = usuario_2; }
}