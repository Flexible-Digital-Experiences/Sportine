package com.sportine.backend.repository;

import com.sportine.backend.model.Likes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query; // ¡Importa este!
import org.springframework.data.repository.query.Param; // ¡Importa este!
import java.util.Optional;

public interface LikesRepository extends JpaRepository<Likes, Integer> {

    /**
     * Busca un Like específico por ID de Post y Username.
     * * Usamos @Query para escribir el SQL (HQL) manualmente y evitar
     * el bug de Spring Boot que no puede leer bien el nombre del método.
     * * :idPublicacion se conecta al parámetro 'idPublicacion'
     * :username se conecta al parámetro 'username'
     */
    @Query("SELECT l FROM Likes l WHERE l.idPublicacion = :idPublicacion AND l.usuarioLike = :username")
    Optional<Likes> findLikeByPostAndUser(
            @Param("idPublicacion") Integer idPublicacion,
            @Param("username") String username
    );

}