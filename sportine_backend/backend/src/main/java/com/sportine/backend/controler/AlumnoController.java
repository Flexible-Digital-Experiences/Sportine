package com.sportine.backend.controler;

import com.sportine.backend.dto.*;
import com.sportine.backend.service.AlumnoPerfilService;
import com.sportine.backend.service.AlumnoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/alumnos")
@RequiredArgsConstructor
public class AlumnoController {

    private final AlumnoService alumnoService;
    private final AlumnoPerfilService alumnoPerfilService;

    @GetMapping("/home/{usuario}")
    public ResponseEntity<HomeAlumnoDTO> obtenerHomeAlumno(
            @PathVariable String usuario) {

        HomeAlumnoDTO response = alumnoService.obtenerHomeAlumno(usuario);
        return ResponseEntity.ok(response);
    }



    @PostMapping("/perfil")
    public ResponseEntity<PerfilAlumnoResponseDTO> crearPerfil(
            @RequestBody PerfilAlumnoDTO perfilAlumnoDTO) {

        try {
            PerfilAlumnoResponseDTO response = alumnoPerfilService.crearPerfilAlumno(perfilAlumnoDTO);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            PerfilAlumnoResponseDTO errorResponse = new PerfilAlumnoResponseDTO();
            errorResponse.setMensaje(e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }


    @GetMapping("/perfil/{usuario}")
    public ResponseEntity<PerfilAlumnoResponseDTO> obtenerPerfil(
            @PathVariable String usuario) {

        try {
            PerfilAlumnoResponseDTO response = alumnoPerfilService.obtenerPerfilAlumno(usuario);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            PerfilAlumnoResponseDTO errorResponse = new PerfilAlumnoResponseDTO();
            errorResponse.setMensaje(e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
    }


    @PutMapping("/perfil/{usuario}")
    public ResponseEntity<PerfilAlumnoResponseDTO> actualizarPerfil(
            @PathVariable String usuario,
            @RequestBody PerfilAlumnoDTO perfilAlumnoDTO) {

        try {
            PerfilAlumnoResponseDTO response = alumnoPerfilService.actualizarPerfilAlumno(usuario, perfilAlumnoDTO);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            PerfilAlumnoResponseDTO errorResponse = new PerfilAlumnoResponseDTO();
            errorResponse.setMensaje(e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }


    @PostMapping("/{usuario}/tarjetas")
    public ResponseEntity<TarjetaResponseDTO> agregarTarjeta(
            @PathVariable String usuario,
            @RequestBody TarjetaDTO tarjetaDTO) {

        try {
            TarjetaResponseDTO response = alumnoPerfilService.agregarTarjeta(usuario, tarjetaDTO);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            TarjetaResponseDTO errorResponse = new TarjetaResponseDTO();
            errorResponse.setMensaje(e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }


    @GetMapping("/{usuario}/tarjetas")
    public ResponseEntity<List<TarjetaResponseDTO>> obtenerTarjetas(
            @PathVariable String usuario) {

        try {
            List<TarjetaResponseDTO> response = alumnoPerfilService.obtenerTarjetas(usuario);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/{usuario}/tarjetas/{idTarjeta}")
    public ResponseEntity<TarjetaResponseDTO> obtenerTarjetaPorId(
            @PathVariable String usuario,
            @PathVariable Integer idTarjeta) {

        try {
            TarjetaResponseDTO response = alumnoPerfilService.obtenerTarjetaPorId(usuario, idTarjeta);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            TarjetaResponseDTO errorResponse = new TarjetaResponseDTO();
            errorResponse.setMensaje(e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
    }


    @PutMapping("/{usuario}/tarjetas/{idTarjeta}")
    public ResponseEntity<TarjetaResponseDTO> actualizarTarjeta(
            @PathVariable String usuario,
            @PathVariable Integer idTarjeta,
            @RequestBody TarjetaDTO tarjetaDTO) {

        try {
            TarjetaResponseDTO response = alumnoPerfilService.actualizarTarjeta(usuario, idTarjeta, tarjetaDTO);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            TarjetaResponseDTO errorResponse = new TarjetaResponseDTO();
            errorResponse.setMensaje(e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }


    @DeleteMapping("/{usuario}/tarjetas/{idTarjeta}")
    public ResponseEntity<Void> eliminarTarjeta(
            @PathVariable String usuario,
            @PathVariable Integer idTarjeta) {

        try {
            alumnoPerfilService.eliminarTarjeta(usuario, idTarjeta);
            return ResponseEntity.noContent().build(); // 204 No Content
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Endpoint para actualizar datos específicos del alumno (parcial)
     * Solo actualiza los campos que vienen en el DTO
     */
    @PutMapping("/{usuario}/actualizar-datos")
    public ResponseEntity<?> actualizarDatosAlumno(
            @PathVariable String usuario,
            @RequestBody ActualizarDatosAlumnoDTO datosDTO) {

        try {
            alumnoPerfilService.actualizarDatosAlumno(usuario, datosDTO);
            return ResponseEntity.ok().body(Map.of("mensaje", "Datos actualizados correctamente"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("mensaje", e.getMessage()));
        }
    }

    /**
     * ✅ NUEVO MÉTODO - Agregar DESPUÉS de actualizarDatosAlumno
     * Endpoint para actualizar solo la foto de perfil del alumno
     * Recibe una imagen y la sube a Cloudinary
     */
    @PostMapping("/{usuario}/actualizar-foto")
    public ResponseEntity<?> actualizarFotoPerfil(
            @PathVariable String usuario,
            @RequestParam("foto") MultipartFile foto) {

        try {
            // Validar que se haya enviado una imagen
            if (foto.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("mensaje", "No se ha enviado ninguna imagen"));
            }

            // Validar tipo de archivo
            String contentType = foto.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.badRequest()
                        .body(Map.of("mensaje", "El archivo debe ser una imagen"));
            }

            // Validar tamaño (máximo 5MB)
            if (foto.getSize() > 5 * 1024 * 1024) {
                return ResponseEntity.badRequest()
                        .body(Map.of("mensaje", "La imagen no debe superar los 5MB"));
            }

            // Actualizar foto de perfil
            String nuevaUrl = alumnoPerfilService.actualizarFotoPerfil(usuario, foto);

            return ResponseEntity.ok()
                    .body(Map.of(
                            "mensaje", "Foto actualizada correctamente",
                            "fotoPerfil", nuevaUrl
                    ));

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("mensaje", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("mensaje", "Error al procesar la imagen: " + e.getMessage()));
        }
    }
}