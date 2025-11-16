package com.sportine.backend.service.impl;

import com.sportine.backend.dto.*;
import com.sportine.backend.model.Rol;
import com.sportine.backend.model.Usuario;
import com.sportine.backend.model.UsuarioRol;
import com.sportine.backend.repository.RolRepository;
import com.sportine.backend.repository.UsuarioRepository;
import com.sportine.backend.repository.UsuarioRolRepository;
import com.sportine.backend.service.UsuarioService;
// --- ¡CAMBIO 1! ---
// Importamos el nuevo servicio
import com.sportine.backend.service.JwtService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final UsuarioRolRepository usuarioRolRepository;


    // Inyectamos el servicio de JWT (Lombok lo hace por RequiredArgsConstructor)
    private final JwtService jwtService;

    // (El método registrarUsuario no cambia)
    @Override
    @Transactional
    public UsuarioResponseDTO registrarUsuario(UsuarioRegistroDTO dto) {
        // ... (código igual)
        if (usuarioRepository.existsByUsuario(dto.getUsuario())) {
            throw new RuntimeException("El usuario ya existe");
        }
        Usuario usuario = new Usuario();
        usuario.setUsuario(dto.getUsuario());
        usuario.setContrasena(dto.getContrasena());
        usuario.setNombre(dto.getNombre());
        usuario.setApellidos(dto.getApellidos());
        usuario.setSexo(dto.getSexo());
        usuario.setEstado(dto.getEstado());
        usuario.setCiudad(dto.getCiudad());
        usuarioRepository.save(usuario);
        Rol rol = rolRepository.findByRol(dto.getRol())
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));
        UsuarioRol usuarioRol = new UsuarioRol();
        usuarioRol.setUsuario(dto.getUsuario());
        usuarioRol.setIdRol(rol.getIdRol());
        usuarioRolRepository.save(usuarioRol);
        return new UsuarioResponseDTO(
                usuario.getUsuario(),
                usuario.getNombre(),
                usuario.getApellidos(),
                dto.getRol(),
                "Usuario registrado exitosamente"
        );
    }

    // (El método obtenerUsuarioPorUsername no cambia)
    @Override
    public UsuarioDetalleDTO obtenerUsuarioPorUsername(String username) {
        // ... (código igual)
        Usuario usuario = usuarioRepository.findByUsuario(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        UsuarioRol usuarioRol = usuarioRolRepository.findByUsuario(username)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado para el usuario"));
        Rol rol = rolRepository.findById(usuarioRol.getIdRol())
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));
        return new UsuarioDetalleDTO(
                usuario.getUsuario(),
                usuario.getNombre(),
                usuario.getApellidos(),
                usuario.getSexo(),
                usuario.getEstado(),
                usuario.getCiudad(),
                rol.getRol()
        );
    }

    @Override
    public LoginResponseDTO login(LoginRequestDTO dto) {

        Usuario usuario = usuarioRepository.findByUsuario(dto.getUsuario())
                .orElse(null);

        if (usuario == null) {
            return new LoginResponseDTO(
                    false, "Usuario no encontrado",
                    null, null, null, null, null, null, null, null // 10 campos
            );
        }

        // ¡OJO! En un proyecto real, aquí se usaría un BCrypt.matches()
        if (!usuario.getContrasena().equals(dto.getContrasena())) {
            return new LoginResponseDTO(
                    false, "Contraseña incorrecta",
                    null, null, null, null, null, null, null, null // 10 campos
            );
        }

        // El login es exitoso, buscamos el rol
        UsuarioRol usuarioRol = usuarioRolRepository.findByUsuario(dto.getUsuario())
                .orElse(null);
        String rolNombre = "";
        if (usuarioRol != null) {
            Rol rol = rolRepository.findById(usuarioRol.getIdRol()).orElse(null);
            rolNombre = rol != null ? rol.getRol() : "";
        }

        // --- ¡CAMBIO 3! (El más importante) ---
        // 1. Generar el token
        String token = jwtService.generateToken(usuario.getUsuario());

        // 2. Retornar el DTO con el token
        return new LoginResponseDTO(
                true,
                "Login exitoso",
                token, // <-- ¡NUEVO CAMPO!
                usuario.getUsuario(),
                usuario.getNombre(),
                usuario.getApellidos(),
                rolNombre,
                usuario.getSexo(),
                usuario.getEstado(),
                usuario.getCiudad()
        );
    }

    // (El método actualizarDatosBasicos no cambia)
    @Override
    @Transactional
    public UsuarioDetalleDTO actualizarDatosBasicos(String username, ActualizarUsuarioDTO dto) {
        // ... (código igual)
        Usuario usuario = usuarioRepository.findByUsuario(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        usuario.setNombre(dto.getNombre());
        usuario.setApellidos(dto.getApellidos());
        usuario.setSexo(dto.getSexo());
        usuario.setEstado(dto.getEstado());
        usuario.setCiudad(dto.getCiudad());
        usuarioRepository.save(usuario);
        UsuarioRol usuarioRol = usuarioRolRepository.findByUsuario(username)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));
        Rol rol = rolRepository.findById(usuarioRol.getIdRol())
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));
        return new UsuarioDetalleDTO(
                usuario.getUsuario(),
                usuario.getNombre(),
                usuario.getApellidos(),
                usuario.getSexo(),
                usuario.getEstado(),
                usuario.getCiudad(),
                rol.getRol()
        );
    }

    // (El método cambiarPassword no cambia)
    @Override
    @Transactional
    public UsuarioResponseDTO cambiarPassword(String username, CambiarPasswordDTO dto) {
        // ... (código igual)
        if (!dto.getPasswordNueva().equals(dto.getPasswordNuevaConfirmar())) {
            throw new RuntimeException("Las contraseñas nuevas no coinciden");
        }
        if (dto.getPasswordNueva() == null || dto.getPasswordNueva().trim().isEmpty()) {
            throw new RuntimeException("La contraseña nueva no puede estar vacía");
        }
        if (dto.getPasswordNueva().length() < 6) {
            throw new RuntimeException("La contraseña debe tener al menos 6 caracteres");
        }
        Usuario usuario = usuarioRepository.findByUsuario(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        if (!usuario.getContrasena().equals(dto.getPasswordActual())) {
            throw new RuntimeException("La contraseña actual es incorrecta");
        }
        usuario.setContrasena(dto.getPasswordNueva());
        usuarioRepository.save(usuario);
        UsuarioRol usuarioRol = usuarioRolRepository.findByUsuario(username)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));
        Rol rol = rolRepository.findById(usuarioRol.getIdRol())
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));
        return new UsuarioResponseDTO(
                usuario.getUsuario(),
                usuario.getNombre(),
                usuario.getApellidos(),
                rol.getRol(),
                "Contraseña actualizada exitosamente"
        );
    }
}