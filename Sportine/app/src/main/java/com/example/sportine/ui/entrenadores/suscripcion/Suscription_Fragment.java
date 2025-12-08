package com.example.sportine.ui.entrenadores.suscripcion;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.sportine.R;
import com.example.sportine.data.ApiService;
import com.example.sportine.data.RetrofitClient;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class Suscription_Fragment extends Fragment {

    private static final String TAG = "SubscriptionFragment";

    private Button btnSubscribe;
    private ProgressBar progressBar;
    private TextView tvStatus;
    private ImageButton btnBack;

    private ApiService apiService;
    private String currentSubscriptionId;
    private String userEmail;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_entrenador_suscription, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Inicializar vistas
        initViews(view);

        // Obtener email del usuario
        SharedPreferences prefs = requireActivity().getSharedPreferences("SportinePrefs", MODE_PRIVATE);
        userEmail = prefs.getString("USER_USERNAME", "");

        if (userEmail.isEmpty()) {
            Toast.makeText(requireContext(), "Error: No hay usuario logueado", Toast.LENGTH_SHORT).show();
            view.post(() -> {
                if (isAdded()) {
                    NavHostFragment.findNavController(this).navigateUp();
                }
            });
            return;
        }

        // Inicializar API
        apiService = RetrofitClient.getClient(requireContext()).create(ApiService.class);

        setupListeners();

        // Verificar si venimos de un Deep Link de PayPal
        manejarDeepLink();

        verificarEstadoSuscripcion();
    }

    private void manejarDeepLink() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("SportinePrefs", MODE_PRIVATE);

        boolean paymentSuccess = prefs.getBoolean("payment_success", false);
        boolean paymentCancelled = prefs.getBoolean("payment_cancelled", false);
        long paymentTimestamp = prefs.getLong("payment_timestamp", 0);

        Log.d(TAG, "=== VERIFICANDO FLAGS EN FRAGMENT ===");
        Log.d(TAG, "payment_success: " + paymentSuccess);
        Log.d(TAG, "payment_cancelled: " + paymentCancelled);

        // Verificar que el flag sea reciente (últimos 2 minutos)
        long currentTime = System.currentTimeMillis();
        boolean isRecent = (currentTime - paymentTimestamp) < 120000; // 2 minutos

        if (paymentSuccess && isRecent) {
            Log.d(TAG, "✅ PAGO EXITOSO DETECTADO - Confirmando suscripción");

            // Limpiar flags
            prefs.edit()
                    .remove("payment_success")
                    .remove("payment_timestamp")
                    .apply();

            // Confirmar suscripción
            confirmarSuscripcion();

        } else if (paymentCancelled) {
            Log.d(TAG, "❌ PAGO CANCELADO DETECTADO");

            // Limpiar flag
            prefs.edit()
                    .remove("payment_cancelled")
                    .apply();

            Toast.makeText(requireContext(),
                    "Suscripción cancelada", Toast.LENGTH_SHORT).show();
            verificarEstadoSuscripcion();
        }
    }

    private void initViews(View view) {
        btnSubscribe = view.findViewById(R.id.btnSubscribe);
        progressBar = view.findViewById(R.id.progressBar);
        tvStatus = view.findViewById(R.id.tvStatus);
        btnBack = view.findViewById(R.id.btn_back);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v ->
                NavHostFragment.findNavController(this).navigateUp()
        );

        btnSubscribe.setOnClickListener(v -> {
            String btnText = btnSubscribe.getText().toString();

            if (btnText.contains("Cancelar")) {
                mostrarDialogoCancelar();
            } else {
                // Para cualquier estado (nuevo o pendiente), crear/obtener y abrir navegador
                iniciarOCompletarSuscripcion();
            }
        });
    }

    private void verificarEstadoSuscripcion() {
        progressBar.setVisibility(View.VISIBLE);

        apiService.verificarEstadoSuscripcion(userEmail).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (!isAdded()) return;

                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    String tipoCuenta = (String) response.body().get("tipo_cuenta");
                    String status = (String) response.body().get("status_paypal");

                    if ("premium".equals(tipoCuenta) && "ACTIVE".equals(status)) {
                        // Premium activo
                        tvStatus.setText("✅ Cuenta Premium Activa");
                        btnSubscribe.setText("Cancelar Suscripción");
                        btnSubscribe.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));

                    } else if ("APPROVAL_PENDING".equals(status)) {
                        // Pendiente de aprobación
                        tvStatus.setText("⏳ Suscripción pendiente de aprobación");
                        btnSubscribe.setText("Suscribirse con PayPal");
                        currentSubscriptionId = (String) response.body().get("subscription_id");

                    } else {
                        // Free - cuenta nueva
                        tvStatus.setText("Cuenta Gratuita - Máximo 10 alumnos");
                        btnSubscribe.setText("Suscribirse con PayPal");
                    }
                } else {
                    Log.e(TAG, "Error en response: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                if (!isAdded()) return;
                progressBar.setVisibility(View.GONE);
                Log.e(TAG, "Error verificando estado: " + t.getMessage());
            }
        });
    }

    private void iniciarOCompletarSuscripcion() {
        progressBar.setVisibility(View.VISIBLE);
        btnSubscribe.setEnabled(false);

        // Si ya existe una suscripción pendiente, obtener su URL
        if (currentSubscriptionId != null) {
            obtenerYAbrirApprovalUrl();
        } else {
            // Si no existe, crear nueva suscripción
            crearNuevaSuscripcion();
        }
    }

    private void crearNuevaSuscripcion() {
        apiService.crearSuscripcion(userEmail).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (!isAdded()) return;

                progressBar.setVisibility(View.GONE);
                btnSubscribe.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    Boolean success = (Boolean) response.body().get("success");

                    if (Boolean.TRUE.equals(success)) {
                        currentSubscriptionId = (String) response.body().get("subscription_id");
                        String approvalUrl = (String) response.body().get("approval_url");

                        Log.d(TAG, "✅ Suscripción creada: " + currentSubscriptionId);
                        Log.d(TAG, "✅ Approval URL: " + approvalUrl);

                        // Guardar el ID para usarlo después del Deep Link
                        requireActivity().getSharedPreferences("SportinePrefs", MODE_PRIVATE)
                                .edit()
                                .putString("pending_subscription_id", currentSubscriptionId)
                                .apply();

                        Log.d(TAG, "✅ Guardando pending_subscription_id en SharedPreferences");

                        // Abrir navegador directamente
                        if (approvalUrl != null && !approvalUrl.isEmpty()) {
                            abrirNavegador(approvalUrl);
                        }
                    } else {
                        String message = (String) response.body().get("message");
                        Toast.makeText(requireContext(),
                                "Error: " + message, Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(requireContext(),
                            "Error al crear suscripción", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                if (!isAdded()) return;

                progressBar.setVisibility(View.GONE);
                btnSubscribe.setEnabled(true);
                Log.e(TAG, "Error: " + t.getMessage());
                Toast.makeText(requireContext(),
                        "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void obtenerYAbrirApprovalUrl() {
        apiService.obtenerApprovalUrl(currentSubscriptionId).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (!isAdded()) return;

                progressBar.setVisibility(View.GONE);
                btnSubscribe.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    Boolean success = (Boolean) response.body().get("success");

                    if (Boolean.TRUE.equals(success)) {
                        String approvalUrl = (String) response.body().get("approval_url");

                        if (approvalUrl != null && !approvalUrl.isEmpty()) {
                            abrirNavegador(approvalUrl);
                        } else {
                            Toast.makeText(requireContext(),
                                    "Error: No se encontró la URL de aprobación",
                                    Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        String message = (String) response.body().get("message");
                        Toast.makeText(requireContext(),
                                "Error: " + message, Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(requireContext(),
                            "Error al obtener URL: " + response.code(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                if (!isAdded()) return;

                progressBar.setVisibility(View.GONE);
                btnSubscribe.setEnabled(true);
                Log.e(TAG, "Error obteniendo approval URL: " + t.getMessage());
                Toast.makeText(requireContext(),
                        "Error de conexión: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void abrirNavegador(String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
            Toast.makeText(requireContext(),
                    "Completa el proceso en el navegador y regresa a la app",
                    Toast.LENGTH_LONG).show();

            Log.d(TAG, "✅ Abriendo navegador con approval_url");
        } catch (Exception e) {
            Log.e(TAG, "Error abriendo navegador: " + e.getMessage());
            Toast.makeText(requireContext(),
                    "Error al abrir el navegador",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void confirmarSuscripcion() {
        // Obtener el subscription_id de SharedPreferences si no lo tenemos
        if (currentSubscriptionId == null || currentSubscriptionId.isEmpty()) {
            SharedPreferences prefs = requireActivity().getSharedPreferences("SportinePrefs", MODE_PRIVATE);
            currentSubscriptionId = prefs.getString("pending_subscription_id", null);

            Log.d(TAG, "Recuperando subscription_id de SharedPreferences: " + currentSubscriptionId);

            if (currentSubscriptionId == null) {
                Toast.makeText(requireContext(),
                        "Error: No se encontró la suscripción",
                        Toast.LENGTH_SHORT).show();
                verificarEstadoSuscripcion();
                return;
            }
        }

        progressBar.setVisibility(View.VISIBLE);
        btnSubscribe.setEnabled(false);

        Log.d(TAG, "✅ Confirmando suscripción con ID: " + currentSubscriptionId);
        Log.d(TAG, "✅ Usuario: " + userEmail);

        // Llamar al endpoint de confirmar
        apiService.confirmarSuscripcion(currentSubscriptionId, userEmail)
                .enqueue(new Callback<Map<String, Object>>() {
                    @Override
                    public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                        if (!isAdded()) return;

                        progressBar.setVisibility(View.GONE);
                        btnSubscribe.setEnabled(true);

                        Log.d(TAG, "Respuesta de confirmar: " + response.code());

                        if (response.isSuccessful() && response.body() != null) {
                            Boolean success = (Boolean) response.body().get("success");

                            Log.d(TAG, "Success: " + success);

                            if (Boolean.TRUE.equals(success)) {
                                String message = (String) response.body().get("message");

                                Toast.makeText(requireContext(),
                                        message != null ? message : "¡Suscripción activada!",
                                        Toast.LENGTH_LONG).show();

                                Log.d(TAG, "✅ Suscripción confirmada exitosamente");

                                // Limpiar el ID pendiente
                                requireActivity().getSharedPreferences("SportinePrefs", MODE_PRIVATE)
                                        .edit()
                                        .remove("pending_subscription_id")
                                        .apply();

                                // Actualizar UI
                                verificarEstadoSuscripcion();

                            } else {
                                String message = (String) response.body().get("message");
                                Toast.makeText(requireContext(),
                                        "Error: " + message, Toast.LENGTH_LONG).show();
                                Log.e(TAG, "Error en respuesta: " + message);
                            }
                        } else {
                            Log.e(TAG, "Error en respuesta: " + response.code());
                            Toast.makeText(requireContext(),
                                    "Error al confirmar suscripción: " + response.code(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                        if (!isAdded()) return;

                        progressBar.setVisibility(View.GONE);
                        btnSubscribe.setEnabled(true);
                        Log.e(TAG, "❌ Error confirmando: " + t.getMessage());
                        Toast.makeText(requireContext(),
                                "Error de conexión al confirmar",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void mostrarDialogoCancelar() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Cancelar Suscripción")
                .setMessage("¿Estás seguro de que deseas cancelar tu suscripción Premium?\n\nTendrás acceso hasta el fin del periodo actual.")
                .setPositiveButton("Sí, cancelar", (dialog, which) -> cancelarSuscripcion())
                .setNegativeButton("No", null)
                .show();
    }

    private void cancelarSuscripcion() {
        progressBar.setVisibility(View.VISIBLE);

        apiService.cancelarSuscripcion(userEmail, "Usuario solicitó cancelación")
                .enqueue(new Callback<Map<String, Object>>() {
                    @Override
                    public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                        if (!isAdded()) return;

                        progressBar.setVisibility(View.GONE);

                        if (response.isSuccessful() && response.body() != null) {
                            Boolean success = (Boolean) response.body().get("success");
                            String message = (String) response.body().get("message");

                            Toast.makeText(requireContext(),
                                    message, Toast.LENGTH_LONG).show();

                            if (Boolean.TRUE.equals(success)) {
                                verificarEstadoSuscripcion();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                        if (!isAdded()) return;

                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(requireContext(),
                                "Error al cancelar", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume - Verificando estado");

        // Primero verificar si hay deep link
        manejarDeepLink();

        // Luego verificar estado general (solo si no hay confirmación pendiente)
        // Este delay evita que se ejecute antes de confirmarSuscripcion()
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (isAdded()) {
                verificarEstadoSuscripcion();
            }
        }, 500);
    }
}