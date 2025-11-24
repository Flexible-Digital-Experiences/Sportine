package com.sportine.backend.repository;

import com.sportine.backend.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface BuscarEntrenadorDeporteRepository extends JpaRepository<Usuario, String> {

    /**
     * Buscar entrenadores por deporte específico Y mismo estado
     * Se usa cuando el usuario busca por un deporte (fut, basket, etc.)
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
        INNER JOIN Entrenador_Deporte ed ON u.usuario = ed.usuario
        INNER JOIN Deporte d ON ed.id_deporte = d.id_deporte
        LEFT JOIN Calificaciones c ON u.usuario = c.usuario_calificado
        WHERE r.nombre_rol = 'entrenador'
          AND d.nombre_deporte = :deporte
          AND u.id_estado = :idEstado
        GROUP BY u.usuario, u.nombre, u.apellidos, u.foto_perfil
        ORDER BY ratingPromedio DESC, u.nombre ASC
        """, nativeQuery = true)
    List<Map<String, Object>> buscarEntrenadoresPorDeporteYEstado(
            @Param("deporte") String deporte,
            @Param("idEstado") Integer idEstado
    );
}