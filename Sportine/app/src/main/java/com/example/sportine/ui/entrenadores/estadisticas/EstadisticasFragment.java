package com.example.sportine.ui.entrenadores.estadisticas;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.sportine.databinding.FragmentEntrenadorEstadisticasBinding;

public class EstadisticasFragment extends Fragment {

    private FragmentEntrenadorEstadisticasBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentEntrenadorEstadisticasBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Aquí, en el futuro, configurarás el Spinner con la lista de alumnos
        // y añadirás los listeners para actualizar los gráficos cuando se seleccione un alumno.
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
