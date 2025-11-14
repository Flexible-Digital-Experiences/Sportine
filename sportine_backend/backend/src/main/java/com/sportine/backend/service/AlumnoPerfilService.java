package com.sportine.backend.service;

import com.sportine.backend.dto.PerfilAlumnoDTO;
import com.sportine.backend.dto.PerfilAlumnoResponseDTO;
import com.sportine.backend.dto.TarjetaDTO;
import com.sportine.backend.dto.TarjetaResponseDTO;

import java.util.List;

public interface AlumnoPerfilService {


    PerfilAlumnoResponseDTO crearPerfilAlumno(PerfilAlumnoDTO dto);

    PerfilAlumnoResponseDTO obtenerPerfilAlumno(String usuario);

    PerfilAlumnoResponseDTO actualizarPerfilAlumno(String usuario, PerfilAlumnoDTO dto);



    // ========== MÉTODOS DE TARJETAS (CONFIGURACIÓN) ==========

    TarjetaResponseDTO agregarTarjeta(String usuario, TarjetaDTO dto);

    List<TarjetaResponseDTO> obtenerTarjetas(String usuario);

    TarjetaResponseDTO obtenerTarjetaPorId(String usuario, Integer idTarjeta);

    TarjetaResponseDTO actualizarTarjeta(String usuario, Integer idTarjeta, TarjetaDTO dto);

    void eliminarTarjeta(String usuario, Integer idTarjeta);
}
