package com.example.sportine.ui.usuarios.social;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView; // <-- Importante
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sportine.R;
import com.example.sportine.data.ApiService;
import com.example.sportine.data.RetrofitClient;
import com.example.sportine.models.UsuarioDetalle;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListaAmigosFragment extends Fragment implements AmigosAdapter.OnItemActionListener {

    private RecyclerView rvListaAmigos;
    private AmigosAdapter adapter;
    private ApiService apiService;
    private List<UsuarioDetalle> listaCompleta = new ArrayList<>(); // Guardamos todos aquí

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alumno_lista_amigos, container, false);

        apiService = RetrofitClient.getClient(requireContext()).create(ApiService.class);

        MaterialToolbar toolbar = view.findViewById(R.id.toolbar_lista_amigos);
        toolbar.setNavigationOnClickListener(v -> NavHostFragment.findNavController(this).navigateUp());

        rvListaAmigos = view.findViewById(R.id.rv_lista_amigos);
        rvListaAmigos.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new AmigosAdapter(false, this);
        rvListaAmigos.setAdapter(adapter);

        // --- CONFIGURAR BUSCADOR ---
        SearchView searchView = view.findViewById(R.id.sv_filtro_amigos);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) { return false; }

            @Override
            public boolean onQueryTextChange(String newText) {
                filtrar(newText); // <-- Filtrado local
                return true;
            }
        });

        cargarMisAmigos();

        return view;
    }

    private void cargarMisAmigos() {
        apiService.verMisAmigos().enqueue(new Callback<List<UsuarioDetalle>>() {
            @Override
            public void onResponse(Call<List<UsuarioDetalle>> call, Response<List<UsuarioDetalle>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listaCompleta = response.body(); // Guardamos la original
                    adapter.setUsuarios(listaCompleta); // Mostramos todos al inicio
                }
            }
            @Override
            public void onFailure(Call<List<UsuarioDetalle>> call, Throwable t) {}
        });
    }

    private void filtrar(String texto) {
        if (texto.isEmpty()) {
            adapter.setUsuarios(listaCompleta);
        } else {
            // Filtramos por nombre O usuario
            List<UsuarioDetalle> filtrada = new ArrayList<>();
            String busqueda = texto.toLowerCase();

            for (UsuarioDetalle u : listaCompleta) {
                String nombre = (u.getNombre() + " " + u.getApellidos()).toLowerCase();
                String usuario = u.getUsuario().toLowerCase();

                if (nombre.contains(busqueda) || usuario.contains(busqueda)) {
                    filtrada.add(u);
                }
            }
            adapter.setUsuarios(filtrada);
        }
    }

    @Override
    public void onAction(UsuarioDetalle usuario) {
        new AlertDialog.Builder(getContext())
                .setTitle("Eliminar Amigo")
                .setMessage("¿Dejar de seguir a " + usuario.getNombre() + "?")
                .setPositiveButton("Eliminar", (dialog, which) -> eliminarAmigoReal(usuario))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void eliminarAmigoReal(UsuarioDetalle usuario) {
        apiService.eliminarAmigo(usuario.getUsuario()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Eliminado", Toast.LENGTH_SHORT).show();
                    cargarMisAmigos(); // Recargar lista del server
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {}
        });
    }
}