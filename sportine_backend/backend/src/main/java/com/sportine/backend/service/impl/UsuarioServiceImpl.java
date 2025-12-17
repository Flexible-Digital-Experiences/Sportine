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

    // ✅ NUEVO: Repositorios para buscar la foto de perfil
    private final InformacionAlumnoRepository informacionAlumnoRepository;
    private final InformacionEntrenadorRepository informacionEntrenadorRepository;

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

    // ✅ NUEVO: Método helper para obtener la foto de perfil
    private String obtenerFotoPerfil(String username) {
        // 1. Buscar en Alumnos
        Optional<InformacionAlumno> alumno = informacionAlumnoRepository.findByUsuario(username);
        if (alumno.isPresent() && alumno.get().getFotoPerfil() != null && !alumno.get().getFotoPerfil().isEmpty()) {
            return alumno.get().getFotoPerfil();
        }

        // 2. Si no, buscar en Entrenadores
        Optional<InformacionEntrenador> entrenador = informacionEntrenadorRepository.findByUsuario(username);
        if (entrenador.isPresent() && entrenador.get().getFotoPerfil() != null && !entrenador.get().getFotoPerfil().isEmpty()) {
            return entrenador.get().getFotoPerfil();
        }

        return null; // Si no tiene foto
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
        usuario.setCorreo(dto.getCorreo());  // ✅ CORRECCIÓN APLICADA
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
                usuario.getCorreo(),
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

        // ✅ CORRECCIÓN: Obtenemos la foto usando el helper
        String fotoPerfil = obtenerFotoPerfil(username);

        // ✅ CORRECCIÓN: Agregamos fotoPerfil al final del constructor
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

    /**
     * Actualiza datos del usuario de forma PARCIAL
     * Solo actualiza los campos que vienen en el DTO (no nulos)
     * ❌ NO actualiza el username (PRIMARY KEY)
     */
    @Override
    @Transactional
    public void actualizarDatosUsuario(String username, ActualizarUsuarioDTO dto) {

        System.out.println("=== ACTUALIZANDO DATOS DE USUARIO (PARCIAL) ===");
        System.out.println("Username (NO MODIFICABLE): " + username);
        System.out.println("Datos recibidos: " + dto);

        // 1. Buscar usuario
        Usuario usuario = obtenerUsuarioOError(username);

        System.out.println("✓ Usuario encontrado: " + usuario.getNombre());

        boolean huboActualizacion = false;

        // 2. Actualizar solo los campos que no sean null
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
            // Buscar el ID del estado por nombre
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
            // TODO: Encriptar contraseña
            // usuario.setContrasena(passwordEncoder.encode(dto.getPassword()));
            usuario.setContrasena(dto.getPassword()); // Por ahora sin encriptar
            huboActualizacion = true;
        }

        if(dto.getCorreo() != null && !dto.getCorreo().trim().isEmpty()){
            System.out.println("✓ Actualizando correo");
            usuario.setCorreo(dto.getCorreo());
            huboActualizacion = true;
        }

        // 3. Guardar si hubo cambios
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
                usuario.getCorreo(),
                rol.getRol(),
                "Contraseña actualizada exitosamente"
        );
    }
}