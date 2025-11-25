package com.example.sportine.ui.usuarios.buscar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sportine.R;
import com.example.sportine.data.ApiService;
import com.example.sportine.data.RetrofitClient;
import com.example.sportine.models.EntrenadorCardDTO;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BuscarFragment extends Fragment implements ResultadosEntrenadoresAdapter.OnEntrenadorClickListener {

    private RecyclerView rvEntrenadores;
    private SearchView searchView;
    private ResultadosEntrenadoresAdapter adapter;
    private ApiService apiService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alumno_buscar, container, false);

        // Inicializar API
        apiService = RetrofitClient.getClient(requireContext()).create(ApiService.class);

        // Inicializar RecyclerView
        rvEntrenadores = view.findViewById(R.id.recycler_entrenadores);
        rvEntrenadores.setLayoutManager(new LinearLayoutManager(getContext()));

        // Inicializar Adapter
        adapter = new ResultadosEntrenadoresAdapter(this);
        rvEntrenadores.setAdapter(adapter);

        // Inicializar SearchView
        searchView = view.findViewById(R.id.search_view_entrenador);
        setupBuscador();

        // Cargar entrenadores iniciales (sin query = top del estado)
        cargarEntrenadores(null);

        return view;
    }

    private void setupBuscador() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                cargarEntrenadores(query);
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Buscar mientras escribe
                if (newText.length() > 2) {
                    // Solo buscar si tiene más de 2 caracteres
                    cargarEntrenadores(newText);
                } else if (newText.isEmpty()) {
                    // Si borra todo, cargar top entrenadores
                    cargarEntrenadores(null);
                }
                return true;
            }
        });
    }

    private void cargarEntrenadores(String query) {
        if (!isAdded()) return; // Verificar que el Fragment esté adjunto

        apiService.buscarEntrenadores(query).enqueue(new Callback<List<EntrenadorCardDTO>>() {
            @Override
            public void onResponse(Call<List<EntrenadorCardDTO>> call,
                                   Response<List<EntrenadorCardDTO>> response) {
                if (!isAdded()) return;

                if (response.isSuccessful() && response.body() != null) {
                    List<EntrenadorCardDTO> entrenadores = response.body();
                    adapter.setEntrenadores(entrenadores);

                    if (entrenadores.isEmpty()) {
                        Toast.makeText(getContext(),
                                "No se encontraron entrenadores",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(),
                            "Error al buscar: " + response.code(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<EntrenadorCardDTO>> call, Throwable t) {
                if (!isAdded()) return;

                Toast.makeText(getContext(),
                        "Error de conexión: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onEntrenadorClick(EntrenadorCardDTO entrenador) {
        // TODO: Navegar al perfil del entrenador o mostrar detalles
        Toast.makeText(getContext(),
                "Seleccionaste a " + entrenador.getNombreCompleto(),
                Toast.LENGTH_SHORT).show();

        // Ejemplo de navegación (cuando lo implementes):
        // Bundle bundle = new Bundle();
        // bundle.putString("usuario", entrenador.getUsuario());
        // NavHostFragment.findNavController(this)
        //     .navigate(R.id.action_buscarFragment_to_perfilEntrenadorFragment, bundle);
    }
}