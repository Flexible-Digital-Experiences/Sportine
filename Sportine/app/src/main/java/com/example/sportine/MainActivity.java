package com.example.sportine;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
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

        // Obtenemos el BottomNavigationView
        BottomNavigationView navView = binding.navView;

        // Obtenemos el NavController
        NavController navController = Navigation.findNavController(
                this,
                R.id.nav_host_fragment_activity_main
        );

        // --- LÓGICA DE ROL ---

        // 1. Leemos el rol guardado en SharedPreferences
        SharedPreferences prefs = getSharedPreferences("SportinePrefs", MODE_PRIVATE);
        String rol = prefs.getString("USER_ROL","alumno");

        // 2. Declaramos la configuración fuera del if/else
        AppBarConfiguration appBarConfiguration;

        // 3. Configuramos según el rol
        if (rol.equals("entrenador")) {
            // === CONFIGURACIÓN PARA ENTRENADOR ===

            // Cambiar el menú del BottomNavigationView
            navView.getMenu().clear();
            navView.inflateMenu(R.menu.menu_entrenador);

            // Establecer el nav graph de entrenador
            navController.setGraph(R.navigation.entrenador_navigation);

            // Configurar los destinos principales para entrenador
            appBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.navigation_home_entrenador,
                    R.id.navigation_stats_entrenador,
                    R.id.navigation_perfil_entrenador,
                    R.id.navigation_solicitudes_entrenador,
                    R.id.navigation_social_entrenador
            ).build();

        } else {
            // === CONFIGURACIÓN PARA ALUMNO ===

            // Cambiar el menú del BottomNavigationView
            navView.getMenu().clear();
            navView.inflateMenu(R.menu.bottom_nav_menu);

            // Establecer el nav graph de alumno
            navController.setGraph(R.navigation.mobile_navigation);

            // Configurar los destinos principales para alumno
            appBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.navigation_home,
                    R.id.navigation_notifications,
                    R.id.navigation_stats,
                    R.id.navigation_perfil,
                    R.id.navigation_buscar,
                    R.id.navigation_social
            ).build();
        }

        // --- FIN DE LA LÓGICA DE ROL ---

        // Conectar la navegación con el BottomNavigationView
        NavigationUI.setupWithNavController(navView, navController);

        // --- MANEJAR DEEP LINK AL CREAR ---
        handleDeepLink(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent); // IMPORTANTE: Actualizar el intent actual
        Log.d(TAG, "onNewIntent llamado");
        handleDeepLink(intent);
    }

    private void handleDeepLink(Intent intent) {
        Uri data = intent.getData();

        Log.d(TAG, "=== DEEP LINK EN MAINACTIVITY ===");
        Log.d(TAG, "Intent: " + intent);
        Log.d(TAG, "URI: " + data);

        if (data != null) {
            String scheme = data.getScheme();
            String host = data.getHost();
            String path = data.getLastPathSegment();

            Log.d(TAG, "Scheme: " + scheme);
            Log.d(TAG, "Host: " + host);
            Log.d(TAG, "Path: " + path);

            if ("sportine".equals(scheme) && "payment".equals(host)) {
                // Guardar en SharedPreferences para que el Fragment lo lea
                SharedPreferences prefs = getSharedPreferences("SportinePrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();

                if ("success".equals(path)) {
                    Log.d(TAG, "✅ PAGO EXITOSO - Guardando flag");
                    editor.putBoolean("payment_success", true);
                    editor.putLong("payment_timestamp", System.currentTimeMillis());
                    editor.apply();

                    Log.d(TAG, "✅ Flags guardados en SharedPreferences");

                } else if ("cancel".equals(path)) {
                    Log.d(TAG, "❌ PAGO CANCELADO - Guardando flag");
                    editor.putBoolean("payment_cancelled", true);
                    editor.apply();
                }

                // Limpiar el URI del intent para que no se procese de nuevo
                intent.setData(null);

                Log.d(TAG, "✅ Deep link procesado, URI limpiado");
            } else {
                Log.d(TAG, "⚠️ Deep link no coincide con sportine://payment");
            }
        } else {
            Log.d(TAG, "No hay URI en el Intent");
        }
    }
}