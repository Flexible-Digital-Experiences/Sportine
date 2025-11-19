package com.sportine.backend.repository;

import com.sportine.backend.model.Comentario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ComentarioRepository extends JpaRepository<Comentario, Integer> {

    List<Comentario> findByIdPublicacionOrderByFechaAsc(Integer idPublicacion);
}