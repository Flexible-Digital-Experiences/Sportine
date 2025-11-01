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

public class DetallesEntrenamientoFragment extends Fragment {

    private FragmentAlumnoDetallesEntrenamientoBinding binding;
    private String deporte;
    private String tituloEntrenamiento;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Recibir argumentos del bundle
        if (getArguments() != null) {
            deporte = getArguments().getString("deporte", "");
            tituloEntrenamiento = getArguments().getString("titulo", "");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAlumnoDetallesEntrenamientoBinding.inflate(inflater, container, false);

        setupUI();


        return binding.getRoot();
    }

    private void setupUI() {
        // Botón de regreso
        binding.btnBack.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });

        // Configurar textos con datos recibidos o valores por defecto
        binding.textTituloEntrenamiento.setText(tituloEntrenamiento);
        
        // Los siguientes datos se llenarán desde el backend en el futuro
        binding.textFecha.setText("");
        binding.textNombreEntrenador.setText("");
        binding.textEspecialidad.setText("");
        binding.textDescripcion.setText("");
        
        // Se pueden establecer imagenes por defecto si es necesario
        // binding.imgAvatarEntrenador.setImageResource(R.drawable.avatar_default);
        // binding.imgDeporteIcon.setImageResource(R.drawable.icon_default);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
