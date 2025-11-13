package com.example.sportine;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.example.sportine.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

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
        String rol = prefs.getString("USER_ROL", "ALUMNO");

        // 2. Declaramos la configuración fuera del if/else
        AppBarConfiguration appBarConfiguration;

        // 3. Configuramos según el rol
        if (rol.equals("ENTRENADOR")) {
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
    }
}