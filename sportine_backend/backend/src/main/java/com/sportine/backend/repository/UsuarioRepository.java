package com.sportine.backend.repository;


import com.sportine.backend.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, String> {

    boolean existsByUsuario(String usuario);
    Optional<Usuario> findByUsuario(String usuario);
}
