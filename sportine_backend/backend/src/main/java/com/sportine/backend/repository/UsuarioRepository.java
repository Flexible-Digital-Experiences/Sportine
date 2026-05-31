package com.sportine.backend.repository;


import com.sportine.backend.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, String> {

    boolean existsByUsuario(String usuario);
    Optional<Usuario> findByUsuario(String usuario);

    @Query("SELECT u FROM Usuario u WHERE " +
            "LOWER(u.usuario) LIKE LOWER(CONCAT('%', :termino, '%')) OR " +
            "LOWER(u.nombre) LIKE LOWER(CONCAT('%', :termino, '%')) OR " +
            "LOWER(u.apellidos) LIKE LOWER(CONCAT('%', :termino, '%'))")
    List<Usuario> buscarPorNombreOUsuario(@Param("termino") String termino);

    Optional<Usuario> findByCorreo(String correo);

    boolean existsByCorreo(String correo);

    @Query(value = "SELECT DISTINCT u.* FROM usuario u " +
            "LEFT JOIN alumno_deporte ad ON u.usuario = ad.usuario " +
            "LEFT JOIN entrenador_deporte ed ON u.usuario = ed.usuario_entrenador " +
            "WHERE u.usuario != :miUsuario " +
            "AND u.usuario NOT IN (SELECT usuario_seguido FROM seguidores WHERE usuario_seguidor = :miUsuario) " +
            "AND u.usuario NOT IN (SELECT usuario_entrenador FROM entrenador_alumno WHERE usuario_alumno = :miUsuario) " +
            "AND u.usuario NOT IN (SELECT usuario_alumno FROM entrenador_alumno WHERE usuario_entrenador = :miUsuario) " +
            "AND (" +
            "   u.id_estado = :idEstado " +
            "   OR ad.id_deporte IN (:misDeportes) " +
            "   OR ed.id_deporte IN (:misDeportes) " +
            ") ORDER BY RAND() LIMIT 10", nativeQuery = true)
    List<Usuario> buscarSugerenciasHibridas(
            @Param("miUsuario") String miUsuario,
            @Param("idEstado") Integer idEstado,
            @Param("misDeportes") List<Integer> misDeportes
    );
}
