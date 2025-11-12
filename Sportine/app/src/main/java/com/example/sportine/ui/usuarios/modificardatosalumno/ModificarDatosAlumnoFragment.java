package com.example.sportine.ui.usuarios.modificardatosalumno;

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
import androidx.navigation.Navigation;

import com.example.sportine.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class ModificarDatosAlumnoFragment extends Fragment {

    // Selectores modernos (AutoCompleteTextView)
    private AutoCompleteTextView actvSexoNuevo;
    private AutoCompleteTextView actvEstadoNuevo;
    private AutoCompleteTextView actvCiudadNuevo;

    // Datos Actuales (TextViews de solo lectura)
    private TextView tvNombreActual, tvSexoActual, tvApellidoActual, tvEstadoActual, tvUsernameActual, tvCiudadActual;
    private TextView tvPasswordActual;
    private ImageView btnTogglePasswordActual;
    private boolean isPasswordVisible = false; // Estado para el toggle

    // Nuevos Datos (TextInputEditTexts)
    private TextInputEditText etNombreNuevo;
    private TextInputEditText etApellidoNuevo;
    private TextInputEditText etUsernameNuevo;
    private TextInputEditText etPasswordNuevo; // Contraseña nueva

    private MaterialButton btnActualizar;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflar el layout de modificar datos
        return inflater.inflate(R.layout.fragment_alumno_modificar_datos, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Inicializar todos los componentes
        inicializarComponentes(view);

        // 2. Cargar datos de referencia (simulación)
        cargarDatosActuales();

        // 3. Configurar Selectores (el antiguo configurarSpinners)
        configurarSelectores();

        // 4. Configurar listeners
        configurarListeners();
    }

    // --- Métodos de Ayuda ---

    private void inicializarComponentes(@NonNull View view) {
        // Botón Volver
        View btnBack = view.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        // Datos Actuales (TextViews)
        tvNombreActual = view.findViewById(R.id.tvNombreActual);
        tvSexoActual = view.findViewById(R.id.tvSexoActual);
        tvApellidoActual = view.findViewById(R.id.tvApellidoActual);
        tvEstadoActual = view.findViewById(R.id.tvEstadoActual);
        tvUsernameActual = view.findViewById(R.id.tvUsernameActual);
        tvCiudadActual = view.findViewById(R.id.tvCiudadActual);
        tvPasswordActual = view.findViewById(R.id.tvPasswordActual);
        btnTogglePasswordActual = view.findViewById(R.id.btnTogglePasswordActual);

        // Nuevos Datos (TextInputEditTexts)
        etNombreNuevo = view.findViewById(R.id.etNombreNuevo);
        etApellidoNuevo = view.findViewById(R.id.etApellidoNuevo);
        etUsernameNuevo = view.findViewById(R.id.etUsernameNuevo);
        etPasswordNuevo = view.findViewById(R.id.etPasswordNuevo);

        // Selectores modernos (AutoCompleteTextViews)
        actvSexoNuevo = view.findViewById(R.id.actvSexoNuevo);
        actvEstadoNuevo = view.findViewById(R.id.actvEstadoNuevo);
        actvCiudadNuevo = view.findViewById(R.id.actvCiudadNuevo);

        // Botón
        btnActualizar = view.findViewById(R.id.btnActualizar);
    }

    private void cargarDatosActuales() {
        // ** SIMULACIÓN: Reemplazar con la lógica real de obtención de datos del Alumno **
        tvNombreActual.setText("Ana");
        tvApellidoActual.setText("García");
        tvSexoActual.setText("Femenino");
        tvEstadoActual.setText("Nuevo León");
        tvCiudadActual.setText("Monterrey");
        tvUsernameActual.setText("AnaG_alumno");
        tvPasswordActual.setText("••••••••"); // Siempre ocultar contraseña en UI
    }

    private void configurarSelectores() {
        // Sexo
        ArrayAdapter<CharSequence> adapterSexo = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.sexo_options,
                android.R.layout.simple_dropdown_item_1line
        );
        actvSexoNuevo.setAdapter(adapterSexo);

        // Estado
        ArrayAdapter<CharSequence> adapterEstado = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.estado_options,
                android.R.layout.simple_dropdown_item_1line
        );
        actvEstadoNuevo.setAdapter(adapterEstado);

        // Ciudad
        ArrayAdapter<CharSequence> adapterCiudad = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.ciudad_options,
                android.R.layout.simple_dropdown_item_1line
        );
        actvCiudadNuevo.setAdapter(adapterCiudad);
    }

    private void configurarListeners() {
        // Toggle de contraseña actual
        btnTogglePasswordActual.setOnClickListener(v -> togglePasswordVisibilityActual());

        // Botón Actualizar
        btnActualizar.setOnClickListener(v -> actualizarDatos());
    }

    private void togglePasswordVisibilityActual() {
        if (isPasswordVisible) {
            // Ocultar contraseña
            tvPasswordActual.setText("••••••••");
            // Aquí puedes cambiar el ícono del ojo a cerrado
        } else {
            // Mostrar contraseña (Usar con cautela)
            tvPasswordActual.setText("contrasenaAlumno123");
            // Aquí puedes cambiar el ícono del ojo a abierto
        }
        isPasswordVisible = !isPasswordVisible;
    }

    private void actualizarDatos() {
        String nombre = etNombreNuevo.getText().toString().trim();
        String apellido = etApellidoNuevo.getText().toString().trim();
        String username = etUsernameNuevo.getText().toString().trim();
        String password = etPasswordNuevo.getText().toString().trim();
        String sexo = actvSexoNuevo.getText().toString().trim();
        String estado = actvEstadoNuevo.getText().toString().trim();
        String ciudad = actvCiudadNuevo.getText().toString().trim();

        // Validación: Asegurarse de que al menos un campo tenga datos
        if (nombre.isEmpty() && apellido.isEmpty() && username.isEmpty() &&
                password.isEmpty() && sexo.isEmpty() && estado.isEmpty() && ciudad.isEmpty()) {
            Toast.makeText(getContext(), "Ingresa al menos un campo para actualizar", Toast.LENGTH_LONG).show();
            return;
        }

        // ** Lógica para enviar los datos a la base de datos o API **

        Toast.makeText(getContext(), "Datos de Alumno actualizados con éxito.", Toast.LENGTH_SHORT).show();

        // Navegar de vuelta después de la actualización exitosa
        Navigation.findNavController(requireView()).navigateUp();
    }
}