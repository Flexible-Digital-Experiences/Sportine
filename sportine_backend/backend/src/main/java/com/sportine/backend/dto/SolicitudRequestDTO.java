package com.sportine.backend.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SolicitudRequestDTO {
    @NotBlank(message = "El usuario del entrenador es obligatorio")
    private String usuarioEntrenador;

    @NotNull(message = "El deporte es obligatorio")
    @Min(value = 1, message = "Debe seleccionar un deporte válido")
    private Integer idDeporte;

    @NotBlank(message = "El nivel es obligatorio")
    @Pattern(regexp = "^(Principiante|Intermedio|Avanzado)$",
            message = "El nivel debe ser: Principiante, Intermedio o Avanzado")
    private String nivel;

    @NotBlank(message = "El motivo de la solicitud es obligatorio")
    @Size(min = 10, max = 255, message = "El motivo debe tener entre 10 y 255 caracteres")
    private String motivo;

    public String getUsuarioEntrenador() { return usuarioEntrenador; }
    public void setUsuarioEntrenador(String usuarioEntrenador) { this.usuarioEntrenador = usuarioEntrenador; }

    public Integer getIdDeporte() { return idDeporte; }
    public void setIdDeporte(Integer idDeporte) { this.idDeporte = idDeporte; }

    public String getNivel() { return nivel; }
    public void setNivel(String nivel) { this.nivel = nivel; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
}
