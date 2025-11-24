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
}