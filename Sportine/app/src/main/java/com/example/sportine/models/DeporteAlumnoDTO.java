// ── DeporteAlumnoDTO.java (Android) ──────────────────────────────────────────
// Para el filtro de chips — lista de deportes que tiene el alumno
package com.example.sportine.models;

import com.google.gson.annotations.SerializedName;

public class DeporteAlumnoDTO {

    @SerializedName("id_deporte")    private Integer idDeporte;
    @SerializedName("nombre_deporte") private String nombreDeporte;

    public Integer getIdDeporte() { return idDeporte; }
    public String getNombreDeporte() { return nombreDeporte; }

    public String getEmoji() {
        if (nombreDeporte == null) return "🏅";
        switch (nombreDeporte.toLowerCase()) {
            case "fútbol":    return "⚽";
            case "basketball":return "🏀";
            case "natación":  return "🏊";
            case "running":   return "🏃";
            case "boxeo":     return "🥊";
            case "tenis":     return "🎾";
            case "gimnasio":  return "💪";
            case "ciclismo":  return "🚴";
            case "béisbol":   return "⚾";
            default:          return "🏅";
        }
    }
}