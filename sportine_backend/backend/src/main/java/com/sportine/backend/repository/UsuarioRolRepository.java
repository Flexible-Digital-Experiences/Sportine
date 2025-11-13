package com.sportine.backend.repository;

import com.sportine.backend.model.Usuario;
import com.sportine.backend.model.UsuarioRol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRolRepository extends JpaRepository<UsuarioRol, Integer> {
    Optional<UsuarioRol> findByUsuario(String usuario);
}
