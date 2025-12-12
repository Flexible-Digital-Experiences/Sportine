package com.example.sportine.ui.usuarios.detallesentrenamiento;

// ✅ 1. IMPORTACIONES AGREGADAS (Esto soluciona el error de la R y del DTO)
import com.example.sportine.R;
import com.example.sportine.models.DetalleEntrenamientoDTO;

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
        if (binding.imgCopaLogro.getVisibility() == View.GONE) return;

        if (viewModel.getDetalle().getValue() == null) return;
        List<AsignarEjercicioDTO> lista = viewModel.getDetalle().getValue().getEjercicios();

        if (lista == null || lista.isEmpty()) return;

        int total = lista.size();
        int completados = 0;
        for (AsignarEjercicioDTO e : lista) {
            if (e.isCompletado()) completados++;
        }

        float porcentaje = (float) completados / total;

        // --- EFECTOS VISUALES ---
        binding.imgCopaLogro.setAlpha(0.3f + (0.7f * porcentaje));
        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(porcentaje);
        binding.imgCopaLogro.setColorFilter(new ColorMatrixColorFilter(matrix));

        // --- DESBLOQUEO ---
        if (completados == total) {
            if (!binding.imgCopaLogro.isClickable()) {
                binding.imgCopaLogro.setClickable(true);
                binding.imgCopaLogro.animate().scaleX(1.3f).scaleY(1.3f).setDuration(200)
                        .withEndAction(() -> binding.imgCopaLogro.animate().scaleX(1f).scaleY(1f).start())
                        .start();
                binding.imgCopaLogro.clearColorFilter();
                Toast.makeText(getContext(), "¡Logro desbloqueado! Toca la copa para presumirlo 🏆", Toast.LENGTH_SHORT).show();
            }
        } else {
            binding.imgCopaLogro.setClickable(false);
        }
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(DetallesViewModel.class);

        // Observar datos del entrenamiento
        viewModel.getDetalle().observe(getViewLifecycleOwner(), dto -> {
            // --- DATOS BÁSICOS ---
            binding.textTituloEntrenamiento.setText(dto.getTitulo());
            binding.textFecha.setText(dto.getFecha() + " " + dto.getHora());
            binding.textNombreEntrenador.setText(dto.getNombreEntrenador());
            binding.textEspecialidad.setText(dto.getEspecialidadEntrenador());
            binding.textDescripcion.setText(dto.getObjetivo());

            // --- LÓGICA DE DIFICULTAD ---
            String dificultad = dto.getDificultad();
            if (dificultad == null) dificultad = "Medio";

            boolean esDificil = dificultad.equalsIgnoreCase("Dificil") ||
                    dificultad.equalsIgnoreCase("Avanzado") ||
                    dificultad.equalsIgnoreCase("Experto");

            if (esDificil) {
                binding.imgCopaLogro.setVisibility(View.VISIBLE);
            } else {
                binding.imgCopaLogro.setVisibility(View.GONE);
            }

            // --- LÓGICA DE EJERCICIOS ---
            if (dto.getEjercicios() != null) {
                binding.textContadorEjercicios.setText(dto.getEjercicios().size() + " ejercicios");
                adapter.setEjercicios(dto.getEjercicios());

                if (esDificil) {
                    actualizarProgresoCopa();
                }
            }

            // --- FOTO DE PERFIL ---
            if (dto.getFotoEntrenador() != null && !dto.getFotoEntrenador().isEmpty()) {
                Glide.with(this)
                        .load(dto.getFotoEntrenador())
                        .placeholder(R.drawable.ic_avatar_default)
                        .circleCrop()
                        .into(binding.imgAvatarEntrenador);
            }

            // =================================================================
            // NUEVA LÓGICA: ÍCONO DEL DEPORTE
            // =================================================================
            if (dto.getDeporteIcono() != null) {
                int iconResId = obtenerIconoDeporte(dto.getDeporteIcono());
                binding.imgDeporteIcon.setImageResource(iconResId);
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

        binding.sliderCansancio.addOnChangeListener((slider, value, fromUser) -> {
            binding.labelCansancio.setText("Nivel de Cansancio (" + (int)value + "/10)");
        });
        binding.sliderDificultad.addOnChangeListener((slider, value, fromUser) -> {
            binding.labelDificultad.setText("Dificultad (" + (int)value + "/10)");
        });

        binding.imgCopaLogro.setOnClickListener(v -> {
            Toast.makeText(getContext(), "¡Presumiendo logro! 🏆🔥", Toast.LENGTH_SHORT).show();
            enviarCompletado(true);
        });

        binding.btnMarcarCompletado.setOnClickListener(v -> {
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
        // 1. Obtener y limpiar el comentario
        String comentario = "";
        if (binding.inputComentario.getText() != null) {
            comentario = binding.inputComentario.getText().toString().trim();
        }

        // 🚨 VALIDACIÓN 1: Comentarios (Max 255 caracteres)
        if (comentario.length() > 255) {
            Toast.makeText(getContext(), "Los comentarios no pueden exceder 255 caracteres", Toast.LENGTH_LONG).show();
            binding.inputLayoutComentario.setError("Máximo 255 caracteres"); // Feedback visual extra en el input
            return; // Detenemos el envío
        } else {
            binding.inputLayoutComentario.setError(null); // Limpiamos error si ya corrigió
        }

        // 2. Obtener valores de los sliders
        int cansancio = (int) binding.sliderCansancio.getValue();
        int dificultad = (int) binding.sliderDificultad.getValue();

        // 🚨 VALIDACIÓN 2: Rangos (1-10)
        // Aunque el Slider visualmente lo limita, validamos por seguridad lógica
        if (cansancio < 1 || cansancio > 10) {
            Toast.makeText(getContext(), "El nivel de cansancio debe ser entre 1 y 10", Toast.LENGTH_SHORT).show();
            return;
        }
        if (dificultad < 1 || dificultad > 10) {
            Toast.makeText(getContext(), "La dificultad percibida debe ser entre 1 y 10", Toast.LENGTH_SHORT).show();
            return;
        }

        // 3. Obtener estado de ánimo
        String animo = "Normal";
        int chipId = binding.chipGroupAnimo.getCheckedChipId();
        if (chipId != -1) {
            com.google.android.material.chip.Chip chip = binding.getRoot().findViewById(chipId);
            if(chip != null) animo = chip.getText().toString();
        }

        // ✅ Si todo pasa, enviamos al ViewModel
        viewModel.completarEntrenamiento(idEntrenamiento, comentario, cansancio, dificultad, animo, publicarLogro);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // ✅ MÉTODO HELPER MEJORADO (A prueba de errores de texto)
    private int obtenerIconoDeporte(String nombreDeporte) {
        if (nombreDeporte == null) return R.drawable.ic_deporte_default;

        // 1. Limpiamos el texto: quitamos espacios y pasamos a minúsculas
        String deporte = nombreDeporte.trim().toLowerCase();

        // 2. Buscamos palabras clave en lugar de coincidencia exacta
        if (deporte.contains("futbol") || deporte.contains("fútbol") || deporte.contains("soccer")) {
            return R.drawable.balon_futbol;
        } else if (deporte.contains("basket") || deporte.contains("basquet") || deporte.contains("baloncesto")) {
            return R.drawable.balon_basket;
        } else if (deporte.contains("natacion") || deporte.contains("natación") || deporte.contains("nadar")) {
            return R.drawable.ic_natacion;
        } else if (deporte.contains("run") || deporte.contains("correr") || deporte.contains("trotar")) {
            return R.drawable.ic_running;
        } else if (deporte.contains("box") || deporte.contains("pugilismo")) {
            return R.drawable.ic_boxeo;
        } else if (deporte.contains("tenis") || deporte.contains("tennis")) {
            return R.drawable.pelota_tenis;
        } else if (deporte.contains("beisbol") || deporte.contains("béisbol") || deporte.contains("baseball")) {
            // ✅ AQUÍ ESTÁ LA CORRECCIÓN: Acepta con y sin tilde
            return R.drawable.ic_beisbol;
        } else if (deporte.contains("gym") || deporte.contains("gimnasio") || deporte.contains("pesas") || deporte.contains("crossfit")) {
            return R.drawable.ic_gimnasio;
        } else if (deporte.contains("ciclis") || deporte.contains("bici") || deporte.contains("veloz")) {
            return R.drawable.ic_ciclismo;
        } else {
            return R.drawable.ic_deporte_default;
        }
    }
}