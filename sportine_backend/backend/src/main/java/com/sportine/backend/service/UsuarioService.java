package com.sportine.backend.service;


import com.sportine.backend.dto.*;

public interface UsuarioService {

    UsuarioResponseDTO registrarUsuario(UsuarioRegistroDTO usuarioRegistroDTO);

    UsuarioDetalleDTO obtenerUsuarioPorUsername(String usuario);

    LoginResponseDTO login(LoginRequestDTO loginRequestDTO);
}
