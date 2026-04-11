package com.sportine.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para registrar/consultar el estado de conexión con APIs externas.
 * Health Connect no requiere OAuth — solo registramos que el usuario lo tiene activo.
 * Strava sí requiere OAuth tokens.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConexionApiDTO {

    @JsonProperty("proveedor")
    private String proveedor; // health_connect, strava, garmin

    @JsonProperty("esta_conectado")
    private Boolean estaConectado;

    @JsonProperty("ultima_sincronizacion")
    private LocalDateTime ultimaSincronizacion;

    // Solo para Strava y futuras APIs con OAuth
    @JsonProperty("oauth_access_token")
    private String oauthAccessToken;

    @JsonProperty("oauth_refresh_token")
    private String oauthRefreshToken;

    @JsonProperty("oauth_expires_at")
    private LocalDateTime oauthExpiresAt;

    @JsonProperty("oauth_scope")
    private String oauthScope;

    @JsonProperty("proveedor_usuario_id")
    private String proveedorUsuarioId;
}