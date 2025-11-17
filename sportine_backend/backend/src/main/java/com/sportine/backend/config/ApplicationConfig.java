package com.sportine.backend.config;

// --- ¡IMPORTS NUEVOS Y CORREGIDOS! ---
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
// ¡Importamos el 'User' oficial de Spring Security!
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
// ¡Importamos las 'autoridades' (roles)!
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.Collections;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

    private final UsuarioRepository usuarioRepository;

    // --- ¡CAMBIO 1: Inyectamos los repos de Roles! ---
    private final UsuarioRolRepository usuarioRolRepository;
    private final RolRepository rolRepository;

    /**
     * Le dice a Spring Security CÓMO buscar y CONSTRUIR un usuario
     * (¡Este es el bean que tenía el error!)
     */
    @Bean
    public UserDetailsService userDetailsService() {

        // Esta es la lambda que da el error
        return username -> {

            // 1. Buscamos al usuario (tu modelo 'Usuario')
            Usuario usuario = usuarioRepository.findByUsuario(username)
                    .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

            // 2. Buscamos su rol (usando los otros repos)
            UsuarioRol usuarioRol = usuarioRolRepository.findByUsuario(username)
                    .orElseThrow(() -> new RuntimeException("Rol no encontrado para el usuario: " + username));

            Rol rol = rolRepository.findById(usuarioRol.getIdRol())
                    .orElseThrow(() -> new RuntimeException("Definición de rol no encontrada"));

            // --- ¡CAMBIO 2: LA CORRECCIÓN! ---
            // Ya no regresamos 'usuario' (tu modelo).
            // Regresamos un 'User' (el 'gafete' de Spring Security)
            return new User(
                    usuario.getUsuario(),

                    // ¡OJO! Basado en tus logs de Hibernate,
                    // tu campo se llama 'contraseña' (con ñ)
                    usuario.getContrasena(),

                    // (En un futuro, aquí podrías poner 'isEnabled', 'isNotExpired', etc.)
                    true, true, true, true,

                    // Le damos su ROL (ej. "ROLE_ALUMNO")
                    // Spring Security necesita el prefijo "ROLE_"
                    Collections.singleton(new SimpleGrantedAuthority("ROLE_" + rol.getRol().toUpperCase()))
            );
        }; // <-- Fin de la lambda
    }

    /**
     * ¡Esto se queda igual!
     * Sigue usando NoOpPasswordEncoder porque tus contraseñas están en texto plano.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}