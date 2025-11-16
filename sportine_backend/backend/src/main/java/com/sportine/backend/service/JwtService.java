package com.sportine.backend.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
public class JwtService {

    // ⚠️ ¡MUY IMPORTANTE! Esto es una clave secreta (hardcodeada).
    // Es INSEGURO para producción, pero perfecto para su proyecto.
    // No la compartan públicamente.
    private final Key SECRET_KEY = Keys.hmacShaKeyFor(
            "SuClaveSecretaSuperLargaYComplicadaParaSportine12345".getBytes()
    );

    // Tiempo de vida del token (ej. 7 días)
    private final long EXPIRATION_TIME = 1000 * 60 * 60 * 24 * 7;

    /**
     * Genera un nuevo Token JWT para el usuario dado.
     */
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username) // El "dueño" del token
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256) // Firma el token
                .compact();
    }

    /**
     * (Esta función la necesitarán después para validar el token
     * en cada petición de tu API de "Social")
     */
    // public String extractUsername(String token) { ... }
    // public boolean validateToken(String token) { ... }
}