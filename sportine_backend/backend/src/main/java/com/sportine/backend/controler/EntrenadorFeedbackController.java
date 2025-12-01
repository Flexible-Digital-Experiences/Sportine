package com.sportine.backend.controler;

import com.sportine.backend.dto.FeedbackResumenDTO;
import com.sportine.backend.model.FeedbackEntrenamiento;
import com.sportine.backend.repository.FeedbackEntrenamientoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/entrenador/feedback")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EntrenadorFeedbackController {

    private final FeedbackEntrenamientoRepository feedbackRepository;

    @GetMapping
    public ResponseEntity<List<FeedbackResumenDTO>> obtenerFeedbacks(Authentication authentication) {
        String usuarioEntrenador = authentication.getName();

        List<FeedbackEntrenamiento> feedbacks = feedbackRepository.findFeedbackPorEntrenador(usuarioEntrenador);

        List<FeedbackResumenDTO> dtos = feedbacks.stream().map(f -> new FeedbackResumenDTO(
                f.getIdFeedback(),
                f.getAlumno().getNombre() + " " + f.getAlumno().getApellidos(),
                // Asumiendo que InformaciónAlumno tiene la foto, si no, null por ahora
                null,
                f.getEntrenamiento().getTituloEntrenamiento(),
                f.getNivelCansancio(),
                f.getDificultadPercibida(),
                f.getEstadoAnimo(),
                f.getComentarios(),
                f.getFechaFeedback()
        )).collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }
}