package com.sportine.backend.repository;

import com.sportine.backend.model.Seguidores;
import com.sportine.backend.model.Usuario; // <--- Importante
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SeguidoresRepository extends JpaRepository<Seguidores, Integer> {

    boolean existsByUsuarioSeguidorAndUsuarioSeguido(String seguidor, String seguido);

    Optional<Seguidores> findByUsuarioSeguidorAndUsuarioSeguido(String seguidor, String seguido);

    long countByUsuarioSeguido(String usuario);
    long countByUsuarioSeguidor(String usuario);

    @Query("SELECT u FROM Usuario u " +
            "JOIN Seguidores s ON u.usuario = s.usuarioSeguido " +
            "WHERE s.usuarioSeguidor = :miUsuario")
    List<Usuario> obtenerAQuienSigo(@Param("miUsuario") String miUsuario);

    /**
     * Cuenta cuántos amigos tiene un usuario
     * Un "amigo" es alguien a quien sigo Y que me sigue de vuelta (relación bidireccional)
     */
    @Query("SELECT COUNT(s) FROM Seguidores s " +
            "WHERE s.usuarioSeguidor = :usuario " +
            "AND EXISTS (" +
            "    SELECT s2 FROM Seguidores s2 " +
            "    WHERE s2.usuarioSeguidor = s.usuarioSeguido " +
            "    AND s2.usuarioSeguido = :usuario" +
            ")")
    Integer contarAmigos(@Param("usuario") String usuario);

    /**
     * Alternativa más simple: Contar TODOS los que sigo (sin importar si me siguen)
     * Usa este si prefieres contar seguidores en lugar de amigos mutuos
     */
    @Query("SELECT COUNT(s) FROM Seguidores s WHERE s.usuarioSeguidor = :usuario")
    Integer contarSiguiendo(@Param("usuario") String usuario);

    /**
     * Contar TODOS los que me siguen
     */
    @Query("SELECT COUNT(s) FROM Seguidores s WHERE s.usuarioSeguido = :usuario")
    Integer contarSeguidores(@Param("usuario") String usuario);
}