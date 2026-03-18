package com.example.sportine.ui.usuarios.detallesEntrenador;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import androidx.browser.customtabs.CustomTabsIntent;
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
import com.example.sportine.models.payment.PaymentApiModels;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetallesEntrenadorFragment extends Fragment {

    private static final String TAG = "DetallesEntrenador";
    private static final int MAX_RESENAS_VISIBLES = 3;

    private ApiService apiService;
    private String usuarioEntrenador;
    private String usuarioAlumno; // ← se lee de SharedPreferences
    private List<ResenaDTO> todasLasResenas;
    private boolean mostrandoTodas = false;
    private boolean hayDeportesDisponibles = true;

    // Guardamos el idDeporte de la relación activa para el pago
    private Integer idDeporteRelacion = null;

    // ── Views principales ──────────────────────────────────────────────────────
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
    private RecyclerView recyclerRelaciones;
    private RelacionDeporteAdapter relacionesAdapter;

    // ── RecyclerViews y Adapters ───────────────────────────────────────────────
    private RecyclerView recyclerDeportes;
    private RecyclerView recyclerResenas;
    private DeportesAdapter deportesAdapter;
    private ResenasAdapter resenasAdapter;

    // ── Layouts de estado de relación ─────────────────────────────────────────
    private MaterialCardView cardNoDisponible;
    private LinearLayout layoutSinRelacion;
    private LinearLayout layoutPendiente;
    private LinearLayout layoutActivo;
    private LinearLayout layoutFinalizado;
    private LinearLayout layoutEsperandoRespuesta;
    private RecyclerView recyclerSolicitudesPendientes;
    private SolicitudesPendientesAdapter solicitudesPendientesAdapter;

    // ── Botones por estado ────────────────────────────────────────────────────
    private MaterialButton btnEnviarSolicitud;
    private MaterialButton btnPagar;           // ← botón de pago (layout_pendiente)
    private MaterialButton btnCalificarActivo;
    private MaterialButton btnSolicitarNuevamente;
    private MaterialButton btnCalificarFinalizado;

    // ── Estado de pago en curso ───────────────────────────────────────────────
    private String pendingOrderId = null;  // guardamos el order_id mientras el usuario está en PayPal

    // ─────────────────────────────────────────────────────────────────────────
    // Lifecycle
    // ─────────────────────────────────────────────────────────────────────────

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

        // Leer usuario logueado
        SharedPreferences prefs = requireContext()
                .getSharedPreferences("SportinePrefs", Context.MODE_PRIVATE);
        usuarioAlumno = prefs.getString("USER_USERNAME", "");

        apiService = RetrofitClient.getClient(requireContext()).create(ApiService.class);

        initViews(view);
        setupRecyclerViews();
        setupListeners();
        verificarDeportesDisponibles();

        return view;
    }

    /**
     * onResume se llama cuando el usuario regresa desde Chrome Custom Tab.
     * Aquí capturamos el deep link si PayPal redirigió a sportine://payment/success
     */
    @Override
    public void onResume() {
        super.onResume();

        SharedPreferences prefs = requireContext()
                .getSharedPreferences("SportinePrefs", Context.MODE_PRIVATE);

        boolean paymentSuccess = prefs.getBoolean("payment_success", false);
        boolean paymentCancelled = prefs.getBoolean("payment_cancelled", false);
        long timestamp = prefs.getLong("payment_timestamp", 0);
        boolean isRecent = (System.currentTimeMillis() - timestamp) < 120000;

        Log.d(TAG, "=== onResume === payment_success: " + paymentSuccess
                + ", isRecent: " + isRecent
                + ", token: " + prefs.getString("payment_token", "null"));

        if (paymentSuccess && isRecent) {
            String token = prefs.getString("payment_token", null);
            Log.d(TAG, "✅ Deep link de pago exitoso detectado. Token: " + token);
            prefs.edit()
                    .remove("payment_success")
                    .remove("payment_token")
                    .remove("payment_payer_id")
                    .remove("payment_timestamp")
                    .apply();
            confirmarPago(token);

        } else if (paymentCancelled && isRecent) {
            Log.d(TAG, "Pago cancelado por el usuario");
            prefs.edit()
                    .remove("payment_cancelled")
                    .remove("payment_timestamp")
                    .apply();
            Toast.makeText(getContext(), "Pago cancelado", Toast.LENGTH_SHORT).show();
            pendingOrderId = null;
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Init
    // ─────────────────────────────────────────────────────────────────────────

    private void initViews(View view) {
        btnBack              = view.findViewById(R.id.btn_back);
        imagePerfil          = view.findViewById(R.id.image_perfil);
        textNombre           = view.findViewById(R.id.text_nombre);
        ratingEntrenador     = view.findViewById(R.id.rating_entrenador);
        textRating           = view.findViewById(R.id.text_rating);
        textNumResenas       = view.findViewById(R.id.text_num_resenas);
        textUbicacion        = view.findViewById(R.id.text_ubicacion);
        textAcerca           = view.findViewById(R.id.text_acerca);
        textPrecio           = view.findViewById(R.id.text_precio);
        textCosto            = view.findViewById(R.id.texto_costo);
        btnVerTodas          = view.findViewById(R.id.btn_ver_todas);
        textSinResenas       = view.findViewById(R.id.text_sin_resenas);
        recyclerDeportes     = view.findViewById(R.id.recycler_deportes);
        recyclerResenas      = view.findViewById(R.id.recycler_resenas);
        recyclerRelaciones = view.findViewById(R.id.recycler_relaciones);
        btnSolicitarMasDeportesPendiente = view.findViewById(R.id.btn_solicitar_mas_deportes_pendiente);
        btnSolicitarMasDeportesActivo    = view.findViewById(R.id.btn_solicitar_mas_deportes_activo);

        cardNoDisponible          = view.findViewById(R.id.card_no_disponible);
        layoutSinRelacion         = view.findViewById(R.id.layout_sin_relacion);
        layoutActivo              = view.findViewById(R.id.layout_activo);
        layoutFinalizado          = view.findViewById(R.id.layout_finalizado);
        layoutEsperandoRespuesta  = view.findViewById(R.id.layout_esperando_respuesta);

        recyclerSolicitudesPendientes = view.findViewById(R.id.recycler_solicitudes_pendientes);

        btnEnviarSolicitud   = view.findViewById(R.id.btn_enviar_solicitud);
        btnPagar             = view.findViewById(R.id.btn_pagar);
        btnCalificarActivo   = view.findViewById(R.id.btn_calificar_activo);
        btnSolicitarNuevamente  = view.findViewById(R.id.btn_solicitar_nuevamente);
        btnCalificarFinalizado  = view.findViewById(R.id.btn_calificar_finalizado);
    }

    private void setupRecyclerViews() {
        recyclerDeportes.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        deportesAdapter = new DeportesAdapter();
        recyclerDeportes.setAdapter(deportesAdapter);

        recyclerResenas.setLayoutManager(new LinearLayoutManager(getContext()));
        resenasAdapter = new ResenasAdapter();
        recyclerResenas.setAdapter(resenasAdapter);

        recyclerSolicitudesPendientes.setLayoutManager(new LinearLayoutManager(getContext()));
        solicitudesPendientesAdapter = new SolicitudesPendientesAdapter();
        recyclerSolicitudesPendientes.setAdapter(solicitudesPendientesAdapter);
        recyclerRelaciones.setLayoutManager(new LinearLayoutManager(getContext()));
        relacionesAdapter = new RelacionDeporteAdapter(new RelacionDeporteAdapter.OnRelacionActionListener() {
            @Override
            public void onPagar(EstadoRelacionDTO.RelacionDeporteDTO relacion) {
                idDeporteRelacion = relacion.getIdDeporte();
                iniciarPago();
            }

            @Override
            public void onCancelar(EstadoRelacionDTO.RelacionDeporteDTO relacion) {
                mostrarDialogoCancelar(relacion);
            }
        });
        recyclerRelaciones.setAdapter(relacionesAdapter);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> NavHostFragment.findNavController(this).navigateUp());
        btnVerTodas.setOnClickListener(v -> toggleResenas());

        btnEnviarSolicitud.setOnClickListener(v -> enviarSolicitud());
        btnPagar.setOnClickListener(v -> iniciarPago());          // ← PAGO
        btnCalificarActivo.setOnClickListener(v -> abrirDialogCalificacion());
        btnSolicitarNuevamente.setOnClickListener(v -> enviarSolicitud());
        btnCalificarFinalizado.setOnClickListener(v -> abrirDialogCalificacion());
        btnSolicitarMasDeportesPendiente.setOnClickListener(v -> enviarSolicitud());
        btnSolicitarMasDeportesActivo.setOnClickListener(v -> enviarSolicitud());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Carga de datos (igual que antes)
    // ─────────────────────────────────────────────────────────────────────────

    private void verificarDeportesDisponibles() {
        if (!isAdded()) return;

        apiService.obtenerFormularioSolicitud(usuarioEntrenador).enqueue(new Callback<FormularioSolicitudDTO>() {
            @Override
            public void onResponse(Call<FormularioSolicitudDTO> call,
                                   Response<FormularioSolicitudDTO> response) {
                if (!isAdded()) return;

                if (response.isSuccessful() && response.body() != null) {
                    FormularioSolicitudDTO formulario = response.body();
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

                        layoutEsperandoRespuesta.setVisibility(View.VISIBLE);
                        solicitudesPendientesAdapter.setSolicitudes(solicitud.getSolicitudes());

                        if (noHayDeportesDisponibles) {
                            hayDeportesDisponibles = false;
                            textPrecio.setVisibility(View.GONE);
                            textCosto.setVisibility(View.GONE);
                        } else {
                            hayDeportesDisponibles = true;
                            textPrecio.setVisibility(View.VISIBLE);
                            textCosto.setVisibility(View.VISIBLE);
                        }
                    } else {
                        layoutEsperandoRespuesta.setVisibility(View.GONE);
                        hayDeportesDisponibles = !noHayDeportesDisponibles;
                        if (noHayDeportesDisponibles) {
                            textPrecio.setVisibility(View.GONE);
                            textCosto.setVisibility(View.GONE);
                        }
                    }
                }

                cargarPerfilEntrenador();
            }

            @Override
            public void onFailure(Call<SolicitudPendienteDTO> call, Throwable t) {
                if (isAdded()) {
                    layoutEsperandoRespuesta.setVisibility(View.GONE);
                    hayDeportesDisponibles = !noHayDeportesDisponibles;
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

    // ─────────────────────────────────────────────────────────────────────────
    // UI
    // ─────────────────────────────────────────────────────────────────────────

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

        mostrarUISegunEstadoRelacion(perfil.getEstadoRelacion());
    }

    private void mostrarUISegunEstadoRelacion(EstadoRelacionDTO estado) {
        Log.d(TAG, "mostrarUISegunEstadoRelacion - hayDeportesDisponibles: " + hayDeportesDisponibles);

        // Ocultar todo por defecto
        layoutSinRelacion.setVisibility(View.GONE);
        layoutActivo.setVisibility(View.GONE);
        layoutFinalizado.setVisibility(View.GONE);
        cardNoDisponible.setVisibility(View.GONE);
        btnSolicitarMasDeportesActivo.setVisibility(View.GONE);
        btnSolicitarNuevamente.setVisibility(View.GONE);
        recyclerRelaciones.setVisibility(View.GONE);

        boolean tieneRelacion = (estado != null && estado.getTieneRelacion());

        // Guardar idDeporte principal para compatibilidad
        if (tieneRelacion && estado.getIdDeporte() != null) {
            idDeporteRelacion = estado.getIdDeporte();
            Log.d(TAG, "idDeporteRelacion guardado: " + idDeporteRelacion);
        }

        // Alimentar RecyclerView si hay relaciones activas o pendientes
        if (tieneRelacion && estado.getRelaciones() != null && !estado.getRelaciones().isEmpty()) {
            recyclerRelaciones.setVisibility(View.VISIBLE);
            relacionesAdapter.setRelaciones(estado.getRelaciones());
        }

        if (!hayDeportesDisponibles) {
            if (tieneRelacion) {
                String estadoRelacion = estado.getEstadoRelacion();

                if ("pendiente".equals(estadoRelacion)) {
                    // Solo el recycler, sin layout extra
                    textPrecio.setVisibility(View.GONE);
                    textCosto.setVisibility(View.GONE);

                } else if ("activo".equals(estadoRelacion)) {
                    layoutActivo.setVisibility(View.VISIBLE);
                    if (estado.getYaCalificado() != null && estado.getYaCalificado()) {
                        btnCalificarActivo.setVisibility(View.GONE);
                    }
                    textPrecio.setVisibility(View.GONE);
                    textCosto.setVisibility(View.GONE);

                } else if ("finalizado".equals(estadoRelacion)) {
                    layoutFinalizado.setVisibility(View.VISIBLE);
                    if (estado.getYaCalificado() != null && estado.getYaCalificado()) {
                        btnCalificarFinalizado.setVisibility(View.GONE);
                    }
                    textPrecio.setVisibility(View.GONE);
                    textCosto.setVisibility(View.GONE);
                }
            } else {
                if (layoutEsperandoRespuesta.getVisibility() != View.VISIBLE) {
                    cardNoDisponible.setVisibility(View.VISIBLE);
                }
            }
            return;
        }

        // ── SÍ hay deportes disponibles ──────────────────────────────────────
        if (!tieneRelacion) {
            layoutSinRelacion.setVisibility(View.VISIBLE);
            textPrecio.setVisibility(View.VISIBLE);
            textCosto.setVisibility(View.VISIBLE);

        } else {
            String estadoRelacion = estado.getEstadoRelacion();

            if ("pendiente".equals(estadoRelacion)) {
                // Solo recycler + botón solicitar otro deporte
                btnSolicitarMasDeportesPendiente.setVisibility(View.VISIBLE);
                textPrecio.setVisibility(View.VISIBLE);
                textCosto.setVisibility(View.VISIBLE);

            } else if ("activo".equals(estadoRelacion)) {
                layoutActivo.setVisibility(View.VISIBLE);
                if (estado.getYaCalificado() != null && estado.getYaCalificado()) {
                    btnCalificarActivo.setVisibility(View.GONE);
                }
                btnSolicitarMasDeportesActivo.setVisibility(View.VISIBLE);
                textPrecio.setVisibility(View.VISIBLE);
                textCosto.setVisibility(View.VISIBLE);

            } else if ("finalizado".equals(estadoRelacion)) {
                layoutFinalizado.setVisibility(View.VISIBLE);
                if (estado.getYaCalificado() != null && estado.getYaCalificado()) {
                    btnCalificarFinalizado.setVisibility(View.GONE);
                }
                btnSolicitarNuevamente.setVisibility(View.VISIBLE);
                textPrecio.setVisibility(View.VISIBLE);
                textCosto.setVisibility(View.VISIBLE);
            }
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // FLUJO DE PAGO
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Paso 1 – El alumno toca "Pagar".
     * Verificamos que el entrenador puede recibir pagos y luego creamos la orden.
     */
    private void iniciarPago() {
        if (usuarioAlumno == null || usuarioAlumno.isEmpty()) {
            Toast.makeText(getContext(), "Error: sesión no válida", Toast.LENGTH_SHORT).show();
            return;
        }

        if (idDeporteRelacion == null) {
            Toast.makeText(getContext(),
                    "Error: no se pudo determinar el deporte. Recarga la pantalla.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // Ya no tocamos btnPagar — el botón está en cada tarjeta del RecyclerView
        apiService.verificarEntrenadorPuedeRecibirPagos(usuarioEntrenador)
                .enqueue(new Callback<PaymentApiModels.PuedeRecibirPagosResponse>() {
                    @Override
                    public void onResponse(
                            Call<PaymentApiModels.PuedeRecibirPagosResponse> call,
                            Response<PaymentApiModels.PuedeRecibirPagosResponse> response) {

                        if (!isAdded()) return;

                        if (response.isSuccessful() && response.body() != null
                                && response.body().isPuedeRecibirPagos()) {
                            crearOrdenDePago();
                        } else {
                            new MaterialAlertDialogBuilder(requireContext())
                                    .setTitle("Entrenador no disponible")
                                    .setMessage("Este entrenador aún no ha configurado su cuenta de pagos en PayPal.")
                                    .setPositiveButton("Entendido", null)
                                    .show();
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<PaymentApiModels.PuedeRecibirPagosResponse> call, Throwable t) {
                        if (!isAdded()) return;
                        Toast.makeText(getContext(),
                                "Error de conexión: " + t.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Paso 2 – Crear la orden en el backend y abrir PayPal en Chrome Custom Tab.
     */
    private void crearOrdenDePago() {
        if (!isAdded()) return;

        btnPagar.setText("Creando orden...");

        apiService.crearSuscripcion(usuarioAlumno, usuarioEntrenador, idDeporteRelacion)
                .enqueue(new Callback<PaymentApiModels.CrearSuscripcionResponse>() {
                    @Override
                    public void onResponse(
                            Call<PaymentApiModels.CrearSuscripcionResponse> call,
                            Response<PaymentApiModels.CrearSuscripcionResponse> response) {

                        if (!isAdded()) return;

                        btnPagar.setEnabled(true);
                        btnPagar.setText("Pagar");

                        if (response.isSuccessful() && response.body() != null
                                && response.body().isSuccess()) {

                            String approvalUrl = response.body().getApprovalUrl();
                            pendingOrderId = response.body().getOrderId();

                            Log.d(TAG, "Orden creada: " + pendingOrderId);
                            Log.d(TAG, "Approval URL: " + approvalUrl);

                            // Abrir PayPal en Chrome Custom Tab
                            abrirPayPalEnChromeTabs(approvalUrl);

                        } else {
                            String msg = (response.body() != null && response.body().getMessage() != null)
                                    ? response.body().getMessage()
                                    : "Error al crear la orden de pago (código " + response.code() + ")";
                            Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<PaymentApiModels.CrearSuscripcionResponse> call, Throwable t) {
                        if (!isAdded()) return;
                        btnPagar.setEnabled(true);
                        btnPagar.setText("Pagar");
                        Toast.makeText(getContext(),
                                "Error de conexión: " + t.getMessage(),
                                Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Error creando orden", t);
                    }
                });
    }

    /**
     * Paso 3 – Abrir la approval_url de PayPal en un Chrome Custom Tab.
     * El usuario verá la UI oficial de PayPal sin salir completamente de la app.
     */
    private void abrirPayPalEnChromeTabs(String approvalUrl) {
        try {
            CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder()
                    .setShowTitle(true)
                    .build();

            customTabsIntent.launchUrl(requireContext(), Uri.parse(approvalUrl));

        } catch (Exception e) {
            Log.e(TAG, "No se pudo abrir Chrome Custom Tab, intentando navegador normal", e);
            // Fallback: abrir en el navegador predeterminado
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(approvalUrl));
            if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
                startActivity(intent);
            } else {
                Toast.makeText(getContext(),
                        "No se encontró un navegador para completar el pago",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Paso 4 – Confirmar el pago después de que PayPal redirige de vuelta.
     * Se llama desde onResume() cuando detectamos el deep link sportine://payment/success
     *
     * @param token token devuelto por PayPal (equivale al order_id en muchos casos)
     */
    private void confirmarPago(String token) {
        if (!isAdded()) return;

        // Usamos el token recibido del deep link (PayPal lo llama "token" pero es el order ID)
        String orderId = (token != null) ? token : pendingOrderId;

        if (orderId == null) {
            Toast.makeText(getContext(),
                    "Error: no se pudo identificar la orden de pago",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "Confirmando pago con orderId: " + orderId);

        // Mostrar loading
        btnPagar.setEnabled(false);
        btnPagar.setText("Confirmando pago...");

        // vaultId es null en el primer pago (no usamos Vault todavía en el flujo manual)
        apiService.confirmarSuscripcion(orderId, null)
                .enqueue(new Callback<PaymentApiModels.ConfirmarSuscripcionResponse>() {
                    @Override
                    public void onResponse(
                            Call<PaymentApiModels.ConfirmarSuscripcionResponse> call,
                            Response<PaymentApiModels.ConfirmarSuscripcionResponse> response) {

                        if (!isAdded()) return;

                        btnPagar.setEnabled(true);
                        btnPagar.setText("Pagar");
                        pendingOrderId = null;

                        if (response.isSuccessful() && response.body() != null
                                && response.body().isSuccess()) {

                            Log.d(TAG, "✅ Pago confirmado exitosamente");

                            new MaterialAlertDialogBuilder(requireContext())
                                    .setTitle("¡Pago exitoso! 🎉")
                                    .setMessage("Tu suscripción ha sido activada. " +
                                            "Ya puedes comenzar a entrenar con " + usuarioEntrenador + ".")
                                    .setPositiveButton("¡Genial!", (dialog, which) -> {
                                        // Regresar atrás o recargar la pantalla
                                        NavHostFragment.findNavController(
                                                DetallesEntrenadorFragment.this).navigateUp();
                                    })
                                    .setCancelable(false)
                                    .show();

                        } else {
                            String msg = (response.body() != null && response.body().getMessage() != null)
                                    ? response.body().getMessage()
                                    : "No se pudo confirmar el pago (código " + response.code() + ")";

                            Log.e(TAG, "Error confirmando pago: " + msg);

                            new MaterialAlertDialogBuilder(requireContext())
                                    .setTitle("Error al confirmar")
                                    .setMessage(msg + "\n\nSi el cargo fue aplicado, contacta a soporte.")
                                    .setPositiveButton("Entendido", null)
                                    .show();
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<PaymentApiModels.ConfirmarSuscripcionResponse> call, Throwable t) {
                        if (!isAdded()) return;
                        btnPagar.setEnabled(true);
                        btnPagar.setText("Pagar");
                        pendingOrderId = null;
                        Log.e(TAG, "Error de red confirmando pago", t);
                        Toast.makeText(getContext(),
                                "Error de conexión al confirmar pago: " + t.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Helpers existentes (sin cambios)
    // ─────────────────────────────────────────────────────────────────────────

    private void enviarSolicitud() {
        Bundle bundle = new Bundle();
        bundle.putString("usuario", usuarioEntrenador);
        NavHostFragment.findNavController(this)
                .navigate(R.id.action_navigation_detallesEntrenador_to_enviarSolicitud, bundle);
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
        if (todasLasResenas == null || todasLasResenas.size() <= MAX_RESENAS_VISIBLES) return;

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

    private void mostrarDialogoCancelar(EstadoRelacionDTO.RelacionDeporteDTO relacion) {
        boolean esPendiente = "pendiente".equals(relacion.getStatusRelacion());
        String mensaje = esPendiente
                ? "¿Seguro que quieres cancelar? La solicitud será rechazada."
                : "¿Seguro que quieres cancelar? Seguirás teniendo acceso hasta " +
                relacion.getFinMensualidad() + ".";

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Cancelar " + relacion.getNombreDeporte())
                .setMessage(mensaje)
                .setPositiveButton("Sí, cancelar", (dialog, which) ->
                        cancelarSuscripcion(relacion))
                .setNegativeButton("No", null)
                .show();
    }

    private void cancelarSuscripcion(EstadoRelacionDTO.RelacionDeporteDTO relacion) {
        apiService.cancelarSuscripcionPorUsuario(
                        usuarioAlumno, usuarioEntrenador,
                        relacion.getIdDeporte(), "Cancelada por el alumno")
                .enqueue(new Callback<java.util.Map<String, Object>>() {
                    @Override
                    public void onResponse(Call<java.util.Map<String, Object>> call,
                                           Response<java.util.Map<String, Object>> response) {
                        if (!isAdded()) return;
                        if (response.isSuccessful() && response.body() != null) {
                            Boolean success = (Boolean) response.body().get("success");
                            String msg = (String) response.body().get("message");
                            if (Boolean.TRUE.equals(success)) {
                                Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
                                // Recargar pantalla
                                verificarDeportesDisponibles();
                            } else {
                                Toast.makeText(getContext(),
                                        msg != null ? msg : "Error al cancelar",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<java.util.Map<String, Object>> call, Throwable t) {
                        if (!isAdded()) return;
                        Toast.makeText(getContext(),
                                "Error de conexión: " + t.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}