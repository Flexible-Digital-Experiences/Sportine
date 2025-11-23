package com.sportine.backend.repository;

import com.sportine.backend.model.Entrenamiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface EntrenamientoRepository extends JpaRepository<Entrenamiento, Integer> {

    List<Entrenamiento> findByUsuarioAndFechaEntrenamiento(String usuario, LocalDate fecha);

    int countByUsuarioAndFechaEntrenamiento(String usuario, LocalDate fecha);

    List<Entrenamiento> findByUsuario(String usuario);
}