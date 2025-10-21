package com.example.sportine.ui.usuarios.editartarjeta;

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

public class EditarTarjetaFragment extends Fragment {

    private Spinner spinnerPaisNuevo;
    private EditText etFechaCaducidadNuevo;

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

        etFechaCaducidadNuevo = view.findViewById(R.id.etFechaCaducidadNuevo);
        spinnerPaisNuevo = view.findViewById(R.id.spinnerPaisNuevo);

        ImageView btnBack = view.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().onBackPressed();
            }
        });



        ArrayAdapter<CharSequence> adapterPaisNuevo = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.pais_options,
                android.R.layout.simple_spinner_item
        );
        adapterPaisNuevo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPaisNuevo.setAdapter(adapterPaisNuevo);

        // Configurar calendario - Al hacer click en el campo
        etFechaCaducidadNuevo.setOnClickListener(new View.OnClickListener() {
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
        Calendar calendario = Calendar.getInstance();
        int año = calendario.get(Calendar.YEAR);
        int mes = calendario.get(Calendar.MONTH);
        int dia = calendario.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String fechaSeleccionada = String.format("%02d/%02d/%d",
                                dayOfMonth, month + 1, year);
                        etFechaCaducidadNuevo.setText(fechaSeleccionada);
                    }
                },
                año, mes, dia
        );

        datePickerDialog.show();
    }
}