package com.example.sportine.ui.usuarios.resultadosBusqueda;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.sportine.R;
import com.google.android.material.card.MaterialCardView;

public class resultadosBusquedaFragment extends Fragment implements View.OnClickListener {
    MaterialCardView cardEntrenador;
    ImageButton btnback;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alumno_resultados_busqueda, container, false);

        // Encuentra la tarjeta
        cardEntrenador = view.findViewById(R.id.card_entrenador);
        cardEntrenador.setOnClickListener (this);
        btnback = view.findViewById(R.id.btn_back);
        btnback.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.card_entrenador){
            // Crear bundle con los datos a pasar
            Bundle bundle = new Bundle();
            bundle.putString("entrenadorId", "123");
            bundle.putString("entrenadorNombre", "Carlos Rodríguez");
            // Navegar al fragmento de detalle
            NavController navControlle = Navigation.findNavController(view);
            navControlle.navigate(R.id.action_navigation_buscar_to_navigation_resultadosBusqueda);
        } else if (view.getId() == R.id.btn_back) {
            requireActivity().onBackPressed();
        }
    }
}