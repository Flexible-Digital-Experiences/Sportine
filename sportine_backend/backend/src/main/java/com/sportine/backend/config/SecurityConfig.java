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

// ── NUEVO IMPORT ─────────────────────────────────────────────
// Necesitamos inyectar el CorsConfigurationSource que
// definimos en CorsConfig.java
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    // ── NUEVO: inyectar el bean de CORS ──────────────────────
    // Spring lo encuentra automáticamente porque está definido
    // como @Bean en CorsConfig.java
    private final CorsConfigurationSource corsConfigurationSource;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        // 1. Deshabilitamos CSRF
        http.csrf(csrf -> csrf.disable());

        // 2. ── NUEVO: Habilitar CORS con nuestra configuración ──
        //
        // Esta línea le dice a Spring Security que use las reglas
        // de CORS que definimos en CorsConfig.java.
        //
        // ¿Por qué hay que hacerlo aquí Y en CorsConfig?
        // Spring Security tiene su propia capa de filtros que
        // procesa las peticiones ANTES que el resto de Spring.
        // Si solo defines CorsConfig pero no lo registras aquí,
        // Spring Security rechaza las peticiones OPTIONS (preflight)
        // antes de que lleguen a tu configuración de CORS.
        //
        http.cors(cors -> cors.configurationSource(corsConfigurationSource));

        // 3. Rutas públicas y privadas (sin cambios)
        http.authorizeHttpRequests(auth -> auth

                .requestMatchers(
                        "/api/usuarios/login",
                        "/api/usuarios/registrar",
                        "/api/usuarios/estados",           // ← añadido: necesario para cargar el select de estados en registro
                        "/api/v2/entrenador/paypal/verificar-onboarding",
                        "/api/v2/estudiante/suscripcion/pago/success",
                        "/api/v2/estudiante/suscripcion/pago/cancel"
                ).permitAll()

                .requestMatchers(HttpMethod.POST, "/api/alumnos/*/actualizar-foto").authenticated()

                .requestMatchers("/api/entrenador/**").permitAll()

                .anyRequest().authenticated()
        );

        // 4. Sesiones stateless (sin cambios)
        http.sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        // 5. Filtro JWT (sin cambios)
        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}