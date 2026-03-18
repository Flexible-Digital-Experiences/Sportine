package com.example.sportine;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.example.sportine.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = binding.navView;

        NavController navController = Navigation.findNavController(
                this,
                R.id.nav_host_fragment_activity_main
        );

        SharedPreferences prefs = getSharedPreferences("SportinePrefs", MODE_PRIVATE);
        String rol = prefs.getString("USER_ROL", "alumno");

        AppBarConfiguration appBarConfiguration;

        if (rol.equals("entrenador")) {
            navView.getMenu().clear();
            navView.inflateMenu(R.menu.menu_entrenador);
            navController.setGraph(R.navigation.entrenador_navigation);
            appBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.navigation_home_entrenador,
                    R.id.navigation_stats_entrenador,
                    R.id.navigation_perfil_entrenador,
                    R.id.navigation_solicitudes_entrenador,
                    R.id.navigation_social_entrenador
            ).build();
        } else {
            navView.getMenu().clear();
            navView.inflateMenu(R.menu.bottom_nav_menu);
            navController.setGraph(R.navigation.mobile_navigation);
            appBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.navigation_home,
                    R.id.navigation_notifications,
                    R.id.navigation_stats,
                    R.id.navigation_perfil,
                    R.id.navigation_buscar,
                    R.id.navigation_social
            ).build();
        }

        NavigationUI.setupWithNavController(navView, navController);

        handleDeepLink(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        Log.d(TAG, "onNewIntent llamado");
        handleDeepLink(intent);
    }

    private void handleDeepLink(Intent intent) {
        Uri data = intent.getData();

        Log.d(TAG, "=== DEEP LINK EN MAINACTIVITY ===");
        Log.d(TAG, "URI: " + data);

        if (data == null) {
            Log.d(TAG, "No hay URI en el Intent");
            return;
        }

        String scheme = data.getScheme();
        String host = data.getHost();
        String path = data.getLastPathSegment();

        Log.d(TAG, "Scheme: " + scheme + ", Host: " + host + ", Path: " + path);

        if (!"sportine".equals(scheme)) {
            Log.d(TAG, "Scheme no reconocido: " + scheme);
            return;
        }

        SharedPreferences prefs = getSharedPreferences("SportinePrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        // ── DEEP LINK DE PAGO ─────────────────────────────────────────────────
        if ("payment".equals(host)) {
            if ("success".equals(path)) {
                String token = data.getQueryParameter("token");
                String payerId = data.getQueryParameter("PayerID");
                Log.d(TAG, "✅ PAGO EXITOSO - Token: " + token + ", PayerID: " + payerId);
                editor.putBoolean("payment_success", true);
                editor.putString("payment_token", token);
                editor.putString("payment_payer_id", payerId);
                editor.putLong("payment_timestamp", System.currentTimeMillis());
                editor.apply();
                Log.d(TAG, "✅ Flags de pago guardados en SharedPreferences");
            } else if ("cancel".equals(path)) {
                Log.d(TAG, "❌ PAGO CANCELADO");
                editor.putBoolean("payment_cancelled", true);
                editor.putLong("payment_timestamp", System.currentTimeMillis());
                editor.apply();
            }

            // ── DEEP LINK DE ONBOARDING ───────────────────────────────────────────
        } else if ("onboarding".equals(host)) {
            if ("success".equals(path)) {
                Log.d(TAG, "✅ ONBOARDING COMPLETADO - Guardando flag");
                editor.putBoolean("onboarding_success", true);
                editor.putLong("onboarding_timestamp", System.currentTimeMillis());
                editor.apply();
                Log.d(TAG, "✅ Flag de onboarding guardado");
            }

        } else {
            Log.d(TAG, "⚠️ Host no reconocido: " + host);
        }

        intent.setData(null);
    }
}