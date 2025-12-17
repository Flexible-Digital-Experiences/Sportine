package com.sportine.backend.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO para el registro de un nuevo usuario con validaciones completas
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioRegistroDTO {

    // ✅ ORDEN CORRECTO: Coincide con Android

    @NotBlank(message = "El usuario no puede estar vacío")
    @Size(min = 3, max = 20, message = "El usuario debe tener entre 3 y 20 caracteres")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "El usuario solo puede contener letras, números y guión bajo")
    private String usuario;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
    private String nombre;

    @NotBlank(message = "Los apellidos son obligatorios")
    @Size(min = 2, max = 50, message = "Los apellidos deben tener entre 2 y 50 caracteres")
    private String apellidos;

    @NotBlank(message = "El sexo es obligatorio")
    @Pattern(regexp = "^(Masculino|Femenino)$", message = "El sexo debe ser: Masculino o Femenino")
    private String sexo;

    @NotNull(message = "El estado es obligatorio")
    @Min(value = 1, message = "Debe seleccionar un estado válido")
    private Integer idEstado;

    @NotBlank(message = "La ciudad es obligatoria")
    @Size(min = 2, max = 100, message = "La ciudad debe tener entre 2 y 100 caracteres")
    private String ciudad;

    @NotBlank(message = "El rol es obligatorio")
    @Pattern(regexp = "^(alumno|entrenador)$", message = "El rol debe ser 'alumno' o 'entrenador'")
    private String rol;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String contrasena;

    @NotBlank(message = "El correo electrónico es obligatorio")
    @Email(message = "El correo electrónico debe ser válido")
    @Size(max = 255, message = "El correo no puede exceder 255 caracteres")
    private String correo;

    @Past(message = "La fecha de nacimiento debe ser en el pasado")
    private LocalDate fechaNacimiento;
}