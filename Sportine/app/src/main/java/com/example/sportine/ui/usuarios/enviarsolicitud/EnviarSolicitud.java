package com.example.sportine.ui.usuarios.enviarsolicitud;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
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
import com.example.sportine.models.FormularioSolicitudDTO;
import com.example.sportine.models.InfoDeporteAlumnoDTO;
import com.example.sportine.models.PerfilEntrenadorDTO;
import com.example.sportine.models.SolicitudResponseDTO;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.example.sportine.models.DeporteDisponibleDTO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.example.sportine.models.SolicitudRequestDTO;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EnviarSolicitud extends Fragment {
    private String usuarioEntrenador;
    private ApiService apiService;

    // Views principales
    private ImageButton btnBack;
    private ImageView imagePerfil;
    private TextView textNombre;
    private RatingBar ratingEntrenador;
    private TextView textRating;
    private TextView textNumResenas;

    // Formulario
    private Spinner spinnerDeporte;
    private TextView textLabelNivel;  // ← NUEVO
    private Spinner spinnerNivel;
    private TextView textNivelActual;
    private TextInputEditText editMotivo;
    private MaterialButton btnEnviarSolicitud;

    // Datos
    private List<DeporteDisponibleDTO> deportesDisponibles = new ArrayList<>();
    private DeporteDisponibleDTO deporteSeleccionado;
    private InfoDeporteAlumnoDTO infoDeporteActual;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alumno_enviar_solicitud, container, false);

        // Obtener argumentos
        if (getArguments() != null) {
            usuarioEntrenador = getArguments().getString("usuario");
        }

        if (usuarioEntrenador == null) {
            Toast.makeText(getContext(), "Error: No se especificó el entrenador", Toast.LENGTH_SHORT).show();
            NavHostFragment.findNavController(this).navigateUp();
            return view;
        }

        // Inicializar API
        apiService = RetrofitClient.getClient(requireContext()).create(ApiService.class);

        // Inicializar views
        initViews(view);

        // Configurar listeners
        setupListeners();

        // Cargar datos
        cargarPerfilEntrenador();
        cargarFormulario();

        return view;
    }

    private void initViews(View view) {
        // Header
        btnBack = view.findViewById(R.id.btn_back);
        imagePerfil = view.findViewById(R.id.image_perfil);
        textNombre = view.findViewById(R.id.text_nombre);
        ratingEntrenador = view.findViewById(R.id.rating_de_entrenador);
        textRating = view.findViewById(R.id.text_rating_de_en);
        textNumResenas = view.findViewById(R.id.text_num_resenas_deen);

        // Formulario
        spinnerDeporte = view.findViewById(R.id.spinner_deporte);
        textLabelNivel = view.findViewById(R.id.text_label_nivel); // ← NUEVO
        spinnerNivel = view.findViewById(R.id.spinner_nivel);
        textNivelActual = view.findViewById(R.id.text_nivel_actual);
        editMotivo = view.findViewById(R.id.edit_motivo);
        btnEnviarSolicitud = view.findViewById(R.id.btn_enviar_solicitud);

        // Inicialmente ocultar spinner de nivel y su label
        textLabelNivel.setVisibility(View.GONE);
        spinnerNivel.setVisibility(View.GONE);
        textNivelActual.setVisibility(View.GONE);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> NavHostFragment.findNavController(this).navigateUp());

        // Listener para selección de deporte
        spinnerDeporte.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) { // Ignorar la primera posición ("Selecciona un deporte")
                    deporteSeleccionado = deportesDisponibles.get(position - 1);
                    consultarInfoDeporte(deporteSeleccionado.getIdDeporte());
                } else {
                    // No hay deporte seleccionado - Ocultar todo
                    textLabelNivel.setVisibility(View.GONE);
                    spinnerNivel.setVisibility(View.GONE);
                    textNivelActual.setVisibility(View.GONE);
                    deporteSeleccionado = null;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                textLabelNivel.setVisibility(View.GONE);
                spinnerNivel.setVisibility(View.GONE);
                textNivelActual.setVisibility(View.GONE);
                deporteSeleccionado = null;
            }
        });

        // Listener para botón de enviar
        btnEnviarSolicitud.setOnClickListener(v -> enviarSolicitud());
    }

    private void cargarPerfilEntrenador() {
        if (!isAdded()) return;

        apiService.obtenerPerfilEntrenador(usuarioEntrenador).enqueue(new Callback<PerfilEntrenadorDTO>() {
            @Override
            public void onResponse(Call<PerfilEntrenadorDTO> call,
                                   Response<PerfilEntrenadorDTO> response) {
                if (!isAdded()) return;

                if (response.isSuccessful() && response.body() != null) {
                    mostrarPerfil(response.body());
                } else {
                    Toast.makeText(getContext(),
                            "Error al cargar perfil: " + response.code(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PerfilEntrenadorDTO> call, Throwable t) {
                if (!isAdded()) return;

                Toast.makeText(getContext(),
                        "Error de conexión: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarPerfil(PerfilEntrenadorDTO perfil) {
        // Foto de perfil
        if (perfil.getFotoPerfil() != null && !perfil.getFotoPerfil().isEmpty()) {
            Glide.with(this)
                    .load(perfil.getFotoPerfil())
                    .placeholder(R.drawable.avatar_user_male)
                    .error(R.drawable.avatar_user_male)
                    .circleCrop()
                    .into(imagePerfil);
        }

        // Nombre
        textNombre.setText(perfil.getNombreCompleto());

        // Calificación
        if (perfil.getCalificacion() != null) {
            float rating = perfil.getCalificacion().getRatingPromedio().floatValue();
            ratingEntrenador.setRating(rating);
            textRating.setText(String.format("%.1f", rating));
            textNumResenas.setText(String.format("(%d)", perfil.getCalificacion().getTotalResenas()));
        }
    }

    private void cargarFormulario() {
        if (!isAdded()) return;

        apiService.obtenerFormularioSolicitud(usuarioEntrenador).enqueue(new Callback<FormularioSolicitudDTO>() {
            @Override
            public void onResponse(Call<FormularioSolicitudDTO> call,
                                   Response<FormularioSolicitudDTO> response) {
                if (!isAdded()) return;

                if (response.isSuccessful() && response.body() != null) {
                    FormularioSolicitudDTO formulario = response.body();
                    deportesDisponibles = formulario.getDeportesDisponibles();

                    if (deportesDisponibles.isEmpty()) {
                        Toast.makeText(getContext(),
                                "No hay deportes disponibles para solicitar",
                                Toast.LENGTH_LONG).show();
                    }

                    configurarSpinnerDeportes();
                    configurarSpinnerNivel();
                } else {
                    Toast.makeText(getContext(),
                            "Error al cargar deportes: " + response.code(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<FormularioSolicitudDTO> call, Throwable t) {
                if (!isAdded()) return;

                Toast.makeText(getContext(),
                        "Error de conexión: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void configurarSpinnerDeportes() {
        List<String> nombresDeportes = new ArrayList<>();
        nombresDeportes.add("Selecciona un deporte"); // Primera opción

        for (DeporteDisponibleDTO deporte : deportesDisponibles) {
            nombresDeportes.add(deporte.getNombreDeporte());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                nombresDeportes
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDeporte.setAdapter(adapter);
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

    private void consultarInfoDeporte(Integer idDeporte) {
        if (!isAdded()) return;

        apiService.obtenerInfoDeporte(idDeporte).enqueue(new Callback<InfoDeporteAlumnoDTO>() {
            @Override
            public void onResponse(Call<InfoDeporteAlumnoDTO> call,
                                   Response<InfoDeporteAlumnoDTO> response) {
                if (!isAdded()) return;

                if (response.isSuccessful() && response.body() != null) {
                    infoDeporteActual = response.body();
                    actualizarUISegunDeporte();
                } else {
                    Toast.makeText(getContext(),
                            "Error al obtener info del deporte: " + response.code(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<InfoDeporteAlumnoDTO> call, Throwable t) {
                if (!isAdded()) return;

                Toast.makeText(getContext(),
                        "Error de conexión: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void actualizarUISegunDeporte() {
        if (infoDeporteActual == null) return;

        if (infoDeporteActual.isTieneNivelRegistrado()) {
            // YA tiene nivel registrado - Mostrar nivel actual
            textLabelNivel.setVisibility(View.GONE);
            spinnerNivel.setVisibility(View.GONE);
            textNivelActual.setVisibility(View.VISIBLE);
            textNivelActual.setText("Tu nivel actual: " + infoDeporteActual.getNivelActual());
        } else {
            // NO tiene nivel - Mostrar spinner para seleccionar
            textLabelNivel.setVisibility(View.VISIBLE);
            spinnerNivel.setVisibility(View.VISIBLE);
            textNivelActual.setVisibility(View.GONE);
        }
    }

    private void enviarSolicitud() {
        // Validaciones
        if (deporteSeleccionado == null) {
            Toast.makeText(getContext(), "Selecciona un deporte", Toast.LENGTH_SHORT).show();
            return;
        }

        String nivel = null;
        if (infoDeporteActual != null && infoDeporteActual.isTieneNivelRegistrado()) {
            // Usar nivel actual del alumno
            nivel = infoDeporteActual.getNivelActual();
        } else if (spinnerNivel.getVisibility() == View.VISIBLE) {
            // Obtener nivel seleccionado del spinner
            nivel = spinnerNivel.getSelectedItem().toString();
        }

        String motivo = editMotivo.getText() != null ? editMotivo.getText().toString().trim() : "";
        if (motivo.isEmpty()) {
            Toast.makeText(getContext(), "Escribe el motivo de tu solicitud", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear objeto de solicitud
        SolicitudRequestDTO request = new SolicitudRequestDTO(
                usuarioEntrenador,
                deporteSeleccionado.getIdDeporte(),
                nivel,
                motivo
        );

        // Enviar solicitud
        enviarSolicitudAlBackend(request);
    }

    private void enviarSolicitudAlBackend(SolicitudRequestDTO request) {
        if (!isAdded()) return;

        btnEnviarSolicitud.setEnabled(false);

        apiService.enviarSolicitud(request).enqueue(new Callback<SolicitudResponseDTO>() {
            @Override
            public void onResponse(Call<SolicitudResponseDTO> call,
                                   Response<SolicitudResponseDTO> response) {
                if (!isAdded()) return;

                btnEnviarSolicitud.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    SolicitudResponseDTO resultado = response.body();

                    Toast.makeText(getContext(),
                            resultado.getMensaje(),
                            Toast.LENGTH_LONG).show();

                    // Volver al perfil del entrenador o a la lista
                    NavHostFragment.findNavController(EnviarSolicitud.this)
                            .navigateUp();

                } else {
                    try {
                        String errorBody = response.errorBody() != null ?
                                response.errorBody().string() : "Error desconocido";
                        Toast.makeText(getContext(),
                                "Error: " + errorBody,
                                Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(getContext(),
                                "Error al enviar solicitud",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<SolicitudResponseDTO> call, Throwable t) {
                if (!isAdded()) return;

                btnEnviarSolicitud.setEnabled(true);
                Toast.makeText(getContext(),
                        "Error de conexión: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}