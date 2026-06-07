package com.example.sportine.ui.usuarios.buscar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sportine.R;
import com.example.sportine.data.ApiService;
import com.example.sportine.data.RetrofitClient;
import com.example.sportine.models.EntrenadorCardDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BuscarFragment extends Fragment implements ResultadosEntrenadoresAdapter.OnEntrenadorClickListener, View.OnClickListener {

    private RecyclerView rvEntrenadores;
    private SearchView searchView;
    private LinearLayout layoutEmptyState;
    private TextView tvSeccionTitulo;
    private ResultadosEntrenadoresAdapter adapter;
    private ApiService apiService;
    private ImageButton solenv;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alumno_buscar, container, false);

        // Inicializar API
        apiService = RetrofitClient.getClient(requireContext()).create(ApiService.class);

        // Inicializar vistas
        rvEntrenadores = view.findViewById(R.id.recycler_entrenadores);
        layoutEmptyState = view.findViewById(R.id.layout_empty_state);
        searchView = view.findViewById(R.id.search_view_entrenador);
        tvSeccionTitulo = view.findViewById(R.id.tv_seccion_titulo);
        solenv = view.findViewById(R.id.solis);
        solenv.setOnClickListener(this);

        // Inicializar RecyclerView
        rvEntrenadores.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ResultadosEntrenadoresAdapter(this);
        rvEntrenadores.setAdapter(adapter);

        // Configurar SearchView
        setupBuscador();

        // Cargar entrenadores iniciales (sin query = top del estado)
        cargarEntrenadoresRecomendados();

        return view;
    }

    private void setupBuscador() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                cargarEntrenadoresNormal(query);
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Buscar mientras escribe
                if (newText.length() > 2) {
                    // Solo buscar si tiene más de 2 caracteres
                    cargarEntrenadoresNormal(newText);
                } else if (newText.isEmpty()) {
                    // Si borra todo, cargar top entrenadores
                    cargarEntrenadoresNormal(null);
                }
                return true;
            }
        });
    }

    private void cargarEntrenadoresRecomendados() {
        // Intentar primero con IA
        apiService.recomendarEntrenadores().enqueue(new Callback<List<EntrenadorCardDTO>>() {
            @Override
            public void onResponse(Call<List<EntrenadorCardDTO>> call,
                                   Response<List<EntrenadorCardDTO>> response) {
                if (!isAdded()) return;
                if (response.isSuccessful() && response.body() != null) {
                    List<EntrenadorCardDTO> entrenadores = response.body();
                    adapter.setEntrenadores(entrenadores);
                    actualizarTituloSeccion(null); // "Entrenadores recomendados"
                    mostrarEmptyState(entrenadores.isEmpty());
                } else {
                    // IA no disponible → fallback a búsqueda normal
                    cargarEntrenadoresNormal(null);
                }
            }

            @Override
            public void onFailure(Call<List<EntrenadorCardDTO>> call, Throwable t) {
                if (!isAdded()) return;
                // Fallback silencioso
                cargarEntrenadoresNormal(null);
            }
        });
    }

    private void cargarEntrenadoresNormal(String query) {
        actualizarTituloSeccion(query);
        apiService.buscarEntrenadores(query).enqueue(new Callback<List<EntrenadorCardDTO>>() {
            @Override
            public void onResponse(Call<List<EntrenadorCardDTO>> call,
                                   Response<List<EntrenadorCardDTO>> response) {
                if (!isAdded()) return;
                if (response.isSuccessful() && response.body() != null) {
                    List<EntrenadorCardDTO> entrenadores = response.body();
                    adapter.setEntrenadores(entrenadores);
                    mostrarEmptyState(entrenadores.isEmpty());
                } else {
                    mostrarEmptyState(true);
                }
            }

            @Override
            public void onFailure(Call<List<EntrenadorCardDTO>> call, Throwable t) {
                if (!isAdded()) return;
                mostrarEmptyState(true);
            }
        });
    }

    /**
     * Actualiza el título de la sección según si hay búsqueda o no
     */
    private void actualizarTituloSeccion(String query) {
        if (query == null || query.trim().isEmpty()) {
            // Sin búsqueda - Mostrar "Entrenadores recomendados"
            tvSeccionTitulo.setText("Entrenadores recomendados");
        } else {
            // Con búsqueda - Mostrar "Entrenadores encontrados"
            tvSeccionTitulo.setText("Entrenadores encontrados");
        }
    }

    /**
     * Muestra u oculta el empty state
     * @param mostrar true para mostrar empty state, false para mostrar recycler
     */
    private void mostrarEmptyState(boolean mostrar) {
        if (mostrar) {
            rvEntrenadores.setVisibility(View.GONE);
            layoutEmptyState.setVisibility(View.VISIBLE);
            tvSeccionTitulo.setVisibility(View.GONE); // Ocultar título cuando no hay resultados
        } else {
            rvEntrenadores.setVisibility(View.VISIBLE);
            layoutEmptyState.setVisibility(View.GONE);
            tvSeccionTitulo.setVisibility(View.VISIBLE); // Mostrar título cuando hay resultados
        }
    }

    @Override
    public void onEntrenadorClick(EntrenadorCardDTO entrenador) {
        Bundle bundle = new Bundle();
        bundle.putString("usuario", entrenador.getUsuario());
        NavHostFragment.findNavController(this)
                .navigate(R.id.action_buscar_to_detallesEntrenador, bundle);
    }

    @Override
    public void onClick(View view) {
        NavHostFragment.findNavController(this)
                .navigate(R.id.action_navigation_buscar_to_solicitudesEnviadas);
    }
}