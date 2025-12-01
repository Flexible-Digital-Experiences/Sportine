package com.example.sportine.ui.entrenadores.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sportine.R;
import com.example.sportine.data.ApiService; // Asegúrate de importar esto
import com.example.sportine.data.RetrofitClient; // Asegúrate de importar esto
import com.example.sportine.models.HomeEntrenadorDTO;
import com.example.sportine.ui.entrenadores.asignarentrenamiento.AsignarEntrenamientoFragment;
import com.example.sportine.ui.entrenadores.feedback.FeedbackFragment;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private RecyclerView alumnosRecyclerView;
    private AlumnosAdapter alumnosAdapter;

    // UI Elements para el header
    private TextView textSaludo, textMensaje, textFecha;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_entrenador_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Inicializar vistas
        textSaludo = view.findViewById(R.id.text_saludo);
        textMensaje = view.findViewById(R.id.text_mensaje);
        textFecha = view.findViewById(R.id.text_fecha);
        alumnosRecyclerView = view.findViewById(R.id.recycler_alumnos);

        setupRecyclerView();
        cargarDatosHome();

        // Dentro de onViewCreated o inicializarVistas:
        View btnNotificaciones = view.findViewById(R.id.btn_notificaciones);
        btnNotificaciones.setOnClickListener(v -> {
            // Usamos el ID que acabamos de poner en entrenador_navigation.xml
            try {
                Navigation.findNavController(v).navigate(R.id.feedbackFragment);
            } catch (Exception e) {
                // Fallback por si acaso
                e.printStackTrace();
                Toast.makeText(getContext(), "Error de navegación", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupRecyclerView() {
        alumnosRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        alumnosAdapter = new AlumnosAdapter();
        alumnosRecyclerView.setAdapter(alumnosAdapter);

        // CONFIGURACIÓN DE NAVEGACIÓN REAL
        alumnosAdapter.setOnAlumnoClickListener(alumno -> {
            // 1. Crear el fragmento destino
            Fragment fragment = AsignarEntrenamientoFragment.newInstance(
                    alumno.getUsuario(),
                    alumno.getNombre() + " " + alumno.getApellidos(),
                    alumno.getFotoPerfil()
            );

            // 2. Realizar la transacción para cambiar de pantalla
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.nav_host_fragment_activity_main, fragment) // Asegúrate que este ID sea el de tu contenedor principal
                        .addToBackStack(null) // Para que el botón "Atrás" funcione
                        .commit();
            }
        });
    }

    private void cargarDatosHome() {
        // CORRECCIÓN: Usamos getClient(getContext()) y creamos la instancia de ApiService
        ApiService apiService = RetrofitClient.getClient(getContext()).create(ApiService.class);

        apiService.obtenerHomeEntrenador().enqueue(new Callback<HomeEntrenadorDTO>() {
            @Override
            public void onResponse(Call<HomeEntrenadorDTO> call, Response<HomeEntrenadorDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    HomeEntrenadorDTO data = response.body();
                    actualizarUI(data);
                } else {
                    Toast.makeText(getContext(), "Error al cargar datos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<HomeEntrenadorDTO> call, Throwable t) {
                Log.e("HomeEntrenador", "Error: " + t.getMessage());
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Fallo de conexión", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void actualizarUI(HomeEntrenadorDTO data) {
        // Verificar que el fragmento siga adjunto antes de actualizar UI
        if (getContext() == null) return;

        // 1. Header
        textSaludo.setText(data.getSaludo());
        textFecha.setText(data.getFecha());
        textMensaje.setText(data.getMensajeDinamico());

        // 2. Lista de alumnos
        if (data.getAlumnos() != null && !data.getAlumnos().isEmpty()) {
            alumnosAdapter.setAlumnos(data.getAlumnos());
        } else {
            alumnosAdapter.setAlumnos(new ArrayList<>());
        }
    }
}