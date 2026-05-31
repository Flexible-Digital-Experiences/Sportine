package com.sportine.backend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final CorsConfigurationSource corsConfigurationSource;
    // ✅ SEGURIDAD: filtros nuevos
    private final RateLimitFilter rateLimitFilter;
    private final SecurityHeadersFilter securityHeadersFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        // 1. CSRF deshabilitado (app stateless con JWT)
        http.csrf(csrf -> csrf.disable());

        // 2. CORS
        http.cors(cors -> cors.configurationSource(corsConfigurationSource));

        // 3. Rutas
        http.authorizeHttpRequests(auth -> auth

                // ── Archivos estáticos (HTML, JS, CSS, imágenes) ──────────
                .requestMatchers(
                        "/pages/**",
                        "/js/**",
                        "/css/**",
                        "/img/**",
                        "/images/**",
                        "/fonts/**",
                        "/favicon.ico",
                        "/",
                        "/index.html"
                ).permitAll()

                // ── API pública ───────────────────────────────────────────
                .requestMatchers(
                        "/api/usuarios/login",
                        "/api/usuarios/registrar",
                        "/api/usuarios/estados",
                        "/api/v2/entrenador/paypal/verificar-onboarding",
                        // Callbacks de PayPal — llegan sin JWT (redirect del navegador)
                        "/api/v2/estudiante/suscripcion/pago/success",
                        "/api/v2/estudiante/suscripcion/pago/cancel"
                ).permitAll()

                .requestMatchers(HttpMethod.POST, "/api/alumnos/*/actualizar-foto").authenticated()

                .requestMatchers("/api/entrenador/**").permitAll()

                .anyRequest().authenticated()
        );

        // 4. Stateless
        http.sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        // 5. Cadena de filtros (orden importa):
        //    SecurityHeaders → RateLimit → JWT
        //
        //    SecurityHeaders va primero para que TODAS las respuestas
        //    (incluyendo 429 y 401) lleven los headers de seguridad.
        http.addFilterBefore(securityHeadersFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(rateLimitFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}