package com.sportine.backend.service;

import com.sportine.backend.dto.ActualizarDatosAlumnoDTO;
import com.sportine.backend.dto.PerfilAlumnoDTO;
import com.sportine.backend.dto.PerfilAlumnoResponseDTO;
import com.sportine.backend.dto.TarjetaDTO;
import com.sportine.backend.dto.TarjetaResponseDTO;

import java.util.List;

public interface AlumnoPerfilService {

    // ========================================
    // MÉTODOS DE PERFIL
    // ========================================

    /**
     * Obtiene el perfil completo del alumno
     */
    PerfilAlumnoResponseDTO obtenerPerfilAlumno(String usuario);

    /**
     * Crea el perfil inicial del alumno
     */
    PerfilAlumnoResponseDTO crearPerfilAlumno(PerfilAlumnoDTO perfilAlumnoDTO);

    /**
     * Actualiza el perfil completo del alumno (requiere todos los campos)
     */
    PerfilAlumnoResponseDTO actualizarPerfilAlumno(String usuario, PerfilAlumnoDTO perfilAlumnoDTO);

    /**
     * 🆕 Actualiza datos específicos del alumno de forma parcial
     * Solo actualiza los campos que vienen en el DTO (no nulos)
     */
    void actualizarDatosAlumno(String usuario, ActualizarDatosAlumnoDTO datosDTO);

    // ========================================
    // MÉTODOS DE TARJETAS
    // ========================================

    /**
     * Agrega una nueva tarjeta al alumno
     */
    TarjetaResponseDTO agregarTarjeta(String usuario, TarjetaDTO tarjetaDTO);

    /**
     * Obtiene todas las tarjetas del alumno
     */
    List<TarjetaResponseDTO> obtenerTarjetas(String usuario);

    /**
     * Obtiene una tarjeta específica por su ID
     */
    TarjetaResponseDTO obtenerTarjetaPorId(String usuario, Integer idTarjeta);

    /**
     * Actualiza una tarjeta existente
     */
    TarjetaResponseDTO actualizarTarjeta(String usuario, Integer idTarjeta, TarjetaDTO tarjetaDTO);

    /**
     * Elimina una tarjeta
     */
    void eliminarTarjeta(String usuario, Integer idTarjeta);
}