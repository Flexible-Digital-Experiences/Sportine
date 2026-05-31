package com.sportine.backend.repository;

import com.sportine.backend.model.EntrenadorAlumno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Repository para manejar las relaciones entre entrenadores y alumnos.
 * Permite obtener los alumnos de un entrenador y viceversa.
 */
@Repository
public interface EntrenadorAlumnoRepository extends JpaRepository<EntrenadorAlumno, Integer> {

    // ========== MÃ‰TODOS EXISTENTES ==========

    /**
     * Obtener todos los alumnos de un entrenador
     * @param usuarioEntrenador Username del entrenador
     * @return Lista de relaciones (alumnos)
     */
    List<EntrenadorAlumno> findByUsuarioEntrenador(String usuarioEntrenador);

    /**
     * Obtener todos los alumnos ACTIVOS de un entrenador
     * @param usuarioEntrenador Username del entrenador
     * @param statusRelacion Estado de la relaciÃ³n (ej: "activo")
     * @return Lista de relaciones activas
     */
    List<EntrenadorAlumno> findByUsuarioEntrenadorAndStatusRelacion(
            String usuarioEntrenador,
            String statusRelacion
    );

    /**
     * Obtener el entrenador de un alumno
     * @param usuarioAlumno Username del alumno
     * @return RelaciÃ³n con el entrenador
     */
    Optional<EntrenadorAlumno> findByUsuarioAlumnoAndStatusRelacion(
            String usuarioAlumno,
            String statusRelacion
    );

    /**
     * Verificar si existe una relaciÃ³n activa entre entrenador y alumno
     * @param usuarioEntrenador Username del entrenador
     * @param usuarioAlumno Username del alumno
     * @param statusRelacion Estado de la relaciÃ³n
     * @return true si existe relaciÃ³n activa
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
     * @return NÃºmero de alumnos activos
     */
    int countByUsuarioEntrenadorAndStatusRelacion(
            String usuarioEntrenador,
            String statusRelacion
    );

    /**
     * Cuenta cuÃ¡ntos entrenadores ACTIVOS tiene contratados un alumno
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

    // ========== NUEVOS MÃ‰TODOS PARA MENSUALIDADES ==========

    /**
     * Encontrar relaciones activas con mensualidad vencida
     * @param statusRelacion Estado de la relaciÃ³n ("activo")
     * @param fecha Fecha lÃ­mite (hoy)
     * @return Lista de relaciones vencidas
     */
    List<EntrenadorAlumno> findByStatusRelacionAndFinMensualidadBefore(
            String statusRelacion,
            LocalDate fecha
    );

    /**
     * Query nativa mÃ¡s eficiente para actualizar en lote
     * Actualiza todas las relaciones activas con mensualidad vencida
     * @param fecha Fecha lÃ­mite (hoy)
     * @return NÃºmero de registros actualizados
     */
    @Modifying
    @Query(value = "UPDATE entrenador_alumno SET status_relacion = 'pendiente' " +
            "WHERE status_relacion = 'activo' AND fin_mensualidad < :fecha",
            nativeQuery = true)
    int actualizarMensualidadesVencidasEnLote(@Param("fecha") LocalDate fecha);

    /**
     * Obtener relaciones que vencen entre dos fechas (Ãºtil para alertas)
     * @param fechaInicio Fecha de inicio del rango
     * @param fechaFin Fecha de fin del rango
     * @return Lista de relaciones que vencen en ese rango
     */
    @Query("SELECT ea FROM EntrenadorAlumno ea " +
            "WHERE ea.statusRelacion = 'activo' " +
            "AND ea.finMensualidad BETWEEN :fechaInicio AND :fechaFin")
    List<EntrenadorAlumno> encontrarMensualidadesPorVencer(
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin
    );

    @Query("SELECT ea FROM EntrenadorAlumno ea WHERE ea.usuarioEntrenador = :usuarioEntrenador AND ea.statusRelacion = 'activo'")
    List<EntrenadorAlumno> findAlumnosActivosByEntrenador(@Param("usuarioEntrenador") String usuarioEntrenador);

    Optional<EntrenadorAlumno> findByUsuarioEntrenadorAndUsuarioAlumno(String usuarioEntrenador, String usuarioAlumno);

    @Query("SELECT COUNT(ea) FROM EntrenadorAlumno ea WHERE ea.usuarioEntrenador = :usuario AND ea.statusRelacion = 'activo'")
    Integer contarAlumnosActivos(@Param("usuario") String usuario);

    @Query(value = """
    SELECT 
        ea.usuario_alumno as usuarioAlumno,
        CONCAT(u.nombre, ' ', u.apellidos) as nombreCompleto,
        ia.foto_perfil as fotoPerfil,
        TIMESTAMPDIFF(YEAR, ia.fecha_nacimiento, CURDATE()) as edad,
        GROUP_CONCAT(DISTINCT d.nombre_deporte ORDER BY d.nombre_deporte SEPARATOR ', ') as deportes,
        MIN(ea.fecha_inicio) as fechaInicio,
        ea.status_relacion as statusRelacion
    FROM entrenador_alumno ea
    INNER JOIN usuario u ON ea.usuario_alumno = u.usuario
    LEFT JOIN informacion_alumno ia ON u.usuario = ia.usuario
    INNER JOIN deporte d ON ea.id_deporte = d.id_deporte
    WHERE ea.usuario_entrenador = :usuarioEntrenador
    GROUP BY ea.usuario_alumno, u.nombre, u.apellidos, ia.foto_perfil, ia.fecha_nacimiento, ea.status_relacion
    ORDER BY MIN(ea.fecha_inicio) DESC
    """, nativeQuery = true)
    List<Map<String, Object>> obtenerAlumnosPorEntrenador(@Param("usuarioEntrenador") String usuarioEntrenador);

    /**
     * Verificar si existe relaciÃ³n entre entrenador, alumno y deporte especÃ­fico
     */
    @Query("SELECT CASE WHEN COUNT(ea) > 0 THEN true ELSE false END " +
            "FROM EntrenadorAlumno ea " +
            "WHERE ea.usuarioEntrenador = :usuarioEntrenador " +
            "AND ea.usuarioAlumno = :usuarioAlumno " +
            "AND ea.idDeporte = :idDeporte")
    boolean existsByUsuarioEntrenadorAndUsuarioAlumnoAndIdDeporte(
            @Param("usuarioEntrenador") String usuarioEntrenador,
            @Param("usuarioAlumno") String usuarioAlumno,
            @Param("idDeporte") Integer idDeporte
    );

    /**
     * Actualizar estado de relaciÃ³n para un deporte especÃ­fico
     */
    @Modifying
    @Transactional
    @Query(value = """
    UPDATE entrenador_alumno 
    SET status_relacion = :nuevoEstado,
        fin_mensualidad = CASE 
            WHEN :nuevoEstado = 'activo' THEN DATE_ADD(CURDATE(), INTERVAL 1 MONTH)
            ELSE fin_mensualidad 
        END
    WHERE usuario_entrenador = :usuarioEntrenador 
      AND usuario_alumno = :usuarioAlumno 
      AND id_deporte = :idDeporte
    """, nativeQuery = true)
    void actualizarEstadoRelacion(
            @Param("usuarioEntrenador") String usuarioEntrenador,
            @Param("usuarioAlumno") String usuarioAlumno,
            @Param("idDeporte") Integer idDeporte,
            @Param("nuevoEstado") String nuevoEstado
    );

    List<EntrenadorAlumno> findByUsuarioEntrenadorAndUsuarioAlumnoAndStatusRelacion(
            String usuarioEntrenador, String usuarioAlumno, String statusRelacion);

    boolean existsByUsuarioEntrenadorAndUsuarioAlumnoAndIdDeporteAndStatusRelacion(
            String usuarioEntrenador, String usuarioAlumno,
            Integer idDeporte, String statusRelacion);
}




