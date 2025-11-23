package com.sportine.backend.repository;

import com.sportine.backend.model.Entrenamiento;
import com.sportine.backend.model.Entrenamiento.EstadoEntrenamiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface EntrenamientoRepository extends JpaRepository<Entrenamiento, Integer> {

    // ========== MÉTODOS QUE YA TIENES ==========

    // Buscar entrenamientos por usuario y fecha
    List<Entrenamiento> findByUsuarioAndFechaEntrenamiento(String usuario, LocalDate fecha);

    // Contar entrenamientos por usuario y fecha
    int countByUsuarioAndFechaEntrenamiento(String usuario, LocalDate fecha);

    // ========== MÉTODOS NUEVOS QUE NECESITAS ==========

    /**
     * Busca todos los entrenamientos de un alumno
     * @param usuario Username del alumno
     * @return Lista de entrenamientos del alumno
     */
    List<Entrenamiento> findByUsuario(String usuario);

    /**
     * Busca entrenamientos de un alumno por estado
     * @param usuario Username del alumno
     * @param estado Estado del entrenamiento
     * @return Lista de entrenamientos filtrados
     */
    List<Entrenamiento> findByUsuarioAndEstadoEntrenamiento(String usuario, EstadoEntrenamiento estado);

    /**
     * Busca entrenamientos creados por un entrenador
     * @param usuarioEntrenador Username del entrenador
     * @return Lista de entrenamientos creados por el entrenador
     */
    List<Entrenamiento> findByUsuarioEntrenador(String usuarioEntrenador);

    /**
     * Busca entrenamientos de un alumno entre dos fechas
     * @param usuario Username del alumno
     * @param fechaInicio Fecha inicial
     * @param fechaFin Fecha final
     * @return Lista de entrenamientos en ese rango
     */
    List<Entrenamiento> findByUsuarioAndFechaEntrenamientoBetween(
            String usuario,
            LocalDate fechaInicio,
            LocalDate fechaFin
    );
}