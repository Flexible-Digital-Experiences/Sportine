package com.sportine.backend.repository;

import com.sportine.backend.model.CatalogoEjercicios;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Repository para acceder al catálogo de ejercicios.
 * Permite buscar ejercicios por deporte para que el entrenador los asigne.
 */
@Repository
public interface CatalogoEjerciciosRepository extends JpaRepository<CatalogoEjercicios, Integer> {

    /**
     * Buscar ejercicios por deporte
     * @param deporte Nombre del deporte (ej: "futbol", "basquetbol")
     * @return Lista de ejercicios de ese deporte
     */
    List<CatalogoEjercicios> findByDeporte(String deporte);

    /**
     * Buscar ejercicios por tipo de medida
     * @param tipoMedida Tipo (ej: "repeticiones", "duracion", "distancia")
     * @return Lista de ejercicios de ese tipo
     */
    List<CatalogoEjercicios> findByTipoMedida(String tipoMedida);

    /**
     * Buscar ejercicios por nombre (búsqueda parcial)
     * @param nombre Nombre del ejercicio
     * @return Lista de ejercicios que coincidan
     */
    List<CatalogoEjercicios> findByNombreEjercicioContainingIgnoreCase(String nombre);
}