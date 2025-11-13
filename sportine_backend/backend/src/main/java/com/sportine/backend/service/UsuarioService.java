package com.sportine.backend.service;


import com.sportine.backend.dto.UsuarioRegistroDTO;
import com.sportine.backend.dto.UsuarioResponseDTO;

public interface UsuarioService {

    UsuarioResponseDTO registrarUsuario(UsuarioRegistroDTO usuarioRegistroDTO);
}
