package com.sportine.backend.repository;

import com.sportine.backend.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface DetalleEntrenadorRepository extends JpaRepository<Usuario, String> {

    /**
     * Obtiene los datos principales del entrenador (perfil completo).
     *
     * @param usuario El username del entrenador
     * @return Map con todos los datos del perfil
     */
    @Query(value = """
        SELECT 
            u.usuario as usuario,
            u.nombre as nombre,
            u.apellidos as apellidos,
            u.ciudad as ciudad,
            e.estado as estado,
            ie.foto_perfil as fotoPerfil,
            ie.descripcion_perfil as descripcionPerfil,
            ie.costo_mensualidad as costoMensualidad,
            ie.limite_alumnos AS limiteAlumnos,
            COUNT(DISTINCT ea.usuario_alumno) AS alumnosActuales
        FROM Usuario u
        LEFT JOIN Estado e ON u.id_estado = e.id_estado
        LEFT JOIN Informacion_Entrenador ie ON u.usuario = ie.usuario
        LEFT JOIN Entrenador_Alumno ea ON u.usuario = ea.usuario_entrenador 
            AND ea.status_relacion = 'activo'
        WHERE u.usuario = :usuario
        GROUP BY u.usuario, u.nombre, u.apellidos, u.ciudad, e.estado, 
                 ie.foto_perfil, ie.descripcion_perfil, ie.costo_mensualidad, ie.limite_alumnos
        """, nativeQuery = true)
    Optional<Map<String, Object>> obtenerDatosEntrenador(@Param("usuario") String usuario);

    /**
     * Obtiene las estadísticas de calificación del entrenador.
     *
     * @param usuario El username del entrenador
     * @return Map con ratingPromedio y totalResenas
     */
    @Query(value = """
        SELECT 
            COALESCE(AVG(calificacion), 0.0) as ratingPromedio,
            COUNT(*) as totalResenas
        FROM Calificaciones
        WHERE usuario_calificado = :usuario
        """, nativeQuery = true)
    Optional<Map<String, Object>> obtenerCalificaciones(@Param("usuario") String usuario);

    /**
     * Obtiene todas las especialidades del entrenador.
     *
     * @param usuario El username del entrenador
     * @return Lista de deportes
     */
    @Query(value = """
        SELECT d.nombre_deporte
        FROM Entrenador_Deporte ed
        INNER JOIN Deporte d ON ed.id_deporte = d.id_deporte
        WHERE ed.usuario = :usuario
        ORDER BY d.nombre_deporte
        """, nativeQuery = true)
    List<String> obtenerEspecialidades(@Param("usuario") String usuario);

    /**
     * Obtiene todas las reseñas del entrenador con información del alumno.
     *
     * @param usuario El username del entrenador
     * @return Lista de maps con datos de cada reseña
     */
    @Query(value = """
        SELECT 
            c.calificacion as ratingDado,
            c.comentarios as comentario,
            CONCAT(u.nombre, ' ', u.apellidos) as nombreAlumno,
            COALESCE(ia.foto_perfil, '') as fotoAlumno
        FROM Calificaciones c
        INNER JOIN Usuario u ON c.usuario = u.usuario
        LEFT JOIN Informacion_Alumno ia ON u.usuario = ia.usuario
        WHERE c.usuario_calificado = :usuario
        ORDER BY c.id_calificacion DESC
        """, nativeQuery = true)
    List<Map<String, Object>> obtenerResenas(@Param("usuario") String usuario);
}