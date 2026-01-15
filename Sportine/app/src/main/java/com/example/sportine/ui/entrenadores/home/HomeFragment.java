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
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sportine.R;
import com.example.sportine.data.ApiService;
import com.example.sportine.data.RetrofitClient;
import com.example.sportine.models.HomeEntrenadorDTO;
import com.example.sportine.ui.entrenadores.asignarentrenamiento.AsignarEntrenamientoFragment;

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

        View btnNotificaciones = view.findViewById(R.id.btn_notificaciones);
        btnNotificaciones.setOnClickListener(v -> {
            try {
                Navigation.findNavController(v).navigate(R.id.feedbackFragment);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Error de navegación", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupRecyclerView() {
        alumnosRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        alumnosAdapter = new AlumnosAdapter();
        alumnosRecyclerView.setAdapter(alumnosAdapter);
        /*
        // CONFIGURACIÓN DE NAVEGACIÓN
        alumnosAdapter.setOnAlumnoClickListener(alumno -> {
            // AHORA PASAMOS 5 ARGUMENTOS (usuario, nombre, foto, deporte, actividad)
            Fragment fragment = AsignarEntrenamientoFragment.newInstance(
                    alumno.getUsuario(),
                    alumno.getNombre() + " " + (alumno.getApellidos() != null ? alumno.getApellidos() : ""),
                    alumno.getFotoPerfil(),
                    alumno.getDeporte(), // Deporte para el icono
                    alumno.getDescripcionActividad() // Actividad para el texto de detalle
            );

            if (getActivity() != null) {
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.nav_host_fragment_activity_main, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
        */

        alumnosAdapter.setOnAlumnoClickListener(alumno -> {

            Bundle args = new Bundle();
            args.putString("usuario", alumno.getUsuario());
            args.putString("nombre",
                    alumno.getNombre() + " " +
                            (alumno.getApellidos() != null ? alumno.getApellidos() : ""));
            args.putString("foto", alumno.getFotoPerfil());
            args.putString("deporte", alumno.getDeporte());
            args.putString("actividad", alumno.getDescripcionActividad());

            NavHostFragment.findNavController(this)
                    .navigate(R.id.asignarEntrenamientoFragment, args);
        });


    }

    private void cargarDatosHome() {
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
        if (getContext() == null) return;

        textSaludo.setText(data.getSaludo());
        textFecha.setText(data.getFecha());
        textMensaje.setText(data.getMensajeDinamico());

        if (data.getAlumnos() != null && !data.getAlumnos().isEmpty()) {
            alumnosAdapter.setAlumnos(data.getAlumnos());
        } else {
            alumnosAdapter.setAlumnos(new ArrayList<>());
        }
    }
}