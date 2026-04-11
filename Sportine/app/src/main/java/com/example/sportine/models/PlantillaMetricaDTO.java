package com.example.sportine.models;

/**
 * DTO que representa una métrica de plantilla de un deporte.
 * Usado para mostrar campos dinámicos en el bottom sheet de resultados.
 *
 * Viene de GET /api/plantillas/deporte/{idDeporte}
 */
public class PlantillaMetricaDTO {

    private Integer idPlantilla;
    private Integer idDeporte;
    private String nombreMetrica;   // ej: "tiros_libres_anotados"
    private String etiqueta;        // ej: "Tiros anotados"
    private String fuente;          // "manual", "health_connect", "calculada"
    private Boolean esPorSerie;     // true = se captura por serie en el bottom sheet
    private String unidad;          // ej: "goles", "metros", "segundos"

    // ── Getters ──────────────────────────────────────────────────────────────
    public Integer getIdPlantilla() { return idPlantilla; }
    public Integer getIdDeporte() { return idDeporte; }
    public String getNombreMetrica() { return nombreMetrica; }
    public String getEtiqueta() { return etiqueta; }
    public String getFuente() { return fuente; }
    public Boolean isEsPorSerie() { return esPorSerie; }
    public String getUnidad() { return unidad; }

    // ── Setters ──────────────────────────────────────────────────────────────
    public void setIdPlantilla(Integer idPlantilla) { this.idPlantilla = idPlantilla; }
    public void setIdDeporte(Integer idDeporte) { this.idDeporte = idDeporte; }
    public void setNombreMetrica(String nombreMetrica) { this.nombreMetrica = nombreMetrica; }
    public void setEtiqueta(String etiqueta) { this.etiqueta = etiqueta; }
    public void setFuente(String fuente) { this.fuente = fuente; }
    public void setEsPorSerie(Boolean esPorSerie) { this.esPorSerie = esPorSerie; }
    public void setUnidad(String unidad) { this.unidad = unidad; }
}