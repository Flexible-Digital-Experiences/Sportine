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

public class DetallesEntrenamientoFragment extends Fragment {

    private FragmentAlumnoDetallesEntrenamientoBinding binding;
    private DetallesViewModel viewModel;
    private EjerciciosAdapter adapter;
    private Integer idEntrenamiento;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Recibimos el ID que viene del Home
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

        adapter.setOnEjercicioCheckListener((ejercicio, isChecked) -> {
            viewModel.cambiarEstadoEjercicio(ejercicio.getIdAsignado(), isChecked);
        });
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(DetallesViewModel.class);

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

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null) Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
        });
    }

    private void setupListeners() {
        binding.btnBack.setOnClickListener(v -> {
            if (getActivity() != null) getActivity().onBackPressed();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}