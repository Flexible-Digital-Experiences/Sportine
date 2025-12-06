package com.example.sportine.ui.entrenadores.alumno;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.example.sportine.R;
import com.example.sportine.data.ApiService;
import com.example.sportine.data.RetrofitClient;
import com.example.sportine.models.AlumnoDetalleEntrenadorDTO;
import com.example.sportine.models.MisAlumnosResponseDTO;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InfoAlumno extends Fragment {

    private ApiService apiService;
    private String usuarioEntrenador;
    private String usuarioAlumno;

    private AlumnoDetalleEntrenadorDTO alumnoDetalle;
    private AlumnoDetalleEntrenadorDTO.DeporteConRelacionDTO deporteSeleccionado;

    // Views
    private ImageButton btnBack;
    private ImageView imagePerfil;
    private TextView textNombre;
    private TextView textEdad;
    private TextView textUbicacion;
    private TextView textEstatura;
    private TextView textPeso;
    private TextView textLesiones;
    private TextView textPadecimientos;

    // Spinners
    private Spinner spinnerDeportes;
    private Spinner spinnerNivel;
    private Spinner spinnerEstado;

    // Info del deporte seleccionado
    private TextView textFechaInicio;
    private TextView textFinMensualidad;

    // Botón guardar
    private MaterialButton btnGuardarCambios;

    // Layouts
    private LinearLayout layoutDatosDeporte;
    private LinearLayout layoutSinDeportes;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_info_alumno, container, false);

        // Obtener usuario del alumno desde arguments
        if (getArguments() != null) {
            usuarioAlumno = getArguments().getString("usuarioAlumno");
        }

        if (usuarioAlumno == null) {
            Toast.makeText(getContext(), "Error: Usuario del alumno no encontrado", Toast.LENGTH_SHORT).show();
            NavHostFragment.findNavController(this).navigateUp();
            return view;
        }

        // Obtener usuario del entrenador desde SharedPreferences
        SharedPreferences prefs = requireContext().getSharedPreferences("SportinePrefs", Context.MODE_PRIVATE);
        usuarioEntrenador = prefs.getString("USER_USERNAME", "");

        if (usuarioEntrenador.isEmpty()) {
            Toast.makeText(getContext(), "Error: Usuario del entrenador no encontrado", Toast.LENGTH_SHORT).show();
            NavHostFragment.findNavController(this).navigateUp();
            return view;
        }

        apiService = RetrofitClient.getClient(requireContext()).create(ApiService.class);

        initViews(view);
        setupListeners();
        cargarDetalleAlumno();

        return view;
    }

    private void initViews(View view) {
        btnBack = view.findViewById(R.id.btn_back);
        imagePerfil = view.findViewById(R.id.image_perfil);
        textNombre = view.findViewById(R.id.text_nombre);
        textEdad = view.findViewById(R.id.text_edad);
        textUbicacion = view.findViewById(R.id.text_ubicacion);
        textEstatura = view.findViewById(R.id.text_estatura);
        textPeso = view.findViewById(R.id.text_peso);
        textLesiones = view.findViewById(R.id.text_lesiones);
        textPadecimientos = view.findViewById(R.id.text_padecimientos);

        spinnerDeportes = view.findViewById(R.id.spinner_deportes);
        spinnerNivel = view.findViewById(R.id.spinner_nivel);
        spinnerEstado = view.findViewById(R.id.spinner_estado);

        textFechaInicio = view.findViewById(R.id.text_fecha_inicio);
        textFinMensualidad = view.findViewById(R.id.text_fin_mensualidad);

        btnGuardarCambios = view.findViewById(R.id.btn_guardar_cambios);

        layoutDatosDeporte = view.findViewById(R.id.layout_datos_deporte);
        layoutSinDeportes = view.findViewById(R.id.layout_sin_deportes);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> NavHostFragment.findNavController(this).navigateUp());

        // Listener para cuando cambia el deporte seleccionado
        spinnerDeportes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (alumnoDetalle != null && alumnoDetalle.getDeportes() != null
                        && position < alumnoDetalle.getDeportes().size()) {
                    deporteSeleccionado = alumnoDetalle.getDeportes().get(position);
                    mostrarDatosDeporte();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        btnGuardarCambios.setOnClickListener(v -> guardarCambios());
    }

    private void cargarDetalleAlumno() {
        if (!isAdded()) return;

        apiService.obtenerDetalleAlumno(usuarioEntrenador, usuarioAlumno)
                .enqueue(new Callback<AlumnoDetalleEntrenadorDTO>() {  // ← SIN wrapper
                    @Override
                    public void onResponse(Call<AlumnoDetalleEntrenadorDTO> call,
                                           Response<AlumnoDetalleEntrenadorDTO> response) {
                        if (!isAdded()) return;

                        if (response.isSuccessful() && response.body() != null) {
                            alumnoDetalle = response.body();  // ← Directamente, sin .getData()
                            mostrarDatosAlumno();
                        } else {
                            Toast.makeText(getContext(),
                                    "Error al cargar datos del alumno",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<AlumnoDetalleEntrenadorDTO> call, Throwable t) {
                        if (!isAdded()) return;
                        Toast.makeText(getContext(),
                                "Error de conexión: " + t.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void mostrarDatosAlumno() {
        // Foto de perfil
        if (alumnoDetalle.getFotoPerfil() != null && !alumnoDetalle.getFotoPerfil().isEmpty()) {
            Glide.with(this)
                    .load(alumnoDetalle.getFotoPerfil())
                    .placeholder(R.drawable.avatar_user_male)
                    .error(R.drawable.avatar_user_male)
                    .circleCrop()
                    .into(imagePerfil);
        }

        // Datos básicos
        textNombre.setText(alumnoDetalle.getNombreCompleto());
        textEdad.setText(alumnoDetalle.getEdad() + " años");
        textUbicacion.setText("📍 " + alumnoDetalle.getCiudad());

        // Datos físicos
        if (alumnoDetalle.getEstatura() != null) {
            textEstatura.setText(String.format("%.2f m", alumnoDetalle.getEstatura()));
        } else {
            textEstatura.setText("No especificado");
        }

        if (alumnoDetalle.getPeso() != null) {
            textPeso.setText(String.format("%.1f kg", alumnoDetalle.getPeso()));
        } else {
            textPeso.setText("No especificado");
        }

        // Datos de salud
        textLesiones.setText(alumnoDetalle.getLesiones() != null && !alumnoDetalle.getLesiones().isEmpty()
                ? alumnoDetalle.getLesiones() : "Ninguna");
        textPadecimientos.setText(alumnoDetalle.getPadecimientos() != null && !alumnoDetalle.getPadecimientos().isEmpty()
                ? alumnoDetalle.getPadecimientos() : "Ninguno");

        // Configurar spinners
        if (alumnoDetalle.getDeportes() != null && !alumnoDetalle.getDeportes().isEmpty()) {
            layoutDatosDeporte.setVisibility(View.VISIBLE);
            layoutSinDeportes.setVisibility(View.GONE);

            configurarSpinnerDeportes();
            configurarSpinnerNivel();
            configurarSpinnerEstado();
        } else {
            layoutDatosDeporte.setVisibility(View.GONE);
            layoutSinDeportes.setVisibility(View.VISIBLE);
        }
    }

    private void configurarSpinnerDeportes() {
        List<String> nombresDeportes = new ArrayList<>();
        for (AlumnoDetalleEntrenadorDTO.DeporteConRelacionDTO deporte : alumnoDetalle.getDeportes()) {
            nombresDeportes.add(deporte.getNombreDeporte());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                nombresDeportes
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDeportes.setAdapter(adapter);
    }

    private void configurarSpinnerNivel() {
        List<String> niveles = Arrays.asList("Principiante", "Intermedio", "Avanzado");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                niveles
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerNivel.setAdapter(adapter);
    }

    private void configurarSpinnerEstado() {
        List<String> estados = Arrays.asList("activo", "pendiente", "finalizado");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                estados
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEstado.setAdapter(adapter);
    }

    private void mostrarDatosDeporte() {
        if (deporteSeleccionado == null) return;

        // Seleccionar nivel actual
        String nivelActual = deporteSeleccionado.getNivel();
        if (nivelActual != null) {
            int posNivel = Arrays.asList("Principiante", "Intermedio", "Avanzado").indexOf(nivelActual);
            if (posNivel >= 0) {
                spinnerNivel.setSelection(posNivel);
            }
        }

        // Seleccionar estado actual
        String estadoActual = deporteSeleccionado.getEstadoRelacion();
        if (estadoActual != null) {
            int posEstado = Arrays.asList("activo", "pendiente", "finalizado").indexOf(estadoActual);
            if (posEstado >= 0) {
                spinnerEstado.setSelection(posEstado);
            }
        }

        // Mostrar fechas
        textFechaInicio.setText("📅 Inicio: " + deporteSeleccionado.getFechaInicio());
        textFinMensualidad.setText("📅 Vence: " + deporteSeleccionado.getFinMensualidad());

    }


    // ✅ MÉTODO HELPER: Convertir nombre de nivel a ID
    private Integer obtenerIdNivel(String nombreNivel) {
        switch (nombreNivel) {
            case "Principiante":
                return 1;
            case "Intermedio":
                return 2;
            case "Avanzado":
                return 3;
            default:
                return 1; // Por defecto Principiante
        }
    }

    private void guardarCambios() {
        if (deporteSeleccionado == null) return;

        String nivelSeleccionado = spinnerNivel.getSelectedItem().toString();
        String estadoSeleccionado = spinnerEstado.getSelectedItem().toString();

        boolean cambioNivel = !nivelSeleccionado.equals(deporteSeleccionado.getNivel());
        boolean cambioEstado = !estadoSeleccionado.equals(deporteSeleccionado.getEstadoRelacion());

        if (!cambioNivel && !cambioEstado) {
            Toast.makeText(getContext(), "No hay cambios para guardar", Toast.LENGTH_SHORT).show();
            return;
        }

        // Actualizar nivel si cambió
        if (cambioNivel) {
            Integer idNivel = obtenerIdNivel(nivelSeleccionado);
            actualizarNivel(deporteSeleccionado.getIdDeporte(), idNivel, cambioEstado, estadoSeleccionado);
        } else if (cambioEstado) {
            actualizarEstado(deporteSeleccionado.getIdDeporte(), estadoSeleccionado);
        }
    }

    private void actualizarNivel(Integer idDeporte, Integer nuevoNivel, boolean tambienEstado, String nuevoEstado) {
        apiService.actualizarNivelAlumno(usuarioEntrenador, usuarioAlumno, idDeporte, nuevoNivel)
                .enqueue(new Callback<MisAlumnosResponseDTO<String>>() {
                    @Override
                    public void onResponse(Call<MisAlumnosResponseDTO<String>> call, Response<MisAlumnosResponseDTO<String>> response) {
                        if (!isAdded()) return;

                        if (response.isSuccessful()) {
                            if (tambienEstado) {
                                actualizarEstado(idDeporte, nuevoEstado);
                            } else {
                                Toast.makeText(getContext(), "Nivel actualizado", Toast.LENGTH_SHORT).show();
                                cargarDetalleAlumno();
                            }
                        } else {
                            Toast.makeText(getContext(), "Error al actualizar nivel", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<MisAlumnosResponseDTO<String>> call, Throwable t) {
                        if (!isAdded()) return;
                        Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void actualizarEstado(Integer idDeporte, String nuevoEstado) {
        apiService.actualizarEstadoRelacion(usuarioEntrenador, usuarioAlumno, idDeporte, nuevoEstado)
                .enqueue(new Callback<MisAlumnosResponseDTO<String>>() {
                    @Override
                    public void onResponse(Call<MisAlumnosResponseDTO<String>> call, Response<MisAlumnosResponseDTO<String>> response) {
                        if (!isAdded()) return;

                        if (response.isSuccessful()) {
                            Toast.makeText(getContext(), "Cambios guardados exitosamente", Toast.LENGTH_SHORT).show();
                            cargarDetalleAlumno();
                        } else {
                            Toast.makeText(getContext(), "Error al actualizar estado", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<MisAlumnosResponseDTO<String>> call, Throwable t) {
                        if (!isAdded()) return;
                        Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}