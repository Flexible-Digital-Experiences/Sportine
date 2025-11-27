package com.example.sportine.models;

public class CalificacionDTO {
    private Double ratingPromedio;
    private Integer totalResenas;

    public CalificacionDTO() {}

    public CalificacionDTO(Double ratingPromedio, Integer totalResenas) {
        this.ratingPromedio = ratingPromedio;
        this.totalResenas = totalResenas;
    }

    public Double getRatingPromedio() { return ratingPromedio; }
    public void setRatingPromedio(Double ratingPromedio) { this.ratingPromedio = ratingPromedio; }

    public Integer getTotalResenas() { return totalResenas; }
    public void setTotalResenas(Integer totalResenas) { this.totalResenas = totalResenas; }
}