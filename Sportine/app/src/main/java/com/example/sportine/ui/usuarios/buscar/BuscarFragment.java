package com.example.sportine.ui.usuarios.buscar;

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

public class BuscarFragment extends Fragment implements View.OnClickListener {
ImageButton buscar;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alumno_buscar, container, false);
        buscar = view.findViewById(R.id.iv_search);
        buscar.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.iv_search) {
        }
    }
}