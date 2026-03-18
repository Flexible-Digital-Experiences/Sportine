package com.example.sportine.ui.entrenadores.paypal;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.browser.customtabs.CustomTabColorSchemeParams;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.sportine.R;
import com.example.sportine.data.ApiService;
import com.example.sportine.data.RetrofitClient;
import com.example.sportine.models.payment.PaymentApiModels;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OnboardingPayPalFragment extends Fragment {

    private static final String TAG = "OnboardingPayPal";
    private static final String PREF_ONBOARDING_ABIERTO = "onboarding_navegador_abierto";

    private View btnBack;
    private MaterialCardView cardEstado;
    private TextView tvEstadoTitulo;
    private TextView tvEstadoDescripcion;
    private MaterialButton btnConectar;
    private MaterialButton btnYaConecte;
    private View layoutCompleted;
    private View progressBar;

    private ApiService apiService;
    private String username;
    private String estadoActual = "not_started";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_onboarding_pay_pal, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        apiService = RetrofitClient.getClient(requireContext()).create(ApiService.class);

        SharedPreferences prefs = requireContext()
                .getSharedPreferences("SportinePrefs", Context.MODE_PRIVATE);
        username = prefs.getString("USER_USERNAME", null);

        inicializarViews(view);
        configurarListeners();
        manejarDeepLink();
        verificarEstadoPayPal();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume - Verificando estado");
        manejarDeepLink();
        if (!"completed".equals(estadoActual)) {
            verificarEstadoPayPal();
        }
    }

    private void manejarDeepLink() {
        SharedPreferences prefs = requireContext()
                .getSharedPreferences("SportinePrefs", Context.MODE_PRIVATE);

        boolean onboardingSuccess = prefs.getBoolean("onboarding_success", false);
        long onboardingTimestamp = prefs.getLong("onboarding_timestamp", 0);

        Log.d(TAG, "=== VERIFICANDO FLAGS EN FRAGMENT ===");
        Log.d(TAG, "onboarding_success: " + onboardingSuccess);

        long currentTime = System.currentTimeMillis();
        boolean isRecent = (currentTime - onboardingTimestamp) < 120000;

        if (onboardingSuccess && isRecent) {
            Log.d(TAG, "✅ ONBOARDING EXITOSO DETECTADO - Actualizando UI");
            prefs.edit()
                    .remove("onboarding_success")
                    .remove("onboarding_timestamp")
                    .remove(PREF_ONBOARDING_ABIERTO)  // limpiar flag de navegador también
                    .apply();
            actualizarUI("completed");
        }
    }

    private void inicializarViews(@NonNull View view) {
        btnBack             = view.findViewById(R.id.btnBack);
        cardEstado          = view.findViewById(R.id.cardEstadoPayPal);
        tvEstadoTitulo      = view.findViewById(R.id.tvEstadoTitulo);
        tvEstadoDescripcion = view.findViewById(R.id.tvEstadoDescripcion);
        btnConectar         = view.findViewById(R.id.btnConectarPayPal);
        btnYaConecte        = view.findViewById(R.id.btnYaConecte);
        layoutCompleted     = view.findViewById(R.id.layoutCompletado);
        progressBar         = view.findViewById(R.id.progressBar);
    }

    private void configurarListeners() {
        btnBack.setOnClickListener(v -> {
            // Al salir limpiar el flag de navegador abierto
            requireContext()
                    .getSharedPreferences("SportinePrefs", Context.MODE_PRIVATE)
                    .edit()
                    .remove(PREF_ONBOARDING_ABIERTO)
                    .apply();
            requireActivity().onBackPressed();
        });

        btnConectar.setOnClickListener(v -> iniciarOnboarding());

        btnYaConecte.setOnClickListener(v -> {
            Log.d(TAG, "Usuario indicó que ya conectó PayPal - verificando con PayPal...");
            verificarOnboardingManual();
        });
    }

    // ==========================================
    // VERIFICAR ESTADO ACTUAL
    // ==========================================
    private void verificarEstadoPayPal() {
        if (username == null) return;

        mostrarLoading(true);

        apiService.verificarEntrenadorPuedeRecibirPagos(username)
                .enqueue(new Callback<PaymentApiModels.PuedeRecibirPagosResponse>() {
                    @Override
                    public void onResponse(
                            Call<PaymentApiModels.PuedeRecibirPagosResponse> call,
                            Response<PaymentApiModels.PuedeRecibirPagosResponse> response) {

                        if (!isAdded()) return;
                        mostrarLoading(false);
                        if (response.isSuccessful() && response.body() != null) {
                            boolean puedeRecibir = response.body().isPuedeRecibirPagos();
                            actualizarUI(puedeRecibir ? "completed" : "not_started");
                        } else {
                            actualizarUI("not_started");
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<PaymentApiModels.PuedeRecibirPagosResponse> call,
                            Throwable t) {
                        if (!isAdded()) return;
                        mostrarLoading(false);
                        Log.e(TAG, "Error verificando estado: " + t.getMessage());
                        actualizarUI("not_started");
                    }
                });
    }

    // ==========================================
    // INICIAR ONBOARDING → abre Chrome Custom Tab
    // ==========================================
    private void iniciarOnboarding() {
        if (username == null) return;

        mostrarLoading(true);
        btnConectar.setEnabled(false);

        apiService.iniciarOnboardingPayPal(username)
                .enqueue(new Callback<java.util.Map<String, Object>>() {
                    @Override
                    public void onResponse(Call<java.util.Map<String, Object>> call,
                                           Response<java.util.Map<String, Object>> response) {
                        if (!isAdded()) return;
                        mostrarLoading(false);
                        btnConectar.setEnabled(true);

                        if (response.isSuccessful() && response.body() != null) {
                            java.util.Map<String, Object> body = response.body();
                            Boolean success = (Boolean) body.get("success");
                            String onboardingUrl = (String) body.get("onboarding_url");

                            if (Boolean.TRUE.equals(success) && onboardingUrl != null) {
                                estadoActual = "pending";

                                // Guardar flag de que el navegador fue abierto
                                requireContext()
                                        .getSharedPreferences("SportinePrefs", Context.MODE_PRIVATE)
                                        .edit()
                                        .putBoolean(PREF_ONBOARDING_ABIERTO, true)
                                        .apply();

                                if (btnYaConecte != null) btnYaConecte.setVisibility(View.VISIBLE);
                                abrirCustomTab(onboardingUrl);
                            } else {
                                String msg = (String) body.get("message");
                                Toast.makeText(requireContext(),
                                        msg != null ? msg : "Error al iniciar onboarding",
                                        Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(requireContext(),
                                    "Error del servidor: " + response.code(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<java.util.Map<String, Object>> call, Throwable t) {
                        if (!isAdded()) return;
                        mostrarLoading(false);
                        btnConectar.setEnabled(true);
                        Log.e(TAG, "Error: " + t.getMessage());
                        Toast.makeText(requireContext(),
                                "Error de conexión: " + t.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void abrirCustomTab(String url) {
        try {
            CustomTabColorSchemeParams colorScheme = new CustomTabColorSchemeParams.Builder()
                    .setToolbarColor(ContextCompat.getColor(requireContext(), R.color.white))
                    .build();

            CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder()
                    .setDefaultColorSchemeParams(colorScheme)
                    .setShowTitle(true)
                    .setUrlBarHidingEnabled(false)
                    .build();

            customTabsIntent.launchUrl(requireContext(), Uri.parse(url));
            Log.d(TAG, "✅ Chrome Custom Tab abierto: " + url);

        } catch (Exception e) {
            Log.e(TAG, "Error abriendo Custom Tab: " + e.getMessage());
            Toast.makeText(requireContext(),
                    "Error al abrir el navegador", Toast.LENGTH_SHORT).show();
        }
    }

    // ==========================================
    // ACTUALIZAR UI SEGÚN ESTADO
    // ==========================================
    private void actualizarUI(String estado) {
        this.estadoActual = estado;
        if (!isAdded()) return;

        // Si el navegador fue abierto y el estado sigue siendo not_started,
        // mostrar el botón "Ya conecté" de todas formas
        boolean navegadorAbierto = requireContext()
                .getSharedPreferences("SportinePrefs", Context.MODE_PRIVATE)
                .getBoolean(PREF_ONBOARDING_ABIERTO, false);

        switch (estado) {
            case "completed":
                cardEstado.setCardBackgroundColor(
                        requireContext().getColor(R.color.paypal_status_completed_bg));
                tvEstadoTitulo.setText("✅ PayPal conectado");
                tvEstadoDescripcion.setText("Tu cuenta PayPal está activa. Tus alumnos ya pueden pagarte.");
                tvEstadoTitulo.setTextColor(requireContext().getColor(R.color.paypal_status_completed_text));
                btnConectar.setVisibility(View.GONE);
                if (btnYaConecte != null) btnYaConecte.setVisibility(View.GONE);
                if (layoutCompleted != null) layoutCompleted.setVisibility(View.VISIBLE);
                // Limpiar flag ya que se completó
                requireContext()
                        .getSharedPreferences("SportinePrefs", Context.MODE_PRIVATE)
                        .edit().remove(PREF_ONBOARDING_ABIERTO).apply();
                break;

            case "pending":
                cardEstado.setCardBackgroundColor(
                        requireContext().getColor(R.color.paypal_status_pending_bg));
                tvEstadoTitulo.setText("⏳ Verificación pendiente");
                tvEstadoDescripcion.setText("Completaste el proceso en PayPal. Estamos verificando tu cuenta.");
                tvEstadoTitulo.setTextColor(requireContext().getColor(R.color.paypal_status_pending_text));
                btnConectar.setText("Volver a intentar");
                btnConectar.setVisibility(View.VISIBLE);
                if (btnYaConecte != null) btnYaConecte.setVisibility(View.VISIBLE);
                if (layoutCompleted != null) layoutCompleted.setVisibility(View.GONE);
                break;

            case "failed":
                cardEstado.setCardBackgroundColor(
                        requireContext().getColor(R.color.paypal_status_failed_bg));
                tvEstadoTitulo.setText("❌ Conexión fallida");
                tvEstadoDescripcion.setText("Hubo un problema al conectar tu cuenta. Por favor intenta de nuevo.");
                tvEstadoTitulo.setTextColor(requireContext().getColor(R.color.paypal_status_failed_text));
                btnConectar.setText("Reintentar");
                btnConectar.setVisibility(View.VISIBLE);
                if (btnYaConecte != null) btnYaConecte.setVisibility(View.GONE);
                if (layoutCompleted != null) layoutCompleted.setVisibility(View.GONE);
                break;

            default: // not_started
                cardEstado.setCardBackgroundColor(
                        requireContext().getColor(R.color.paypal_status_default_bg));
                tvEstadoTitulo.setText("Conecta tu cuenta PayPal");
                tvEstadoDescripcion.setText("Para recibir pagos de tus alumnos necesitas vincular tu cuenta PayPal.");
                tvEstadoTitulo.setTextColor(requireContext().getColor(R.color.paypal_status_default_text));
                btnConectar.setText("Conectar cuenta PayPal");
                btnConectar.setVisibility(View.VISIBLE);
                if (layoutCompleted != null) layoutCompleted.setVisibility(View.GONE);

                // Si el navegador fue abierto antes, mantener el botón visible
                if (btnYaConecte != null) {
                    btnYaConecte.setVisibility(navegadorAbierto ? View.VISIBLE : View.GONE);
                }
                break;
        }
    }

    private void mostrarLoading(boolean mostrar) {
        if (progressBar != null) {
            progressBar.setVisibility(mostrar ? View.VISIBLE : View.GONE);
        }
    }

    private void verificarOnboardingManual() {
        if (username == null) return;

        mostrarLoading(true);
        if (btnYaConecte != null) btnYaConecte.setEnabled(false);

        apiService.verificarOnboardingManual(username)
                .enqueue(new Callback<java.util.Map<String, Object>>() {
                    @Override
                    public void onResponse(Call<java.util.Map<String, Object>> call,
                                           Response<java.util.Map<String, Object>> response) {
                        if (!isAdded()) return;
                        mostrarLoading(false);
                        if (btnYaConecte != null) btnYaConecte.setEnabled(true);

                        if (response.isSuccessful() && response.body() != null) {
                            java.util.Map<String, Object> body = response.body();
                            Boolean completado = (Boolean) body.get("completado");

                            if (Boolean.TRUE.equals(completado)) {
                                Log.d(TAG, "✅ Onboarding completado - actualizando UI");
                                // Limpiar flag de navegador abierto
                                requireContext()
                                        .getSharedPreferences("SportinePrefs", Context.MODE_PRIVATE)
                                        .edit().remove(PREF_ONBOARDING_ABIERTO).apply();
                                actualizarUI("completed");
                                Toast.makeText(requireContext(),
                                        "¡Cuenta PayPal conectada exitosamente!",
                                        Toast.LENGTH_LONG).show();
                            } else {
                                String mensaje = (String) body.get("message");
                                Log.d(TAG, "Onboarding no completado aún: " + mensaje);
                                Toast.makeText(requireContext(),
                                        mensaje != null ? mensaje :
                                                "Aún no completaste el proceso en PayPal.",
                                        Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(requireContext(),
                                    "Error al verificar: " + response.code(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<java.util.Map<String, Object>> call, Throwable t) {
                        if (!isAdded()) return;
                        mostrarLoading(false);
                        if (btnYaConecte != null) btnYaConecte.setEnabled(true);
                        Log.e(TAG, "Error verificando: " + t.getMessage());
                        Toast.makeText(requireContext(),
                                "Error de conexión: " + t.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }
}