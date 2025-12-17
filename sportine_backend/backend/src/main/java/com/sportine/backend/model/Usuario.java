package com.sportine.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Usuario")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {
    @Id
    @Column(name = "usuario")
    private String usuario;

    @Column(name="correo")
    private String correo;

    @Column(name = "contraseña")
    private String contrasena;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "apellidos")
    private String apellidos;

    @Column(name = "sexo")
    private String sexo;

    @Column(name = "id_estado")
    private Integer idEstado;

    @Column(name = "ciudad")
    private String ciudad;
}
