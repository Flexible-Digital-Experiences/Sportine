package com.sportine.backend.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    // ⚠️ ¡IMPORTANTE! Esta clave debe ser LA MISMA que usaron para crear el token
    // (Puse la que te di de ejemplo)
    private final Key SECRET_KEY = Keys.hmacShaKeyFor(
            "SuClaveSecretaSuperLargaYComplicadaParaSportine12345".getBytes()
    );

    // Tiempo de vida del token (ej. 7 días)
    private final long EXPIRATION_TIME = 1000 * 60 * 60 * 24 * 7;

    // --- (Este método ya lo tenías) ---
    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username) // El "dueño" del token
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256) // Firma el token
                .compact();
    }

    // --- ¡NUEVOS MÉTODOS (Leer el token) ---

    /**
     * Extrae el 'username' (el "subject") del token.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Valida si el token es correcto y no ha expirado.
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    /**
     * Método genérico para extraer cualquier "claim" (dato) del token.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}