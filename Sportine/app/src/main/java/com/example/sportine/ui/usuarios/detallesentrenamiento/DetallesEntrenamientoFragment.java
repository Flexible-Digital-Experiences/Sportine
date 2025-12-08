package com.example.sportine.ui.usuarios.detallesentrenamiento;

import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.bumptech.glide.Glide;
import com.example.sportine.databinding.FragmentAlumnoDetallesEntrenamientoBinding;
import com.example.sportine.models.AsignarEjercicioDTO;

import java.util.List;

public class DetallesEntrenamientoFragment extends Fragment {

    private FragmentAlumnoDetallesEntrenamientoBinding binding;
    private DetallesViewModel viewModel;
    private EjerciciosAdapter adapter;
    private Integer idEntrenamiento;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            idEntrenamiento = getArguments().getInt("idEntrenamiento", -1);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAlumnoDetallesEntrenamientoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupRecyclerView();
        setupViewModel();
        setupListeners();

        if (idEntrenamiento != -1) {
            viewModel.cargarDetalles(idEntrenamiento);
        } else {
            Toast.makeText(getContext(), "Error: Entrenamiento no identificado", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupRecyclerView() {
        adapter = new EjerciciosAdapter();
        binding.recyclerEjercicios.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerEjercicios.setAdapter(adapter);

        // Listener para actualizar estado individual (CheckBox)
        adapter.setOnEjercicioCheckListener((ejercicio, isChecked) -> {
            ejercicio.setCompletado(isChecked);
            viewModel.cambiarEstadoEjercicio(ejercicio.getIdAsignado(), isChecked);

            // 🔥 ACTUALIZAR LA COPA EN TIEMPO REAL
            actualizarProgresoCopa();
        });
    }

    // ✅ MÉTODO INTELIGENTE: Ilumina la copa y valida dificultad
    private void actualizarProgresoCopa() {
        // 1. Si la copa está oculta (porque es fácil), no gastamos recursos calculando
        if (binding.imgCopaLogro.getVisibility() == View.GONE) return;

        if (viewModel.getDetalle().getValue() == null) return;
        List<AsignarEjercicioDTO> lista = viewModel.getDetalle().getValue().getEjercicios();

        if (lista == null || lista.isEmpty()) return;

        int total = lista.size();
        int completados = 0;
        for (AsignarEjercicioDTO e : lista) {
            if (e.isCompletado()) completados++;
        }

        // Porcentaje (0.0 a 1.0)
        float porcentaje = (float) completados / total;

        // --- EFECTOS VISUALES ---

        // A. Opacidad: Sube de 0.3 (apagado) a 1.0 (encendido)
        binding.imgCopaLogro.setAlpha(0.3f + (0.7f * porcentaje));

        // B. Color: Se satura de Gris (B&N) a Color Real
        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(porcentaje);
        binding.imgCopaLogro.setColorFilter(new ColorMatrixColorFilter(matrix));

        // --- DESBLOQUEO ---
        if (completados == total) {
            if (!binding.imgCopaLogro.isClickable()) {
                // ¡YA ACABÓ! Habilitamos el click
                binding.imgCopaLogro.setClickable(true);

                // Efecto "POP" para avisar que se desbloqueó
                binding.imgCopaLogro.animate().scaleX(1.3f).scaleY(1.3f).setDuration(200)
                        .withEndAction(() -> binding.imgCopaLogro.animate().scaleX(1f).scaleY(1f).start())
                        .start();

                // Quitamos cualquier filtro para que se vea el PNG original brillante
                binding.imgCopaLogro.clearColorFilter();

                Toast.makeText(getContext(), "¡Logro desbloqueado! Toca la copa para presumirlo 🏆", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Aún le falta, deshabilitamos click
            binding.imgCopaLogro.setClickable(false);
        }
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(DetallesViewModel.class);

        // Observar datos del entrenamiento
        viewModel.getDetalle().observe(getViewLifecycleOwner(), dto -> {
            binding.textTituloEntrenamiento.setText(dto.getTitulo());
            binding.textFecha.setText(dto.getFecha() + " " + dto.getHora());
            binding.textNombreEntrenador.setText(dto.getNombreEntrenador());
            binding.textEspecialidad.setText(dto.getEspecialidadEntrenador());
            binding.textDescripcion.setText(dto.getObjetivo());

            // --- LÓGICA DE DIFICULTAD EXCLUSIVA ---
            String dificultad = dto.getDificultad(); // Asegúrate que tu DTO tenga este campo
            if (dificultad == null) dificultad = "Medio"; // Fallback por seguridad

            // Solo mostramos la copa si es un reto real
            boolean esDificil = dificultad.equalsIgnoreCase("Dificil") ||
                    dificultad.equalsIgnoreCase("Avanzado") ||
                    dificultad.equalsIgnoreCase("Experto");

            if (esDificil) {
                binding.imgCopaLogro.setVisibility(View.VISIBLE);
            } else {
                binding.imgCopaLogro.setVisibility(View.GONE);
            }
            // --------------------------------------

            if (dto.getEjercicios() != null) {
                binding.textContadorEjercicios.setText(dto.getEjercicios().size() + " ejercicios");
                adapter.setEjercicios(dto.getEjercicios());

                // Calculamos estado inicial de la copa (si es visible)
                if (esDificil) {
                    actualizarProgresoCopa();
                }
            }

            if (dto.getFotoEntrenador() != null) {
                Glide.with(this).load(dto.getFotoEntrenador()).into(binding.imgAvatarEntrenador);
            }
        });

        // Observar éxito al completar
        viewModel.getEntrenamientoCompletado().observe(getViewLifecycleOwner(), exito -> {
            if (exito) {
                Toast.makeText(getContext(), "¡Entrenamiento completado! 💪", Toast.LENGTH_SHORT).show();
                if (getActivity() != null) getActivity().onBackPressed();
            }
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null) Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
        });
    }

    private void setupListeners() {
        binding.btnBack.setOnClickListener(v -> {
            if (getActivity() != null) getActivity().onBackPressed();
        });

        // Feedback visual de los Sliders
        binding.sliderCansancio.addOnChangeListener((slider, value, fromUser) -> {
            binding.labelCansancio.setText("Nivel de Cansancio (" + (int)value + "/10)");
        });
        binding.sliderDificultad.addOnChangeListener((slider, value, fromUser) -> {
            binding.labelDificultad.setText("Dificultad (" + (int)value + "/10)");
        });

        // 🏆 CLICK EN LA COPA: Publicar Logro = TRUE
        binding.imgCopaLogro.setOnClickListener(v -> {
            Toast.makeText(getContext(), "¡Presumiendo logro! 🏆🔥", Toast.LENGTH_SHORT).show();
            enviarCompletado(true);
        });

        // ✅ CLICK EN BOTÓN NORMAL: Publicar Logro = FALSE (Privado)
        binding.btnMarcarCompletado.setOnClickListener(v -> {
            // Validaciones de ejercicios pendientes
            boolean hayPendientes = false;
            if (viewModel.getDetalle().getValue() != null && viewModel.getDetalle().getValue().getEjercicios() != null) {
                for (AsignarEjercicioDTO ejercicio : viewModel.getDetalle().getValue().getEjercicios()) {
                    if (!ejercicio.isCompletado()) {
                        hayPendientes = true;
                        break;
                    }
                }
            }

            if (hayPendientes) {
                new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                        .setTitle("Ejercicios pendientes")
                        .setMessage("No has marcado todos los ejercicios. ¿Deseas finalizar de todas formas?")
                        .setPositiveButton("Sí, terminar", (dialog, which) -> enviarCompletado(false))
                        .setNegativeButton("No", null)
                        .show();
            } else {
                enviarCompletado(false);
            }
        });
    }

    private void enviarCompletado(boolean publicarLogro) {
        String comentario = "";
        if (binding.inputComentario.getText() != null) {
            comentario = binding.inputComentario.getText().toString();
        }

        int cansancio = (int) binding.sliderCansancio.getValue();
        int dificultad = (int) binding.sliderDificultad.getValue();

        String animo = "Normal";
        int chipId = binding.chipGroupAnimo.getCheckedChipId();
        if (chipId != -1) {
            com.google.android.material.chip.Chip chip = binding.getRoot().findViewById(chipId);
            if(chip != null) animo = chip.getText().toString();
        }

        // Pasamos el booleano al ViewModel para que le diga al backend qué hacer
        viewModel.completarEntrenamiento(idEntrenamiento, comentario, cansancio, dificultad, animo, publicarLogro);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}