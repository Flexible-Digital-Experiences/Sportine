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
}