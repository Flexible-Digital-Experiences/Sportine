package com.sportine.backend.repository;

import com.sportine.backend.model.Likes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LikesRepository extends JpaRepository<Likes, Integer> {

    /**
     * --- ¡CORRECCIÓN AQUÍ! ---
     * Le damos la consulta exacta para que no falle en silencio.
     * (Compara los 'l.idPublicacion' con los campos de tu Likes.java)
     */
    @Query("SELECT l FROM Likes l WHERE l.idPublicacion = :idPublicacion AND l.usuarioLike = :username")
    Optional<Likes> findLikeByPostAndUser(
            @Param("idPublicacion") Integer idPublicacion,
            @Param("username") String username
    );

    /**
     * Este se queda igual (ya lo habíamos arreglado).
     */
    int countByIdPublicacion(Integer idPublicacion);

    /**
     * Este se queda igual (ya lo habíamos arreglado).
     */
    @Query("SELECT COUNT(l) > 0 FROM Likes l WHERE l.idPublicacion = :idPublicacion AND l.usuarioLike = :username")
    boolean existsByIdPublicacionAndUsuarioLike(
            @Param("idPublicacion") Integer idPublicacion,
            @Param("username") String username
    );
}