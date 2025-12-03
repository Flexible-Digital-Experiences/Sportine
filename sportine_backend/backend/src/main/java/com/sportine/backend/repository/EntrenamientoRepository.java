package com.sportine.backend.repository;

import com.sportine.backend.model.Entrenamiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EntrenamientoRepository extends JpaRepository<Entrenamiento, Integer> {

    // ==========================================
    // MÉTODOS ORIGINALES (no tocar)
    // ==========================================

    List<Entrenamiento> findByUsuarioAndFechaEntrenamiento(String usuario, LocalDate fechaEntrenamiento);

    List<Entrenamiento> findByUsuario(String usuario);

    List<Entrenamiento> findByUsuarioEntrenador(String usuarioEntrenador);

    List<Entrenamiento> findByUsuarioOrderByFechaEntrenamientoDesc(String usuario);

    // ==========================================
    // QUERY NATIVA para el home del alumno
    // ==========================================

    @Query(value = "SELECT * FROM entrenamiento " +
            "WHERE usuario = :usuario " +
            "AND fecha_entrenamiento = :fecha " +
            "AND estado_entrenamiento != 'finalizado' " +
            "ORDER BY hora_entrenamiento ASC",
            nativeQuery = true)
    List<Entrenamiento> findEntrenamientosDelDia(
            @Param("usuario") String usuario,
            @Param("fecha") String fecha
    );

    // ==========================================
    // QUERY para contar entrenamientos
    // ==========================================

    Long countByUsuario(String usuario);

    Long countByUsuarioEntrenador(String usuarioEntrenador);

    // ==========================================
    // QUERIES PARA ESTADÍSTICAS
    // ==========================================

    /**
     * Contar entrenamientos completados (finalizados) de un alumno
     */
    @Query("SELECT COUNT(e) FROM Entrenamiento e " +
            "WHERE e.usuario = :usuario " +
            "AND e.estadoEntrenamiento = com.sportine.backend.model.Entrenamiento$EstadoEntrenamiento.finalizado")
    Long countCompletadosByUsuario(@Param("usuario") String usuario);

    /**
     * Contar entrenamientos por deporte de un alumno
     */
    @Query("SELECT COUNT(e) FROM Entrenamiento e " +
            "WHERE e.usuario = :usuario " +
            "AND e.idDeporte = :idDeporte " +
            "AND e.estadoEntrenamiento = com.sportine.backend.model.Entrenamiento$EstadoEntrenamiento.finalizado")
    Long countByUsuarioAndIdDeporte(
            @Param("usuario") String usuario,
            @Param("idDeporte") Integer idDeporte
    );

    /**
     * Obtener entrenamientos completados ordenados por fecha (para calcular rachas)
     */
    @Query("SELECT e FROM Entrenamiento e " +
            "WHERE e.usuario = :usuario " +
            "AND e.estadoEntrenamiento = com.sportine.backend.model.Entrenamiento$EstadoEntrenamiento.finalizado " +
            "ORDER BY e.fechaEntrenamiento DESC")
    List<Entrenamiento> findCompletadosByUsuarioOrderByFecha(@Param("usuario") String usuario);

    /**
     * Encontrar todos los deportes únicos que ha entrenado un alumno
     */
    @Query("SELECT DISTINCT e.idDeporte FROM Entrenamiento e " +
            "WHERE e.usuario = :usuario " +
            "AND e.idDeporte IS NOT NULL " +
            "AND e.estadoEntrenamiento = com.sportine.backend.model.Entrenamiento$EstadoEntrenamiento.finalizado")
    List<Integer> findDistinctDeportesByUsuario(@Param("usuario") String usuario);

    /**
     * Contar entrenamientos completados del alumno con un entrenador específico
     */
    @Query("SELECT COUNT(e) FROM Entrenamiento e " +
            "WHERE e.usuario = :usuario " +
            "AND e.usuarioEntrenador = :usuarioEntrenador " +
            "AND e.estadoEntrenamiento = com.sportine.backend.model.Entrenamiento$EstadoEntrenamiento.finalizado")
    Long countByUsuarioAndUsuarioEntrenador(
            @Param("usuario") String usuario,
            @Param("usuarioEntrenador") String usuarioEntrenador
    );
}