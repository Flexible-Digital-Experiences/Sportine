package com.sportine.backend.repository;

import com.sportine.backend.model.EjerciciosAsignados;
import com.sportine.backend.model.EjerciciosAsignados.StatusEjercicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Repository para manejar los ejercicios asignados a entrenamientos.
 * Permite obtener ejercicios de un entrenamiento específico.
 */
@Repository
public interface EjerciciosAsignadosRepository extends JpaRepository<EjerciciosAsignados, Integer> {

    /**
     * Obtener todos los ejercicios de un entrenamiento ordenados
     * @param idEntrenamiento ID del entrenamiento
     * @return Lista de ejercicios ordenados por orden_ejercicio
     */
    List<EjerciciosAsignados> findByIdEntrenamientoOrderByOrdenEjercicioAsc(Integer idEntrenamiento);

    /**
     * Obtener ejercicios de un entrenamiento con un estado específico
     * @param idEntrenamiento ID del entrenamiento
     * @param status Estado del ejercicio
     * @return Lista de ejercicios con ese estado
     */
    List<EjerciciosAsignados> findByIdEntrenamientoAndStatusEjercicio(
            Integer idEntrenamiento,
            StatusEjercicio status
    );

    /**
     * Contar ejercicios de un entrenamiento
     * @param idEntrenamiento ID del entrenamiento
     * @return Total de ejercicios
     */
    int countByIdEntrenamiento(Integer idEntrenamiento);

    /**
     * Contar ejercicios completados de un entrenamiento
     * @param idEntrenamiento ID del entrenamiento
     * @param status Estado (completado)
     * @return Número de ejercicios completados
     */
    int countByIdEntrenamientoAndStatusEjercicio(
            Integer idEntrenamiento,
            StatusEjercicio status
    );

    /**
     * Eliminar todos los ejercicios de un entrenamiento
     * @param idEntrenamiento ID del entrenamiento
     */
    void deleteByIdEntrenamiento(Integer idEntrenamiento);

    /**
     * Obtener ejercicios asignados a un alumno específico
     * @param usuario Username del alumno
     * @return Lista de ejercicios asignados a ese alumno
     */
    List<EjerciciosAsignados> findByUsuario(String usuario);
}