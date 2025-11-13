package com.sportine.backend.controler;

import com.sportine.backend.dto.HomeAlumnoDTO;
import com.sportine.backend.service.AlumnoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/alumnos")
@RequiredArgsConstructor
public class AlumnoController {

    private final AlumnoService alumnoService;

    @GetMapping("/home/{usuario}")
    public ResponseEntity<HomeAlumnoDTO> obtenerHomeAlumno(
            @PathVariable String usuario) {

        HomeAlumnoDTO response = alumnoService.obtenerHomeAlumno(usuario);
        return ResponseEntity.ok(response);
    }
}