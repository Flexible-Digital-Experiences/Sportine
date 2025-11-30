package com.example.sportine.ui.usuarios.solicitudesenviadas;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sportine.R;
import com.example.sportine.data.ApiService;
import com.example.sportine.data.RetrofitClient;
import com.example.sportine.models.SolicitudEnviadaDTO;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SolicitudesEnviadas extends Fragment {

    private ApiService apiService;

    // Views
    private ImageButton btnBack;
    private RecyclerView recyclerSolicitudes;
    private LinearLayout layoutEmptyState;

    // Adapter
    private SolicitudesEnviadasAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_solicitudes_enviadas, container, false);

        apiService = RetrofitClient.getClient(requireContext()).create(ApiService.class);

        initViews(view);
        setupRecyclerView();
        setupListeners();

        cargarSolicitudes();

        return view;
    }

    private void initViews(View view) {
        btnBack = view.findViewById(R.id.btn_back);
        recyclerSolicitudes = view.findViewById(R.id.recycler_solicitudes);
        layoutEmptyState = view.findViewById(R.id.layout_empty_state);
    }

    private void setupRecyclerView() {
        recyclerSolicitudes.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new SolicitudesEnviadasAdapter(new SolicitudesEnviadasAdapter.OnSolicitudClickListener() {
            @Override
            public void onSolicitudClick(SolicitudEnviadaDTO solicitud) {
                // Navegar a detalles del entrenador
                navegarADetallesEntrenador(solicitud.getUsuarioEntrenador());
            }

            @Override
            public void onEliminarClick(SolicitudEnviadaDTO solicitud, int position) {
                // Mostrar diálogo de confirmación
                mostrarDialogoEliminar(solicitud, position);
            }
        });

        recyclerSolicitudes.setAdapter(adapter);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> NavHostFragment.findNavController(this).navigateUp());
    }

    private void cargarSolicitudes() {
        if (!isAdded()) return;

        apiService.obtenerSolicitudesEnviadas().enqueue(new Callback<List<SolicitudEnviadaDTO>>() {
            @Override
            public void onResponse(Call<List<SolicitudEnviadaDTO>> call,
                                   Response<List<SolicitudEnviadaDTO>> response) {
                if (!isAdded()) return;

                if (response.isSuccessful() && response.body() != null) {
                    List<SolicitudEnviadaDTO> solicitudes = response.body();

                    if (solicitudes.isEmpty()) {
                        // Mostrar estado vacío
                        recyclerSolicitudes.setVisibility(View.GONE);
                        layoutEmptyState.setVisibility(View.VISIBLE);
                    } else {
                        // Mostrar solicitudes
                        recyclerSolicitudes.setVisibility(View.VISIBLE);
                        layoutEmptyState.setVisibility(View.GONE);
                        adapter.setSolicitudes(solicitudes);
                    }
                } else {
                    Toast.makeText(getContext(),
                            "Error al cargar solicitudes: " + response.code(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<SolicitudEnviadaDTO>> call, Throwable t) {
                if (!isAdded()) return;

                Toast.makeText(getContext(),
                        "Error de conexión: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarDialogoEliminar(SolicitudEnviadaDTO solicitud, int position) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Eliminar solicitud")
                .setMessage("¿Estás seguro de que deseas eliminar esta solicitud a " +
                        solicitud.getNombreEntrenador() + "?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    eliminarSolicitud(solicitud.getIdSolicitud(), position);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void eliminarSolicitud(Integer idSolicitud, int position) {
        if (!isAdded()) return;

        apiService.eliminarSolicitud(idSolicitud).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (!isAdded()) return;

                if (response.isSuccessful()) {
                    Toast.makeText(getContext(),
                            "Solicitud eliminada",
                            Toast.LENGTH_SHORT).show();

                    // Eliminar del adapter
                    adapter.eliminarSolicitud(position);

                    // Si no quedan solicitudes, mostrar estado vacío
                    if (adapter.getItemCount() == 0) {
                        recyclerSolicitudes.setVisibility(View.GONE);
                        layoutEmptyState.setVisibility(View.VISIBLE);
                    }
                } else {
                    Toast.makeText(getContext(),
                            "Error al eliminar solicitud",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                if (!isAdded()) return;

                Toast.makeText(getContext(),
                        "Error de conexión: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navegarADetallesEntrenador(String usuarioEntrenador) {
        Bundle bundle = new Bundle();
        bundle.putString("usuario", usuarioEntrenador);

        NavHostFragment.findNavController(this)
                .navigate(R.id.action_solicitudesEnviadas_to_navigation_detallesEntrenador, bundle);
    }
}