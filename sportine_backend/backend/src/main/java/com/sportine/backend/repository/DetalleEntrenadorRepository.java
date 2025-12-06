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
            ie.correo as correo,
            ie.telefono as telefono,
            COUNT(DISTINCT ea.usuario_alumno) AS alumnosActuales
        FROM Usuario u
        LEFT JOIN Estado e ON u.id_estado = e.id_estado
        LEFT JOIN Informacion_Entrenador ie ON u.usuario = ie.usuario
        LEFT JOIN Entrenador_Alumno ea ON u.usuario = ea.usuario_entrenador 
            AND ea.status_relacion = 'activo'
        WHERE u.usuario = :usuario
        GROUP BY u.usuario, u.nombre, u.apellidos, u.ciudad, e.estado, 
                 ie.foto_perfil, ie.descripcion_perfil, ie.costo_mensualidad, 
                 ie.limite_alumnos, ie.correo, ie.telefono
        """, nativeQuery = true)
    Optional<Map<String, Object>> obtenerDatosEntrenador(@Param("usuario") String usuario);

    @Query(value = """
        SELECT 
            COALESCE(AVG(calificacion), 0.0) as ratingPromedio,
            COUNT(*) as totalResenas
        FROM Calificaciones
        WHERE usuario_calificado = :usuario
        """, nativeQuery = true)
    Optional<Map<String, Object>> obtenerCalificaciones(@Param("usuario") String usuario);

    @Query(value = """
        SELECT d.nombre_deporte
        FROM Entrenador_Deporte ed
        INNER JOIN Deporte d ON ed.id_deporte = d.id_deporte
        WHERE ed.usuario = :usuario
        ORDER BY d.nombre_deporte
        """, nativeQuery = true)
    List<String> obtenerEspecialidades(@Param("usuario") String usuario);

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

    /**
     * Obtiene el estado de la relación entre un alumno y un entrenador.
     * Retorna información de la relación más reciente si existe.
     */
    @Query(value = """
        SELECT 
            ea.id_relacion as idRelacion,
            ea.status_relacion as statusRelacion,
            ea.id_deporte as idDeporte,
            d.nombre_deporte as nombreDeporte
        FROM Entrenador_Alumno ea
        INNER JOIN Deporte d ON ea.id_deporte = d.id_deporte
        WHERE ea.usuario_entrenador = :usuarioEntrenador
          AND ea.usuario_alumno = :usuarioAlumno
        ORDER BY ea.id_relacion DESC
        LIMIT 1
        """, nativeQuery = true)
    Optional<Map<String, Object>> obtenerEstadoRelacion(
            @Param("usuarioEntrenador") String usuarioEntrenador,
            @Param("usuarioAlumno") String usuarioAlumno
    );

    /**
     * Verifica si el alumno ya calificó al entrenador.
     */
    @Query(value = """
    SELECT COUNT(*)
    FROM Calificaciones
    WHERE usuario = :usuarioAlumno
      AND usuario_calificado = :usuarioEntrenador
    """, nativeQuery = true)
    Long verificarSiYaCalifico(
            @Param("usuarioAlumno") String usuarioAlumno,
            @Param("usuarioEntrenador") String usuarioEntrenador
    );
}