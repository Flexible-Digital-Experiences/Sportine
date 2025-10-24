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
import com.example.sportine.ui.usuarios.detallesentrenamiento.DetallesEntrenamientoFragment;

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
        binding.textSaludo.setText("Hola de nuevo, Alumno!");
        binding.textMensaje.setText("¿Listo para mejorar hoy?");

        // Configurar RecyclerView con tarjetas
        setupRecyclerDeportes();

        return binding.getRoot();
    }

    private void setupRecyclerDeportes() {
        // Lista de deportes de ejemplo
        List<String> deportes = Arrays.asList(
                "Fútbol",
                "Natación",
                "Beisbol",
                "Tenis",
                "Boxeo",
                "Basket"
        );

        // Adapter con el listener de clicks
        DeportesAdapter adapter = new DeportesAdapter(deportes, (deporte, tituloEntrenamiento) -> {
            // Abrir el fragmento de detalles usando Navigation Component
            abrirDetalleEntrenamiento(deporte, tituloEntrenamiento);
        });

        binding.recyclerDeportes.setAdapter(adapter);
        binding.recyclerDeportes.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void abrirDetalleEntrenamiento(String deporte, String tituloEntrenamiento) {
        try {
            // Crear el bundle con los datos
            Bundle args = new Bundle();
            args.putString("deporte", deporte);
            args.putString("titulo", tituloEntrenamiento);

            // Navegar usando Navigation Component
            NavController navController = Navigation.findNavController(requireView());
            navController.navigate(R.id.action_home_to_detalles, args);
        } catch (Exception e) {
            // Fallback: usar FragmentTransaction si Navigation falla
            e.printStackTrace();

            DetallesEntrenamientoFragment detallesFragment = new DetallesEntrenamientoFragment();
            Bundle args = new Bundle();
            args.putString("deporte", deporte);
            args.putString("titulo", tituloEntrenamiento);
            detallesFragment.setArguments(args);

            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.nav_host_fragment_activity_main, detallesFragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}