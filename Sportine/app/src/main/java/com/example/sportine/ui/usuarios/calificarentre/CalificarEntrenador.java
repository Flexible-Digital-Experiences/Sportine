package com.example.sportine.ui.usuarios.calificarentre;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
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
import com.example.sportine.models.PerfilEntrenadorDTO;
import com.example.sportine.models.CalificacionRequestDTO;
import com.example.sportine.models.CalificacionResponseDTO;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CalificarEntrenador extends Fragment {
    private String usuarioEntrenador;
    private ApiService apiService;

    // Views principales
    private ImageButton btnBack;
    private ImageView imagePerfil;
    private TextView textNombre;
    private RatingBar ratingEntrenador;
    private TextView textRating;
    private TextView textNumResenas;

    // Formulario de Calificación
    private RatingBar ratingBarAlumno;
    private TextView textRatingValue;
    private TextInputEditText editComentario;
    private MaterialButton btnEnviarCalificacion;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calificar_entrenador, container, false);

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

        // Formulario de Calificación
        ratingBarAlumno = view.findViewById(R.id.rating_bar_alumno);
        textRatingValue = view.findViewById(R.id.text_rating_value);
        editComentario = view.findViewById(R.id.edit_motivo);
        btnEnviarCalificacion = view.findViewById(R.id.btn_enviar_solicitud);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> NavHostFragment.findNavController(this).navigateUp());

        // Listener para RatingBar del alumno
        ratingBarAlumno.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            if (fromUser) {
                actualizarTextoCalificacion(rating);
            }
        });

        // Listener para botón de enviar
        btnEnviarCalificacion.setOnClickListener(v -> enviarCalificacion());
    }

    private void actualizarTextoCalificacion(float rating) {
        int ratingInt = (int) rating;
        String texto;

        switch (ratingInt) {
            case 1:
                texto = "⭐ Malo";
                break;
            case 2:
                texto = "⭐⭐ Regular";
                break;
            case 3:
                texto = "⭐⭐⭐ Bueno";
                break;
            case 4:
                texto = "⭐⭐⭐⭐ Muy Bueno";
                break;
            case 5:
                texto = "⭐⭐⭐⭐⭐ Excelente";
                break;
            default:
                texto = "Selecciona tu calificación";
                break;
        }

        textRatingValue.setText(texto);
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

        // Calificación actual del entrenador
        if (perfil.getCalificacion() != null) {
            float rating = perfil.getCalificacion().getRatingPromedio().floatValue();
            ratingEntrenador.setRating(rating);
            textRating.setText(String.format("%.1f", rating));
            textNumResenas.setText(String.format("(%d)", perfil.getCalificacion().getTotalResenas()));
        } else {
            // Si no tiene calificaciones aún
            ratingEntrenador.setRating(0);
            textRating.setText("0.0");
            textNumResenas.setText("(0)");
        }
    }

    private void enviarCalificacion() {
        // Validar calificación
        float rating = ratingBarAlumno.getRating();
        if (rating == 0) {
            Toast.makeText(getContext(), "Por favor selecciona una calificación", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validar comentario
        String comentario = editComentario.getText() != null ?
                editComentario.getText().toString().trim() : "";
        if (comentario.isEmpty()) {
            Toast.makeText(getContext(), "Por favor escribe un comentario", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear objeto de calificación
        CalificacionRequestDTO request = new CalificacionRequestDTO(
                usuarioEntrenador,
                (int) rating,
                comentario
        );

        // Enviar calificación
        enviarCalificacionAlBackend(request);
    }

    private void enviarCalificacionAlBackend(CalificacionRequestDTO request) {
        if (!isAdded()) return;

        // Deshabilitar botón mientras se envía
        btnEnviarCalificacion.setEnabled(false);
        btnEnviarCalificacion.setText("Enviando...");

        apiService.enviarCalificacion(request).enqueue(new Callback<CalificacionResponseDTO>() {
            @Override
            public void onResponse(Call<CalificacionResponseDTO> call,
                                   Response<CalificacionResponseDTO> response) {
                if (!isAdded()) return;

                btnEnviarCalificacion.setEnabled(true);
                btnEnviarCalificacion.setText("Postear Calificación");

                if (response.isSuccessful() && response.body() != null) {
                    CalificacionResponseDTO resultado = response.body();

                    Toast.makeText(getContext(),
                            resultado.getMensaje() != null ?
                                    resultado.getMensaje() : "Calificación enviada con éxito",
                            Toast.LENGTH_LONG).show();

                    // Volver atrás
                    NavHostFragment.findNavController(CalificarEntrenador.this)
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
                                "Error al enviar calificación",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<CalificacionResponseDTO> call, Throwable t) {
                if (!isAdded()) return;

                btnEnviarCalificacion.setEnabled(true);
                btnEnviarCalificacion.setText("Postear Calificación");

                Toast.makeText(getContext(),
                        "Error de conexión: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}