package com.sportine.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class TarjetaDTO {
    private String usuario;
    private String numeroTarjeta;
    private LocalDate fechaCaducidad;
    private String nombreTitular;
    private String apellidosTitular;
    private String direccionFacturacion;
    private String localidad;
    private String codigoPostal;
    private String pais;
    private String telefono;
}