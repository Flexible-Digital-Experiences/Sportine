package com.example.sportine.ui.usuarios.agregarformapago;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation; // Usado para el botón Volver

import com.example.sportine.R;
// Importa la clase auxiliar de fecha que ya debes haber creado
import com.example.sportine.utils.MonthYearPickerDialog;

import java.util.Calendar;

public class AgregarformapagoFragment extends Fragment {

    // Se inicializan todos los campos que están en el XML moderno
    private Spinner spinnerPais;
    private EditText etFechaCaducidad;
    private EditText etNumeroTarjeta, etCVC, etNombreTitular, etApellidosTitular;
    private EditText etDireccionFacturacion, etLocalidad, etCodigoPostal, etTelefono;
    private Button btnAgregarTarjeta;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Asegúrate de que este R.layout.fragment_alumno_agregar_forma_pago es el correcto
        return inflater.inflate(R.layout.fragment_alumno_agregar_forma_pago, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Inicializar todas las vistas
        inicializarComponentes(view);

        // 2. Configurar botón de Volver
        View btnBack = view.findViewById(R.id.btnBack);
        // Usamos el Navigation Component para volver a la pantalla anterior
        btnBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        // 3. Configurar Spinner de País
        configurarSpinnerPais();

        // 4. Configurar selección de Fecha (solo Mes y Año)
        configurarSelectorFecha(view);

        // 5. Configurar botón Agregar Tarjeta
        btnAgregarTarjeta.setOnClickListener(v -> agregarTarjeta(view));
    }

    // --- Lógica de Inicialización ---

    private void inicializarComponentes(@NonNull View view) {
        // Inicializar campos de entrada
        etFechaCaducidad = view.findViewById(R.id.etFechaCaducidad);
        spinnerPais = view.findViewById(R.id.spinnerPais);
        etNumeroTarjeta = view.findViewById(R.id.etNumeroTarjeta);
        etCVC = view.findViewById(R.id.etCVC);
        etNombreTitular = view.findViewById(R.id.etNombreTitular);
        etApellidosTitular = view.findViewById(R.id.etApellidosTitular);
        etDireccionFacturacion = view.findViewById(R.id.etDireccionFacturacion);
        etLocalidad = view.findViewById(R.id.etLocalidad);
        etCodigoPostal = view.findViewById(R.id.etCodigoPostal);
        etTelefono = view.findViewById(R.id.etTelefono);

        // Inicializar botón
        btnAgregarTarjeta = view.findViewById(R.id.btnAgregarTarjeta);
    }

    private void configurarSpinnerPais() {
        // Asegúrate de tener el array 'pais_options' en strings.xml
        ArrayAdapter<CharSequence> adapterPais = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.pais_options,
                android.R.layout.simple_spinner_item
        );
        adapterPais.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPais.setAdapter(adapterPais);
    }

    // --- Lógica de Fecha (Usando MonthYearPickerDialog) ---

    private void configurarSelectorFecha(View view) {
        // El EditText maneja el clic
        etFechaCaducidad.setOnClickListener(v -> mostrarDatePicker());
    }

    private void mostrarDatePicker() {
        // Usa la clase auxiliar para seleccionar solo Mes y Año (MM/AA)
        MonthYearPickerDialog pd = new MonthYearPickerDialog();
        pd.setListener((view, year, month, dayOfMonth) -> {
            // Formatea a MM/AA
            String fechaSeleccionada = String.format("%02d/%02d", month + 1, year % 100);
            etFechaCaducidad.setText(fechaSeleccionada);
        });
        pd.show(getChildFragmentManager(), "MonthYearPickerDialog");
    }

    // --- Lógica de Procesamiento y Validación ---

    private void agregarTarjeta(View view) {
        // 1. Recolectar datos
        String numero = etNumeroTarjeta.getText().toString().trim();
        String cvc = etCVC.getText().toString().trim();
        String fechaCad = etFechaCaducidad.getText().toString().trim();
        String nombre = etNombreTitular.getText().toString().trim();
        String apellidos = etApellidosTitular.getText().toString().trim();
        String direccion = etDireccionFacturacion.getText().toString().trim();
        String localidad = etLocalidad.getText().toString().trim();
        String codigoPostal = etCodigoPostal.getText().toString().trim();
        String telefono = etTelefono.getText().toString().trim();
        String pais = spinnerPais.getSelectedItem().toString();

        boolean hayError = false;

        // 2. Validación de campos (Ajusta la validación de obligatorio según tus requisitos)

        if (numero.isEmpty() || numero.length() < 13 || numero.length() > 16) {
            etNumeroTarjeta.setError("Número de tarjeta inválido (13-16 dígitos).");
            hayError = true;
        }
        if (cvc.isEmpty() || cvc.length() < 3 || cvc.length() > 4) {
            etCVC.setError("CVC inválido (3 o 4 dígitos).");
            hayError = true;
        }
        if (fechaCad.isEmpty() || !esFechaCaducidadValida(fechaCad)) {
            etFechaCaducidad.setError("Fecha de caducidad inválida/expirada (MM/AA).");
            hayError = true;
        }
        if (nombre.isEmpty()) {
            etNombreTitular.setError("Nombre del titular requerido.");
            hayError = true;
        }
        if (apellidos.isEmpty()) {
            etApellidosTitular.setError("Apellidos del titular requeridos.");
            hayError = true;
        }
        if (pais.equals(getResources().getStringArray(R.array.pais_options)[0])) {
            Toast.makeText(requireContext(), "Por favor, selecciona un País.", Toast.LENGTH_SHORT).show();
            hayError = true;
        }

        if (hayError) {
            Toast.makeText(requireContext(), "🚨 Revisa los campos con errores.", Toast.LENGTH_LONG).show();
            return;
        }

        // 3. Si no hay errores, procede a agregar la tarjeta.

        // ** Aquí va la lógica real para guardar los datos en tu base de datos o API **

        // Simulación de éxito
        Toast.makeText(requireContext(), "✅ Tarjeta agregada exitosamente.", Toast.LENGTH_SHORT).show();

        // Regresar a la pantalla anterior
        Navigation.findNavController(view).navigateUp();
    }

    // Función para validar que la fecha no haya expirado y tenga el formato MM/AA
    private boolean esFechaCaducidadValida(String fecha) {
        if (!fecha.matches("\\d{2}/\\d{2}")) return false;

        try {
            String[] partes = fecha.split("/");
            int mes = Integer.parseInt(partes[0]);
            int año = 2000 + Integer.parseInt(partes[1]);

            if (mes < 1 || mes > 12) return false;

            Calendar ahora = Calendar.getInstance();
            int mesActual = ahora.get(Calendar.MONTH) + 1;
            int añoActual = ahora.get(Calendar.YEAR);

            if (año < añoActual || (año == añoActual && mes < mesActual)) {
                return false; // Ha expirado
            }
            return true;

        } catch (Exception e) {
            return false;
        }
    }
}