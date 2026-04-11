package com.sportine.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "Conexiones_Api_Externa")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConexionApiExterna {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_conexion")
    private Integer idConexion;

    @Column(name = "usuario", nullable = false)
    private String usuario;

    @Enumerated(EnumType.STRING)
    @Column(name = "proveedor", nullable = false)
    private Proveedor proveedor;

    @Column(name = "esta_conectado")
    private Boolean estaConectado = false;

    @Column(name = "ultima_sincronizacion")
    private LocalDateTime ultimaSincronizacion;

    // OAuth — solo para Strava y futuras APIs
    @Column(name = "oauth_access_token", columnDefinition = "TEXT")
    private String oauthAccessToken;

    @Column(name = "oauth_refresh_token", columnDefinition = "TEXT")
    private String oauthRefreshToken;

    @Column(name = "oauth_expires_at")
    private LocalDateTime oauthExpiresAt;

    @Column(name = "oauth_scope")
    private String oauthScope;

    @Column(name = "proveedor_usuario_id")
    private String proveedorUsuarioId;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum Proveedor {
        health_connect, strava, garmin
    }
}