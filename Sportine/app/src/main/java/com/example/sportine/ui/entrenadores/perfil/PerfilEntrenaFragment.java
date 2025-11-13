package com.example.sportine.ui.entrenadores.perfil;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.sportine.R;
// Asegúrate de importar MaterialCardView si lo estás usando como botón,
// aunque lo manejamos como View para simplificar el código del listener.
import com.google.android.material.card.MaterialCardView;

public class PerfilEntrenaFragment extends Fragment {

    // Componentes de la interfaz para mostrar datos y contadores
    private TextView txtSaludoEntrenador;
    private TextView tvDescripcionEntrenador;
    private TextView tvNombreEntrenador;
    private TextView tvApellidoEntrenador;
    private TextView tvUsernameEntrenador;
    private TextView tvSexoEntrenador;
    private TextView tvEstadoEntrenador;
    private TextView tvCiudadEntrenador;
    private TextView tvContadorAlumnos;
    private TextView tvContadorAmigos;

    // Controles
    private View btnSettings; // Ahora es un MaterialCardView, pero se maneja como View
    private Button btnCompletardatos;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_entrenador_perfil, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Inicializar todos los componentes
        inicializarComponentes(view);

        // 2. Configurar Listeners de Navegación
        configurarListeners();

        // 3. Cargar y mostrar datos (Ejemplo de simulación)
        cargarDatosEntrenador();
    }

    // --- Inicialización y Configuración ---

    private void inicializarComponentes(@NonNull View view) {
        // Campos de datos
        txtSaludoEntrenador = view.findViewById(R.id.txtSaludoEntrenador);
        tvDescripcionEntrenador = view.findViewById(R.id.tvDescripcionEntrenador);
        tvNombreEntrenador = view.findViewById(R.id.tvNombreEntrenador);
        tvApellidoEntrenador = view.findViewById(R.id.tvApellidoEntrenador);
        tvUsernameEntrenador = view.findViewById(R.id.tvUsernameEntrenador);
        tvSexoEntrenador = view.findViewById(R.id.tvSexoEntrenador);
        tvEstadoEntrenador = view.findViewById(R.id.tvEstadoEntrenador);
        tvCiudadEntrenador = view.findViewById(R.id.tvCiudadEntrenador);

        // Contadores
        tvContadorAlumnos = view.findViewById(R.id.tvContadorAlumnos);
        tvContadorAmigos = view.findViewById(R.id.tvContadorAmigos);

        // Botones
        btnSettings = view.findViewById(R.id.btnSettings);
        btnCompletardatos = view.findViewById(R.id.btnCompletarentrena);

        // NOTA: La ImageView del avatar (R.id.iv_avatar_perfil_entrenador) también debería inicializarse
        // si planeas cargar la imagen de perfil de forma dinámica (ej. con Glide o Picasso).
    }

    private void configurarListeners() {
        // Listener para el botón/tarjeta de Configuración
        btnSettings.setOnClickListener(v -> {
            // Asegúrate de que el R.id.action_... existe en tu navigation graph
            Navigation.findNavController(v)
                    .navigate(R.id.action_perfilentre_to_configuracion);
        });

        // Listener para el botón Completar Datos
        btnCompletardatos.setOnClickListener(v -> {
            // Asegúrate de que el R.id.action_... existe en tu navigation graph
            Navigation.findNavController(v)
                    .navigate(R.id.action_perfilentre_to_completar_datos);
        });
    }

    // --- Lógica de Datos (Simulación) ---

    private void cargarDatosEntrenador() {
        // ** Aquí debes implementar la lógica para obtener los datos reales del entrenador
        //    desde tu Base de Datos o API **

        String nombre = "Martín";
        String apellido = "Pérez";
        String username = "Martinp_entrena";
        String sexo = "Masculino";
        String estado = "Jalisco";
        String ciudad = "Guadalajara";
        String descripcion = "Instructor de basquetball, fútbol y tennis con más de 15 años de experiencia con alumnos de talla nacional.";
        int alumnos = 12;
        int amigos = 58;

        // Mostrar datos en la UI
        txtSaludoEntrenador.setText(String.format("Hola %s", nombre));
        tvDescripcionEntrenador.setText(descripcion);
        tvNombreEntrenador.setText(nombre);
        tvApellidoEntrenador.setText(apellido);
        tvUsernameEntrenador.setText(username);
        tvSexoEntrenador.setText(sexo);
        tvEstadoEntrenador.setText(estado);
        tvCiudadEntrenador.setText(ciudad);

        // Mostrar contadores
        tvContadorAlumnos.setText(String.valueOf(alumnos));
        tvContadorAmigos.setText(String.valueOf(amigos));

        // Si el perfil está completo, podrías ocultar el botón de completar datos.
        // if (perfilCompleto) btnCompletardatos.setVisibility(View.GONE);
    }
}