package com.sportine.backend.repository;

import com.sportine.backend.model.PlantillaMetricasDeporte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlantillaMetricasDeporteRepository extends JpaRepository<PlantillaMetricasDeporte, Integer> {

    // Para cargar todas las métricas de un deporte (endpoint público)
    List<PlantillaMetricasDeporte> findByIdDeporteOrderByOrdenDisplayAsc(Integer idDeporte);

    // Para buscar una métrica específica por nombre (usada al guardar series)
    Optional<PlantillaMetricasDeporte> findByIdDeporteAndNombreMetrica(Integer idDeporte, String nombreMetrica);

    // Para filtrar solo las que son por serie
    List<PlantillaMetricasDeporte> findByIdDeporteAndEsPorSerieTrue(Integer idDeporte);

    @Query(value = "SELECT * FROM Plantilla_Metricas_Deporte WHERE id_deporte = :idDeporte AND fuente = :fuente ORDER BY orden_display", nativeQuery = true)
    List<PlantillaMetricasDeporte> findByIdDeporteAndFuente(
            @Param("idDeporte") Integer idDeporte,
            @Param("fuente") String fuente);

}