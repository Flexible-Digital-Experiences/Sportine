package com.sportine.backend.repository;

import com.sportine.backend.model.EjerciciosAsignados;
import com.sportine.backend.model.EjerciciosAsignados.StatusEjercicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EjerciciosAsignadosRepository extends JpaRepository<EjerciciosAsignados, Integer> {

    /**
     * Obtener todos los ejercicios de un entrenamiento ordenados por creación
     */
    List<EjerciciosAsignados> findByIdEntrenamientoOrderByIdAsignadoAsc(Integer idEntrenamiento);

    /**
     * Eliminar ejercicios de un entrenamiento (Necesario para editar/sobrescribir)
     */
    void deleteByIdEntrenamiento(Integer idEntrenamiento);

    int countByIdEntrenamiento(Integer idEntrenamiento);

    int countByIdEntrenamientoAndStatusEjercicio(
            Integer idEntrenamiento,
            StatusEjercicio status
    );

    List<EjerciciosAsignados> findByUsuario(String usuario);
}