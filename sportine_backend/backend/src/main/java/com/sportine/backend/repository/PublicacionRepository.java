package com.sportine.backend.repository;

import com.sportine.backend.model.Publicacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
//  <[Tu Model], [Tipo de dato del ID (es Integer)]>
public interface PublicacionRepository extends JpaRepository<Publicacion, Integer> {


}