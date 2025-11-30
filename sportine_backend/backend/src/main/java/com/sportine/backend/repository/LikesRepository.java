package com.sportine.backend.repository;

import com.sportine.backend.model.Likes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LikesRepository extends JpaRepository<Likes, Integer> {


    @Query("SELECT l FROM Likes l WHERE l.idPublicacion = :idPublicacion AND l.usuarioLike = :username")
    Optional<Likes> findLikeByPostAndUser(
            @Param("idPublicacion") Integer idPublicacion,
            @Param("username") String username
    );

    int countByIdPublicacion(Integer idPublicacion);

    @Query("SELECT COUNT(l) > 0 FROM Likes l WHERE l.idPublicacion = :idPublicacion AND l.usuarioLike = :username")
    boolean existsByIdPublicacionAndUsuarioLike(
            @Param("idPublicacion") Integer idPublicacion,
            @Param("username") String username
    );

    void deleteByIdPublicacion(Integer idPublicacion);
}