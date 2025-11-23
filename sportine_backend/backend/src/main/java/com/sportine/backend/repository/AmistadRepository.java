package com.sportine.backend.repository;

import com.sportine.backend.model.Amistad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AmistadRepository extends JpaRepository<Amistad, Integer> {

    // Verificar si son amigos (usando tus nombres de variables: usuario_1 y usuario_2)
    @Query("SELECT a FROM Amistad a WHERE " +
            "(a.usuario_1 = :u1 AND a.usuario_2 = :u2) OR " +
            "(a.usuario_1 = :u2 AND a.usuario_2 = :u1)")
    Optional<Amistad> findAmistadEntre(@Param("u1") String user1, @Param("u2") String user2);

    // Buscar amigos
    @Query("SELECT a FROM Amistad a WHERE a.usuario_1 = :user OR a.usuario_2 = :user")
    List<Amistad> findAllAmistadesDe(@Param("user") String user);
}