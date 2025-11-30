package com.example.sportine.ui.usuarios.detallesentrenamiento;

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
        setupListeners(); // Aquí está la magia nueva

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
            // Importante: Actualizamos el objeto local para la validación final
            ejercicio.setCompletado(isChecked);
            viewModel.cambiarEstadoEjercicio(ejercicio.getIdAsignado(), isChecked);
        });
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

            if (dto.getEjercicios() != null) {
                binding.textContadorEjercicios.setText(dto.getEjercicios().size() + " ejercicios");
                adapter.setEjercicios(dto.getEjercicios());
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

        // Feedback visual de los Sliders (Opcional)
        binding.sliderCansancio.addOnChangeListener((slider, value, fromUser) -> {
            binding.labelCansancio.setText("Nivel de Cansancio (" + (int)value + "/10)");
        });
        binding.sliderDificultad.addOnChangeListener((slider, value, fromUser) -> {
            binding.labelDificultad.setText("Dificultad (" + (int)value + "/10)");
        });

        // CLICK DEL BOTÓN COMPLETAR
        binding.btnMarcarCompletado.setOnClickListener(v -> {

            // 1. Validar si hay ejercicios pendientes
            boolean hayPendientes = false;
            // Usamos la lista actual del ViewModel que hemos ido actualizando con los CheckBox
            if (viewModel.getDetalle().getValue() != null &&
                    viewModel.getDetalle().getValue().getEjercicios() != null) {

                for (AsignarEjercicioDTO ejercicio : viewModel.getDetalle().getValue().getEjercicios()) {
                    if (!ejercicio.isCompletado()) {
                        hayPendientes = true;
                        break;
                    }
                }
            }

            // 2. Recolectar datos del formulario
            String comentario = "";
            if (binding.inputComentario.getText() != null) {
                comentario = binding.inputComentario.getText().toString();
            }

            int cansancio = (int) binding.sliderCansancio.getValue();
            int dificultad = (int) binding.sliderDificultad.getValue();

            // Obtener el texto del Chip seleccionado
            String animo = "Normal";
            int chipId = binding.chipGroupAnimo.getCheckedChipId();
            if (chipId != -1) {
                com.google.android.material.chip.Chip chip = binding.getRoot().findViewById(chipId);
                if(chip != null) animo = chip.getText().toString();
            }

            // Variables finales para usar dentro del lambda
            final String comFinal = comentario;
            final int cansFinal = cansancio;
            final int difFinal = dificultad;
            final String animoFinal = animo;

            // 3. Lógica de decisión
            if (hayPendientes) {
                new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                        .setTitle("Ejercicios pendientes")
                        .setMessage("No has marcado todos los ejercicios. ¿Deseas finalizar de todas formas?")
                        .setPositiveButton("Sí, terminar", (dialog, which) -> {
                            viewModel.completarEntrenamiento(idEntrenamiento, comFinal, cansFinal, difFinal, animoFinal);
                        })
                        .setNegativeButton("No", null)
                        .show();
            } else {
                viewModel.completarEntrenamiento(idEntrenamiento, comFinal, cansFinal, difFinal, animoFinal);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}