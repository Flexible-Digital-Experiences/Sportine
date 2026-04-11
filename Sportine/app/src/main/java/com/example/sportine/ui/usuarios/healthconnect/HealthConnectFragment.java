package com.example.sportine.ui.usuarios.healthconnect;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.health.connect.client.PermissionController;

import com.example.sportine.R;
import com.example.sportine.data.ApiService;
import com.example.sportine.data.RetrofitClient;
import com.example.sportine.data.HealthConnectManager;
import com.example.sportine.models.healthconnect.ProgresoHealthConnectRequest;
import com.example.sportine.models.healthconnect.HcSesionEjercicio;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HealthConnectFragment extends Fragment {

    private static final String TAG = "HealthConnectFragment";
    private static final String ARG_ID_ENTRENAMIENTO = "id_entrenamiento";
    private static final String ARG_NOMBRE_ENTRENAMIENTO = "nombre_entrenamiento";
    private static final String PREFS_NAME = "sportine_prefs";
    private static final String KEY_HC_CONECTADO = "hc_conectado";

    private int idEntrenamiento;
    private String nombreEntrenamiento;

    private HealthConnectManager hcManager;
    private ApiService apiService;

    private ActivityResultLauncher<Set<String>> permisosLauncher;

    private List<HcSesionEjercicio> sesionesEncontradas = new ArrayList<>();
    private HcSesionEjercicio sesionSeleccionada = null;

    // ── Views ─────────────────────────────────────────────────────────────────
    private ProgressBar progressBar;
    private LinearLayout layoutContenido;
    private MaterialCardView cardNoDisponible;
    private Button btnInstalarHc;
    private MaterialCardView cardSinPermisos;
    private MaterialButton btnSolicitarPermisos;
    private MaterialCardView cardBuscando;
    private MaterialCardView cardSinSesiones;
    private MaterialButton btnCompletarSinHc;
    private MaterialCardView cardUnaSesion;
    private TextView tvUnaSesionDetalle;
    private MaterialButton btnConfirmarSesion;
    private MaterialButton btnNoVincularUna;
    private MaterialCardView cardMultiplesSesiones;
    private Spinner spinnerSesiones;
    private MaterialButton btnVincularSeleccionada;
    private MaterialButton btnNoVincularMulti;

    public static HealthConnectFragment newInstance(int idEntrenamiento, String nombre) {
        HealthConnectFragment f = new HealthConnectFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_ID_ENTRENAMIENTO, idEntrenamiento);
        args.putString(ARG_NOMBRE_ENTRENAMIENTO, nombre);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            idEntrenamiento = getArguments().getInt(ARG_ID_ENTRENAMIENTO);
            nombreEntrenamiento = getArguments().getString(ARG_NOMBRE_ENTRENAMIENTO, "");
        }

        hcManager = new HealthConnectManager(requireContext());
        apiService = RetrofitClient.getClient(requireContext()).create(ApiService.class);

        permisosLauncher = registerForActivityResult(
                PermissionController.createRequestPermissionResultContract(),
                otorgados -> {
                    Log.d(TAG, "Permisos HC otorgados: " + otorgados.size()
                            + " de " + HealthConnectManager.PERMISOS_REQUERIDOS.size());
                    if (otorgados.containsAll(HealthConnectManager.PERMISOS_REQUERIDOS)) {
                        // ✅ Guardar que HC quedó conectado
                        guardarHcConectado();
                        registrarConexionYBuscarSesiones();
                    } else {
                        mostrarEstado(Estado.SIN_PERMISOS);
                        Toast.makeText(getContext(),
                                "Se necesitan todos los permisos para sincronizar",
                                Toast.LENGTH_LONG).show();
                    }
                }
        );
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_health_connect, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        inicializarVistas(view);
        verificarDisponibilidadYPermisos();
    }

    private void inicializarVistas(View view) {
        progressBar         = view.findViewById(R.id.progress_bar_hc);
        layoutContenido     = view.findViewById(R.id.layout_contenido_hc);
        cardNoDisponible    = view.findViewById(R.id.card_hc_no_disponible);
        btnInstalarHc       = view.findViewById(R.id.btn_instalar_hc);
        cardSinPermisos     = view.findViewById(R.id.card_hc_sin_permisos);
        btnSolicitarPermisos= view.findViewById(R.id.btn_solicitar_permisos_hc);
        cardBuscando        = view.findViewById(R.id.card_hc_buscando);
        cardSinSesiones     = view.findViewById(R.id.card_hc_sin_sesiones);
        btnCompletarSinHc   = view.findViewById(R.id.btn_completar_sin_hc);
        cardUnaSesion       = view.findViewById(R.id.card_hc_una_sesion);
        tvUnaSesionDetalle  = view.findViewById(R.id.tv_una_sesion_detalle);
        btnConfirmarSesion  = view.findViewById(R.id.btn_confirmar_sesion_hc);
        btnNoVincularUna    = view.findViewById(R.id.btn_no_vincular_una);
        cardMultiplesSesiones = view.findViewById(R.id.card_hc_multiples_sesiones);
        spinnerSesiones     = view.findViewById(R.id.spinner_sesiones_hc);
        btnVincularSeleccionada = view.findViewById(R.id.btn_vincular_seleccionada_hc);
        btnNoVincularMulti  = view.findViewById(R.id.btn_no_vincular_multi);

        btnInstalarHc.setOnClickListener(v -> abrirPlayStoreHC());
        btnSolicitarPermisos.setOnClickListener(v -> solicitarPermisos());
        btnCompletarSinHc.setOnClickListener(v -> completarSinHealthConnect());
        btnConfirmarSesion.setOnClickListener(v -> {
            if (sesionSeleccionada != null) sincronizarSesion(sesionSeleccionada);
        });
        btnNoVincularUna.setOnClickListener(v -> completarSinHealthConnect());
        btnVincularSeleccionada.setOnClickListener(v -> {
            int pos = spinnerSesiones.getSelectedItemPosition();
            if (pos >= 0 && pos < sesionesEncontradas.size()) {
                sincronizarSesion(sesionesEncontradas.get(pos));
            }
        });
        btnNoVincularMulti.setOnClickListener(v -> completarSinHealthConnect());
    }

    // ── Flujo principal ───────────────────────────────────────────────────────

    private void verificarDisponibilidadYPermisos() {
        mostrarCargando(true);

        if (!hcManager.isDisponible()) {
            mostrarEstado(hcManager.necesitaActualizacion()
                    ? Estado.NO_DISPONIBLE_ACTUALIZAR : Estado.NO_DISPONIBLE);
            return;
        }

        hcManager.tieneTodosLosPermisos(new HealthConnectManager.Callback<Boolean>() {
            @Override
            public void onSuccess(Boolean tienePermisos) {
                if (tienePermisos) {
                    // ✅ Tiene permisos → guardar y buscar sesiones
                    guardarHcConectado();
                    registrarConexionYBuscarSesiones();
                } else {
                    mostrarEstado(Estado.SIN_PERMISOS);
                }
            }
            @Override
            public void onError(Exception e) {
                Log.e(TAG, "Error verificando permisos", e);
                mostrarEstado(Estado.SIN_PERMISOS);
            }
        });
    }

    private void solicitarPermisos() {
        permisosLauncher.launch(HealthConnectManager.PERMISOS_REQUERIDOS);
    }

    private void registrarConexionYBuscarSesiones() {
        apiService.registrarConexionHealthConnect()
                .enqueue(new Callback<Map<String, Object>>() {
                    @Override
                    public void onResponse(Call<Map<String, Object>> call,
                                           Response<Map<String, Object>> response) {
                        Log.d(TAG, "Conexión HC registrada: " + response.code());
                    }
                    @Override
                    public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                        Log.w(TAG, "No se pudo registrar conexión HC: " + t.getMessage());
                    }
                });
        buscarSesionesDelDia();
    }

    private void buscarSesionesDelDia() {
        mostrarEstado(Estado.BUSCANDO);
        hcManager.leerSesionesDeHoy(new HealthConnectManager.Callback<List<HcSesionEjercicio>>() {
            @Override
            public void onSuccess(List<HcSesionEjercicio> sesiones) {
                sesionesEncontradas = sesiones;
                Log.d(TAG, "Sesiones HC encontradas hoy: " + sesiones.size());
                if (sesiones.isEmpty()) {
                    mostrarEstado(Estado.SIN_SESIONES);
                } else if (sesiones.size() == 1) {
                    sesionSeleccionada = sesiones.get(0);
                    mostrarEstado(Estado.UNA_SESION);
                } else {
                    mostrarEstado(Estado.MULTIPLES_SESIONES);
                }
            }
            @Override
            public void onError(Exception e) {
                Log.e(TAG, "Error leyendo sesiones HC", e);
                Toast.makeText(getContext(),
                        "Error al leer Health Connect: " + e.getMessage(),
                        Toast.LENGTH_LONG).show();
                mostrarEstado(Estado.SIN_SESIONES);
            }
        });
    }

    private void sincronizarSesion(HcSesionEjercicio sesion) {
        mostrarCargando(true);
        ProgresoHealthConnectRequest request =
                ProgresoHealthConnectRequest.desde(sesion, idEntrenamiento);
        apiService.sincronizarHealthConnect(request)
                .enqueue(new Callback<Map<String, Object>>() {
                    @Override
                    public void onResponse(Call<Map<String, Object>> call,
                                           Response<Map<String, Object>> response) {
                        mostrarCargando(false);
                        if (response.isSuccessful()) {
                            Log.d(TAG, "✅ Sesión HC sincronizada");
                            mostrarExito(sesion);
                        } else {
                            Toast.makeText(getContext(),
                                    "Error al guardar (" + response.code() + ")",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                        mostrarCargando(false);
                        Toast.makeText(getContext(),
                                "Error de conexión: " + t.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void completarSinHealthConnect() {
        if (getActivity() != null) getActivity().onBackPressed();
    }

    // ── SharedPreferences ─────────────────────────────────────────────────────

    /**
     * Guarda que el usuario ya conectó Health Connect.
     * A partir de este momento, DetallesEntrenamientoFragment
     * irá directo a HC sin preguntar.
     */
    private void guardarHcConectado() {
        if (getContext() == null) return;
        getContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit()
                .putBoolean(KEY_HC_CONECTADO, true)
                .apply();
        Log.d(TAG, "✅ hc_conectado guardado en SharedPreferences");
    }

    // ── UI ────────────────────────────────────────────────────────────────────

    private enum Estado {
        NO_DISPONIBLE, NO_DISPONIBLE_ACTUALIZAR,
        SIN_PERMISOS, BUSCANDO, SIN_SESIONES, UNA_SESION, MULTIPLES_SESIONES
    }

    private void mostrarEstado(Estado estado) {
        if (!isAdded()) return;
        requireActivity().runOnUiThread(() -> {
            mostrarCargando(false);
            ocultarTodo();
            switch (estado) {
                case NO_DISPONIBLE:
                case NO_DISPONIBLE_ACTUALIZAR:
                    cardNoDisponible.setVisibility(View.VISIBLE);
                    break;
                case SIN_PERMISOS:
                    cardSinPermisos.setVisibility(View.VISIBLE);
                    break;
                case BUSCANDO:
                    cardBuscando.setVisibility(View.VISIBLE);
                    break;
                case SIN_SESIONES:
                    cardSinSesiones.setVisibility(View.VISIBLE);
                    break;
                case UNA_SESION:
                    if (sesionSeleccionada != null) {
                        tvUnaSesionDetalle.setText(sesionSeleccionada.getTextoSpinner());

                        TextView tvCalorias = requireView().findViewById(R.id.tv_preview_calorias);
                        TextView tvDistancia = requireView().findViewById(R.id.tv_preview_distancia);

                        if (tvCalorias != null)
                            tvCalorias.setText(sesionSeleccionada.getCaloriasKcal() != null
                                    ? String.valueOf(sesionSeleccionada.getCaloriasKcal()) : "--");
                        if (tvDistancia != null)
                            tvDistancia.setText(sesionSeleccionada.getDistanciaMetros() != null
                                    ? String.format("%.1f km", sesionSeleccionada.getDistanciaMetros() / 1000) : "--");
                    }
                    cardUnaSesion.setVisibility(View.VISIBLE);
                    break;
                case MULTIPLES_SESIONES:
                    configurarSpinner();
                    cardMultiplesSesiones.setVisibility(View.VISIBLE);
                    break;
            }
        });
    }

    private void configurarSpinner() {
        List<String> etiquetas = new ArrayList<>();
        for (HcSesionEjercicio s : sesionesEncontradas) etiquetas.add(s.getTextoSpinner());
        spinnerSesiones.setAdapter(new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                etiquetas));
    }

    private void mostrarExito(HcSesionEjercicio sesion) {
        if (!isAdded()) return;
        new AlertDialog.Builder(requireContext())
                .setTitle("¡Datos sincronizados! 🎉")
                .setMessage("Se guardaron tus métricas de " + sesion.getEtiquetaTipo()
                        + " (" + sesion.getDuracionActivaMin() + " min"
                        + (sesion.getCaloriasKcal() != null
                        ? ", " + sesion.getCaloriasKcal() + " kcal" : "")
                        + ") en tu entrenamiento.")
                .setPositiveButton("¡Listo!", (d, w) -> {
                    if (getActivity() != null) getActivity().onBackPressed();
                })
                .setCancelable(false)
                .show();
    }

    private void ocultarTodo() {
        cardNoDisponible.setVisibility(View.GONE);
        cardSinPermisos.setVisibility(View.GONE);
        cardBuscando.setVisibility(View.GONE);
        cardSinSesiones.setVisibility(View.GONE);
        cardUnaSesion.setVisibility(View.GONE);
        cardMultiplesSesiones.setVisibility(View.GONE);
        layoutContenido.setVisibility(View.VISIBLE);
    }

    private void mostrarCargando(boolean cargando) {
        if (!isAdded()) return;
        requireActivity().runOnUiThread(() -> {
            progressBar.setVisibility(cargando ? View.VISIBLE : View.GONE);
            layoutContenido.setVisibility(cargando ? View.GONE : View.VISIBLE);
        });
    }

    private void abrirPlayStoreHC() {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=com.google.android.apps.healthdata")));
        } catch (Exception e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.apps.healthdata")));
        }
    }
}