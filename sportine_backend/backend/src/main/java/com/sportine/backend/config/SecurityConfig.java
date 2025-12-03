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

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    // (Spring inyectará aquí el bean de UserDetailsService
    // que creamos en ApplicationConfig)

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        // 1. Deshabilitamos CSRF (no lo usamos con APIs REST stateless)
        http.csrf(csrf -> csrf.disable());

        // 2. Definimos qué rutas son PÚBLICAS y cuáles PRIVADAS
        http.authorizeHttpRequests(auth -> auth

                // Rutas PÚBLICAS (Login y Registro)
                .requestMatchers("/api/usuarios/login", "/api/usuarios/registrar").permitAll()

                // ✅ NUEVO: Permitir actualización de foto de perfil (con autenticación)
                .requestMatchers(HttpMethod.POST, "/api/alumnos/*/actualizar-foto").authenticated()

                // ¡TODAS LAS DEMÁS (incluyendo tu /api/social/**)
                // requieren que el usuario esté autenticado!
                .anyRequest().authenticated()
        );

        // 3. Configurar el manejo de sesiones (Stateless)
        // (Le decimos a Spring que NO guarde sesiones, que usaremos Tokens)
        http.sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        // 4. ¡AÑADIR NUESTRO FILTRO!
        // (Le decimos a Spring que USE nuestro JwtAuthFilter ANTES del filtro
        // estándar de autenticación de username/password)
        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}