package com.sportine.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO de respuesta para POST /api/buscar-entrenadores/recomendar.
 *
 * Contiene todos los campos de EntrenadorCardDTO (aplanados, no anidados)
 * más el score de compatibilidad calculado por FastAPI.
 *
 * Aplanar los campos en lugar de anidar el EntrenadorCardDTO permite que el
 * frontend reutilice exactamente la misma lógica de renderizado que ya tiene
 * para las cards de búsqueda, solo agregando el badge de score.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EntrenadorRecomendadoDTO {

    // ── Campos de EntrenadorCardDTO (iguales, para compatibilidad con el frontend) ──

    private String usuario;
    private String nombreCompleto;
    private String fotoPerfil;
    private Double ratingPromedio;
    private List<String> especialidades;
    private Integer limiteAlumnos;
    private Integer alumnosActuales;

    // ── Campo nuevo: score del algoritmo de IA (0-100) ────────────────────────

    /**
     * Score de compatibilidad calculado por FastAPI (0-100).
     * Considera: coincidencia de deportes (35%), rating (30%),
     * espacios disponibles (20%) y actividad de alumnos actuales (15%).
     */
    private Double scoreCompatibilidad;

    // ── Factory method para construir desde un EntrenadorCardDTO ──────────────

    /**
     * Crea un EntrenadorRecomendadoDTO copiando los datos de una card existente
     * y agregando el score recibido de FastAPI.
     *
     * Uso:
     *   EntrenadorRecomendadoDTO.desde(card, scoreMap.get(card.getUsuario()))
     */
    public static EntrenadorRecomendadoDTO desde(EntrenadorCardDTO card, Double score) {
        EntrenadorRecomendadoDTO dto = new EntrenadorRecomendadoDTO();
        dto.setUsuario(card.getUsuario());
        dto.setNombreCompleto(card.getNombreCompleto());
        dto.setFotoPerfil(card.getFotoPerfil());
        dto.setRatingPromedio(card.getRatingPromedio());
        dto.setEspecialidades(card.getEspecialidades());
        dto.setLimiteAlumnos(card.getLimiteAlumnos());
        dto.setAlumnosActuales(card.getAlumnosActuales());
        dto.setScoreCompatibilidad(score);
        return dto;
    }
}