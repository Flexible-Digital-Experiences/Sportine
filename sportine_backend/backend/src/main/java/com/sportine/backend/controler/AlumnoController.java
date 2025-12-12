package com.sportine.backend.controler;

import com.sportine.backend.dto.*;
import com.sportine.backend.service.AlumnoPerfilService;
import com.sportine.backend.service.AlumnoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller de alumnos con validaciones NO INVASIVAS
 * Si no hay errores de validación, ejecuta EXACTAMENTE igual que antes
 */
@RestController
@RequestMapping("/api/alumnos")
@RequiredArgsConstructor
@Slf4j
public class AlumnoController {

    private final AlumnoService alumnoService;
    private final AlumnoPerfilService alumnoPerfilService;

    /**
     * ✅ SIN CAMBIOS - No necesita validación
     */
    @GetMapping("/home/{usuario}")
    public ResponseEntity<HomeAlumnoDTO> obtenerHomeAlumno(
            @PathVariable String usuario) {

        HomeAlumnoDTO response = alumnoService.obtenerHomeAlumno(usuario);
        return ResponseEntity.ok(response);
    }

    /**
     * ✅ AGREGADO: @Valid y BindingResult (no cambia la lógica si no hay errores)
     */
    @PostMapping("/perfil")
    public ResponseEntity<?> crearPerfil(
            @Valid @RequestBody PerfilAlumnoDTO perfilAlumnoDTO,
            BindingResult result) {

        // ✅ SOLO SI HAY ERRORES de validación, retorna errores
        if (result.hasErrors()) {
            Map<String, String> errores = new HashMap<>();
            for (FieldError error : result.getFieldErrors()) {
                errores.put(error.getField(), error.getDefaultMessage());
            }
            return ResponseEntity.badRequest().body(errores);
        }

        // ✅ SI NO HAY ERRORES, ejecuta TU CÓDIGO ORIGINAL SIN CAMBIOS
        try {
            PerfilAlumnoResponseDTO response = alumnoPerfilService.crearPerfilAlumno(perfilAlumnoDTO);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            PerfilAlumnoResponseDTO errorResponse = new PerfilAlumnoResponseDTO();
            errorResponse.setMensaje(e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * ✅ SIN CAMBIOS - Tu código original
     */
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

    /**
     * ✅ AGREGADO: @Valid y BindingResult (no cambia la lógica si no hay errores)
     */
    @PutMapping("/perfil/{usuario}")
    public ResponseEntity<?> actualizarPerfil(
            @PathVariable String usuario,
            @Valid @RequestBody PerfilAlumnoDTO perfilAlumnoDTO,
            BindingResult result) {

        // ✅ SOLO SI HAY ERRORES de validación, retorna errores
        if (result.hasErrors()) {
            Map<String, String> errores = new HashMap<>();
            for (FieldError error : result.getFieldErrors()) {
                errores.put(error.getField(), error.getDefaultMessage());
            }
            return ResponseEntity.badRequest().body(errores);
        }

        // ✅ SI NO HAY ERRORES, ejecuta TU CÓDIGO ORIGINAL SIN CAMBIOS
        try {
            PerfilAlumnoResponseDTO response = alumnoPerfilService.actualizarPerfilAlumno(usuario, perfilAlumnoDTO);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            PerfilAlumnoResponseDTO errorResponse = new PerfilAlumnoResponseDTO();
            errorResponse.setMensaje(e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * ✅ AGREGADO: @Valid y BindingResult (no cambia la lógica si no hay errores)
     */
    @PostMapping("/{usuario}/tarjetas")
    public ResponseEntity<?> agregarTarjeta(
            @PathVariable String usuario,
            @Valid @RequestBody TarjetaDTO tarjetaDTO,
            BindingResult result) {

        // ✅ SOLO SI HAY ERRORES de validación, retorna errores
        if (result.hasErrors()) {
            Map<String, String> errores = new HashMap<>();
            for (FieldError error : result.getFieldErrors()) {
                errores.put(error.getField(), error.getDefaultMessage());
            }
            return ResponseEntity.badRequest().body(errores);
        }

        // ✅ SI NO HAY ERRORES, ejecuta TU CÓDIGO ORIGINAL SIN CAMBIOS
        try {
            TarjetaResponseDTO response = alumnoPerfilService.agregarTarjeta(usuario, tarjetaDTO);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            TarjetaResponseDTO errorResponse = new TarjetaResponseDTO();
            errorResponse.setMensaje(e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * ✅ SIN CAMBIOS - Tu código original
     */
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

    /**
     * ✅ SIN CAMBIOS - Tu código original
     */
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

    /**
     * ✅ AGREGADO: @Valid y BindingResult (no cambia la lógica si no hay errores)
     */
    @PutMapping("/{usuario}/tarjetas/{idTarjeta}")
    public ResponseEntity<?> actualizarTarjeta(
            @PathVariable String usuario,
            @PathVariable Integer idTarjeta,
            @Valid @RequestBody TarjetaDTO tarjetaDTO,
            BindingResult result) {

        // ✅ SOLO SI HAY ERRORES de validación, retorna errores
        if (result.hasErrors()) {
            Map<String, String> errores = new HashMap<>();
            for (FieldError error : result.getFieldErrors()) {
                errores.put(error.getField(), error.getDefaultMessage());
            }
            return ResponseEntity.badRequest().body(errores);
        }

        // ✅ SI NO HAY ERRORES, ejecuta TU CÓDIGO ORIGINAL SIN CAMBIOS
        try {
            TarjetaResponseDTO response = alumnoPerfilService.actualizarTarjeta(usuario, idTarjeta, tarjetaDTO);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            TarjetaResponseDTO errorResponse = new TarjetaResponseDTO();
            errorResponse.setMensaje(e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * ✅ SIN CAMBIOS - Tu código original
     */
    @DeleteMapping("/{usuario}/tarjetas/{idTarjeta}")
    public ResponseEntity<Void> eliminarTarjeta(
            @PathVariable String usuario,
            @PathVariable Integer idTarjeta) {

        try {
            alumnoPerfilService.eliminarTarjeta(usuario, idTarjeta);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * ✅ AGREGADO: @Valid y BindingResult (no cambia la lógica si no hay errores)
     */
    @PutMapping("/{usuario}/actualizar-datos")
    public ResponseEntity<?> actualizarDatosAlumno(
            @PathVariable String usuario,
            @Valid @RequestBody ActualizarDatosAlumnoDTO datosDTO,
            BindingResult result) {

        // ✅ SOLO SI HAY ERRORES de validación, retorna errores
        if (result.hasErrors()) {
            Map<String, String> errores = new HashMap<>();
            for (FieldError error : result.getFieldErrors()) {
                errores.put(error.getField(), error.getDefaultMessage());
            }
            return ResponseEntity.badRequest().body(errores);
        }

        // ✅ SI NO HAY ERRORES, ejecuta TU CÓDIGO ORIGINAL SIN CAMBIOS
        try {
            alumnoPerfilService.actualizarDatosAlumno(usuario, datosDTO);
            return ResponseEntity.ok().body(Map.of("mensaje", "Datos actualizados correctamente"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("mensaje", e.getMessage()));
        }
    }

    /**
     * ✅ SIN CAMBIOS - Tu código original EXACTO
     */
    @PostMapping("/{usuario}/actualizar-foto")
    public ResponseEntity<?> actualizarFotoPerfil(
            @PathVariable String usuario,
            @RequestParam("foto") MultipartFile foto) {

        try {
            if (foto.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("mensaje", "No se ha enviado ninguna imagen"));
            }

            String contentType = foto.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.badRequest()
                        .body(Map.of("mensaje", "El archivo debe ser una imagen"));
            }

            if (foto.getSize() > 5 * 1024 * 1024) {
                return ResponseEntity.badRequest()
                        .body(Map.of("mensaje", "La imagen no debe superar los 5MB"));
            }

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