package com.example.sportine.ui.usuarios.detallesEntrenador;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.example.sportine.R;

public class DetallesEntrenadorFragment extends Fragment implements View.OnClickListener {
    ImageView btnBack;
    Button boton;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_alumno_ver_detalles_entrenador, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnBack = view.findViewById(R.id.btn_back);
        btnBack.setOnClickListener(this);

        boton = view.findViewById(R.id.btn_contratar);
        boton.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_back){
            requireActivity().onBackPressed();
        }
        else if (view.getId() == R.id.btn_contratar) {
            Navigation.findNavController(view).navigate(R.id.action_navigation_detallesEntrenador_to_enviarSolicitud);
        }
    }
}