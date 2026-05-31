package com.sportine.backend.service.impl;

import com.sportine.backend.dto.*;
import com.sportine.backend.exception.ConflictoException;
import com.sportine.backend.exception.DatosInvalidosException;
import com.sportine.backend.exception.RecursoNoEncontradoException;
import com.sportine.backend.model.*;
import com.sportine.backend.repository.*;
import com.sportine.backend.service.JwtService;
import com.sportine.backend.service.UsuarioService;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final UsuarioRolRepository usuarioRolRepository;
    private final JwtService jwtService;
    private final EstadoRepository estadoRepository;
    private final InformacionAlumnoRepository informacionAlumnoRepository;
    private final InformacionEntrenadorRepository informacionEntrenadorRepository;

    // ✅ SEGURIDAD: inyectado desde ApplicationConfig (BCryptPasswordEncoder)
    private final PasswordEncoder passwordEncoder;

    // ============================================
    // MÉTODOS HELPER PRIVADOS (EVITA REPETICIÓN)
    // ============================================

    private Usuario obtenerUsuarioOError(String username) {
        return usuarioRepository.findByUsuario(username)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario", username));
    }

    private Rol obtenerRolOError(String nombreRol) {
        return rolRepository.findByRol(nombreRol)
                .orElseThrow(() -> new RecursoNoEncontradoException("Rol", nombreRol));
    }

    private UsuarioRol obtenerUsuarioRolOError(String username) {
        return usuarioRolRepository.findByUsuario(username)
                .orElseThrow(() -> new RecursoNoEncontradoException("Rol del usuario", username));
    }

    private String obtenerFotoPerfil(String username) {
        Optional<InformacionAlumno> alumno = informacionAlumnoRepository.findByUsuario(username);
        if (alumno.isPresent() && alumno.get().getFotoPerfil() != null && !alumno.get().getFotoPerfil().isEmpty()) {
            return alumno.get().getFotoPerfil();
        }
        Optional<InformacionEntrenador> entrenador = informacionEntrenadorRepository.findByUsuario(username);
        if (entrenador.isPresent() && entrenador.get().getFotoPerfil() != null && !entrenador.get().getFotoPerfil().isEmpty()) {
            return entrenador.get().getFotoPerfil();
        }
        return null;
    }

    // ============================================
    // MÉTODOS DEL SERVICIO
    // ============================================

    @Override
    @Transactional
    public UsuarioResponseDTO registrarUsuario(UsuarioRegistroDTO dto) {

        if (usuarioRepository.existsByUsuario(dto.getUsuario())) {
            throw new ConflictoException("Usuario", dto.getUsuario());
        }

        Usuario usuario = new Usuario();
        usuario.setUsuario(dto.getUsuario());
        // ✅ SEGURIDAD: hashear antes de guardar
        usuario.setContrasena(passwordEncoder.encode(dto.getContrasena()));
        usuario.setNombre(dto.getNombre());
        usuario.setApellidos(dto.getApellidos());
        usuario.setSexo(dto.getSexo());
        usuario.setIdEstado(dto.getIdEstado());
        usuario.setCiudad(dto.getCiudad());
        usuario.setCorreo(dto.getCorreo());
        usuarioRepository.save(usuario);

        Rol rol = obtenerRolOError(dto.getRol());

        UsuarioRol usuarioRol = new UsuarioRol();
        usuarioRol.setUsuario(dto.getUsuario());
        usuarioRol.setIdRol(rol.getIdRol());
        usuarioRolRepository.save(usuarioRol);

        return new UsuarioResponseDTO(
                usuario.getUsuario(),
                usuario.getNombre(),
                usuario.getApellidos(),
                usuario.getCorreo(),
                dto.getRol(),
                "Usuario registrado exitosamente"
        );
    }

    @Override
    public UsuarioDetalleDTO obtenerUsuarioPorUsername(String username) {

        Usuario usuario = obtenerUsuarioOError(username);
        UsuarioRol usuarioRol = obtenerUsuarioRolOError(username);

        Rol rol = rolRepository.findById(usuarioRol.getIdRol())
                .orElseThrow(() -> new RecursoNoEncontradoException("Rol con ID",
                        usuarioRol.getIdRol().toString()));

        Estado estado = estadoRepository.findById(usuario.getIdEstado()).orElse(null);
        String nombreEstado = estado != null ? estado.getEstado() : "";
        String fotoPerfil = obtenerFotoPerfil(username);

        return new UsuarioDetalleDTO(
                usuario.getUsuario(),
                usuario.getNombre(),
                usuario.getApellidos(),
                usuario.getSexo(),
                usuario.getCorreo(),
                nombreEstado,
                usuario.getCiudad(),
                rol.getRol(),
                false,
                fotoPerfil
        );
    }

    @Override
    public LoginResponseDTO login(LoginRequestDTO dto) {

        Usuario usuario = usuarioRepository.findByUsuario(dto.getUsuario()).orElse(null);

        if (usuario == null) {
            return new LoginResponseDTO(false, "Usuario no encontrado",
                    null, null, null, null, null, null, null, null);
        }

        // ✅ SEGURIDAD: BCrypt en lugar de .equals() en texto plano
        if (!passwordEncoder.matches(dto.getContrasena(), usuario.getContrasena())) {
            return new LoginResponseDTO(false, "Contraseña incorrecta",
                    null, null, null, null, null, null, null, null);
        }

        UsuarioRol usuarioRol = usuarioRolRepository.findByUsuario(dto.getUsuario()).orElse(null);
        String rolNombre = "";
        if (usuarioRol != null) {
            Rol rol = rolRepository.findById(usuarioRol.getIdRol()).orElse(null);
            rolNombre = rol != null ? rol.getRol() : "";
        }

        if ("ELIMINADO".equals(rolNombre)) {
            return new LoginResponseDTO(false, "Esta cuenta ha sido eliminada",
                    null, null, null, null, null, null, null, null);
        }

        Estado estado = estadoRepository.findById(usuario.getIdEstado()).orElse(null);
        String nombreEstado = estado != null ? estado.getEstado() : "";
        String token = jwtService.generateToken(usuario.getUsuario());

        return new LoginResponseDTO(
                true, "Login exitoso", token,
                usuario.getUsuario(), usuario.getNombre(), usuario.getApellidos(),
                rolNombre, usuario.getSexo(), nombreEstado, usuario.getCiudad()
        );
    }

    @Override
    @Transactional
    public void actualizarDatosUsuario(String username, ActualizarUsuarioDTO dto) {

        System.out.println("=== ACTUALIZANDO DATOS DE USUARIO (PARCIAL) ===");
        System.out.println("Username (NO MODIFICABLE): " + username);
        System.out.println("Datos recibidos: " + dto);

        Usuario usuario = obtenerUsuarioOError(username);
        System.out.println("✓ Usuario encontrado: " + usuario.getNombre());

        boolean huboActualizacion = false;

        if (dto.getNombre() != null && !dto.getNombre().trim().isEmpty()) {
            System.out.println("✓ Actualizando nombre: " + dto.getNombre());
            usuario.setNombre(dto.getNombre());
            huboActualizacion = true;
        }

        if (dto.getApellidos() != null && !dto.getApellidos().trim().isEmpty()) {
            System.out.println("✓ Actualizando apellidos: " + dto.getApellidos());
            usuario.setApellidos(dto.getApellidos());
            huboActualizacion = true;
        }

        if (dto.getSexo() != null && !dto.getSexo().trim().isEmpty()) {
            System.out.println("✓ Actualizando sexo: " + dto.getSexo());
            usuario.setSexo(dto.getSexo());
            huboActualizacion = true;
        }

        if (dto.getEstado() != null && !dto.getEstado().trim().isEmpty()) {
            Estado estado = estadoRepository.findByEstado(dto.getEstado())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Estado", dto.getEstado()));
            System.out.println("✓ Actualizando estado: " + dto.getEstado() + " (ID: " + estado.getIdEstado() + ")");
            usuario.setIdEstado(estado.getIdEstado());
            huboActualizacion = true;
        }

        if (dto.getCiudad() != null && !dto.getCiudad().trim().isEmpty()) {
            System.out.println("✓ Actualizando ciudad: " + dto.getCiudad());
            usuario.setCiudad(dto.getCiudad());
            huboActualizacion = true;
        }

        if (dto.getPassword() != null && !dto.getPassword().trim().isEmpty()) {
            System.out.println("✓ Actualizando contraseña");
            // ✅ SEGURIDAD: hashear al actualizar desde el perfil
            usuario.setContrasena(passwordEncoder.encode(dto.getPassword()));
            huboActualizacion = true;
        }

        if (dto.getCorreo() != null && !dto.getCorreo().trim().isEmpty()) {
            System.out.println("✓ Actualizando correo");
            usuario.setCorreo(dto.getCorreo());
            huboActualizacion = true;
        }

        if (huboActualizacion) {
            usuarioRepository.save(usuario);
            System.out.println("✓✓✓ Usuario actualizado correctamente");
            System.out.println("    (El username NO fue modificado - PRIMARY KEY)");
        } else {
            System.out.println("⚠ No se enviaron campos para actualizar");
            throw new DatosInvalidosException("No se proporcionaron datos para actualizar");
        }

        System.out.println("=== FIN ACTUALIZACIÓN ===");
    }

    @Override
    @Transactional
    public UsuarioResponseDTO cambiarPassword(String username, CambiarPasswordDTO dto) {

        if (!dto.getPasswordNueva().equals(dto.getPasswordNuevaConfirmar())) {
            throw new DatosInvalidosException("Las contraseñas nuevas no coinciden");
        }

        if (dto.getPasswordNueva() == null || dto.getPasswordNueva().trim().isEmpty()) {
            throw new DatosInvalidosException("passwordNueva", "no puede estar vacía");
        }

        if (dto.getPasswordNueva().length() < 6) {
            throw new DatosInvalidosException("passwordNueva", "debe tener al menos 6 caracteres");
        }

        Usuario usuario = obtenerUsuarioOError(username);

        // ✅ SEGURIDAD: verificar contraseña actual con BCrypt
        if (!passwordEncoder.matches(dto.getPasswordActual(), usuario.getContrasena())) {
            throw new DatosInvalidosException("La contraseña actual es incorrecta");
        }

        // ✅ SEGURIDAD: guardar nueva contraseña hasheada
        usuario.setContrasena(passwordEncoder.encode(dto.getPasswordNueva()));
        usuarioRepository.save(usuario);

        UsuarioRol usuarioRol = obtenerUsuarioRolOError(username);
        Rol rol = rolRepository.findById(usuarioRol.getIdRol())
                .orElseThrow(() -> new RecursoNoEncontradoException("Rol con ID",
                        usuarioRol.getIdRol().toString()));

        return new UsuarioResponseDTO(
                usuario.getUsuario(),
                usuario.getNombre(),
                usuario.getApellidos(),
                usuario.getCorreo(),
                rol.getRol(),
                "Contraseña actualizada exitosamente"
        );
    }
}