package com.sportine.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "Tarjeta")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tarjeta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tarjeta")
    private Integer idTarjeta;

    @Column(name = "usuario")
    private String usuario;

    @Column(name = "numero_tarjeta")
    private String numeroTarjeta;

    @Column(name = "fecha_caducidad")
    private LocalDate fechaCaducidad;

    @Column(name = "nombre_titular")
    private String nombreTitular;

    @Column(name = "apellidos_titular")
    private String apellidosTitular;

    @Column(name = "direccion_facturacion")
    private String direccionFacturacion;

    @Column(name = "localidad")
    private String localidad;

    @Column(name = "codigo_postal")
    private String codigoPostal;

    @Column(name = "pais")
    private String pais;

    @Column(name = "telefono")
    private String telefono;
}