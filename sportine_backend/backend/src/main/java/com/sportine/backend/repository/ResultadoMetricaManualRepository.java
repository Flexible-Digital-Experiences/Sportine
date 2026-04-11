package com.sportine.backend.repository;

import com.sportine.backend.model.ResultadoMetricaManual;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResultadoMetricaManualRepository extends JpaRepository<ResultadoMetricaManual, Integer> {

    // Para buscar una métrica específica de una serie (upsert)
    Optional<ResultadoMetricaManual> findByIdEntrenamientoAndIdPlantillaAndUsuarioAndNumeroSerie(
            Integer idEntrenamiento,
            Integer idPlantilla,
            String usuario,
            Integer numeroSerie
    );

    // Para obtener todas las métricas de un entrenamiento (estadísticas)
    List<ResultadoMetricaManual> findByIdEntrenamientoAndUsuario(Integer idEntrenamiento, String usuario);

    // Para sumar métricas totales por entrenamiento y plantilla (para el agente n8n)
    List<ResultadoMetricaManual> findByIdEntrenamientoAndIdPlantillaAndUsuario(
            Integer idEntrenamiento,
            Integer idPlantilla,
            String usuario
    );
}