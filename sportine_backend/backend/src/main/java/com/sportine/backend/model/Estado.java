package com.sportine.backend.model;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

    @Entity
    @Table(name = "Estado")
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class Estado{

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "id_estado")
        private Integer idEstado;

        @Column(name = "estado")
        private String estado;
}
