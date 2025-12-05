package com.example.sportine.ui.usuarios.social;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.sportine.models.UsuarioDetalle;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BuscarAmigoFragment extends Fragment implements AmigosAdapter.OnItemActionListener {

    private RecyclerView rvResultados;
    private SearchView searchView;
    private AmigosAdapter adapter;
    private ApiService apiService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alumno_buscar_amigo, container, false);

        apiService = RetrofitClient.getClient(requireContext()).create(ApiService.class);

        MaterialToolbar toolbar = view.findViewById(R.id.toolbar_buscar_amigo);
        toolbar.setNavigationOnClickListener(v -> NavHostFragment.findNavController(this).navigateUp());

        rvResultados = view.findViewById(R.id.rv_resultados_amigos);
        rvResultados.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new AmigosAdapter(true, this);
        rvResultados.setAdapter(adapter);

        searchView = view.findViewById(R.id.search_view_amigo);
        setupBuscador();

        return view;
    }

    private void setupBuscador() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                buscarPersonas(query);
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() > 0) {
                    buscarPersonas(newText);
                } else {
                    adapter.setUsuarios(new java.util.ArrayList<>());
                }
                return true;
            }
        });
    }

    private void buscarPersonas(String query) {
        if (query.isEmpty()) return;

        apiService.buscarPersonas(query).enqueue(new Callback<List<UsuarioDetalle>>() {
            @Override
            public void onResponse(Call<List<UsuarioDetalle>> call, Response<List<UsuarioDetalle>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<UsuarioDetalle> resultados = response.body();
                    adapter.setUsuarios(resultados);

                    if (resultados.isEmpty()) {
                        // Opcional: Toast solo si es submit explícito para no spamear mientras escribe
                        // Toast.makeText(getContext(), "No se encontraron usuarios", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<UsuarioDetalle>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onAction(UsuarioDetalle usuario) {
        // ✅ CORRECCIÓN: Aquí ya NO actualizamos la UI ni el modelo.
        // El Adapter ya se encargó de eso instantáneamente (Optimistic UI).
        // Aquí solo hacemos la llamada silenciosa al backend.

        apiService.seguirUsuario(usuario.getUsuario()).enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                if (response.isSuccessful()) {
                    String mensaje = (response.body() != null) ? response.body().get("mensaje") : "Acción realizada";
                    // Opcional: Mostrar toast breve
                    // Toast.makeText(getContext(), mensaje, Toast.LENGTH_SHORT).show();
                } else {
                    // ⚠️ Si falla, aquí deberíamos revertir el cambio visual en el adapter (opcional avanzado)
                    Toast.makeText(getContext(), "Error al seguir usuario", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                Toast.makeText(getContext(), "Fallo de red", Toast.LENGTH_SHORT).show();
            }
        });
    }
}