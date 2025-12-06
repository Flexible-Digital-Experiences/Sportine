package com.sportine.backend.repository;

import com.sportine.backend.model.AlumnoDeporte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AlumnoDeporteRepository extends JpaRepository<AlumnoDeporte, Integer> {

    List<AlumnoDeporte> findByUsuario(String usuario);

    void deleteByUsuario(String usuario);

    /**
     * Busca la relación alumno-deporte
     */
    @Query("SELECT ad FROM AlumnoDeporte ad " +
            "WHERE ad.usuario = :usuario AND ad.idDeporte = :idDeporte")
    Optional<AlumnoDeporte> findByUsuarioAndIdDeporte(
            @Param("usuario") String usuario,
            @Param("idDeporte") Integer idDeporte
    );

    /**
     * Inserta un nuevo registro de alumno-deporte
     */
    @Modifying
    @Transactional
    @Query(value = "INSERT INTO Alumno_Deporte (usuario, id_deporte, id_nivel, fecha_inicio) " +
            "VALUES (:usuario, :idDeporte, " +
            "(SELECT id_nivel FROM Nivel WHERE nombre_nivel = :nombreNivel), :fechaInicio)",
            nativeQuery = true)
    void insertarAlumnoDeporte(
            @Param("usuario") String usuario,
            @Param("idDeporte") Integer idDeporte,
            @Param("nombreNivel") String nombreNivel,
            @Param("fechaInicio") LocalDate fechaInicio
    );

    /**
     * Actualizar el nivel del alumno en un deporte específico
     */
    @Modifying
    @Transactional
    @Query("UPDATE AlumnoDeporte ad " +
            "SET ad.idNivel = :nuevoNivel " +
            "WHERE ad.usuario = :usuario " +
            "AND ad.idDeporte = :idDeporte")
    void actualizarNivel(
            @Param("usuario") String usuario,
            @Param("idDeporte") Integer idDeporte,
            @Param("nuevoNivel") Integer nuevoNivel
    );
}