package com.sportine.backend.repository;

import com.sportine.backend.model.AlumnoDeporte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AlumnoDeporteRepository extends JpaRepository<AlumnoDeporte, Integer> {

    List<AlumnoDeporte> findByUsuario(String usuario);
    void deleteByUsuario(String usuario);
}