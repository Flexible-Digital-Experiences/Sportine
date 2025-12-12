package com.sportine.backend.controler;

import com.sportine.backend.dto.*;
import com.sportine.backend.exception.DatosInvalidosException;
import com.sportine.backend.exception.RecursoNoEncontradoException;
import com.sportine.backend.model.Estado;
import com.sportine.backend.repository.EstadoRepository;
import com.sportine.backend.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller de usuarios con validaciones NO INVASIVAS
 * Si no hay errores de validación, ejecuta EXACTAMENTE igual que antes
 */
@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@Slf4j
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final EstadoRepository estadoRepository;

    /**
     * ✅ AGREGADO: @Valid y BindingResult (no cambia la lógica si no hay errores)
     */
    @PostMapping("/registrar")
    public ResponseEntity<?> registrarUsuario(
            @Valid @RequestBody UsuarioRegistroDTO usuarioRegistroDTO,
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
        UsuarioResponseDTO response = usuarioService.registrarUsuario(usuarioRegistroDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * ✅ SIN CAMBIOS - No necesita validación
     */
    @GetMapping("/{usuario}")
    public ResponseEntity<UsuarioDetalleDTO> obtenerUsuario(
            @PathVariable String usuario) {

        UsuarioDetalleDTO response = usuarioService.obtenerUsuarioPorUsername(usuario);
        return ResponseEntity.ok(response);
    }

    /**
     * ✅ SIN CAMBIOS - Tu código original
     */
    @GetMapping("/estados")
    public ResponseEntity<List<Estado>> obtenerEstados() {
        List<Estado> estados = estadoRepository.findAll();
        return ResponseEntity.ok(estados);
    }

    /**
     * ✅ AGREGADO: @Valid y BindingResult (no cambia la lógica si no hay errores)
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Valid @RequestBody LoginRequestDTO loginRequestDTO,
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
        LoginResponseDTO response = usuarioService.login(loginRequestDTO);

        if (!response.isSuccess()) {
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        return ResponseEntity.ok(response);
    }

    /**
     * ✅ AGREGADO: @Valid y BindingResult (no cambia la lógica si no hay errores)
     */
    @PutMapping("/{usuario}/password")
    public ResponseEntity<?> cambiarPassword(
            @PathVariable String usuario,
            @Valid @RequestBody CambiarPasswordDTO cambiarPasswordDTO,
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
            UsuarioResponseDTO response = usuarioService.cambiarPassword(usuario, cambiarPasswordDTO);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            UsuarioResponseDTO errorResponse = new UsuarioResponseDTO();
            errorResponse.setMensaje(e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * ✅ AGREGADO: @Valid y BindingResult (no cambia la lógica si no hay errores)
     */
    @PutMapping("/{usuario}/actualizar")
    public ResponseEntity<?> actualizarDatosUsuario(
            @PathVariable String usuario,
            @Valid @RequestBody ActualizarUsuarioDTO datosDTO,
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
            usuarioService.actualizarDatosUsuario(usuario, datosDTO);
            return ResponseEntity.ok()
                    .body(Map.of(
                            "mensaje", "Datos actualizados correctamente",
                            "usuario", usuario,
                            "nota", "El username NO fue modificado (PRIMARY KEY)"
                    ));
        } catch (DatosInvalidosException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "mensaje", e.getMessage(),
                            "error", true
                    ));
        } catch (RecursoNoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "mensaje", e.getMessage(),
                            "error", true
                    ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "mensaje", "Error interno del servidor",
                            "error", true
                    ));
        }
    }
}