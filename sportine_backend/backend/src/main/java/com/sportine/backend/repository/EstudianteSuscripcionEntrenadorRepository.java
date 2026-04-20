package com.sportine.backend.repository;

import com.sportine.backend.model.EstudianteSuscripcionEntrenador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EstudianteSuscripcionEntrenadorRepository
        extends JpaRepository<EstudianteSuscripcionEntrenador, Integer> {

    // ✅ Cuenta alumnos activos desde Entrenador_Alumno (tabla con datos reales)
    // Excluye solo 'finalizado' — activo y pendiente cuentan como alumno
    @Query(value = "SELECT COUNT(*) FROM Entrenador_Alumno " +
            "WHERE usuario_entrenador = :usuario " +
            "AND status_relacion != 'finalizado'",
            nativeQuery = true)
    int contarAlumnosActivos(@Param("usuario") String usuario);
}