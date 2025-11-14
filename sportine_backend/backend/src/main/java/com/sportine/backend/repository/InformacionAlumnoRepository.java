package com.sportine.backend.repository;

import com.sportine.backend.model.InformacionAlumno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface InformacionAlumnoRepository extends JpaRepository<InformacionAlumno, String> {

    Optional<InformacionAlumno> findByUsuario(String usuario);
    boolean existsByUsuario(String usuario);
}