package com.example.sportine.utils;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import java.util.Calendar;

public class MonthYearPickerDialog extends DialogFragment {

    private DatePickerDialog.OnDateSetListener listener;

    public void setListener(DatePickerDialog.OnDateSetListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH); // Se requiere para inicializar, aunque se oculta

        DatePickerDialog dialog = new DatePickerDialog(requireContext(), AlertDialog.THEME_HOLO_LIGHT, listener, year, month, day);

        // Ocultar el campo de día (funciona en la mayoría de implementaciones de Android)
        try {
            View dayPicker = dialog.getDatePicker().findViewById(
                    getResources().getIdentifier("day", "id", "android")
            );
            if (dayPicker != null) {
                dayPicker.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            // Manejar la excepción si no se puede encontrar el día, pero permitir que el diálogo se muestre
            e.printStackTrace();
        }

        return dialog;
    }
}
