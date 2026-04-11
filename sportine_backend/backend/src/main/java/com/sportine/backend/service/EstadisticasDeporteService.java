// ── EstadisticasDeporteService.java ──────────────────────────────────────────
package com.sportine.backend.service;

import com.sportine.backend.dto.CarreraDeporteDTO;
import com.sportine.backend.dto.MetricasUltimosDTO;

public interface EstadisticasDeporteService {

    /** 3 cards con acumulados históricos del alumno en el deporte */
    CarreraDeporteDTO obtenerCarreraDeporte(String usuario, Integer idDeporte);

    /** Evolución de métricas en los últimos N entrenamientos */
    MetricasUltimosDTO obtenerMetricasUltimos(String usuario, Integer idDeporte, int limite);
}