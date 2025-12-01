package com.sportine.backend.repository;

import com.sportine.backend.model.Calificaciones;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CalificacionesRepository extends JpaRepository<Calificaciones, Integer> {

    // Verificar si un alumno ya calificó a un entrenador
    boolean existsByUsuarioAndUsuarioCalificado(String usuario, String usuarioCalificado);

    // Obtener calificación de un alumno específico a un entrenador
    Optional<Calificaciones> findByUsuarioAndUsuarioCalificado(String usuario, String usuarioCalificado);
}
