package com.example.sportine.ui.usuarios.dashboard;


import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.sportine.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;

public class EstadisticsFragment extends Fragment {

    private BarChart barChart;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_alumno_estadisticas, container, false);

        barChart = root.findViewById(R.id.barChart);
        mostrarGraficaEjemplo();

        return root;
    }

    private void mostrarGraficaEjemplo() {
        // Datos de ejemplo
        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0, 10));
        entries.add(new BarEntry(1, 20));
        entries.add(new BarEntry(2, 15));
        entries.add(new BarEntry(3, 30));
        entries.add(new BarEntry(4, 25));

        BarDataSet dataSet = new BarDataSet(entries, "Rendimiento semanal");
        dataSet.setColors(Color.parseColor("#3B82F6"));
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(12f);

        BarData data = new BarData(dataSet);
        barChart.setData(data);

        // Personalización visual
        Description description = new Description();
        description.setText("Ejemplo de rendimiento (Frontend)");
        barChart.setDescription(description);

        barChart.animateY(1000);
        barChart.invalidate(); // Refresca la gráfica
    }
}

