package com.sportine.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "Seguidores")
@Data
public class Seguidores {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_seguimiento")
    private Integer idSeguimiento;

    @Column(name = "usuario_seguidor")
    private String usuarioSeguidor;

    @Column(name = "usuario_seguido")
    private String usuarioSeguido;
    @Column(name = "fecha_seguimiento")
    private LocalDateTime fechaSeguimiento;

    @PrePersist
    protected void onCreate() {
        fechaSeguimiento = LocalDateTime.now();
    }
}