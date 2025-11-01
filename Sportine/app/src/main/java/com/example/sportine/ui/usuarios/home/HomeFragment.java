package com.example.sportine.ui.usuarios.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.sportine.R;
import com.example.sportine.databinding.FragmentAlumnoHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentAlumnoHomeBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAlumnoHomeBinding.inflate(inflater, container, false);

        // Los datos se llenarán desde el backend
        binding.textFecha.setText("");
        binding.textSaludo.setText("");
        binding.textMensaje.setText("");

        // Configurar RecyclerView
        setupRecyclerDeportes();

        return binding.getRoot();
    }

    private void setupRecyclerDeportes() {
        // Adapter con el listener de clicks
        DeportesAdapter adapter = new DeportesAdapter((deporte, tituloEntrenamiento) -> {
            // Abrir el fragmento de detalles
            abrirDetalleEntrenamiento(deporte, tituloEntrenamiento);
        });

        binding.recyclerDeportes.setAdapter(adapter);
        binding.recyclerDeportes.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void abrirDetalleEntrenamiento(String deporte, String tituloEntrenamiento) {
        // Crear el bundle con los datos
        Bundle args = new Bundle();
        args.putString("deporte", deporte);
        args.putString("titulo", tituloEntrenamiento);

        // Navegar usando Navigation Component
        NavController navController = Navigation.findNavController(requireView());
        navController.navigate(R.id.action_home_to_detalles, args);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
