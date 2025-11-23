package com.sportine.backend.service.impl;

import com.sportine.backend.dto.*;
import com.sportine.backend.exception.ConflictoException;
import com.sportine.backend.exception.DatosInvalidosException;
import com.sportine.backend.exception.RecursoNoEncontradoException;
import com.sportine.backend.model.Estado;
import com.sportine.backend.model.Rol;
import com.sportine.backend.model.Usuario;
import com.sportine.backend.model.UsuarioRol;
import com.sportine.backend.repository.EstadoRepository;
import com.sportine.backend.repository.RolRepository;
import com.sportine.backend.repository.UsuarioRepository;
import com.sportine.backend.repository.UsuarioRolRepository;
import com.sportine.backend.service.JwtService;
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
    private final JwtService jwtService;
    private final EstadoRepository estadoRepository;

    // ============================================
    // MÉTODOS HELPER PRIVADOS (EVITA REPETICIÓN)
    // ============================================

    /**
     * Busca un usuario por username o lanza excepción si no existe
     */
    private Usuario obtenerUsuarioOError(String username) {
        return usuarioRepository.findByUsuario(username)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario", username));
    }

    /**
     * Busca un rol por nombre o lanza excepción si no existe
     */
    private Rol obtenerRolOError(String nombreRol) {
        return rolRepository.findByRol(nombreRol)
                .orElseThrow(() -> new RecursoNoEncontradoException("Rol", nombreRol));
    }

    /**
     * Busca el rol de un usuario o lanza excepción
     */
    private UsuarioRol obtenerUsuarioRolOError(String username) {
        return usuarioRolRepository.findByUsuario(username)
                .orElseThrow(() -> new RecursoNoEncontradoException("Rol del usuario", username));
    }

    // ============================================
    // MÉTODOS DEL SERVICIO
    // ============================================

    @Override
    @Transactional
    public UsuarioResponseDTO registrarUsuario(UsuarioRegistroDTO dto) {

        // Validación: Usuario ya existe
        if (usuarioRepository.existsByUsuario(dto.getUsuario())) {
            throw new ConflictoException("Usuario", dto.getUsuario());
        }

        // Crear usuario
        Usuario usuario = new Usuario();
        usuario.setUsuario(dto.getUsuario());
        usuario.setContrasena(dto.getContrasena());
        usuario.setNombre(dto.getNombre());
        usuario.setApellidos(dto.getApellidos());
        usuario.setSexo(dto.getSexo());
        usuario.setIdEstado(dto.getIdEstado());
        usuario.setCiudad(dto.getCiudad());
        usuarioRepository.save(usuario);

        // Usar método helper
        Rol rol = obtenerRolOError(dto.getRol());

        // Asignar rol
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

    @Override
    public UsuarioDetalleDTO obtenerUsuarioPorUsername(String username) {

        // Usar método helper
        Usuario usuario = obtenerUsuarioOError(username);
        UsuarioRol usuarioRol = obtenerUsuarioRolOError(username);

        Rol rol = rolRepository.findById(usuarioRol.getIdRol())
                .orElseThrow(() -> new RecursoNoEncontradoException("Rol con ID",
                        usuarioRol.getIdRol().toString()));

        Estado estado = estadoRepository.findById(usuario.getIdEstado())
                .orElse(null);
        String nombreEstado = estado != null ? estado.getEstado() : "";

        return new UsuarioDetalleDTO(
                usuario.getUsuario(),
                usuario.getNombre(),
                usuario.getApellidos(),
                usuario.getSexo(),
                nombreEstado,
                usuario.getCiudad(),
                rol.getRol(),
                false
        );
    }

    @Override
    public LoginResponseDTO login(LoginRequestDTO dto) {

        Usuario usuario = usuarioRepository.findByUsuario(dto.getUsuario())
                .orElse(null);

        // Retornar respuesta clara en lugar de excepción
        if (usuario == null) {
            return new LoginResponseDTO(
                    false,
                    "Usuario no encontrado",
                    null, null, null, null, null, null, null, null
            );
        }

        // Validar contraseña
        if (!usuario.getContrasena().equals(dto.getContrasena())) {
            return new LoginResponseDTO(
                    false,
                    "Contraseña incorrecta",
                    null, null, null, null, null, null, null, null
            );
        }

        // Login exitoso
        UsuarioRol usuarioRol = usuarioRolRepository.findByUsuario(dto.getUsuario())
                .orElse(null);

        String rolNombre = "";
        if (usuarioRol != null) {
            Rol rol = rolRepository.findById(usuarioRol.getIdRol()).orElse(null);
            rolNombre = rol != null ? rol.getRol() : "";
        }

        Estado estado = estadoRepository.findById(usuario.getIdEstado())
                .orElse(null);
        String nombreEstado = estado != null ? estado.getEstado() : "";

        String token = jwtService.generateToken(usuario.getUsuario());

        return new LoginResponseDTO(
                true,
                "Login exitoso",
                token,
                usuario.getUsuario(),
                usuario.getNombre(),
                usuario.getApellidos(),
                rolNombre,
                usuario.getSexo(),
                nombreEstado,
                usuario.getCiudad()
        );
    }

    @Override
    @Transactional
    public UsuarioDetalleDTO actualizarDatosBasicos(String username, ActualizarUsuarioDTO dto) {

        // Usar método helper
        Usuario usuario = obtenerUsuarioOError(username);

        usuario.setNombre(dto.getNombre());
        usuario.setApellidos(dto.getApellidos());
        usuario.setSexo(dto.getSexo());
        usuario.setIdEstado(dto.getIdEstado());
        usuario.setCiudad(dto.getCiudad());

        usuarioRepository.save(usuario);

        UsuarioRol usuarioRol = obtenerUsuarioRolOError(username);
        Rol rol = rolRepository.findById(usuarioRol.getIdRol())
                .orElseThrow(() -> new RecursoNoEncontradoException("Rol con ID",
                        usuarioRol.getIdRol().toString()));

        Estado estado = estadoRepository.findById(usuario.getIdEstado())
                .orElse(null);
        String nombreEstado = estado != null ? estado.getEstado() : "";

        return new UsuarioDetalleDTO(
                usuario.getUsuario(),
                usuario.getNombre(),
                usuario.getApellidos(),
                usuario.getSexo(),
                nombreEstado,
                usuario.getCiudad(),
                rol.getRol(),
                false
        );
    }

    @Override
    @Transactional
    public UsuarioResponseDTO cambiarPassword(String username, CambiarPasswordDTO dto) {

        // Validaciones más claras
        if (!dto.getPasswordNueva().equals(dto.getPasswordNuevaConfirmar())) {
            throw new DatosInvalidosException("Las contraseñas nuevas no coinciden");
        }

        if (dto.getPasswordNueva() == null || dto.getPasswordNueva().trim().isEmpty()) {
            throw new DatosInvalidosException("passwordNueva", "no puede estar vacía");
        }

        if (dto.getPasswordNueva().length() < 6) {
            throw new DatosInvalidosException("passwordNueva",
                    "debe tener al menos 6 caracteres");
        }

        //  Usar método helper
        Usuario usuario = obtenerUsuarioOError(username);

        // Validar contraseña actual
        if (!usuario.getContrasena().equals(dto.getPasswordActual())) {
            throw new DatosInvalidosException("La contraseña actual es incorrecta");
        }

        usuario.setContrasena(dto.getPasswordNueva());
        usuarioRepository.save(usuario);

        UsuarioRol usuarioRol = obtenerUsuarioRolOError(username);
        Rol rol = rolRepository.findById(usuarioRol.getIdRol())
                .orElseThrow(() -> new RecursoNoEncontradoException("Rol con ID",
                        usuarioRol.getIdRol().toString()));

        return new UsuarioResponseDTO(
                usuario.getUsuario(),
                usuario.getNombre(),
                usuario.getApellidos(),
                rol.getRol(),
                "Contraseña actualizada exitosamente"
        );
    }
}