package com.sportine.backend.repository;

import com.sportine.backend.model.EntrenadorAlumno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * Repository para manejar las relaciones entre entrenadores y alumnos.
 * Permite obtener los alumnos de un entrenador y viceversa.
 */
@Repository
public interface EntrenadorAlumnoRepository extends JpaRepository<EntrenadorAlumno, Integer> {

    // ==========================================
    // MÉTODOS ORIGINALES (ya existían)
    // ==========================================

    /**
     * Obtener todos los alumnos de un entrenador
     * @param usuarioEntrenador Username del entrenador
     * @return Lista de relaciones (alumnos)
     */
    List<EntrenadorAlumno> findByUsuarioEntrenador(String usuarioEntrenador);

    /**
     * Obtener todos los alumnos ACTIVOS de un entrenador
     * @param usuarioEntrenador Username del entrenador
     * @param statusRelacion Estado de la relación (ej: "activo")
     * @return Lista de relaciones activas
     */
    List<EntrenadorAlumno> findByUsuarioEntrenadorAndStatusRelacion(
            String usuarioEntrenador,
            String statusRelacion
    );

    /**
     * Obtener el entrenador de un alumno
     * @param usuarioAlumno Username del alumno
     * @return Relación con el entrenador
     */
    Optional<EntrenadorAlumno> findByUsuarioAlumnoAndStatusRelacion(
            String usuarioAlumno,
            String statusRelacion
    );

    /**
     * Verificar si existe una relación activa entre entrenador y alumno
     * @param usuarioEntrenador Username del entrenador
     * @param usuarioAlumno Username del alumno
     * @param statusRelacion Estado de la relación
     * @return true si existe relación activa
     */
    boolean existsByUsuarioEntrenadorAndUsuarioAlumnoAndStatusRelacion(
            String usuarioEntrenador,
            String usuarioAlumno,
            String statusRelacion
    );

    /**
     * Contar alumnos activos de un entrenador
     * @param usuarioEntrenador Username del entrenador
     * @param statusRelacion Estado ("activo")
     * @return Número de alumnos activos
     */
    int countByUsuarioEntrenadorAndStatusRelacion(
            String usuarioEntrenador,
            String statusRelacion
    );

    /**
     * Cuenta cuántos entrenadores ACTIVOS tiene contratados un alumno
     */
    @Query("SELECT COUNT(ea) FROM EntrenadorAlumno ea " +
            "WHERE ea.usuarioAlumno = :usuario " +
            "AND ea.statusRelacion = 'activo'")
    Integer contarEntrenadoresActivos(@Param("usuario") String usuario);

    /**
     * Cuenta TODOS los entrenadores (sin importar status)
     */
    @Query("SELECT COUNT(ea) FROM EntrenadorAlumno ea " +
            "WHERE ea.usuarioAlumno = :usuario")
    Integer contarTodosEntrenadores(@Param("usuario") String usuario);

    // ==========================================
    // MÉTODOS NUEVOS PARA ESTADÍSTICAS
    // ==========================================

    /**
     * Obtener todos los alumnos activos de un entrenador (mismo comportamiento que el método anterior
     * pero con nombre más explícito para estadísticas)
     * @param usuarioEntrenador Username del entrenador
     * @return Lista de relaciones activas
     */
    @Query("SELECT ea FROM EntrenadorAlumno ea " +
            "WHERE ea.usuarioEntrenador = :usuarioEntrenador " +
            "AND ea.statusRelacion = 'activo'")
    List<EntrenadorAlumno> findAlumnosActivosByEntrenador(@Param("usuarioEntrenador") String usuarioEntrenador);

    /**
     * Encontrar relación específica entre entrenador y alumno (sin importar status)
     * @param usuarioEntrenador Username del entrenador
     * @param usuarioAlumno Username del alumno
     * @return Optional con la relación si existe
     */
    Optional<EntrenadorAlumno> findByUsuarioEntrenadorAndUsuarioAlumno(
            String usuarioEntrenador,
            String usuarioAlumno
    );

    /**
     * Contar alumnos activos de un entrenador (alternativo usando Query)
     * @param usuarioEntrenador Username del entrenador
     * @return Número de alumnos activos
     */
    @Query("SELECT COUNT(ea) FROM EntrenadorAlumno ea " +
            "WHERE ea.usuarioEntrenador = :usuarioEntrenador " +
            "AND ea.statusRelacion = 'activo'")
    Integer countAlumnosActivosQuery(@Param("usuarioEntrenador") String usuarioEntrenador);

    /**
     * Obtener todos los entrenadores de un alumno (sin importar estado)
     * @param usuarioAlumno Username del alumno
     * @return Lista de relaciones
     */
    List<EntrenadorAlumno> findByUsuarioAlumno(String usuarioAlumno);
}