package com.example.sportine.ui.usuarios.detallesEntrenador;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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
import com.example.sportine.models.SolicitudPendienteDTO;
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
    private boolean hayDeportesDisponibles = true;

    // TODO: Cuando implementes la obtención de datos de contacto, guárdalos aquí
    private String correoEntrenador = ""; // Se obtendrá del backend
    private String telefonoEntrenador = ""; // Se obtendrá del backend

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
    private MaterialButton btnSolicitarMasDeportesPendiente;
    private MaterialButton btnSolicitarMasDeportesActivo;

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
    private LinearLayout layoutEsperandoRespuesta;
    private RecyclerView recyclerSolicitudesPendientes;
    private SolicitudesPendientesAdapter solicitudesPendientesAdapter;

    // Botones por estado
    private MaterialButton btnEnviarSolicitud;
    private MaterialButton btnEnviarCorreo;
    private MaterialButton btnEnviarWhatsapp;
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
        verificarDeportesDisponibles();

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
        btnSolicitarMasDeportesPendiente = view.findViewById(R.id.btn_solicitar_mas_deportes_pendiente);
        btnSolicitarMasDeportesActivo = view.findViewById(R.id.btn_solicitar_mas_deportes_activo);

        cardNoDisponible = view.findViewById(R.id.card_no_disponible);

        layoutSinRelacion = view.findViewById(R.id.layout_sin_relacion);
        layoutPendiente = view.findViewById(R.id.layout_pendiente);
        layoutActivo = view.findViewById(R.id.layout_activo);
        layoutFinalizado = view.findViewById(R.id.layout_finalizado);
        btnEnviarSolicitud = view.findViewById(R.id.btn_enviar_solicitud);
        btnEnviarCorreo = view.findViewById(R.id.btn_enviar_correo);
        btnEnviarWhatsapp = view.findViewById(R.id.btn_enviar_whatsapp);
        btnCalificarActivo = view.findViewById(R.id.btn_calificar_activo);
        btnSolicitarNuevamente = view.findViewById(R.id.btn_solicitar_nuevamente);
        btnCalificarFinalizado = view.findViewById(R.id.btn_calificar_finalizado);

        layoutEsperandoRespuesta = view.findViewById(R.id.layout_esperando_respuesta);
        recyclerSolicitudesPendientes = view.findViewById(R.id.recycler_solicitudes_pendientes);
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

        recyclerSolicitudesPendientes.setLayoutManager(new LinearLayoutManager(getContext()));
        solicitudesPendientesAdapter = new SolicitudesPendientesAdapter();
        recyclerSolicitudesPendientes.setAdapter(solicitudesPendientesAdapter);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> NavHostFragment.findNavController(this).navigateUp());
        btnVerTodas.setOnClickListener(v -> toggleResenas());
        btnEnviarSolicitud.setOnClickListener(v -> enviarSolicitud());
        btnEnviarCorreo.setOnClickListener(v -> enviarCorreo());
        btnEnviarWhatsapp.setOnClickListener(v -> enviarWhatsapp());
        btnCalificarActivo.setOnClickListener(v -> abrirDialogCalificacion());
        btnSolicitarNuevamente.setOnClickListener(v -> enviarSolicitud());
        btnCalificarFinalizado.setOnClickListener(v -> abrirDialogCalificacion());
        btnSolicitarMasDeportesPendiente.setOnClickListener(v -> enviarSolicitud());
        btnSolicitarMasDeportesActivo.setOnClickListener(v -> enviarSolicitud());
    }

    private void verificarDeportesDisponibles() {
        if (!isAdded()) return;

        apiService.obtenerFormularioSolicitud(usuarioEntrenador).enqueue(new Callback<FormularioSolicitudDTO>() {
            @Override
            public void onResponse(Call<FormularioSolicitudDTO> call,
                                   Response<FormularioSolicitudDTO> response) {
                if (!isAdded()) return;

                if (response.isSuccessful() && response.body() != null) {
                    FormularioSolicitudDTO formulario = response.body();

                    // ✅ SIEMPRE verificar si hay solicitudes pendientes primero
                    verificarSolicitudPendiente(formulario.getDeportesDisponibles().isEmpty());
                }
            }

            @Override
            public void onFailure(Call<FormularioSolicitudDTO> call, Throwable t) {
                if (isAdded()) {
                    hayDeportesDisponibles = true;
                    cardNoDisponible.setVisibility(View.GONE);
                    layoutEsperandoRespuesta.setVisibility(View.GONE);
                }
            }
        });
    }

    private void verificarSolicitudPendiente(boolean noHayDeportesDisponibles) {
        if (!isAdded()) return;

        apiService.verificarSolicitudPendiente(usuarioEntrenador).enqueue(new Callback<SolicitudPendienteDTO>() {
            @Override
            public void onResponse(Call<SolicitudPendienteDTO> call,
                                   Response<SolicitudPendienteDTO> response) {
                if (!isAdded()) return;

                if (response.isSuccessful() && response.body() != null) {
                    SolicitudPendienteDTO solicitud = response.body();

                    if (solicitud.getTieneSolicitudPendiente() &&
                            solicitud.getSolicitudes() != null &&
                            !solicitud.getSolicitudes().isEmpty()) {

                        // ✅ SIEMPRE mostrar solicitudes pendientes si existen
                        layoutEsperandoRespuesta.setVisibility(View.VISIBLE);
                        solicitudesPendientesAdapter.setSolicitudes(solicitud.getSolicitudes());

                        // Decidir qué más mostrar según si hay deportes disponibles
                        if (noHayDeportesDisponibles) {
                            // ⚠️ NO hay más deportes disponibles
                            // Puede ser porque: (1) ya los tiene todos, o (2) el entrenador no entrena otros
                            // La diferencia la sabremos cuando veamos el EstadoRelacionDTO
                            hayDeportesDisponibles = false;
                            textPrecio.setVisibility(View.GONE);
                            textCosto.setVisibility(View.GONE);
                        } else {
                            // ✅ SÍ hay deportes disponibles - mostrar solicitudes Y permitir enviar más
                            hayDeportesDisponibles = true;
                            textPrecio.setVisibility(View.VISIBLE);
                            textCosto.setVisibility(View.VISIBLE);
                        }
                    } else {
                        // No hay solicitudes pendientes EN REVISIÓN
                        layoutEsperandoRespuesta.setVisibility(View.GONE);

                        if (noHayDeportesDisponibles) {
                            // ⚠️ No hay deportes disponibles
                            // Verificaremos en mostrarUISegunEstadoRelacion si es porque tiene relación
                            hayDeportesDisponibles = false;
                            textPrecio.setVisibility(View.GONE);
                            textCosto.setVisibility(View.GONE);
                        } else {
                            // ✅ Hay deportes disponibles
                            hayDeportesDisponibles = true;
                        }
                    }
                }

                // ✅ CARGAR PERFIL AL FINAL (después de saber si hay deportes disponibles)
                cargarPerfilEntrenador();
            }

            @Override
            public void onFailure(Call<SolicitudPendienteDTO> call, Throwable t) {
                if (isAdded()) {
                    layoutEsperandoRespuesta.setVisibility(View.GONE);
                    hayDeportesDisponibles = !noHayDeportesDisponibles;

                    // ✅ CARGAR PERFIL incluso si falla
                    cargarPerfilEntrenador();
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

        // TODO: Cuando implementes la obtención de datos de contacto del backend
        // correoEntrenador = perfil.getCorreo();
        // telefonoEntrenador = perfil.getTelefono();

        mostrarUISegunEstadoRelacion(perfil.getEstadoRelacion());
    }

    private void mostrarUISegunEstadoRelacion(EstadoRelacionDTO estado) {
        Log.d("DetallesEntrenador", "============================================");
        Log.d("DetallesEntrenador", "=== DEBUG mostrarUISegunEstadoRelacion ===");
        Log.d("DetallesEntrenador", "hayDeportesDisponibles: " + hayDeportesDisponibles);

        if (estado != null) {
            Log.d("DetallesEntrenador", "estado != null: TRUE");
            Log.d("DetallesEntrenador", "tieneRelacion: " + estado.getTieneRelacion());
            Log.d("DetallesEntrenador", "estadoRelacion: '" + estado.getEstadoRelacion() + "'");
            Log.d("DetallesEntrenador", "idDeporte: " + estado.getIdDeporte());
            Log.d("DetallesEntrenador", "nombreDeporte: " + estado.getNombreDeporte());
        } else {
            Log.d("DetallesEntrenador", "estado es NULL");
        }
        Log.d("DetallesEntrenador", "============================================");

        layoutSinRelacion.setVisibility(View.GONE);
        layoutPendiente.setVisibility(View.GONE);
        layoutActivo.setVisibility(View.GONE);
        layoutFinalizado.setVisibility(View.GONE);
        cardNoDisponible.setVisibility(View.GONE);

        // Ocultar botones de "solicitar más deportes" por defecto
        btnSolicitarMasDeportesPendiente.setVisibility(View.GONE);
        btnSolicitarMasDeportesActivo.setVisibility(View.GONE);
        btnSolicitarNuevamente.setVisibility(View.GONE);

        // ✅ PRIMERO: Verificar si TIENE relación (pendiente/activo/finalizado)
        boolean tieneRelacion = (estado != null && estado.getTieneRelacion());
        Log.d("DetallesEntrenador", "tieneRelacion calculado: " + tieneRelacion);

        // ❌ Si NO hay deportes disponibles
        if (!hayDeportesDisponibles) {
            Log.d("DetallesEntrenador", "Entrando a bloque: NO hay deportes disponibles");

            if (tieneRelacion) {
                String estadoRelacion = estado.getEstadoRelacion();
                Log.d("DetallesEntrenador", "Tiene relación con estado: '" + estadoRelacion + "'");

                if ("pendiente".equals(estadoRelacion)) {
                    Log.d("DetallesEntrenador", "CASO 5: Pendiente sin más deportes");
                    // Caso 5: Solicitud aceptada (pendiente) sin MÁS deportes disponibles
                    layoutPendiente.setVisibility(View.VISIBLE);
                    textPrecio.setVisibility(View.GONE);
                    textCosto.setVisibility(View.GONE);
                    btnSolicitarMasDeportesPendiente.setVisibility(View.GONE);

                } else if ("activo".equals(estadoRelacion)) {
                    Log.d("DetallesEntrenador", "CASO 7: Activo sin más deportes");
                    // Caso 7: Relación activa sin más deportes disponibles
                    layoutActivo.setVisibility(View.VISIBLE);
                    if (estado.getYaCalificado() != null && estado.getYaCalificado()) {
                        btnCalificarActivo.setVisibility(View.GONE);
                    }
                    textPrecio.setVisibility(View.GONE);
                    textCosto.setVisibility(View.GONE);
                    btnSolicitarMasDeportesActivo.setVisibility(View.GONE);

                } else if ("finalizado".equals(estadoRelacion)) {
                    Log.d("DetallesEntrenador", "CASO 9: Finalizado sin más deportes");
                    // Caso 9: Relación finalizada sin más deportes disponibles
                    layoutFinalizado.setVisibility(View.VISIBLE);
                    if (estado.getYaCalificado() != null && estado.getYaCalificado()) {
                        btnCalificarFinalizado.setVisibility(View.GONE);
                    }
                    textPrecio.setVisibility(View.GONE);
                    textCosto.setVisibility(View.GONE);
                    btnSolicitarNuevamente.setVisibility(View.GONE);
                }
            } else {
                Log.d("DetallesEntrenador", "CASO 1-2: No hay deportes Y no hay relación");
                // Caso 1 y 2: NO hay deportes Y NO hay relación
                if (layoutEsperandoRespuesta.getVisibility() != View.VISIBLE) {
                    cardNoDisponible.setVisibility(View.VISIBLE);
                }
            }
            return;
        }

        // ✅ SÍ hay deportes disponibles - Casos 3, 4, 6, 8, 10
        Log.d("DetallesEntrenador", "Entrando a bloque: SÍ hay deportes disponibles");

        if (!tieneRelacion) {
            Log.d("DetallesEntrenador", "CASO 3-4: Sin relación con deportes disponibles");
            // Caso 3 y 4: Sin relación con deportes disponibles
            layoutSinRelacion.setVisibility(View.VISIBLE);
            textPrecio.setVisibility(View.VISIBLE);
            textCosto.setVisibility(View.VISIBLE);

        } else {
            String estadoRelacion = estado.getEstadoRelacion();
            Log.d("DetallesEntrenador", "Tiene relación con estado: '" + estadoRelacion + "' y SÍ hay deportes disponibles");

            if ("pendiente".equals(estadoRelacion)) {
                Log.d("DetallesEntrenador", "CASO 6: Pendiente CON más deportes disponibles");
                // Caso 6a y 6b: Pendiente con MÁS deportes disponibles
                layoutPendiente.setVisibility(View.VISIBLE);
                btnSolicitarMasDeportesPendiente.setVisibility(View.VISIBLE);
                textPrecio.setVisibility(View.VISIBLE);
                textCosto.setVisibility(View.VISIBLE);

            } else if ("activo".equals(estadoRelacion)) {
                Log.d("DetallesEntrenador", "CASO 8: Activo CON más deportes disponibles");
                // Caso 8a y 8b: Activo con MÁS deportes disponibles
                layoutActivo.setVisibility(View.VISIBLE);
                if (estado.getYaCalificado() != null && estado.getYaCalificado()) {
                    btnCalificarActivo.setVisibility(View.GONE);
                }
                btnSolicitarMasDeportesActivo.setVisibility(View.VISIBLE);
                textPrecio.setVisibility(View.VISIBLE);
                textCosto.setVisibility(View.VISIBLE);

            } else if ("finalizado".equals(estadoRelacion)) {
                Log.d("DetallesEntrenador", "CASO 10: Finalizado CON deportes disponibles");
                // Caso 10: Finalizado con deportes disponibles
                layoutFinalizado.setVisibility(View.VISIBLE);
                if (estado.getYaCalificado() != null && estado.getYaCalificado()) {
                    btnCalificarFinalizado.setVisibility(View.GONE);
                }
                btnSolicitarNuevamente.setVisibility(View.VISIBLE);
                textPrecio.setVisibility(View.VISIBLE);
                textCosto.setVisibility(View.VISIBLE);
            }
        }

        Log.d("DetallesEntrenador", "=== FIN DEBUG ===");
    }

    private void enviarSolicitud() {
        Bundle bundle = new Bundle();
        bundle.putString("usuario", usuarioEntrenador);
        NavHostFragment.findNavController(this)
                .navigate(R.id.action_navigation_detallesEntrenador_to_enviarSolicitud, bundle);
    }

    /**
     * Abre la aplicación de correo para enviar un mensaje al entrenador
     */
    private void enviarCorreo() {
        // TODO: Cuando implementes la obtención del correo, usa: correoEntrenador
        if (correoEntrenador == null || correoEntrenador.isEmpty()) {
            Toast.makeText(getContext(),
                    "Correo del entrenador no disponible",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:" + correoEntrenador));
        intent.putExtra(Intent.EXTRA_SUBJECT, "Consulta sobre entrenamiento");

        try {
            startActivity(Intent.createChooser(intent, "Enviar correo"));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getContext(),
                    "No hay aplicaciones de correo instaladas",
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Abre WhatsApp para enviar un mensaje al entrenador
     */
    private void enviarWhatsapp() {
        // TODO: Cuando implementes la obtención del teléfono, usa: telefonoEntrenador
        if (telefonoEntrenador == null || telefonoEntrenador.isEmpty()) {
            Toast.makeText(getContext(),
                    "Número de WhatsApp del entrenador no disponible",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // Formato: 52 + 10 dígitos (ejemplo: 5215512345678)
        String numeroFormateado = telefonoEntrenador.replaceAll("[^0-9]", "");
        if (!numeroFormateado.startsWith("52")) {
            numeroFormateado = "52" + numeroFormateado;
        }

        String mensaje = "Hola, me interesa tu entrenamiento";
        String url = "https://wa.me/" + numeroFormateado + "?text=" + Uri.encode(mensaje);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));

        try {
            startActivity(intent);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getContext(),
                    "WhatsApp no está instalado",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void abrirDialogCalificacion() {
        Bundle bundle = new Bundle();
        bundle.putString("usuario", usuarioEntrenador);
        NavHostFragment.findNavController(this)
                .navigate(R.id.action_navigation_detallesEntrenador_to_calificarEntrenador, bundle);
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