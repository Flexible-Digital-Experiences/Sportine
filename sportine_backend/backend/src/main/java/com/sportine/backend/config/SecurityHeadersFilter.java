package com.sportine.backend.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro que agrega headers de seguridad HTTP a todas las respuestas.
 *
 * Estos headers protegen contra ataques comunes del lado del navegador:
 */
@Component
public class SecurityHeadersFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // El browser no debe adivinar el Content-Type de la respuesta.
        // Evita ataques donde se sube un archivo con contenido JavaScript
        // disfrazado de imagen y el browser lo ejecuta.
        response.setHeader("X-Content-Type-Options", "nosniff");

        // Prohíbe que el sitio sea embebido en un <iframe> de otro dominio.
        // Protege contra clickjacking (ej: overlay invisible sobre botón de pago).
        response.setHeader("X-Frame-Options", "DENY");

        // Activa el filtro XSS del browser (legacy; útil para IE/Edge viejos).
        response.setHeader("X-XSS-Protection", "1; mode=block");

        // Controla qué información de URL se envía en el header Referer.
        // "strict-origin-when-cross-origin": solo envía el origen (sin path ni query)
        // cuando se hace una petición cross-origin. Protege tokens en URLs.
        response.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");

        // Las respuestas de la API no deben cachearse en el browser.
        // Evita que datos sensibles (perfil, token) queden en caché local.
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, private");
        response.setHeader("Pragma", "no-cache");

        filterChain.doFilter(request, response);
    }
}