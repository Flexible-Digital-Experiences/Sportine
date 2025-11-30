package com.sportine.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "Notificacion")
@Data
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idNotificacion;

    @Column(name = "usuario_destino")
    private String usuarioDestino;

    @Column(name = "usuario_actor")
    private String usuarioActor;

    @Enumerated(EnumType.STRING)
    private TipoNotificacion tipo;

    @Column(name = "id_referencia")
    private Integer idReferencia;

    private String mensaje;

    private Boolean leido = false;

    private LocalDateTime fecha;

    @PrePersist
    protected void onCreate() {
        fecha = LocalDateTime.now();
    }

    public enum TipoNotificacion {
        LIKE, COMENTARIO, SEGUIDOR
    }
}