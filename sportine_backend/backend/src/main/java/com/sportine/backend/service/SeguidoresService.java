package com.sportine.backend.service;

import com.sportine.backend.dto.UsuarioDetalleDTO;
import java.util.List;

public interface SeguidoresService {

    String toggleSeguirUsuario(String miUsuario, String usuarioObjetivo);

    boolean loSigo(String miUsuario, String usuarioObjetivo);

    List<UsuarioDetalleDTO> buscarPersonas(String query, String miUsuario);

    List<UsuarioDetalleDTO> obtenerMisAmigos(String miUsuario);
}