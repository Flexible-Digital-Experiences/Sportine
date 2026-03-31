package com.sportine.backend.config;

// ============================================================
//   CorsConfig.java  —  Sportine · Configuración global de CORS
//
//   ¿Qué es CORS?
//   Cross-Origin Resource Sharing. Es una política de seguridad
//   del navegador que bloquea peticiones HTTP entre dominios
//   distintos por defecto.
//
//   Tu frontend corre en: http://localhost:5500 (o similar con Live Server)
//   Tu backend corre en:  http://localhost:8080
//
//   Como son puertos distintos, el navegador los trata como
//   "orígenes diferentes" y bloquea las peticiones del frontend
//   hacia el backend. Este archivo le dice al backend que SÍ
//   acepte esas peticiones.
//
//   ¿Por qué un archivo separado en lugar de @CrossOrigin en cada controller?
//   - Se aplica globalmente a todos los endpoints actuales y futuros.
//   - Se integra correctamente con Spring Security (importante).
//   - Si mañana agregas un controller nuevo, ya tiene CORS por defecto.
//   - Si necesitas cambiar la URL del frontend, cambias un solo lugar.
// ============================================================

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration config = new CorsConfiguration();

        // ── Orígenes permitidos ──────────────────────────────────
        //
        // Aquí defines desde qué URLs puede llamar tu frontend.
        //
        // Durante DESARROLLO: Live Server de VS Code usa el puerto 5500.
        // También agregamos 5501 por si Live Server lo usa como alternativo,
        // y el puerto 3000 por si alguien usa otro servidor local.
        //
        // IMPORTANTE: Cuando suban el proyecto a producción, reemplazar
        // estos valores con la URL real del frontend desplegado.
        // Ejemplo: "https://sportine.com"
        //
        config.setAllowedOrigins(List.of(
                "http://localhost:5500",
                "http://127.0.0.1:5500",
                "http://localhost:5501",
                "http://127.0.0.1:5501",
                "http://localhost:3000",
                "http://127.0.0.1:3000"
        ));

        // ── Métodos HTTP permitidos ──────────────────────────────
        //
        // GET    → obtener datos (perfil, estados, etc.)
        // POST   → crear (login, registro, publicaciones, etc.)
        // PUT    → actualizar (editar perfil, cambiar contraseña, etc.)
        // DELETE → eliminar
        // OPTIONS → petición "preflight" que el navegador manda
        //           automáticamente antes de POST/PUT para verificar CORS.
        //           Si no permitimos OPTIONS, esas peticiones fallan.
        //
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // ── Headers permitidos en las peticiones ─────────────────
        //
        // "Authorization" → donde va el token JWT ("Bearer eyJhbGc...")
        // "Content-Type"  → para indicar que mandamos JSON
        // "*"             → cualquier otro header estándar
        //
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "*"));

        // ── Permitir credenciales ────────────────────────────────
        //
        // Necesario para que el navegador pueda enviar el header
        // Authorization con el token JWT en peticiones autenticadas.
        //
        config.setAllowCredentials(true);

        // ── Tiempo de cache del preflight ────────────────────────
        //
        // El navegador guarda en caché la respuesta del preflight
        // OPTIONS por este tiempo (en segundos) para no repetirlo
        // en cada petición. 3600 = 1 hora.
        //
        config.setMaxAge(3600L);

        // Aplicar esta configuración a TODOS los endpoints ("/**")
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}