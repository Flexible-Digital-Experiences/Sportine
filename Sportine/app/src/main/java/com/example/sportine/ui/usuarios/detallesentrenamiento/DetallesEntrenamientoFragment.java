package com.example.sportine.ui.usuarios.detallesentrenamiento;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.sportine.R;
import com.example.sportine.databinding.FragmentAlumnoDetallesEntrenamientoBinding;
import com.example.sportine.ui.usuarios.detallesentrenamiento.EjerciciosAdapter;

import java.util.ArrayList;
import java.util.List;

public class DetallesEntrenamientoFragment extends Fragment {

    private FragmentAlumnoDetallesEntrenamientoBinding binding;
    private String deporte;
    private String tituloEntrenamiento;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Recibir argumentos del bundle
        if (getArguments() != null) {
            deporte = getArguments().getString("deporte", "Fútbol");
            tituloEntrenamiento = getArguments().getString("titulo", "Entrenamiento especializado");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAlumnoDetallesEntrenamientoBinding.inflate(inflater, container, false);

        setupUI();
        setupRecyclerEjercicios();

        return binding.getRoot();
    }

    private void setupUI() {
        // Botón de regreso
        binding.btnBack.setOnClickListener(v -> {
            requireActivity().onBackPressed();
        });

        // Configurar textos según el deporte
        binding.textFecha.setText("20 de octubre de 2025, 16:00");
        binding.textTituloEntrenamiento.setText(tituloEntrenamiento != null ?
                tituloEntrenamiento : "Entrenamiento especializado");

        // Información del entrenador (puedes personalizar según el deporte)
        binding.textNombreEntrenador.setText(obtenerNombreEntrenador(deporte));
        binding.textEspecialidad.setText("Entrenador Profesional");

        // Descripción del entrenamiento
        binding.textDescripcion.setText(obtenerDescripcion(deporte));

        // Configurar imágenes
        binding.imgAvatarEntrenador.setImageResource(R.drawable.avatar_user_male);
        binding.imgDeporteIcon.setImageResource(obtenerIconoDeporte(deporte));
    }

    private void setupRecyclerEjercicios() {
        List<EjerciciosAdapter.Ejercicio> ejercicios = obtenerEjerciciosPorDeporte(deporte);

        EjerciciosAdapter adapter = new EjerciciosAdapter(ejercicios);
        binding.recyclerEjercicios.setAdapter(adapter);
        binding.recyclerEjercicios.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private String obtenerNombreEntrenador(String deporte) {
        if (deporte == null) return "Carlos Hernández";

        switch (deporte.toLowerCase()) {
            case "fútbol":
            case "futbol":
                return "Carlos Hernández";
            case "natación":
            case "natacion":
                return "Ana Martínez";
            case "beisbol":
                return "Roberto García";
            case "tenis":
                return "Laura Sánchez";
            case "boxeo":
                return "Miguel Rodríguez";
            case "básquetbol":
            case "basquetbol":
            case "basket":
                return "David Torres";
            default:
                return "Entrenador Profesional";
        }
    }

    private String obtenerDescripcion(String deporte) {
        if (deporte == null) return "Entrenamiento diseñado para mejorar tus habilidades.";

        switch (deporte.toLowerCase()) {
            case "fútbol":
            case "futbol":
                return "Este entrenamiento está diseñado para mejorar tu resistencia cardiovascular y control del balón. Trabajaremos en ejercicios de alta intensidad combinados con técnicas de precisión.";
            case "natación":
            case "natacion":
                return "Sesión enfocada en perfeccionar tu técnica de brazada y aumentar tu velocidad en el agua. Incluye ejercicios de respiración y resistencia.";
            case "beisbol":
                return "Práctica intensiva de pitcheo con énfasis en precisión y velocidad. Incluye ejercicios de calentamiento específico y trabajo de brazos.";
            case "tenis":
                return "Entrenamiento centrado en mejorar tu saque y técnica de volea. Trabajaremos movimientos de pies y posicionamiento en la cancha.";
            case "boxeo":
                return "Sesión completa de técnicas de golpeo y movimientos defensivos. Incluye trabajo de saco, guantes y sparring controlado.";
            case "básquetbol":
            case "basquetbol":
            case "basket":
                return "Práctica de tiros libres, triples y jugadas tácticas. Incluye ejercicios de pase, recepción y estrategias de juego en equipo.";
            default:
                return "Entrenamiento diseñado para mejorar tus habilidades deportivas. Incluye ejercicios de técnica, resistencia y trabajo táctico.";
        }
    }

    private int obtenerIconoDeporte(String deporte) {
        if (deporte == null) return R.drawable.logo_sportine;

        switch (deporte.toLowerCase()) {
            case "fútbol":
            case "futbol":
                return R.drawable.balon_futbol;
            case "natación":
            case "natacion":
                return R.drawable.logo_sportine; // Cambia por icono de natación si lo tienes
            case "beisbol":
                return R.drawable.logo_sportine; // Cambia por icono de beisbol si lo tienes
            case "tenis":
                return R.drawable.logo_sportine; // Cambia por icono de tenis si lo tienes
            case "boxeo":
                return R.drawable.logo_sportine; // Cambia por icono de boxeo si lo tienes
            case "básquetbol":
            case "basquetbol":
            case "basket":
                return R.drawable.logo_sportine; // Cambia por icono de basket si lo tienes
            default:
                return R.drawable.logo_sportine;
        }
    }

    private List<EjerciciosAdapter.Ejercicio> obtenerEjerciciosPorDeporte(String deporte) {
        List<EjerciciosAdapter.Ejercicio> ejercicios = new ArrayList<>();

        if (deporte == null) deporte = "Fútbol";

        switch (deporte.toLowerCase()) {
            case "fútbol":
            case "futbol":
                ejercicios.add(new EjerciciosAdapter.Ejercicio(
                        "Calentamiento dinámico",
                        "Ejercicios de movilidad articular",
                        "10 min"));
                ejercicios.add(new EjerciciosAdapter.Ejercicio(
                        "Ejercicios de conducción",
                        "Control del balón con ambas piernas",
                        "15 min"));
                ejercicios.add(new EjerciciosAdapter.Ejercicio(
                        "Pases de precisión",
                        "Trabajo en parejas a diferentes distancias",
                        "20 min"));
                ejercicios.add(new EjerciciosAdapter.Ejercicio(
                        "Circuito de velocidad",
                        "Sprints cortos con cambios de dirección",
                        "15 min"));
                ejercicios.add(new EjerciciosAdapter.Ejercicio(
                        "Estiramientos finales",
                        "Relajación muscular y flexibilidad",
                        "10 min"));
                break;

            case "natación":
            case "natacion":
                ejercicios.add(new EjerciciosAdapter.Ejercicio(
                        "Calentamiento en seco",
                        "Movilidad de hombros y brazos",
                        "8 min"));
                ejercicios.add(new EjerciciosAdapter.Ejercicio(
                        "Series de técnica",
                        "Crol y espalda con enfoque en brazada",
                        "25 min"));
                ejercicios.add(new EjerciciosAdapter.Ejercicio(
                        "Ejercicios de respiración",
                        "Práctica de patrón respiratorio",
                        "12 min"));
                ejercicios.add(new EjerciciosAdapter.Ejercicio(
                        "Series de velocidad",
                        "Sprints de 50m con descansos",
                        "20 min"));
                ejercicios.add(new EjerciciosAdapter.Ejercicio(
                        "Nado suave",
                        "Recuperación activa",
                        "5 min"));
                break;

            case "beisbol":
                ejercicios.add(new EjerciciosAdapter.Ejercicio(
                        "Calentamiento de brazos",
                        "Rotaciones y estiramientos específicos",
                        "12 min"));
                ejercicios.add(new EjerciciosAdapter.Ejercicio(
                        "Práctica de lanzamiento",
                        "Mecánica y precisión del pitcheo",
                        "25 min"));
                ejercicios.add(new EjerciciosAdapter.Ejercicio(
                        "Bateo en jaula",
                        "Trabajo de swing y timing",
                        "20 min"));
                ejercicios.add(new EjerciciosAdapter.Ejercicio(
                        "Trabajo defensivo",
                        "Fildeo y tiros a bases",
                        "18 min"));
                ejercicios.add(new EjerciciosAdapter.Ejercicio(
                        "Enfriamiento",
                        "Estiramientos y hielo en hombro",
                        "10 min"));
                break;

            case "tenis":
                ejercicios.add(new EjerciciosAdapter.Ejercicio(
                        "Calentamiento general",
                        "Trote y movilidad articular",
                        "10 min"));
                ejercicios.add(new EjerciciosAdapter.Ejercicio(
                        "Práctica de saque",
                        "Técnica y potencia del servicio",
                        "20 min"));
                ejercicios.add(new EjerciciosAdapter.Ejercicio(
                        "Golpes de fondo",
                        "Derecha y revés desde línea base",
                        "25 min"));
                ejercicios.add(new EjerciciosAdapter.Ejercicio(
                        "Volea y smash",
                        "Trabajo en la red",
                        "15 min"));
                ejercicios.add(new EjerciciosAdapter.Ejercicio(
                        "Estiramientos",
                        "Recuperación y flexibilidad",
                        "10 min"));
                break;

            case "boxeo":
                ejercicios.add(new EjerciciosAdapter.Ejercicio(
                        "Saltar la cuerda",
                        "Calentamiento cardiovascular",
                        "10 min"));
                ejercicios.add(new EjerciciosAdapter.Ejercicio(
                        "Trabajo de sombra",
                        "Técnica de golpes y movimiento",
                        "15 min"));
                ejercicios.add(new EjerciciosAdapter.Ejercicio(
                        "Saco pesado",
                        "Potencia y combinaciones",
                        "20 min"));
                ejercicios.add(new EjerciciosAdapter.Ejercicio(
                        "Guantes de enfoque",
                        "Precisión y velocidad",
                        "15 min"));
                ejercicios.add(new EjerciciosAdapter.Ejercicio(
                        "Core y enfriamiento",
                        "Abdominales y estiramiento",
                        "10 min"));
                break;

            case "básquetbol":
            case "basquetbol":
            case "basket":
                ejercicios.add(new EjerciciosAdapter.Ejercicio(
                        "Calentamiento dinámico",
                        "Ejercicios de movilidad y activación",
                        "10 min"));
                ejercicios.add(new EjerciciosAdapter.Ejercicio(
                        "Práctica de tiro",
                        "Tiros libres y triples desde diferentes ángulos",
                        "20 min"));
                ejercicios.add(new EjerciciosAdapter.Ejercicio(
                        "Ejercicios de pase",
                        "Pase de pecho, picado y sobre la cabeza",
                        "15 min"));
                ejercicios.add(new EjerciciosAdapter.Ejercicio(
                        "Jugadas tácticas",
                        "Práctica de estrategias ofensivas y defensivas",
                        "25 min"));
                ejercicios.add(new EjerciciosAdapter.Ejercicio(
                        "Estiramientos finales",
                        "Recuperación muscular",
                        "10 min"));
                break;

            default:
                ejercicios.add(new EjerciciosAdapter.Ejercicio(
                        "Calentamiento general",
                        "Preparación física básica",
                        "10 min"));
                ejercicios.add(new EjerciciosAdapter.Ejercicio(
                        "Ejercicios técnicos",
                        "Trabajo específico del deporte",
                        "30 min"));
                ejercicios.add(new EjerciciosAdapter.Ejercicio(
                        "Práctica táctica",
                        "Situaciones de juego",
                        "20 min"));
                ejercicios.add(new EjerciciosAdapter.Ejercicio(
                        "Enfriamiento",
                        "Estiramientos y recuperación",
                        "10 min"));
                break;
        }

        return ejercicios;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}