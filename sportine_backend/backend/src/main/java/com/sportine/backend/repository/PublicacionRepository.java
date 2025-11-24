package com.sportine.backend.repository;

import com.sportine.backend.model.Publicacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PublicacionRepository extends JpaRepository<Publicacion, Integer> {

    // --- CORRECCIÓN AQUÍ ---
    // Cambiamos 'p.fechaPublicacion' por 'p.fecha_publicacion' (o como se llame en tu Entity)
    @Query("SELECT p FROM Publicacion p " +
            "WHERE p.usuario IN " +
            "(SELECT s.usuarioSeguido FROM Seguidores s WHERE s.usuarioSeguidor = :miUsuario) " +
            "OR p.usuario = :miUsuario " +
            "ORDER BY p.fecha_publicacion ASC")
    List<Publicacion> obtenerFeedPersonalizado(@Param("miUsuario") String miUsuario);
}