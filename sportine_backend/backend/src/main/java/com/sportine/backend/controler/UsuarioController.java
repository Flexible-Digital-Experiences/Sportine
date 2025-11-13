package com.sportine.backend.controler;


import com.sportine.backend.dto.UsuarioDetalleDTO;
import com.sportine.backend.dto.UsuarioRegistroDTO;
import com.sportine.backend.dto.UsuarioResponseDTO;
import com.sportine.backend.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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


}
