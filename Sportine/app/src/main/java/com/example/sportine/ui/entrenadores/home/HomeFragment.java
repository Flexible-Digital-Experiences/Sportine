package com.example.sportine.ui.entrenadores.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sportine.R;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private RecyclerView alumnosRecyclerView;
    private AlumnosAdapter alumnosAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_entrenador_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        alumnosRecyclerView = view.findViewById(R.id.recycler_alumnos);
        setupRecyclerView();
    }

    private void setupRecyclerView() {
        alumnosRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        alumnosAdapter = new AlumnosAdapter();
        alumnosRecyclerView.setAdapter(alumnosAdapter);

        // Aquí es donde, en el futuro, observarías el ViewModel para obtener la lista de alumnos
        // y la pasarías al adaptador con alumnosAdapter.setAlumnos(listaDeAlumnos);
        alumnosAdapter.setAlumnos(new ArrayList<>()); // Inicialmente se muestra una lista vacía
    }
}
