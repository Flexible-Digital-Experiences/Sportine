package com.sportine.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "Usuario_rol")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioRol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario_rol")
    private Integer idUsuarioRol;

    @Column(name = "id_rol")
    private Integer idRol;

    @Column(name = "usuario")
    private String usuario;
}