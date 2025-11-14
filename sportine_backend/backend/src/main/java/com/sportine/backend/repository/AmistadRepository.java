package com.sportine.backend.repository;

import com.sportine.backend.model.Amistad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AmistadRepository extends JpaRepository<Amistad, Integer> {

    /**
     * Busca una amistad en cualquier dirección (A->B o B->A).
     * Útil para saber si ya son amigos o para borrar la amistad.
     */
    @Query("SELECT a FROM Amistad a WHERE (a.usuario_1 = :u1 AND a.usuario_2 = :u2) OR (a.usuario_1 = :u2 AND a.usuario_2 = :u1)")
    Optional<Amistad> findAmistad(@Param("u1") String u1, @Param("u2") String u2);

    /**
     * Busca todas las relaciones de amistad de un usuario (donde él es usuario_1 o usuario_2).
     */
    @Query("SELECT a FROM Amistad a WHERE a.usuario_1 = :username OR a.usuario_2 = :username")
    List<Amistad> findAllAmigos(@Param("username") String username);
}