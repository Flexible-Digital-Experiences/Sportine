package com.sportine.backend.repository;

import com.sportine.backend.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface BuscarEntrenadorNombreRepository extends JpaRepository<Usuario, String> {

    /**
     * Buscar entrenadores por nombre (SIN filtro de estado)
     * Se usa cuando el usuario busca por nombre
     * INCLUYE FILTRO DE LÍMITE DE ALUMNOS - Solo muestra entrenadores con disponibilidad
     */
    @Query(value = """
        SELECT
            u.usuario,
            CONCAT(u.nombre, ' ', u.apellidos) AS nombreCompleto,
            ie.foto_perfil AS fotoPerfil,
            COALESCE(AVG(c.calificacion), 0.0) AS ratingPromedio,
            ie.limite_alumnos AS limiteAlumnos,
            COUNT(DISTINCT ea.usuario_alumno) AS alumnosActuales
        FROM Usuario u
        INNER JOIN Usuario_Rol ur ON u.usuario = ur.usuario
        INNER JOIN Rol r ON ur.id_rol = r.id_rol
        LEFT JOIN Informacion_Entrenador ie ON u.usuario = ie.usuario
        LEFT JOIN Calificaciones c ON u.usuario = c.usuario_calificado
        LEFT JOIN Entrenador_Alumno ea ON u.usuario = ea.usuario_entrenador 
            AND ea.status_relacion = 'activo'
        WHERE r.rol = 'entrenador'
          AND (:query IS NULL OR LOWER(CONCAT(u.nombre, ' ', u.apellidos)) LIKE LOWER(CONCAT('%', :query, '%')))
        GROUP BY u.usuario, u.nombre, u.apellidos, ie.foto_perfil, ie.limite_alumnos
        HAVING COUNT(DISTINCT ea.usuario_alumno) < ie.limite_alumnos
        ORDER BY ratingPromedio DESC, u.nombre ASC
        """, nativeQuery = true)
    List<Map<String, Object>> buscarEntrenadores(@Param("query") String query);

    /**
     * Buscar mejores entrenadores del mismo estado (carga inicial)
     * Se usa cuando el usuario entra a la pantalla sin buscar nada
     * INCLUYE FILTRO DE LÍMITE DE ALUMNOS - Solo muestra entrenadores con disponibilidad
     */
    @Query(value = """
        SELECT
            u.usuario,
            CONCAT(u.nombre, ' ', u.apellidos) AS nombreCompleto,
            ie.foto_perfil AS fotoPerfil,
            COALESCE(AVG(c.calificacion), 0.0) AS ratingPromedio,
            ie.limite_alumnos AS limiteAlumnos,
            COUNT(DISTINCT ea.usuario_alumno) AS alumnosActuales
        FROM Usuario u
        INNER JOIN Usuario_Rol ur ON u.usuario = ur.usuario
        INNER JOIN Rol r ON ur.id_rol = r.id_rol
        LEFT JOIN Informacion_Entrenador ie ON u.usuario = ie.usuario
        LEFT JOIN Calificaciones c ON u.usuario = c.usuario_calificado
        LEFT JOIN Entrenador_Alumno ea ON u.usuario = ea.usuario_entrenador 
            AND ea.status_relacion = 'activo'
        WHERE r.rol = 'entrenador'
          AND u.id_estado = :idEstado
        GROUP BY u.usuario, u.nombre, u.apellidos, ie.foto_perfil, ie.limite_alumnos
        HAVING COUNT(DISTINCT ea.usuario_alumno) < ie.limite_alumnos
        ORDER BY ratingPromedio DESC, u.nombre ASC
        LIMIT 20
        """, nativeQuery = true)
    List<Map<String, Object>> buscarEntrenadoresPorEstado(@Param("idEstado") Integer idEstado);

    /**
     * Obtener especialidades de un entrenador
     */
    @Query(value = """
        SELECT d.nombre_deporte
        FROM Entrenador_Deporte ed
        INNER JOIN Deporte d ON ed.id_deporte = d.id_deporte
        WHERE ed.usuario = :usuario
        """, nativeQuery = true)
    List<String> obtenerEspecialidadesEntrenador(@Param("usuario") String usuario);
}