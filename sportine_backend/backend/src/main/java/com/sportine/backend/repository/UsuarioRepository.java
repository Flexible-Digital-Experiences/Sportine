package com.sportine.backend.repository;


import com.sportine.backend.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, String> {

    boolean existsByUsuario(String usuario);
    Optional<Usuario> findByUsuario(String usuario);

    @Query("SELECT u FROM Usuario u WHERE u.usuario LIKE %:termino% OR u.nombre LIKE %:termino% OR u.apellidos LIKE %:termino%")
    List<Usuario> buscarPorNombreOUsuario(@Param("termino") String termino);
}
