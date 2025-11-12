package com.example.sportine.ui.entrenadores.configuracion;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.sportine.R;
// Importamos MaterialCardView para el botón de retroceso (si es una card)
import com.google.android.material.card.MaterialCardView;

public class ConfiguracionEntrenadorFragment extends Fragment {

    // Componentes de visualización de datos
    private TextView tvNombre;
    private TextView tvApellido;
    private TextView tvUsername;
    private TextView tvSexo;
    private TextView tvEstado;
    private TextView tvCiudad;
    private TextView tvPassword;
    private ImageView btnTogglePassword;
    private TextView tvCostoMensualidad;
    private TextView tvAlumnosInscritos;
    private TextView tvDineroGanado;

    // Componentes de configuración de tema
    private CheckBox checkClaro;
    private CheckBox checkObscuro;

    // Controles de navegación/acción
    private View btnBack; // Es el MaterialCardView de volver
    private Button btnModificar;

    // ** VARIABLES DE CLASE CORREGIDAS **
    private String currentPassword = "••••••••"; // Contraseña real, inicializada como máscara
    private boolean isPasswordVisible = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_entrenador_configuracion, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Inicializar Componentes
        inicializarComponentes(view);

        // 2. Configurar Listeners (Navegación y Lógica)
        configurarListeners(view);

        // 3. Cargar Datos
        cargarDatosEntrenador();
    }

    // --- Métodos de Ayuda ---

    private void inicializarComponentes(@NonNull View view) {
        // Datos Personales
        tvNombre = view.findViewById(R.id.tvNombre);
        tvApellido = view.findViewById(R.id.tvApellido);
        tvUsername = view.findViewById(R.id.tvUsername);
        tvSexo = view.findViewById(R.id.tvSexo);
        tvEstado = view.findViewById(R.id.tvEstado);
        tvCiudad = view.findViewById(R.id.tvCiudad);
        tvPassword = view.findViewById(R.id.tvPassword);
        btnTogglePassword = view.findViewById(R.id.btnTogglePassword);

        // Detalles de Clases
        tvCostoMensualidad = view.findViewById(R.id.tvCostoMensualidad);
        tvAlumnosInscritos = view.findViewById(R.id.tvAlumnosInscritos);
        tvDineroGanado = view.findViewById(R.id.tvDineroGanado);

        // Tema
        checkClaro = view.findViewById(R.id.checkClaro);
        checkObscuro = view.findViewById(R.id.checkObscuro);

        // Controles de Navegación/Acción
        btnBack = view.findViewById(R.id.btnBack);
        btnModificar = view.findViewById(R.id.btnModificarentrena);
    }

    private void configurarListeners(@NonNull View view) {
        // Botón Volver Atrás
        btnBack.setOnClickListener(v -> requireActivity().onBackPressed());

        // Botón Modificar (Navegación)
        btnModificar.setOnClickListener(v -> {
            // Asegúrate de que el R.id.action_... existe en tu navigation graph
            Navigation.findNavController(v)
                    .navigate(R.id.action_configuracionEntrenadorFragment_to_modificarDatosEntrenadorFragment);
        });

        // Toggle para mostrar/ocultar Contraseña
        btnTogglePassword.setOnClickListener(v -> togglePasswordVisibility());

        // Listeners para CheckBox de Tema
        checkClaro.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                checkObscuro.setChecked(false);
                aplicarTema("Claro");
            }
        });

        checkObscuro.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                checkClaro.setChecked(false);
                aplicarTema("Oscuro");
            }
        });
    }

    // Lógica para mostrar/ocultar contraseña (CORREGIDA)
    private void togglePasswordVisibility() {
        // Definimos el texto que se mostrará cuando esté visible (la contraseña real)
        // y el texto que se mostrará cuando esté oculto (la máscara de puntos).
        String hiddenText = "••••••••";
        String visibleText = currentPassword; // La contraseña real guardada en la variable de clase

        if (isPasswordVisible) {
            // Ocultar contraseña (si actualmente está visible)
            tvPassword.setText(hiddenText);
            // Reemplaza R.drawable.ic_ojocerrado con tu ícono de ojo cerrado/slash real
            // Dejo R.drawable.ic_ojover como placeholder si solo tienes ese.
            btnTogglePassword.setImageResource(R.drawable.ic_ojover);
            isPasswordVisible = false;
        } else {
            // Mostrar contraseña (si actualmente está oculta)
            tvPassword.setText(visibleText);
            // Reemplaza R.drawable.ic_ojoabierto con tu ícono de ojo abierto real
            btnTogglePassword.setImageResource(R.drawable.ic_ojover);
            isPasswordVisible = true;
        }
    }

    // Lógica simulada de aplicación de tema
    private void aplicarTema(String tema) {
        Toast.makeText(getContext(), "Aplicando tema: " + tema, Toast.LENGTH_SHORT).show();
    }


    // Lógica simulada de carga de datos
    private void cargarDatosEntrenador() {
        // ** Reemplazar con la lógica real de obtención de datos (BD/API) **

        // Datos Personales
        String nombre = "Martín";
        String apellido = "Pérez";
        String username = "Martinp_entrena";
        String sexo = "Masculino";
        String estado = "Jalisco";
        String ciudad = "Guadalajara";
        String passwordReal = "MiContraseñaSegura123"; // Contraseña cargada de la BD

        // Detalles de Clases
        double costoMensualidad = 800.00;
        int alumnosInscritos = 12;
        double dineroGanado = 15000.00;

        String temaActual = "Claro"; // Simulación del tema guardado

        // ** 1. GUARDA LA CONTRASEÑA REAL EN LA VARIABLE DE CLASE **
        currentPassword = passwordReal;

        // 2. Asignar datos a la UI
        tvNombre.setText(nombre);
        tvApellido.setText(apellido);
        tvUsername.setText(username);
        tvSexo.setText(sexo);
        tvEstado.setText(estado);
        tvCiudad.setText(ciudad);

        // Asignar contraseña (muestra la máscara inicial)
        tvPassword.setText("••••••••");

        // Asignar detalles de clases (usando formato de moneda)
        tvCostoMensualidad.setText(String.format("$ %,.2f", costoMensualidad));
        tvAlumnosInscritos.setText(String.valueOf(alumnosInscritos));
        tvDineroGanado.setText(String.format("$ %,.2f", dineroGanado));

        // Asignar tema
        if ("Claro".equals(temaActual)) {
            checkClaro.setChecked(true);
        } else if ("Oscuro".equals(temaActual)) {
            checkObscuro.setChecked(true);
        }
    }
}