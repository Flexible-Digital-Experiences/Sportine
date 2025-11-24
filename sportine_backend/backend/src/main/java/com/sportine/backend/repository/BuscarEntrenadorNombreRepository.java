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
     */
    @Query(value = """
        SELECT DISTINCT
            u.usuario,
            CONCAT(u.nombre, ' ', u.apellidos) AS nombreCompleto,
            u.foto_perfil AS fotoPerfil,
            COALESCE(AVG(c.calificacion), 0.0) AS ratingPromedio
        FROM Usuario u
        INNER JOIN Usuario_Rol ur ON u.usuario = ur.usuario
        INNER JOIN Rol r ON ur.id_rol = r.id_rol
        LEFT JOIN Calificaciones c ON u.usuario = c.usuario_calificado
        WHERE r.nombre_rol = 'entrenador'
          AND (:query IS NULL OR LOWER(CONCAT(u.nombre, ' ', u.apellidos)) LIKE LOWER(CONCAT('%', :query, '%')))
        GROUP BY u.usuario, u.nombre, u.apellidos, u.foto_perfil
        ORDER BY ratingPromedio DESC, u.nombre ASC
        """, nativeQuery = true)
    List<Map<String, Object>> buscarEntrenadores(@Param("query") String query);

    /**
     * Buscar mejores entrenadores del mismo estado (carga inicial)
     * Se usa cuando el usuario entra a la pantalla sin buscar nada
     */
    @Query(value = """
        SELECT DISTINCT
            u.usuario,
            CONCAT(u.nombre, ' ', u.apellidos) AS nombreCompleto,
            u.foto_perfil AS fotoPerfil,
            COALESCE(AVG(c.calificacion), 0.0) AS ratingPromedio
        FROM Usuario u
        INNER JOIN Usuario_Rol ur ON u.usuario = ur.usuario
        INNER JOIN Rol r ON ur.id_rol = r.id_rol
        LEFT JOIN Calificaciones c ON u.usuario = c.usuario_calificado
        WHERE r.nombre_rol = 'entrenador'
          AND u.id_estado = :idEstado
        GROUP BY u.usuario, u.nombre, u.apellidos, u.foto_perfil
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
