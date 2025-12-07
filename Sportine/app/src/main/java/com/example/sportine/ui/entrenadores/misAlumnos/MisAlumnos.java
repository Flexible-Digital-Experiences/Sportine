package com.example.sportine.ui.entrenadores.misAlumnos;

import android.content.Context;
import android.content.SharedPreferences;
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
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.sportine.R;
import com.example.sportine.data.ApiService;
import com.example.sportine.data.RetrofitClient;
import com.example.sportine.models.AlumnoEntrenadorDTO;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MisAlumnos extends Fragment {

    private ApiService apiService;
    private String usuarioEntrenador;
    private boolean mostrandoPendientes = false;

    // Views
    private ImageButton btnBack;  // ✅ AGREGADO
    private RecyclerView recyclerAlumnos;
    private LinearLayout layoutEmptyState;
    private LottieAnimationView imagenNoEncontrado;
    private TextView textoError;
    private MaterialButton btnFiltroPendientes;

    // Adapter
    private MisAlumnosAdapter alumnosAdapter;

    // Listas
    private List<AlumnoEntrenadorDTO> todosLosAlumnos = new ArrayList<>();
    private List<AlumnoEntrenadorDTO> alumnosPendientes = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mis_alumnos, container, false);

        // Obtener usuario del entrenador desde SharedPreferences
        SharedPreferences prefs = requireContext().getSharedPreferences("SportinePrefs", Context.MODE_PRIVATE);
        usuarioEntrenador = prefs.getString("USER_USERNAME", "");

        if (usuarioEntrenador.isEmpty()) {
            Toast.makeText(getContext(), "Error: Usuario no encontrado", Toast.LENGTH_SHORT).show();
            NavHostFragment.findNavController(this).navigateUp();
            return view;
        }

        apiService = RetrofitClient.getClient(requireContext()).create(ApiService.class);

        initViews(view);
        setupRecyclerView();
        setupListeners();

        cargarAlumnos();

        return view;
    }

    private void initViews(View view) {
        btnBack = view.findViewById(R.id.btn_back);  // ✅ AGREGADO
        recyclerAlumnos = view.findViewById(R.id.recycler_entrenadores);
        layoutEmptyState = view.findViewById(R.id.layout_empty_state);
        imagenNoEncontrado = view.findViewById(R.id.imagen_noenco);
        textoError = view.findViewById(R.id.texto_err);
        btnFiltroPendientes = view.findViewById(R.id.btn_filtro_pendientes);
    }

    private void setupRecyclerView() {
        recyclerAlumnos.setLayoutManager(new LinearLayoutManager(getContext()));
        alumnosAdapter = new MisAlumnosAdapter(alumno -> {
            // Click en tarjeta de alumno
            Bundle bundle = new Bundle();
            bundle.putString("usuarioAlumno", alumno.getUsuarioAlumno());
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_misAlumnos_to_infoAlumno, bundle);
        });
        recyclerAlumnos.setAdapter(alumnosAdapter);
    }

    private void setupListeners() {
        // ✅ AGREGADO: Listener del botón back
        btnBack.setOnClickListener(v -> {
            NavHostFragment.findNavController(this).navigateUp();
        });

        btnFiltroPendientes.setOnClickListener(v -> toggleFiltro());
    }

    private void cargarAlumnos() {
        if (!isAdded()) return;

        apiService.obtenerMisAlumnos(usuarioEntrenador).enqueue(new Callback<List<AlumnoEntrenadorDTO>>() {
            @Override
            public void onResponse(Call<List<AlumnoEntrenadorDTO>> call,
                                   Response<List<AlumnoEntrenadorDTO>> response) {
                if (!isAdded()) return;

                if (response.isSuccessful() && response.body() != null) {
                    List<AlumnoEntrenadorDTO> todosLosDatos = response.body();

                    // ✅ FILTRAR: Excluir finalizados y separar pendientes
                    todosLosAlumnos = new ArrayList<>();
                    alumnosPendientes = new ArrayList<>();

                    for (AlumnoEntrenadorDTO alumno : todosLosDatos) {
                        String status = alumno.getStatusRelacion();

                        // ✅ Ignorar finalizados
                        if (!"finalizado".equalsIgnoreCase(status)) {
                            todosLosAlumnos.add(alumno);

                            // Agregar a pendientes si aplica
                            if ("pendiente".equalsIgnoreCase(status)) {
                                alumnosPendientes.add(alumno);
                            }
                        }
                    }

                    mostrarAlumnos(todosLosAlumnos);
                } else {
                    Toast.makeText(getContext(),
                            "Error al cargar alumnos: " + response.code(),
                            Toast.LENGTH_SHORT).show();
                    mostrarEstadoVacio();
                }
            }

            @Override
            public void onFailure(Call<List<AlumnoEntrenadorDTO>> call, Throwable t) {
                if (!isAdded()) return;

                Toast.makeText(getContext(),
                        "Error de conexión: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
                mostrarEstadoVacio();
            }
        });
    }

    private void toggleFiltro() {
        mostrandoPendientes = !mostrandoPendientes;

        if (mostrandoPendientes) {
            // Mostrar solo pendientes
            btnFiltroPendientes.setText("Todos");
            btnFiltroPendientes.setTextColor(getResources().getColor(R.color.chip_text_selected, null));
            btnFiltroPendientes.setIconTintResource(R.color.chip_text_selected);
            mostrarAlumnos(alumnosPendientes);
        } else {
            // Mostrar todos
            btnFiltroPendientes.setText("Pendientes");
            btnFiltroPendientes.setTextColor(getResources().getColor(R.color.button_reject, null));
            btnFiltroPendientes.setIconTintResource(R.color.button_reject);
            mostrarAlumnos(todosLosAlumnos);
        }
    }

    private void mostrarAlumnos(List<AlumnoEntrenadorDTO> alumnos) {
        if (alumnos == null || alumnos.isEmpty()) {
            mostrarEstadoVacio();
        } else {
            recyclerAlumnos.setVisibility(View.VISIBLE);
            layoutEmptyState.setVisibility(View.GONE);
            alumnosAdapter.setAlumnos(alumnos);
        }
    }

    private void mostrarEstadoVacio() {
        recyclerAlumnos.setVisibility(View.GONE);
        layoutEmptyState.setVisibility(View.VISIBLE);

        if (mostrandoPendientes) {
            textoError.setText("No tienes alumnos pendientes");
        } else {
            textoError.setText("No se encontraron alumnos");
        }
    }
}