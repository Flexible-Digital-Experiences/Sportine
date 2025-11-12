package com.example.sportine;

import android.content.SharedPreferences; // ¡Importado! Para leer el rol
import android.os.Bundle;
import android.view.Menu; // ¡Importado! Para acceder al menú
import android.view.MenuItem; // ¡Importado! Para acceder al botón
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

        // Usamos ViewBinding (que ya tenías)
        BottomNavigationView navView = binding.navView;

        // Configuración de los destinos principales
        // (Asegúrate de que TODOS tus destinos de primer nivel estén aquí)
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home,
                R.id.navigation_notifications,
                R.id.navigation_stats,
                R.id.navigation_perfil,
                R.id.navigation_buscar,
                R.id.navigation_social // Asegúrate de que 'social' esté aquí también
        ).build();

        NavController navController = Navigation.findNavController(
                this,
                R.id.nav_host_fragment_activity_main
        );

        // --- INICIO DE LA LÓGICA DE ROL ---

        // 1. Leemos el Rol que guardamos en el Login
        SharedPreferences prefs = getSharedPreferences("SportinePrefs", MODE_PRIVATE);
        // "ALUMNO" es el valor por defecto si no encuentra nada
        String rol = prefs.getString("USER_ROL", "ALUMNO");

        // 2. Obtenemos el menú de la barra
        Menu menu = navView.getMenu();

        // 3. Buscamos el botón que queremos cambiar (el de "Buscar")
        //    ¡Asegúrate de que el ID 'navigation_buscar' sea correcto!
        MenuItem buscarItem = menu.findItem(R.id.navigation_buscar);

        // 4. ¡Aplicamos la magia!
        if (rol.equals("ENTRENADOR")) {
            // Si es Entrenador, le cambiamos el título y el ícono
            buscarItem.setTitle(R.string.title_solicitudes); // (Necesitas "Solicitudes" en strings.xml)
            buscarItem.setIcon(R.drawable.ic_solicitudes_black_24dp); // (Necesitas este ícono)
        } else {
            // Si es Alumno, nos aseguramos de que tenga los valores de "Buscar"
            buscarItem.setTitle(R.string.title_buscar); // (Necesitas "Buscar" en strings.xml)
            buscarItem.setIcon(R.drawable.ic_buscar_black_24dp); // (Necesitas este ícono)
        }

        // --- FIN DE LA LÓGICA DE ROL ---

        // Esto conecta la navegación (tu código original)
        NavigationUI.setupWithNavController(binding.navView, navController);
    }
}