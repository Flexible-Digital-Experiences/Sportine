package com.sportine.backend.repository;

import com.sportine.backend.model.ResultadoSeriesEjercicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResultadoSeriesEjercicioRepository
        extends JpaRepository<ResultadoSeriesEjercicio, Integer> {

    List<ResultadoSeriesEjercicio> findByIdAsignadoOrderByNumeroSerieAsc(Integer idAsignado);

    Optional<ResultadoSeriesEjercicio> findByIdAsignadoAndNumeroSerie(
            Integer idAsignado, Integer numeroSerie);

    // Contar series completadas o parciales de un ejercicio
    @Query("SELECT COUNT(r) FROM ResultadoSeriesEjercicio r " +
            "WHERE r.idAsignado = :idAsignado " +
            "AND r.status IN ('completado', 'parcial')")
    Long contarSeriesCompletadasOParciales(@Param("idAsignado") Integer idAsignado);

    // Eliminar todas las series de un ejercicio (por si se reasigna)
    @Modifying
    @Transactional
    void deleteByIdAsignado(Integer idAsignado);
}