package com.example.sportine.ui.usuarios.detallesEntrenador;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.example.sportine.models.EstadoRelacionDTO;
import com.example.sportine.models.FormularioSolicitudDTO;
import com.example.sportine.models.PerfilEntrenadorDTO;
import com.example.sportine.models.ResenaDTO;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetallesEntrenadorFragment extends Fragment {

    private static final int MAX_RESENAS_VISIBLES = 3;

    private ApiService apiService;
    private String usuarioEntrenador;
    private List<ResenaDTO> todasLasResenas;
    private boolean mostrandoTodas = false;
    private boolean hayDeportesDisponibles = true; // ← NUEVO FLAG

    // Views principales
    private ImageButton btnBack;
    private ImageView imagePerfil;
    private TextView textNombre;
    private RatingBar ratingEntrenador;
    private TextView textRating;
    private TextView textNumResenas;
    private TextView textUbicacion;
    private TextView textAcerca;
    private TextView textPrecio;
    private TextView btnVerTodas;
    private TextView textSinResenas;
    private TextView textCosto;

    // RecyclerViews
    private RecyclerView recyclerDeportes;
    private RecyclerView recyclerResenas;

    // Adapters
    private DeportesAdapter deportesAdapter;
    private ResenasAdapter resenasAdapter;

    // Layouts de estados de relación
    private MaterialCardView cardNoDisponible;
    private LinearLayout layoutSinRelacion;
    private LinearLayout layoutPendiente;
    private LinearLayout layoutActivo;
    private LinearLayout layoutFinalizado;

    // Botones por estado
    private MaterialButton btnEnviarSolicitud;
    private MaterialButton btnIrAPagar;
    private MaterialButton btnCancelarMensualidad;
    private MaterialButton btnCalificarActivo;
    private MaterialButton btnSolicitarNuevamente;
    private MaterialButton btnCalificarFinalizado;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alumno_ver_detalles_entrenador, container, false);

        if (getArguments() != null) {
            usuarioEntrenador = getArguments().getString("usuario");
        }

        if (usuarioEntrenador == null) {
            Toast.makeText(getContext(), "Error: No se especificó el entrenador", Toast.LENGTH_SHORT).show();
            NavHostFragment.findNavController(this).navigateUp();
            return view;
        }

        apiService = RetrofitClient.getClient(requireContext()).create(ApiService.class);

        initViews(view);
        setupRecyclerViews();
        setupListeners();

        // ✅ PRIMERO verificar deportes disponibles
        verificarDeportesDisponibles();
        // DESPUÉS cargar perfil
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
        textCosto = view.findViewById(R.id.texto_costo);
        btnVerTodas = view.findViewById(R.id.btn_ver_todas);
        textSinResenas = view.findViewById(R.id.text_sin_resenas);
        recyclerDeportes = view.findViewById(R.id.recycler_deportes);
        recyclerResenas = view.findViewById(R.id.recycler_resenas);

        cardNoDisponible = view.findViewById(R.id.card_no_disponible);

        layoutSinRelacion = view.findViewById(R.id.layout_sin_relacion);
        layoutPendiente = view.findViewById(R.id.layout_pendiente);
        layoutActivo = view.findViewById(R.id.layout_activo);
        layoutFinalizado = view.findViewById(R.id.layout_finalizado);

        btnEnviarSolicitud = view.findViewById(R.id.btn_enviar_solicitud);
        btnIrAPagar = view.findViewById(R.id.btn_ir_a_pagar);
        btnCancelarMensualidad = view.findViewById(R.id.btn_cancelar_mensualidad);
        btnCalificarActivo = view.findViewById(R.id.btn_calificar_activo);
        btnSolicitarNuevamente = view.findViewById(R.id.btn_solicitar_nuevamente);
        btnCalificarFinalizado = view.findViewById(R.id.btn_calificar_finalizado);
    }

    private void setupRecyclerViews() {
        recyclerDeportes.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false)
        );
        deportesAdapter = new DeportesAdapter();
        recyclerDeportes.setAdapter(deportesAdapter);

        recyclerResenas.setLayoutManager(new LinearLayoutManager(getContext()));
        resenasAdapter = new ResenasAdapter();
        recyclerResenas.setAdapter(resenasAdapter);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> NavHostFragment.findNavController(this).navigateUp());
        btnVerTodas.setOnClickListener(v -> toggleResenas());

        btnEnviarSolicitud.setOnClickListener(v -> enviarSolicitud());
        btnIrAPagar.setOnClickListener(v -> irAPagar());
        btnCancelarMensualidad.setOnClickListener(v -> cancelarMensualidad());
        btnCalificarActivo.setOnClickListener(v -> abrirDialogCalificacion());
        btnSolicitarNuevamente.setOnClickListener(v -> enviarSolicitud());
        btnCalificarFinalizado.setOnClickListener(v -> abrirDialogCalificacion());
    }

    // ============================================
    // ✅ ACTUALIZADO: Verificar deportes disponibles
    // ============================================
    private void verificarDeportesDisponibles() {
        if (!isAdded()) return;

        apiService.obtenerFormularioSolicitud(usuarioEntrenador).enqueue(new Callback<FormularioSolicitudDTO>() {
            @Override
            public void onResponse(Call<FormularioSolicitudDTO> call,
                                   Response<FormularioSolicitudDTO> response) {
                if (!isAdded()) return;

                if (response.isSuccessful() && response.body() != null) {
                    FormularioSolicitudDTO formulario = response.body();

                    if (formulario.getDeportesDisponibles().isEmpty()) {
                        // ❌ NO hay deportes disponibles
                        hayDeportesDisponibles = false;
                        cardNoDisponible.setVisibility(View.VISIBLE);
                        textPrecio.setVisibility(View.GONE);
                        textCosto.setVisibility(View.GONE);


                        // ✅ OCULTAR TODOS LOS LAYOUTS DE ACCIÓN
                        layoutSinRelacion.setVisibility(View.GONE);
                        layoutPendiente.setVisibility(View.GONE);
                        layoutActivo.setVisibility(View.GONE);
                        layoutFinalizado.setVisibility(View.GONE);
                    } else {
                        // ✅ SÍ hay deportes disponibles
                        hayDeportesDisponibles = true;
                        cardNoDisponible.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFailure(Call<FormularioSolicitudDTO> call, Throwable t) {
                if (isAdded()) {
                    hayDeportesDisponibles = true; // Por defecto permitir
                    cardNoDisponible.setVisibility(View.GONE);
                }
            }
        });
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
        if (perfil.getFotoPerfil() != null && !perfil.getFotoPerfil().isEmpty()) {
            Glide.with(this)
                    .load(perfil.getFotoPerfil())
                    .placeholder(R.drawable.avatar_user_male)
                    .error(R.drawable.avatar_user_male)
                    .circleCrop()
                    .into(imagePerfil);
        }

        textNombre.setText(perfil.getNombreCompleto());

        if (perfil.getCalificacion() != null) {
            float rating = perfil.getCalificacion().getRatingPromedio().floatValue();
            ratingEntrenador.setRating(rating);
            textRating.setText(String.format("%.1f", rating));
            textNumResenas.setText(String.format("(%d)", perfil.getCalificacion().getTotalResenas()));
        }

        textUbicacion.setText(perfil.getUbicacion());
        textAcerca.setText(perfil.getAcercaDeMi());
        textPrecio.setText(String.format("$%d MXN", perfil.getCostoMensual()));

        deportesAdapter.setDeportes(perfil.getEspecialidades());
        mostrarResenas(perfil.getResenas());

        // ✅ SOLO mostrar UI de relación SI hay deportes disponibles
        if (hayDeportesDisponibles) {
            mostrarUISegunEstadoRelacion(perfil.getEstadoRelacion());
        }
    }

    private void mostrarUISegunEstadoRelacion(EstadoRelacionDTO estado) {
        layoutSinRelacion.setVisibility(View.GONE);
        layoutPendiente.setVisibility(View.GONE);
        layoutActivo.setVisibility(View.GONE);
        layoutFinalizado.setVisibility(View.GONE);

        if (estado == null || !estado.getTieneRelacion()) {
            layoutSinRelacion.setVisibility(View.VISIBLE);
        } else {
            String estadoRelacion = estado.getEstadoRelacion();

            if ("pendiente".equals(estadoRelacion)) {
                layoutPendiente.setVisibility(View.VISIBLE);
            } else if ("activo".equals(estadoRelacion)) {
                layoutActivo.setVisibility(View.VISIBLE);
                if (estado.getYaCalificado() != null && estado.getYaCalificado()) {
                    btnCalificarActivo.setVisibility(View.GONE);
                }
            } else if ("finalizado".equals(estadoRelacion)) {
                layoutFinalizado.setVisibility(View.VISIBLE);
                if (estado.getYaCalificado() != null && estado.getYaCalificado()) {
                    btnCalificarFinalizado.setVisibility(View.GONE);
                }
            }
        }
    }

    private void enviarSolicitud() {
        Bundle bundle = new Bundle();
        bundle.putString("usuario", usuarioEntrenador);
        NavHostFragment.findNavController(this)
                .navigate(R.id.action_navigation_detallesEntrenador_to_enviarSolicitud, bundle);
    }

    private void irAPagar() {
        Toast.makeText(getContext(), "Funcionalidad de pago próximamente", Toast.LENGTH_SHORT).show();
    }

    private void cancelarMensualidad() {
        Toast.makeText(getContext(), "Funcionalidad de cancelar mensualidad próximamente", Toast.LENGTH_SHORT).show();
    }

    private void abrirDialogCalificacion() {
        Toast.makeText(getContext(), "Funcionalidad de calificación próximamente", Toast.LENGTH_SHORT).show();
    }

    private void mostrarResenas(List<ResenaDTO> resenas) {
        todasLasResenas = resenas;

        if (resenas == null || resenas.isEmpty()) {
            recyclerResenas.setVisibility(View.GONE);
            btnVerTodas.setVisibility(View.GONE);
            textSinResenas.setVisibility(View.VISIBLE);
        } else if (resenas.size() <= MAX_RESENAS_VISIBLES) {
            recyclerResenas.setVisibility(View.VISIBLE);
            btnVerTodas.setVisibility(View.GONE);
            textSinResenas.setVisibility(View.GONE);
            resenasAdapter.setResenas(resenas);
        } else {
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
            resenasAdapter.setResenas(todasLasResenas.subList(0, MAX_RESENAS_VISIBLES));
            btnVerTodas.setText("Ver todas (" + todasLasResenas.size() + ")");
            mostrandoTodas = false;
        } else {
            resenasAdapter.setResenas(todasLasResenas);
            btnVerTodas.setText("Ver menos");
            mostrandoTodas = true;
        }
    }
}