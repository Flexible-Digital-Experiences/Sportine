package com.sportine.backend.controler;


import com.sportine.backend.dto.*;
import com.sportine.backend.exception.DatosInvalidosException;
import com.sportine.backend.exception.RecursoNoEncontradoException;
import com.sportine.backend.model.Estado;
import com.sportine.backend.repository.EstadoRepository;
import com.sportine.backend.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PostMapping("/registrar")
    public ResponseEntity<UsuarioResponseDTO> registrarUsuario(
            @RequestBody UsuarioRegistroDTO usuarioRegistroDTO) {
        UsuarioResponseDTO response = usuarioService.registrarUsuario(usuarioRegistroDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{usuario}")
    public ResponseEntity<UsuarioDetalleDTO> obtenerUsuario(
            @PathVariable String usuario) {  // ← Toma el valor de la URL

        UsuarioDetalleDTO response = usuarioService.obtenerUsuarioPorUsername(usuario);
        return ResponseEntity.ok(response);  // ← HTTP 200 OK
    }

    private EstadoRepository estadoRepository;
    @GetMapping("/estados")
    public ResponseEntity<List<Estado>> obtenerEstados() {
        List<Estado> estados = estadoRepository.findAll();
        return ResponseEntity.ok(estados);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(
            @RequestBody LoginRequestDTO loginRequestDTO) {

        LoginResponseDTO response = usuarioService.login(loginRequestDTO);

        // Si el login falla, devuelve 401 Unauthorized
        if (!response.isSuccess()) {
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        // Si el login es exitoso, devuelve 200 OK
        return ResponseEntity.ok(response);
    }


    @PutMapping("/{usuario}/password")
    public ResponseEntity<UsuarioResponseDTO> cambiarPassword(
            @PathVariable String usuario,
            @RequestBody CambiarPasswordDTO cambiarPasswordDTO) {

        try {
            UsuarioResponseDTO response = usuarioService.cambiarPassword(usuario, cambiarPasswordDTO);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            UsuarioResponseDTO errorResponse = new UsuarioResponseDTO();
            errorResponse.setMensaje(e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{usuario}/actualizar")
    public ResponseEntity<?> actualizarDatosUsuario(
            @PathVariable String usuario,
            @RequestBody ActualizarUsuarioDTO datosDTO) {

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
