package com.example.sportine.ui.usuarios.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.sportine.databinding.FragmentAlumnoHomeBinding;

import java.util.Arrays;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentAlumnoHomeBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAlumnoHomeBinding.inflate(inflater, container, false);

        // Personalizar textos
        binding.textFecha.setText("20 de octubre de 2025");
        binding.textSaludo.setText("Entrenamientos");  // <-- cambiamos a "Entrenamientos"
        binding.textMensaje.setText("¿Listo para mejorar hoy?");

        // Configurar RecyclerView con tarjetas
        setupRecyclerDeportes();

        return binding.getRoot();
    }

    private void setupRecyclerDeportes() {
        // Lista de deportes de ejemplo
        List<String> deportes = Arrays.asList("Fútbol", "Natación", "Ciclismo", "Tenis", "Boxeo");

        // Adapter
        DeportesAdapter adapter = new DeportesAdapter(deportes);
        binding.recyclerDeportes.setAdapter(adapter);

        // LayoutManager vertical
        binding.recyclerDeportes.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
