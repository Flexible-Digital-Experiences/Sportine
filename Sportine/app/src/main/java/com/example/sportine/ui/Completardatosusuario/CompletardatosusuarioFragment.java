package com.example.sportine.ui.Completardatosusuario;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.example.sportine.R;

public class CompletardatosusuarioFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_completardatosusuario, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView btnBack = view.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().onBackPressed();
            }
        });

        // Configurar Spinners
        setupSpinners(view);

    }

    private void setupSpinners(View view) {
        // Spinner de Nivel
        Spinner spinnerNivel = view.findViewById(R.id.spinnerNivel);
        String[] niveles = {"Nivel", "Principiante", "Intermedio", "Avanzado", "Profesional"};
        ArrayAdapter<String> adapterNivel = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                niveles
        );
        adapterNivel.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerNivel.setAdapter(adapterNivel);

        // Spinner de Edad
        Spinner spinnerEdad = view.findViewById(R.id.spinnerEdad);
        String[] edades = new String[83];
        edades[0] = "Edad";
        for (int i = 1; i < 83; i++) {
            edades[i] = String.valueOf(i + 17); // De 18 a 100
        }
        ArrayAdapter<String> adapterEdad = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                edades
        );
        adapterEdad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEdad.setAdapter(adapterEdad);

        // Spinner de Género
        Spinner spinnerGenero = view.findViewById(R.id.spinnerGenero);
        String[] generos = {"Género", "Masculino", "Femenino", "No Binario", "Género fluido", "Agénero", "Bigénero" , "Demigénero" , "Transgenero" , "Cisgenero" , "Prefiero no decir"};
        ArrayAdapter<String> adapterGenero = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                generos
        );
        adapterGenero.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGenero.setAdapter(adapterGenero);

        // Spinner de Deportes
        Spinner spinnerDeportes = view.findViewById(R.id.spinnerDeportes);
        String[] deportes = {"Deportes", "Fútbol", "Basketball", "Tenis", "Natación", "Atletismo"};
        ArrayAdapter<String> adapterDeportes = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                deportes
        );
        adapterDeportes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDeportes.setAdapter(adapterDeportes);
    }

}