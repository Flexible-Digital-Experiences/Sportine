package com.sportine.backend.repository;

import com.sportine.backend.model.FeedbackEntrenamiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * Repository para manejar el feedback de entrenamientos.
 * Permite que el alumno deje comentarios opcionales al completar.
 */
@Repository
public interface FeedbackEntrenamientoRepository extends JpaRepository<FeedbackEntrenamiento, Integer> {

    /**
     * Buscar feedback por entrenamiento
     * @param idEntrenamiento ID del entrenamiento
     * @return Feedback del entrenamiento
     */
    Optional<FeedbackEntrenamiento> findByIdEntrenamiento(Integer idEntrenamiento);

    /**
     * Buscar feedback por entrenamiento y usuario
     * @param idEntrenamiento ID del entrenamiento
     * @param usuario Username del alumno
     * @return Feedback específico
     */
    Optional<FeedbackEntrenamiento> findByIdEntrenamientoAndUsuario(
            Integer idEntrenamiento,
            String usuario
    );

    /**
     * Obtener todos los feedbacks de un alumno
     * @param usuario Username del alumno
     * @return Lista de feedbacks del alumno
     */
    List<FeedbackEntrenamiento> findByUsuario(String usuario);

    /**
     * Verificar si un entrenamiento ya tiene feedback
     * @param idEntrenamiento ID del entrenamiento
     * @param usuario Username del alumno
     * @return true si ya existe feedback
     */
    boolean existsByIdEntrenamientoAndUsuario(
            Integer idEntrenamiento,
            String usuario
    );

    /**
     * Obtener feedbacks de entrenamientos de un alumno específico
     * ordenados por fecha más reciente
     * @param usuario Username del alumno
     * @return Lista de feedbacks ordenados
     */
    List<FeedbackEntrenamiento> findByUsuarioOrderByFechaFeedbackDesc(String usuario);

    /**
     * Calcular promedio de nivel de cansancio de un alumno
     * @param usuario Username del alumno
     * @return Promedio de cansancio (1-10)
     */
    @Query("SELECT AVG(f.nivelCansancio) FROM FeedbackEntrenamiento f WHERE f.usuario = :usuario")
    Double calcularPromedioNivelCansancio(@Param("usuario") String usuario);

    /**
     * Calcular promedio de dificultad percibida de un alumno
     * @param usuario Username del alumno
     * @return Promedio de dificultad (1-10)
     */
    @Query("SELECT AVG(f.dificultadPercibida) FROM FeedbackEntrenamiento f WHERE f.usuario = :usuario")
    Double calcularPromedioDificultadPercibida(@Param("usuario") String usuario);
}