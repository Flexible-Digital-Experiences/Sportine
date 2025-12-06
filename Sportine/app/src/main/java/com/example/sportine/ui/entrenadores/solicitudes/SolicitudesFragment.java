package com.example.sportine.ui.entrenadores.solicitudes;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.sportine.R;
import com.example.sportine.data.ApiService;
import com.example.sportine.data.RetrofitClient;
import com.example.sportine.models.RespuestaSolicitudRequestDTO;
import com.example.sportine.models.RespuestaSolicitudResponseDTO;
import com.example.sportine.models.SolicitudEntrenadorDTO;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SolicitudesFragment extends Fragment {

    private static final String TAG = "SolicitudesFragment";

    private ApiService apiService;
    private String usuarioEntrenador;

    // Views
    private RecyclerView rvSolicitudes;
    private LinearLayout emptyStateLayout;
    private LottieAnimationView lottieNoEncontrado;
    private TextView tvEmptyState;
    private MaterialButton btnVerMisAlumnos;
    private MaterialButton btnRechazar;
    private MaterialButton btnAceptar;
    private LinearLayout botones_aceprech;

    // Adapter
    private SolicitudesAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_entrenador_solicitudes, container, false);

        SharedPreferences prefs = requireContext().getSharedPreferences("SportinePrefs", Context.MODE_PRIVATE);
        usuarioEntrenador = prefs.getString("USER_USERNAME", "");

        if (usuarioEntrenador.isEmpty()) {
            Log.e(TAG, "Usuario no encontrado en SharedPreferences");
            Toast.makeText(getContext(), "Error: Usuario no encontrado", Toast.LENGTH_SHORT).show();
            return view;
        }

        Log.d(TAG, "Usuario entrenador: " + usuarioEntrenador);

        apiService = RetrofitClient.getClient(requireContext()).create(ApiService.class);

        initViews(view);
        setupRecyclerView();
        setupListeners();

        cargarSolicitudesEnRevision();

        return view;
    }

    private void initViews(View view) {
        rvSolicitudes = view.findViewById(R.id.rvSolicitudes);
        emptyStateLayout = view.findViewById(R.id.emptyStateLayout);
        lottieNoEncontrado = view.findViewById(R.id.imagen_noenco);
        tvEmptyState = view.findViewById(R.id.tvEmptyState);
        btnVerMisAlumnos = view.findViewById(R.id.btnVerMisAlumnos);
        btnRechazar = view.findViewById(R.id.btnRechazar);
        btnAceptar = view.findViewById(R.id.btnAceptar);
        botones_aceprech = view.findViewById(R.id.layoutBottomButtons);
    }

    private void setupRecyclerView() {
        adapter = new SolicitudesAdapter();
        rvSolicitudes.setLayoutManager(new LinearLayoutManager(getContext()));
        rvSolicitudes.setAdapter(adapter);

        adapter.setMostrarCheckbox(true);

        adapter.setListener(solicitud -> {
            Toast.makeText(getContext(),
                    "Solicitud de: " + solicitud.getNombreAlumno(),
                    Toast.LENGTH_SHORT).show();
        });
    }

    private void setupListeners() {
        // ✅ Botón Ver Mis Alumnos - NAVEGACIÓN IMPLEMENTADA
        btnVerMisAlumnos.setOnClickListener(v -> {
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_navigation_solicitudes_entrenador_to_misAlumnos);
        });

        // Botón Aceptar
        btnAceptar.setOnClickListener(v -> aceptarSolicitudesSeleccionadas());

        // Botón Rechazar
        btnRechazar.setOnClickListener(v -> rechazarSolicitudesSeleccionadas());
    }

    private void cargarSolicitudesEnRevision() {
        if (!isAdded()) return;

        Log.d(TAG, "Cargando solicitudes para: " + usuarioEntrenador);

        apiService.obtenerSolicitudesEnRevision(usuarioEntrenador)
                .enqueue(new Callback<List<SolicitudEntrenadorDTO>>() {
                    @Override
                    public void onResponse(Call<List<SolicitudEntrenadorDTO>> call,
                                           Response<List<SolicitudEntrenadorDTO>> response) {
                        if (!isAdded()) return;

                        Log.d(TAG, "Response code: " + response.code());

                        if (response.isSuccessful() && response.body() != null) {
                            List<SolicitudEntrenadorDTO> solicitudes = response.body();

                            Log.d(TAG, "Solicitudes recibidas: " + solicitudes.size());

                            if (solicitudes.isEmpty()) {
                                mostrarEstadoVacio();
                            } else {
                                ocultarEstadoVacio();
                                adapter.setSolicitudes(solicitudes);

                                if (!solicitudes.isEmpty()) {
                                    SolicitudEntrenadorDTO primera = solicitudes.get(0);
                                    Log.d(TAG, "Primera solicitud - ID: " + primera.getIdSolicitud()
                                            + ", Alumno: " + primera.getNombreAlumno()
                                            + ", Deporte: " + primera.getNombreDeporte());
                                }
                            }
                        } else {
                            Log.e(TAG, "Error en response: " + response.code());
                            Toast.makeText(getContext(),
                                    "Error al cargar solicitudes: " + response.code(),
                                    Toast.LENGTH_SHORT).show();
                            mostrarEstadoVacio();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<SolicitudEntrenadorDTO>> call, Throwable t) {
                        if (!isAdded()) return;

                        Log.e(TAG, "Error de conexión", t);
                        Toast.makeText(getContext(),
                                "Error de conexión: " + t.getMessage(),
                                Toast.LENGTH_SHORT).show();
                        mostrarEstadoVacio();
                    }
                });
    }

    private void aceptarSolicitudesSeleccionadas() {
        Set<Integer> seleccionadas = adapter.getSolicitudesSeleccionadas();

        if (seleccionadas.isEmpty()) {
            Toast.makeText(getContext(),
                    "Selecciona al menos una solicitud",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Confirmar aceptación")
                .setMessage("¿Estás seguro de aceptar " + seleccionadas.size() +
                        " solicitud(es)? El alumno podrá comenzar a entrenar contigo.")
                .setPositiveButton("Aceptar", (dialog, which) -> {
                    procesarRespuestas(new ArrayList<>(seleccionadas), "aceptar");
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void rechazarSolicitudesSeleccionadas() {
        Set<Integer> seleccionadas = adapter.getSolicitudesSeleccionadas();

        if (seleccionadas.isEmpty()) {
            Toast.makeText(getContext(),
                    "Selecciona al menos una solicitud",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Confirmar rechazo")
                .setMessage("¿Estás seguro de rechazar " + seleccionadas.size() + " solicitud(es)?")
                .setPositiveButton("Rechazar", (dialog, which) -> {
                    procesarRespuestas(new ArrayList<>(seleccionadas), "rechazar");
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void procesarRespuestas(List<Integer> idsSolicitudes, String accion) {
        int totalSolicitudes = idsSolicitudes.size();
        int[] procesadas = {0};
        int[] exitosas = {0};
        int[] fallidas = {0};

        for (Integer idSolicitud : idsSolicitudes) {
            RespuestaSolicitudRequestDTO request = new RespuestaSolicitudRequestDTO(idSolicitud, accion);

            Log.d(TAG, "Procesando solicitud ID: " + idSolicitud + " - Acción: " + accion);

            apiService.responderSolicitud(usuarioEntrenador, request).enqueue(new Callback<RespuestaSolicitudResponseDTO>() {
                @Override
                public void onResponse(Call<RespuestaSolicitudResponseDTO> call, Response<RespuestaSolicitudResponseDTO> response) {
                    if (!isAdded()) return;

                    procesadas[0]++;

                    if (response.isSuccessful() && response.body() != null) {
                        exitosas[0]++;
                        Log.d(TAG, "Solicitud " + idSolicitud + " procesada: " + response.body().getMensaje());
                    } else {
                        fallidas[0]++;
                        Log.e(TAG, "Error al procesar solicitud " + idSolicitud + ": " + response.code());
                    }

                    if (procesadas[0] == totalSolicitudes) {
                        String mensaje;
                        if (fallidas[0] == 0) {
                            mensaje = accion.equals("aceptar")
                                    ? "Solicitudes aceptadas exitosamente"
                                    : "Solicitudes rechazadas exitosamente";
                        } else {
                            mensaje = "Procesadas: " + exitosas[0] + " exitosas, " + fallidas[0] + " fallidas";
                        }

                        Toast.makeText(getContext(), mensaje, Toast.LENGTH_LONG).show();
                        adapter.clearSelections();
                        cargarSolicitudesEnRevision();
                    }
                }

                @Override
                public void onFailure(Call<RespuestaSolicitudResponseDTO> call, Throwable t) {
                    if (!isAdded()) return;

                    procesadas[0]++;
                    fallidas[0]++;
                    Log.e(TAG, "Error de conexión al procesar solicitud " + idSolicitud, t);

                    if (procesadas[0] == totalSolicitudes) {
                        Toast.makeText(getContext(),
                                "Procesadas: " + exitosas[0] + " exitosas, " + fallidas[0] + " fallidas",
                                Toast.LENGTH_LONG).show();
                        adapter.clearSelections();
                        cargarSolicitudesEnRevision();
                    }
                }
            });
        }
    }

    private void mostrarEstadoVacio() {
        Log.d(TAG, "Mostrando estado vacío");
        rvSolicitudes.setVisibility(View.GONE);
        emptyStateLayout.setVisibility(View.VISIBLE);
        botones_aceprech.setVisibility(View.GONE);
    }

    private void ocultarEstadoVacio() {
        Log.d(TAG, "Ocultando estado vacío");
        rvSolicitudes.setVisibility(View.VISIBLE);
        emptyStateLayout.setVisibility(View.GONE);
    }
}