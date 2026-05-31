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
        u.correo as correo,
        ie.foto_perfil as fotoPerfil,
        ie.descripcion_perfil as descripcionPerfil,
        ie.costo_mensualidad as costoMensualidad,
        ie.limite_alumnos AS limiteAlumnos,
        COUNT(DISTINCT ea.usuario_alumno) AS alumnosActuales
    FROM usuario u
    LEFT JOIN estado e ON u.id_estado = e.id_estado
    LEFT JOIN informacion_entrenador ie ON u.usuario = ie.usuario
    LEFT JOIN entrenador_alumno ea ON u.usuario = ea.usuario_entrenador 
        AND ea.status_relacion != 'finalizado'
    WHERE u.usuario = :usuario
    GROUP BY u.usuario, u.nombre, u.apellidos, u.ciudad, e.estado, u.correo,
             ie.foto_perfil, ie.descripcion_perfil, ie.costo_mensualidad, 
             ie.limite_alumnos
    """, nativeQuery = true)
    Optional<Map<String, Object>> obtenerDatosEntrenador(@Param("usuario") String usuario);

    @Query(value = """
        SELECT 
            COALESCE(AVG(calificacion), 0.0) as ratingPromedio,
            COUNT(*) as totalResenas
        FROM calificaciones
        WHERE usuario_calificado = :usuario
        """, nativeQuery = true)
    Optional<Map<String, Object>> obtenerCalificaciones(@Param("usuario") String usuario);

    @Query(value = """
        SELECT d.nombre_deporte
        FROM entrenador_deporte ed
        INNER JOIN deporte d ON ed.id_deporte = d.id_deporte
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
        FROM calificaciones c
        INNER JOIN usuario u ON c.usuario = u.usuario
        LEFT JOIN informacion_alumno ia ON u.usuario = ia.usuario
        WHERE c.usuario_calificado = :usuario
        ORDER BY c.id_calificacion DESC
        """, nativeQuery = true)
    List<Map<String, Object>> obtenerResenas(@Param("usuario") String usuario);

    @Query(value = """
        SELECT 
            ea.id_relacion as idRelacion,
            ea.status_relacion as statusRelacion,
            ea.id_deporte as idDeporte,
            d.nombre_deporte as nombreDeporte
        FROM entrenador_alumno ea
        INNER JOIN deporte d ON ea.id_deporte = d.id_deporte
        WHERE ea.usuario_entrenador = :usuarioEntrenador
          AND ea.usuario_alumno = :usuarioAlumno
        ORDER BY ea.id_relacion DESC
        LIMIT 1
        """, nativeQuery = true)
    Optional<Map<String, Object>> obtenerEstadoRelacion(
            @Param("usuarioEntrenador") String usuarioEntrenador,
            @Param("usuarioAlumno") String usuarioAlumno
    );

    @Query(value = """
    SELECT COUNT(*)
    FROM calificaciones
    WHERE usuario = :usuarioAlumno
      AND usuario_calificado = :usuarioEntrenador
    """, nativeQuery = true)
    Long verificarSiYaCalifico(
            @Param("usuarioAlumno") String usuarioAlumno,
            @Param("usuarioEntrenador") String usuarioEntrenador
    );

    @Query(value = """
    SELECT 
        ea.id_relacion as idRelacion,
        ea.status_relacion as statusRelacion,
        ea.id_deporte as idDeporte,
        d.nombre_deporte as nombreDeporte,
        ea.fin_mensualidad as finMensualidad,
        COALESCE(ase.status_suscripcion, '') as statusSuscripcion
    FROM entrenador_alumno ea
    INNER JOIN deporte d ON ea.id_deporte = d.id_deporte
    LEFT JOIN estudiante_suscripcion_entrenador ase 
        ON ase.usuario_estudiante = :usuarioAlumno
        AND ase.usuario_entrenador = :usuarioEntrenador
        AND ase.id_deporte = ea.id_deporte
        AND ase.status_suscripcion IN ('active', 'cancelled')
    WHERE ea.usuario_entrenador = :usuarioEntrenador
      AND ea.usuario_alumno = :usuarioAlumno
      AND ea.status_relacion IN ('pendiente', 'activo')
    ORDER BY ea.id_relacion DESC
    """, nativeQuery = true)
    List<Map<String, Object>> obtenerRelaciones(
            @Param("usuarioEntrenador") String usuarioEntrenador,
            @Param("usuarioAlumno") String usuarioAlumno
    );
}




