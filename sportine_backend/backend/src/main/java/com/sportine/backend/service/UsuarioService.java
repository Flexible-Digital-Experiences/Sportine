package com.sportine.backend.service;


import com.sportine.backend.dto.UsuarioDetalleDTO;
import com.sportine.backend.dto.UsuarioRegistroDTO;
import com.sportine.backend.dto.UsuarioResponseDTO;

public interface UsuarioService {

    UsuarioResponseDTO registrarUsuario(UsuarioRegistroDTO usuarioRegistroDTO);

    UsuarioDetalleDTO obtenerUsuarioPorUsername(String usuario);
}
