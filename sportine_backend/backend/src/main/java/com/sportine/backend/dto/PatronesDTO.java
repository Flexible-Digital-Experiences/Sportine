package com.sportine.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * DTO de respuesta para GET /patrones/{usuario} de FastAPI.
 *
 * Refleja el schema Pydantic PatronesResponse.
 * Varios campos pueden ser null si el alumno no tiene datos suficientes.
 */
@Data
public class PatronesDTO {

    private String usuario;

    /**
     * Día de la semana con mayor quema calórica promedio.
     * Ej: "sábado". Null si no hay sesiones con datos HC.
     * En JSON: "mejor_dia_semana".
     */
    @JsonProperty("mejor_dia_semana")
    private String mejorDiaSemana;

    /**
     * Porcentaje de consistencia: días entrenados / días totales del período.
     * Siempre presente (0.0 si no hay datos). En JSON: "indice_consistencia_pct".
     */
    @JsonProperty("indice_consistencia_pct")
    private Double indiceConsistenciaPct;

    /**
     * Correlación de Pearson entre estado de ánimo y calorías quemadas.
     * Rango [-1, 1]. Null si no hay datos suficientes. En JSON: "correlacion_animo_calorias".
     */
    @JsonProperty("correlacion_animo_calorias")
    private Double correlacionAnimoCalorias;

    /**
     * Promedio de días entre sesiones consecutivas.
     * Null si hay menos de 2 sesiones. En JSON: "frecuencia_promedio_dias".
     */
    @JsonProperty("frecuencia_promedio_dias")
    private Double frecuenciaPromedioDias;

    /**
     * Total de sesiones usadas para el análisis. En JSON: "total_sesiones_analizadas".
     */
    @JsonProperty("total_sesiones_analizadas")
    private Integer totalSesionesAnalizadas;

    /**
     * Mensaje adicional del algoritmo (ej: advertencia de datos insuficientes).
     * Puede ser null. En JSON: "mensaje".
     */
    private String mensaje;
}