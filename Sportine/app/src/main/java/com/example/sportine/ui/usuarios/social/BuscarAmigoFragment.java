package com.example.sportine.ui.usuarios.social;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import com.example.sportine.R;
import com.google.android.material.appbar.MaterialToolbar;

public class BuscarAmigoFragment extends Fragment {

    private SearchView searchView;
    private RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alumno_buscar_amigo, container, false);

        MaterialToolbar toolbar = view.findViewById(R.id.toolbar_buscar_amigo);
        searchView = view.findViewById(R.id.search_view_amigo);
        recyclerView = view.findViewById(R.id.rv_resultados_amigos);

        // Lógica para el botón de "Atrás" en la barra
        toolbar.setNavigationOnClickListener(v -> {
            NavHostFragment.findNavController(this).navigateUp();
        });

        // Aquí iría la lógica del RecyclerView y el SearchView
        // setupRecyclerView();
        // setupSearchView();

        return view;
    }
}
