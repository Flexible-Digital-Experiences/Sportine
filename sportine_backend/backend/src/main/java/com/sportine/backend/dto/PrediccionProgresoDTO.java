package com.sportine.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * DTO de respuesta para GET /prediccion-progreso/{usuario}?id_deporte=X&dias=30 de FastAPI.
 *
 * Refleja el schema Pydantic PrediccionProgresoResponse.
 *
 * Estructura del JSON de FastAPI:
 * {
 *   "usuario": "alumno_test",
 *   "id_deporte": 1,
 *   "predicciones": [
 *     {
 *       "nombre_metrica": "goles",
 *       "prediccion": {
 *         "tendencia_por_dia": 0.1257,
 *         "r2_confianza": 0.881,
 *         "proyeccion_30_dias": [
 *           { "dia": 1, "valor_proyectado": 12.4 },
 *           ...
 *         ]
 *       },
 *       "mejor_sesion_actual": 5.0,
 *       "dias_para_superar_record": 12,
 *       "mensaje": "Tendencia positiva con alta confianza."
 *     }
 *   ]
 * }
 */
@Data
public class PrediccionProgresoDTO {

    private String usuario;

    /** ID del deporte analizado. En JSON: "id_deporte". */
    @JsonProperty("id_deporte")
    private Integer idDeporte;

    /** Lista de predicciones, una por métrica disponible del deporte. */
    private List<ResultadoMetrica> predicciones;

    // ─────────────────────────────────────────────────────────────
    // Nivel 2 — Una predicción por métrica
    // ─────────────────────────────────────────────────────────────

    @Data
    public static class ResultadoMetrica {

        /** Nombre interno de la métrica, ej: "goles". En JSON: "nombre_metrica". */
        @JsonProperty("nombre_metrica")
        private String nombreMetrica;

        /** Datos de la proyección lineal. */
        private PrediccionMetrica prediccion;

        /** Mejor valor registrado en una sola sesión. Null si no hay datos. En JSON: "mejor_sesion_actual". */
        @JsonProperty("mejor_sesion_actual")
        private Double mejorSesionActual;

        /** Días estimados para superar el récord. Null si ya lo superó o no hay datos. En JSON: "dias_para_superar_record". */
        @JsonProperty("dias_para_superar_record")
        private Integer diasParaSuperarRecord;

        /** Mensaje interpretativo del algoritmo. */
        private String mensaje;
    }

    // ─────────────────────────────────────────────────────────────
    // Nivel 3 — Datos de la regresión lineal
    // ─────────────────────────────────────────────────────────────

    @Data
    public static class PrediccionMetrica {

        /** Mejora o deterioro promedio por día. En JSON: "tendencia_por_dia". */
        @JsonProperty("tendencia_por_dia")
        private Double tendenciaPorDia;

        /**
         * Coeficiente de determinación R² del modelo.
         * 0.0 = sin ajuste, 1.0 = ajuste perfecto. >0.7 se considera bueno.
         * En JSON: "r2_confianza".
         */
        @JsonProperty("r2_confianza")
        private Double r2Confianza;

        /**
         * Puntos de proyección para graficar la línea punteada en Chart.js.
         * El campo se llama "proyeccion_30_dias" independientemente del parámetro dias.
         * En JSON: "proyeccion_30_dias".
         */
        @JsonProperty("proyeccion_30_dias")
        private List<PuntoProyeccion> proyeccion30Dias;
    }

    // ─────────────────────────────────────────────────────────────
    // Nivel 4 — Un punto de la proyección (x, y) para Chart.js
    // ─────────────────────────────────────────────────────────────

    @Data
    public static class PuntoProyeccion {

        /** Número de día futuro (1, 2, 3 ... N). */
        private Integer dia;

        /** Valor proyectado para ese día. En JSON: "valor_proyectado". */
        @JsonProperty("valor_proyectado")
        private Double valorProyectado;
    }
}