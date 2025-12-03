package com.sportine.backend.repository;

import com.sportine.backend.model.ProgresoEntrenamiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
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
     */
    Optional<ProgresoEntrenamiento> findByIdEntrenamientoAndUsuario(
            Integer idEntrenamiento,
            String usuario
    );

    /**
     * Obtener todos los progresos de un alumno
     */
    List<ProgresoEntrenamiento> findByUsuario(String usuario);

    /**
     * Obtener entrenamientos completados de un alumno
     */
    List<ProgresoEntrenamiento> findByUsuarioAndCompletado(
            String usuario,
            Boolean completado
    );

    /**
     * Verificar si un entrenamiento ya tiene progreso registrado
     */
    boolean existsByIdEntrenamientoAndUsuario(
            Integer idEntrenamiento,
            String usuario
    );

    /**
     * Contar entrenamientos completados en un rango de fechas
     */
    @Query("SELECT COUNT(p) FROM ProgresoEntrenamiento p " +
            "WHERE p.usuario = :usuario " +
            "AND p.completado = true " +
            "AND p.fechaFinalizacion BETWEEN :fechaInicio AND :fechaFin")
    Long contarEntrenamientosCompletadosEnRango(
            @Param("usuario") String usuario,
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin
    );

    /**
     * Obtener progresos completados ordenados por fecha de finalización (para rachas)
     */
    @Query("SELECT p FROM ProgresoEntrenamiento p " +
            "WHERE p.usuario = :usuario " +
            "AND p.completado = true " +
            "ORDER BY p.fechaFinalizacion DESC")
    List<ProgresoEntrenamiento> findCompletadosOrderByFecha(@Param("usuario") String usuario);

    /**
     * Contar entrenamientos completados totales de un alumno
     */
    @Query("SELECT COUNT(p) FROM ProgresoEntrenamiento p " +
            "WHERE p.usuario = :usuario " +
            "AND p.completado = true")
    Long countCompletadosByUsuario(@Param("usuario") String usuario);

    /**
     * Obtener el progreso más reciente de un alumno
     */
    @Query("SELECT p FROM ProgresoEntrenamiento p " +
            "WHERE p.usuario = :usuario " +
            "ORDER BY p.fechaFinalizacion DESC")
    List<ProgresoEntrenamiento> findTopByUsuarioOrderByFechaFinalizacionDesc(@Param("usuario") String usuario);

    /**
     * Verificar si el alumno entrenó en una fecha específica
     */
    @Query("SELECT COUNT(p) > 0 FROM ProgresoEntrenamiento p " +
            "WHERE p.usuario = :usuario " +
            "AND p.completado = true " +
            "AND DATE(p.fechaFinalizacion) = :fecha")
    boolean existsCompletadoEnFecha(
            @Param("usuario") String usuario,
            @Param("fecha") LocalDate fecha
    );

    /**
     * Contar días únicos con entrenamientos completados
     */
    @Query(value = "SELECT COUNT(DISTINCT DATE(fecha_finalizacion)) " +
            "FROM Progreso_Entrenamiento " +
            "WHERE usuario = :usuario " +
            "AND completado = true",
            nativeQuery = true)
    Long contarDiasUnicos(@Param("usuario") String usuario);
}