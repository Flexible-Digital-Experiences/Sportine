package com.sportine.backend.config;

import com.sportine.backend.model.Rol;
import com.sportine.backend.model.Usuario;
import com.sportine.backend.model.UsuarioRol;
import com.sportine.backend.repository.RolRepository;
import com.sportine.backend.repository.UsuarioRolRepository;
import com.sportine.backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
// ✅ CAMBIO: BCryptPasswordEncoder en lugar de NoOpPasswordEncoder
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioRolRepository usuarioRolRepository;
    private final RolRepository rolRepository;

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {

            Usuario usuario = usuarioRepository.findByUsuario(username)
                    .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

            UsuarioRol usuarioRol = usuarioRolRepository.findByUsuario(username)
                    .orElseThrow(() -> new RuntimeException("Rol no encontrado para el usuario: " + username));

            Rol rol = rolRepository.findById(usuarioRol.getIdRol())
                    .orElseThrow(() -> new RuntimeException("Definición de rol no encontrada"));

            if ("ELIMINADO".equals(rol.getRol())) {
                throw new UsernameNotFoundException("Cuenta eliminada: " + username);
            }

            return new User(
                    usuario.getUsuario(),
                    usuario.getContrasena(), // ya viene el hash BCrypt de la DB
                    true, true, true, true,
                    Collections.singleton(new SimpleGrantedAuthority("ROLE_" + rol.getRol().toUpperCase()))
            );
        };
    }

    /**
     * ✅ SEGURIDAD: BCrypt reemplaza NoOpPasswordEncoder.
     * Spring Security usa este bean automáticamente para verificar
     * passwords en el flujo de autenticación (JwtAuthFilter).
     * También lo inyectamos en UsuarioServiceImpl para hashear
     * al registrar y comparar al hacer login/cambiarPassword.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}