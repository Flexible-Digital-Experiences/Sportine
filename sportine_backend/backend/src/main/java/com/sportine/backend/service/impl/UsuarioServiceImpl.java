package com.sportine.backend.service.impl;

import com.sportine.backend.dto.*;
import com.sportine.backend.model.Rol;
import com.sportine.backend.model.Usuario;
import com.sportine.backend.model.UsuarioRol;
import com.sportine.backend.repository.RolRepository;
import com.sportine.backend.repository.UsuarioRepository;
import com.sportine.backend.repository.UsuarioRolRepository;
import com.sportine.backend.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final UsuarioRolRepository usuarioRolRepository;

    @Override
    @Transactional
    public UsuarioResponseDTO registrarUsuario(UsuarioRegistroDTO dto) {

        // Validar si el usuario ya existe
        if (usuarioRepository.existsByUsuario(dto.getUsuario())) {
            throw new RuntimeException("El usuario ya existe");
        }

        // Crear el usuario
        Usuario usuario = new Usuario();
        usuario.setUsuario(dto.getUsuario());
        usuario.setContrasena(dto.getContrasena());
        usuario.setNombre(dto.getNombre());
        usuario.setApellidos(dto.getApellidos());
        usuario.setSexo(dto.getSexo());
        usuario.setEstado(dto.getEstado());
        usuario.setCiudad(dto.getCiudad());

        usuarioRepository.save(usuario);

        // Buscar el rol
        Rol rol = rolRepository.findByRol(dto.getRol())
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

        // Crear la relación Usuario_rol
        UsuarioRol usuarioRol = new UsuarioRol();
        usuarioRol.setUsuario(dto.getUsuario());
        usuarioRol.setIdRol(rol.getIdRol());

        usuarioRolRepository.save(usuarioRol);

        // Retornar respuesta
        return new UsuarioResponseDTO(
                usuario.getUsuario(),
                usuario.getNombre(),
                usuario.getApellidos(),
                dto.getRol(),
                "Usuario registrado exitosamente"
        );
    }

    @Override
    public UsuarioDetalleDTO obtenerUsuarioPorUsername(String username) {

        // 1. Buscar el usuario en UsuarioRepository (NO en UsuarioRolRepository)
        Usuario usuario = usuarioRepository.findByUsuario(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 2. Buscar su rol en UsuarioRolRepository
        UsuarioRol usuarioRol = usuarioRolRepository.findByUsuario(username)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado para el usuario"));

        // 3. Obtener el nombre del rol
        Rol rol = rolRepository.findById(usuarioRol.getIdRol())
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

        // 4. Crear y devolver el DTO
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
                    false,
                    "Usuario no encontrado",
                    null, null, null, null, null, null, null
            );
        }

        if (!usuario.getContrasena().equals(dto.getContrasena())) {
            return new LoginResponseDTO(
                    false,
                    "Contraseña incorrecta",
                    null, null, null, null, null, null, null
            );
        }

        UsuarioRol usuarioRol = usuarioRolRepository.findByUsuario(dto.getUsuario())
                .orElse(null);

        String rolNombre = "";
        if (usuarioRol != null) {
            Rol rol = rolRepository.findById(usuarioRol.getIdRol()).orElse(null);
            rolNombre = rol != null ? rol.getRol() : "";
        }

        return new LoginResponseDTO(
                true,
                "Login exitoso",
                usuario.getUsuario(),
                usuario.getNombre(),
                usuario.getApellidos(),
                rolNombre,
                usuario.getSexo(),
                usuario.getEstado(),
                usuario.getCiudad()
        );
    }
    @Override
    @Transactional
    public UsuarioDetalleDTO actualizarDatosBasicos(String username, ActualizarUsuarioDTO dto) {

        // 1. Buscar el usuario
        Usuario usuario = usuarioRepository.findByUsuario(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 2. Actualizar los campos
        usuario.setNombre(dto.getNombre());
        usuario.setApellidos(dto.getApellidos());
        usuario.setSexo(dto.getSexo());
        usuario.setEstado(dto.getEstado());
        usuario.setCiudad(dto.getCiudad());

        // 3. Guardar cambios
        usuarioRepository.save(usuario);

        // 4. Obtener el rol para la respuesta
        UsuarioRol usuarioRol = usuarioRolRepository.findByUsuario(username)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

        Rol rol = rolRepository.findById(usuarioRol.getIdRol())
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

        // 5. Retornar el usuario actualizado
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
    @Transactional
    public UsuarioResponseDTO cambiarPassword(String username, CambiarPasswordDTO dto) {

        // 1. Validar que las contraseñas nuevas coincidan
        if (!dto.getPasswordNueva().equals(dto.getPasswordNuevaConfirmar())) {
            throw new RuntimeException("Las contraseñas nuevas no coinciden");
        }

        // 2. Validar que la nueva contraseña no esté vacía
        if (dto.getPasswordNueva() == null || dto.getPasswordNueva().trim().isEmpty()) {
            throw new RuntimeException("La contraseña nueva no puede estar vacía");
        }

        // 3. Validar que la nueva contraseña tenga al menos 6 caracteres
        if (dto.getPasswordNueva().length() < 6) {
            throw new RuntimeException("La contraseña debe tener al menos 6 caracteres");
        }

        // 4. Buscar el usuario
        Usuario usuario = usuarioRepository.findByUsuario(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 5. Validar que la contraseña actual sea correcta
        if (!usuario.getContrasena().equals(dto.getPasswordActual())) {
            throw new RuntimeException("La contraseña actual es incorrecta");
        }

        // 6. Actualizar la contraseña
        usuario.setContrasena(dto.getPasswordNueva());
        usuarioRepository.save(usuario);

        // 7. Obtener el rol para la respuesta
        UsuarioRol usuarioRol = usuarioRolRepository.findByUsuario(username)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

        Rol rol = rolRepository.findById(usuarioRol.getIdRol())
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

        // 8. Retornar confirmación
        return new UsuarioResponseDTO(
                usuario.getUsuario(),
                usuario.getNombre(),
                usuario.getApellidos(),
                rol.getRol(),
                "Contraseña actualizada exitosamente"
        );
    }
}