package com.sportine.backend.repository;

import com.sportine.backend.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface EntrenadorRepository extends JpaRepository<Usuario, String> {

    @Query(value = """
        SELECT DISTINCT
            u.usuario as usuario,
            CONCAT(u.nombre, ' ', u.apellidos) as nombreCompleto,
            ie.foto_perfil as fotoPerfil,
            COALESCE(AVG(c.calificacion), 0.0) as ratingPromedio
        FROM Usuario u
        INNER JOIN Usuario_rol ur ON u.usuario = ur.usuario
        INNER JOIN Rol r ON ur.id_rol = r.id_rol
        LEFT JOIN Informacion_Entrenador ie ON u.usuario = ie.usuario
        LEFT JOIN Calificaciones c ON u.usuario = c.usuario
        WHERE r.rol = 'ENTRENADOR'
        AND (:searchQuery IS NULL 
             OR LOWER(u.nombre) LIKE LOWER(CONCAT('%', :searchQuery, '%'))
             OR LOWER(u.apellidos) LIKE LOWER(CONCAT('%', :searchQuery, '%')))
        GROUP BY u.usuario, u.nombre, u.apellidos, ie.foto_perfil
        ORDER BY ratingPromedio DESC
        """, nativeQuery = true)
    List<Map<String, Object>> buscarEntrenadores(@Param("searchQuery") String searchQuery);

    @Query(value = """
        SELECT ed.deporte
        FROM Entrenador_Deporte ed
        WHERE ed.usuario = :usuario
        ORDER BY ed.deporte
        """, nativeQuery = true)
    List<String> obtenerEspecialidadesEntrenador(@Param("usuario") String usuario);
}
