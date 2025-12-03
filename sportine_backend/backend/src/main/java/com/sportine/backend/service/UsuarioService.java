package com.sportine.backend.service;


import com.sportine.backend.dto.*;

public interface UsuarioService {

    UsuarioResponseDTO registrarUsuario(UsuarioRegistroDTO usuarioRegistroDTO);

    UsuarioDetalleDTO obtenerUsuarioPorUsername(String usuario);

    LoginResponseDTO login(LoginRequestDTO loginRequestDTO);

    UsuarioResponseDTO cambiarPassword(String usuario, CambiarPasswordDTO dto);

    /**
     * Actualiza datos del usuario de forma PARCIAL
     * Solo actualiza los campos que vienen en el DTO
     * ❌ NO actualiza el username (PRIMARY KEY)
     */
    void actualizarDatosUsuario(String username, ActualizarUsuarioDTO dto);
}
