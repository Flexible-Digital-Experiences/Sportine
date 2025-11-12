package com.example.sportine.ui.entrenadores.modificardatosentrenador;

import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.sportine.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class ModificarDatosEntrenadorFragment extends Fragment {

    // Datos Actuales (TextViews)
    private TextView tvNombreActual;
    private TextView tvSexoActual;
    private TextView tvApellidoActual;
    private TextView tvEstadoActual;
    private TextView tvUsernameActual;
    private TextView tvCiudadActual;
    private TextView tvPasswordActual;
    private ImageView btnTogglePasswordActual;
    private boolean isPasswordVisible = false;

    // Nuevos Datos (Editables)
    private TextInputEditText etNombreNuevo;
    private TextInputEditText etApellidoNuevo;
    private TextInputEditText etUsernameNuevo;
    private TextInputEditText etPasswordNuevo;

    // Selectores modernos (AutoCompleteTextView)
    private AutoCompleteTextView actvSexoNuevo;
    private AutoCompleteTextView actvEstadoNuevo;
    private AutoCompleteTextView actvCiudadNuevo;

    private MaterialButton btnActualizar;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Asegúrate de que este R.layout corresponde al XML que modernizamos
        return inflater.inflate(R.layout.fragment_entrenador_modificar_datos, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Inicializar Componentes
        inicializarComponentes(view);

        // 2. Cargar datos actuales (Simulación)
        cargarDatosActuales();

        // 3. Configurar Selectores
        configurarSelectores(view);

        // 4. Configurar Listeners
        configurarListeners(view);
    }

    // --- Métodos de Ayuda ---

    private void inicializarComponentes(@NonNull View view) {
        // Botón Volver
        View btnBack = view.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> requireActivity().onBackPressed());

        // Datos Actuales (TextViews)
        tvNombreActual = view.findViewById(R.id.tvNombreActual);
        tvSexoActual = view.findViewById(R.id.tvSexoActual);
        tvApellidoActual = view.findViewById(R.id.tvApellidoActual);
        tvEstadoActual = view.findViewById(R.id.tvEstadoActual);
        tvUsernameActual = view.findViewById(R.id.tvUsernameActual);
        tvCiudadActual = view.findViewById(R.id.tvCiudadActual);
        tvPasswordActual = view.findViewById(R.id.tvPasswordActual);
        btnTogglePasswordActual = view.findViewById(R.id.btnTogglePasswordActual);

        // Nuevos Datos (Editables)
        etNombreNuevo = view.findViewById(R.id.etNombreNuevo);
        etApellidoNuevo = view.findViewById(R.id.etUsernameNuevo);
        etUsernameNuevo = view.findViewById(R.id.etUsernameNuevo);
        etPasswordNuevo = view.findViewById(R.id.etPasswordNuevo);

        // Selectores modernos
        actvSexoNuevo = view.findViewById(R.id.actvSexoNuevo);
        actvEstadoNuevo = view.findViewById(R.id.actvEstadoNuevo);
        actvCiudadNuevo = view.findViewById(R.id.actvCiudadNuevo);

        // Botón
        btnActualizar = view.findViewById(R.id.btnActualizar);
    }

    private void cargarDatosActuales() {
        // ** SIMULACIÓN: Reemplazar con la lógica real de obtención de datos **

        tvNombreActual.setText("Martín");
        tvApellidoActual.setText("Pérez");
        tvSexoActual.setText("Masculino");
        tvEstadoActual.setText("Jalisco");
        tvCiudadActual.setText("Guadalajara");
        tvUsernameActual.setText("Martinp_entrena");
        // Ocultar la contraseña real con puntos (aunque en la vida real no se guarda)
        tvPasswordActual.setText("••••••••");
    }

    private void configurarSelectores(@NonNull View view) {
        // Los Arrays.xml deben existir en R.array

        // Sexo
        ArrayAdapter<CharSequence> adapterSexo = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.sexo_options,
                android.R.layout.simple_dropdown_item_1line // Layout simple para AutoCompleteTextView
        );
        actvSexoNuevo.setAdapter(adapterSexo);

        // Estado
        ArrayAdapter<CharSequence> adapterEstado = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.estado_options,
                android.R.layout.simple_dropdown_item_1line
        );
        actvEstadoNuevo.setAdapter(adapterEstado);

        // Ciudad (Dependerá del estado seleccionado, aquí usamos uno genérico)
        ArrayAdapter<CharSequence> adapterCiudad = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.ciudad_options, // Usar array de ciudades específico
                android.R.layout.simple_dropdown_item_1line
        );
        actvCiudadNuevo.setAdapter(adapterCiudad);
    }

    private void configurarListeners(@NonNull View view) {

        // Toggle de contraseña actual (solo simulación visual)
        btnTogglePasswordActual.setOnClickListener(v -> togglePasswordVisibilityActual());

        // Botón Actualizar
        btnActualizar.setOnClickListener(v -> actualizarDatosRegistro());
    }

    private void togglePasswordVisibilityActual() {
        if (isPasswordVisible) {
            // Ocultar contraseña
            tvPasswordActual.setText("••••••••");
            btnTogglePasswordActual.setImageResource(R.drawable.ic_ojover); // Reemplazar con el ícono de ojo cerrado si es necesario
        } else {
            // Mostrar contraseña (Simulación - NO RECOMENDADO en producción)
            // Se debería mostrar solo si el usuario confirma su identidad
            tvPasswordActual.setText(tvUsernameActual.getText().toString() + "123"); // Simulación de una contraseña
            btnTogglePasswordActual.setImageResource(R.drawable.ic_ojover); // Ícono de ojo abierto
        }
        isPasswordVisible = !isPasswordVisible;
    }

    private void actualizarDatosRegistro() {
        String nombre = etNombreNuevo.getText().toString().trim();
        String apellido = etApellidoNuevo.getText().toString().trim();
        String username = etUsernameNuevo.getText().toString().trim();
        String password = etPasswordNuevo.getText().toString().trim();
        String sexo = actvSexoNuevo.getText().toString().trim();
        String estado = actvEstadoNuevo.getText().toString().trim();
        String ciudad = actvCiudadNuevo.getText().toString().trim();

        // Validación simple de campos vacíos
        if (nombre.isEmpty() && apellido.isEmpty() && username.isEmpty() &&
                password.isEmpty() && sexo.isEmpty() && estado.isEmpty() && ciudad.isEmpty()) {
            Toast.makeText(getContext(), "Ingresa al menos un campo para actualizar", Toast.LENGTH_LONG).show();
            return;
        }

        // ** Lógica real para enviar los datos a la base de datos o API **

        Toast.makeText(getContext(), "Iniciando proceso de actualización...", Toast.LENGTH_SHORT).show();

        // Después de la actualización exitosa, se podría navegar:
        // Navigation.findNavController(requireView()).popBackStack();
    }
}

