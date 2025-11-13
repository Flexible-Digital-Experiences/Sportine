package com.sportine.backend.service.impl;

import com.sportine.backend.dto.UsuarioRegistroDTO;
import com.sportine.backend.dto.UsuarioResponseDTO;
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
}