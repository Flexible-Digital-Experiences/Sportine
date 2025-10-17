package com.example.sportine.ui.Agregarformapago;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.sportine.R;
import java.util.Calendar;

public class AgregarformapagoFragment extends Fragment {

    private Spinner spinnerPais;
    private EditText etFechaCaducidad;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_agregarformapago, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Botón para volver atrás
        ImageView btnBack = view.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().onBackPressed();
            }
        });

        // Inicializar vistas
        etFechaCaducidad = view.findViewById(R.id.etFechaCaducidad);
        spinnerPais = view.findViewById(R.id.spinnerPais);

        // Configurar Spinner de País
        ArrayAdapter<CharSequence> adapterPais = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.pais_options,
                android.R.layout.simple_spinner_item
        );
        adapterPais.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPais.setAdapter(adapterPais);

        // Configurar calendario - Al hacer click en el campo de fecha
        etFechaCaducidad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDatePicker();
            }
        });

        // Configurar calendario - Al hacer click en el ícono
        ImageView btnCalendario = view.findViewById(R.id.btnCalendario);
        btnCalendario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDatePicker();
            }
        });
    }

    private void mostrarDatePicker() {
        // Obtener fecha actual
        Calendar calendario = Calendar.getInstance();
        int año = calendario.get(Calendar.YEAR);
        int mes = calendario.get(Calendar.MONTH);
        int dia = calendario.get(Calendar.DAY_OF_MONTH);

        // Crear y mostrar DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        // Formatear y mostrar la fecha seleccionada
                        String fechaSeleccionada = String.format("%02d/%02d/%d",
                                dayOfMonth, month + 1, year);
                        etFechaCaducidad.setText(fechaSeleccionada);
                    }
                },
                año, mes, dia
        );

        datePickerDialog.show();
    }
}