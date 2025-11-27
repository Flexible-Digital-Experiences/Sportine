package com.example.sportine.ui.usuarios.detallesEntrenador;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.sportine.R;
import com.example.sportine.data.ApiService;
import com.example.sportine.data.RetrofitClient;
import com.example.sportine.models.PerfilEntrenadorDTO;
import com.example.sportine.models.ResenaDTO;
import com.example.sportine.ui.usuarios.detallesEntrenador.DeportesAdapter;
import com.example.sportine.ui.usuarios.detallesEntrenador.ResenasAdapter;
import com.google.android.material.button.MaterialButton;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetallesEntrenadorFragment extends Fragment {

    private static final int MAX_RESENAS_VISIBLES = 3; // Máximo de reseñas a mostrar inicialmente

    private ApiService apiService;
    private String usuarioEntrenador;
    private List<ResenaDTO> todasLasResenas; // Lista completa de reseñas
    private boolean mostrandoTodas = false; // Estado actual

    // Views
    private ImageButton btnBack;
    private ImageView imagePerfil;
    private TextView textNombre;
    private RatingBar ratingEntrenador;
    private TextView textRating;
    private TextView textNumResenas;
    private TextView textUbicacion;
    private TextView textAcerca;
    private TextView textPrecio;
    private MaterialButton btnContratar;
    private TextView btnVerTodas;
    private TextView textSinResenas;

    // RecyclerViews
    private RecyclerView recyclerDeportes;
    private RecyclerView recyclerResenas;

    // Adapters
    private DeportesAdapter deportesAdapter;
    private ResenasAdapter resenasAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alumno_ver_detalles_entrenador, container, false);

        // Obtener el usuario del bundle
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

        // Configurar RecyclerViews
        setupRecyclerViews();

        // Configurar listeners
        setupListeners();

        // Cargar datos
        cargarPerfilEntrenador();

        return view;
    }

    private void initViews(View view) {
        btnBack = view.findViewById(R.id.btn_back);
        imagePerfil = view.findViewById(R.id.image_perfil);
        textNombre = view.findViewById(R.id.text_nombre);
        ratingEntrenador = view.findViewById(R.id.rating_entrenador);
        textRating = view.findViewById(R.id.text_rating);
        textNumResenas = view.findViewById(R.id.text_num_resenas);
        textUbicacion = view.findViewById(R.id.text_ubicacion);
        textAcerca = view.findViewById(R.id.text_acerca);
        textPrecio = view.findViewById(R.id.text_precio);
        btnContratar = view.findViewById(R.id.btn_contratar);
        btnVerTodas = view.findViewById(R.id.btn_ver_todas);
        textSinResenas = view.findViewById(R.id.text_sin_resenas);
        recyclerDeportes = view.findViewById(R.id.recycler_deportes);
        recyclerResenas = view.findViewById(R.id.recycler_resenas);
    }

    private void setupRecyclerViews() {
        // RecyclerView de deportes (horizontal)
        recyclerDeportes.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false)
        );
        deportesAdapter = new DeportesAdapter();
        recyclerDeportes.setAdapter(deportesAdapter);

        // RecyclerView de reseñas (vertical)
        recyclerResenas.setLayoutManager(new LinearLayoutManager(getContext()));
        resenasAdapter = new ResenasAdapter();
        recyclerResenas.setAdapter(resenasAdapter);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> NavHostFragment.findNavController(this).navigateUp());

        btnContratar.setOnClickListener(v -> {
            // TODO: Implementar lógica de contratación
            Toast.makeText(getContext(),
                    "Funcionalidad de contratación próximamente",
                    Toast.LENGTH_SHORT).show();
        });

        // Botón "Ver todas"
        btnVerTodas.setOnClickListener(v -> toggleResenas());
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

        // Ubicación
        textUbicacion.setText(perfil.getUbicacion());

        // Acerca de mí
        textAcerca.setText(perfil.getAcercaDeMi());

        // Precio
        textPrecio.setText(String.format("$%d MXN", perfil.getCostoMensual()));

        // Especialidades
        deportesAdapter.setDeportes(perfil.getEspecialidades());

        // Reseñas con límite
        mostrarResenas(perfil.getResenas());
    }

    private void mostrarResenas(List<ResenaDTO> resenas) {
        todasLasResenas = resenas;

        if (resenas == null || resenas.isEmpty()) {
            // No hay reseñas
            recyclerResenas.setVisibility(View.GONE);
            btnVerTodas.setVisibility(View.GONE);
            textSinResenas.setVisibility(View.VISIBLE);
        } else if (resenas.size() <= MAX_RESENAS_VISIBLES) {
            // Hay 3 o menos reseñas - Mostrar todas sin botón
            recyclerResenas.setVisibility(View.VISIBLE);
            btnVerTodas.setVisibility(View.GONE);
            textSinResenas.setVisibility(View.GONE);
            resenasAdapter.setResenas(resenas);
        } else {
            // Hay más de 3 reseñas - Mostrar solo 3 inicialmente
            recyclerResenas.setVisibility(View.VISIBLE);
            btnVerTodas.setVisibility(View.VISIBLE);
            textSinResenas.setVisibility(View.GONE);
            resenasAdapter.setResenas(resenas.subList(0, MAX_RESENAS_VISIBLES));
            btnVerTodas.setText("Ver todas (" + resenas.size() + ")");
        }
    }

    private void toggleResenas() {
        if (todasLasResenas == null || todasLasResenas.size() <= MAX_RESENAS_VISIBLES) {
            return;
        }

        if (mostrandoTodas) {
            // Mostrar solo las primeras 3
            resenasAdapter.setResenas(todasLasResenas.subList(0, MAX_RESENAS_VISIBLES));
            btnVerTodas.setText("Ver todas (" + todasLasResenas.size() + ")");
            mostrandoTodas = false;
        } else {
            // Mostrar todas
            resenasAdapter.setResenas(todasLasResenas);
            btnVerTodas.setText("Ver menos");
            mostrandoTodas = true;
        }
    }
}