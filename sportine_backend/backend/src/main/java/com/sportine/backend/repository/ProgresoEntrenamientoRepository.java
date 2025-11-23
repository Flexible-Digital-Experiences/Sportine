package com.sportine.backend.repository;

import com.sportine.backend.model.ProgresoEntrenamiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository para manejar el progreso de entrenamientos.
 * Registra cuándo el alumno inicia y completa cada entrenamiento.
 */
@Repository
public interface ProgresoEntrenamientoRepository extends JpaRepository<ProgresoEntrenamiento, Integer> {

    /**
     * Buscar progreso por entrenamiento y usuario
     * @param idEntrenamiento ID del entrenamiento
     * @param usuario Username del alumno
     * @return Progreso del entrenamiento
     */
    Optional<ProgresoEntrenamiento> findByIdEntrenamientoAndUsuario(
            Integer idEntrenamiento,
            String usuario
    );

    /**
     * Obtener todos los progresos de un alumno
     * @param usuario Username del alumno
     * @return Lista de progresos
     */
    List<ProgresoEntrenamiento> findByUsuario(String usuario);

    /**
     * Obtener entrenamientos completados de un alumno
     * @param usuario Username del alumno
     * @param completado true para completados, false para no completados
     * @return Lista de progresos
     */
    List<ProgresoEntrenamiento> findByUsuarioAndCompletado(
            String usuario,
            Boolean completado
    );

    /**
     * Verificar si un entrenamiento ya tiene progreso registrado
     * @param idEntrenamiento ID del entrenamiento
     * @param usuario Username del alumno
     * @return true si existe progreso
     */
    boolean existsByIdEntrenamientoAndUsuario(
            Integer idEntrenamiento,
            String usuario
    );

    /**
     * Contar entrenamientos completados en un rango de fechas
     * @param usuario Username del alumno
     * @param fechaInicio Fecha de inicio del rango
     * @param fechaFin Fecha final del rango
     * @return Número de entrenamientos completados
     */
    @Query("SELECT COUNT(p) FROM ProgresoEntrenamiento p " +
            "WHERE p.usuario = :usuario " +
            "AND p.completado = true " +
            "AND p.fechaFinalizacion BETWEEN :fechaInicio AND :fechaFin")
    int contarEntrenamientosCompletadosEnRango(
            @Param("usuario") String usuario,
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin
    );
}