package com.example.sportine.ui.usuarios.editartarjeta;

import android.app.AlertDialog;
import android.content.Context;
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
import androidx.navigation.Navigation;

import com.example.sportine.R;
import com.example.sportine.utils.MonthYearPickerDialog; // ASEGÚRATE DE QUE EL PAQUETE SEA CORRECTO

import java.util.Calendar;

public class EditarTarjetaFragment extends Fragment {

    private Spinner spinnerPaisNuevo;
    private EditText etFechaCaducidadNuevo;
    private EditText etNumeroTarjetaNuevo, etDireccionNuevo, etLocalidadNuevo;
    private EditText etNombreTitularNuevo, etCodigoPostalNuevo, etApellidosTitularNuevo, etTelefonoNuevo;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_alumno_editar_tarjeta, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Inicializar componentes
        inicializarComponentes(view);

        // 2. Configurar botón de Volver
        View btnBack = view.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        // 3. Configurar Spinner de País
        configurarSpinnerPais();

        // 4. Configurar selección de Fecha (solo Mes y Año)
        configurarSelectorFecha(view); // Pasamos 'view' para buscar el icono si existe

        // 5. Configurar botón Actualizar
        Button btnActualizar = view.findViewById(R.id.btnActualizar);
        btnActualizar.setOnClickListener(v -> actualizarDatosTarjeta(view));
    }

    private void inicializarComponentes(@NonNull View view) {
        etFechaCaducidadNuevo = view.findViewById(R.id.etFechaCaducidadNuevo);
        etNumeroTarjetaNuevo = view.findViewById(R.id.etNumeroTarjetaNuevo);
        etDireccionNuevo = view.findViewById(R.id.etDireccionNuevo);
        etLocalidadNuevo = view.findViewById(R.id.etLocalidadNuevo);
        etNombreTitularNuevo = view.findViewById(R.id.etNombreTitularNuevo);
        etCodigoPostalNuevo = view.findViewById(R.id.etCodigoPostalNuevo);
        etApellidosTitularNuevo = view.findViewById(R.id.etApellidosTitularNuevo);
        etTelefonoNuevo = view.findViewById(R.id.etTelefonoNuevo);
        spinnerPaisNuevo = view.findViewById(R.id.spinnerPaisNuevo);
    }

    private void configurarSpinnerPais() {
        ArrayAdapter<CharSequence> adapterPaisNuevo = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.pais_options,
                android.R.layout.simple_spinner_item
        );
        adapterPaisNuevo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPaisNuevo.setAdapter(adapterPaisNuevo);
    }

    private void configurarSelectorFecha(View view) {
        // Es suficiente con asignar el listener al EditText.
        // El TextInputLayout garantiza que el clic en el área del campo funcione.
        etFechaCaducidadNuevo.setOnClickListener(v -> mostrarDatePicker());

    }

    private void mostrarDatePicker() {
        MonthYearPickerDialog pd = new MonthYearPickerDialog();
        pd.setListener((view, year, month, dayOfMonth) -> {
            // month es 0-index, por eso usamos month + 1
            String fechaSeleccionada = String.format("%02d/%02d", month + 1, year % 100);
            etFechaCaducidadNuevo.setText(fechaSeleccionada);
        });
        pd.show(getChildFragmentManager(), "MonthYearPickerDialog");
    }

    private void actualizarDatosTarjeta(View view) {
        // 1. Recolectar datos
        String numeroNuevo = etNumeroTarjetaNuevo.getText().toString().trim();
        String direccionNueva = etDireccionNuevo.getText().toString().trim();
        String fechaCadNuevo = etFechaCaducidadNuevo.getText().toString().trim();
        String localidadNueva = etLocalidadNuevo.getText().toString().trim();
        String nombreNuevo = etNombreTitularNuevo.getText().toString().trim();
        String codigoPostalNuevo = etCodigoPostalNuevo.getText().toString().trim();
        String apellidosNuevo = etApellidosTitularNuevo.getText().toString().trim();
        String telefonoNuevo = etTelefonoNuevo.getText().toString().trim();

        String paisNuevo = spinnerPaisNuevo.getSelectedItem().toString();
        boolean isPaisChanged = !paisNuevo.equals(getResources().getStringArray(R.array.pais_options)[0]);

        // 2. Validación de campos vacíos (al menos uno debe estar lleno)
        boolean hayDatosNuevos = !numeroNuevo.isEmpty() || !direccionNueva.isEmpty() || !fechaCadNuevo.isEmpty() ||
                !localidadNueva.isEmpty() || !nombreNuevo.isEmpty() || !codigoPostalNuevo.isEmpty() ||
                !apellidosNuevo.isEmpty() || !telefonoNuevo.isEmpty() || isPaisChanged;

        if (!hayDatosNuevos) {
            Toast.makeText(requireContext(), "Debes ingresar al menos un dato nuevo para actualizar la tarjeta.", Toast.LENGTH_LONG).show();
            return;
        }

        // 3. Validación de formato (Ejemplo: Número de tarjeta y Fecha de caducidad)
        if (!numeroNuevo.isEmpty() && (numeroNuevo.length() < 13 || numeroNuevo.length() > 16)) {
            etNumeroTarjetaNuevo.setError("El número de tarjeta es inválido.");
            return;
        }

        if (!fechaCadNuevo.isEmpty() && !esFechaCaducidadValida(fechaCadNuevo)) {
            etFechaCaducidadNuevo.setError("La fecha de caducidad debe ser MM/AA y no debe haber expirado.");
            return;
        }


        // 4. Procesamiento (Llamada a tu API/Base de Datos aquí)

        // Simulación de éxito
        Toast.makeText(requireContext(), "Datos de la tarjeta actualizados exitosamente.", Toast.LENGTH_SHORT).show();

        // Regresar a la pantalla anterior
        Navigation.findNavController(view).navigateUp();
    }

    private boolean esFechaCaducidadValida(String fecha) {
        // Formato esperado: MM/AA (e.g., 03/27)
        if (!fecha.matches("\\d{2}/\\d{2}")) return false;

        try {
            String[] partes = fecha.split("/");
            int mes = Integer.parseInt(partes[0]);
            int año = 2000 + Integer.parseInt(partes[1]);

            if (mes < 1 || mes > 12) return false;

            Calendar ahora = Calendar.getInstance();
            int mesActual = ahora.get(Calendar.MONTH) + 1;
            int añoActual = ahora.get(Calendar.YEAR);

            // Verificar si ha expirado
            if (año < añoActual || (año == añoActual && mes < mesActual)) {
                return false;
            }
            return true;

        } catch (Exception e) {
            return false;
        }
    }
}