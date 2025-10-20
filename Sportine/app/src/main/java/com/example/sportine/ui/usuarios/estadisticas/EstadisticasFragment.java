package com.example.sportine.ui.usuarios.estadisticas;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.sportine.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;
import java.util.Arrays;

public class EstadisticasFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_alumno_estadisticas, container, false);

        // Contenedor donde agregaremos las tarjetas
        LinearLayout contenedorGraficas = root.findViewById(R.id.contenedor_graficas);

        if (contenedorGraficas != null) {
            // Crear las 3 tarjetas con diferentes gráficas
            agregarGraficaBarras(contenedorGraficas);
            agregarGraficaLineas(contenedorGraficas);
            agregarGraficaPastel(contenedorGraficas);
        }

        return root;
    }

    // GRÁFICA 1: Canastas encestadas por día (BarChart)
    private void agregarGraficaBarras(LinearLayout contenedor) {
        View cardView = LayoutInflater.from(getContext()).inflate(R.layout.item_alumno_estadistica_card, contenedor, false);

        cardView.findViewById(R.id.text_titulo_stat).setVisibility(View.VISIBLE);
        ((android.widget.TextView) cardView.findViewById(R.id.text_titulo_stat)).setText("Canastas encestadas");
        ((android.widget.TextView) cardView.findViewById(R.id.text_periodo)).setText("Esta semana");

        BarChart barChart = cardView.findViewById(R.id.barChart);
        barChart.setVisibility(View.VISIBLE);

        // Datos de ejemplo
        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0f, 12f));
        entries.add(new BarEntry(1f, 18f));
        entries.add(new BarEntry(2f, 15f));
        entries.add(new BarEntry(3f, 22f));
        entries.add(new BarEntry(4f, 20f));
        entries.add(new BarEntry(5f, 25f));
        entries.add(new BarEntry(6f, 19f));

        BarDataSet dataSet = new BarDataSet(entries, "Canastas");
        dataSet.setColor(Color.parseColor("#3B82F6"));
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(10f);

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.8f);

        barChart.setData(barData);
        barChart.setFitBars(true);

        // Configuración del eje X
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(
                Arrays.asList("Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom")
        ));

        barChart.getAxisRight().setEnabled(false);
        barChart.getAxisLeft().setAxisMinimum(0f);

        Description desc = new Description();
        desc.setText("");
        barChart.setDescription(desc);

        barChart.animateY(1000);
        barChart.invalidate();

        contenedor.addView(cardView);
    }

    // GRÁFICA 2: Evolución de triples (LineChart)
    private void agregarGraficaLineas(LinearLayout contenedor) {
        View cardView = LayoutInflater.from(getContext()).inflate(R.layout.item_alumno_estadistica_card, contenedor, false);

        ((android.widget.TextView) cardView.findViewById(R.id.text_titulo_stat)).setText("Triples convertidos");
        ((android.widget.TextView) cardView.findViewById(R.id.text_periodo)).setText("Mensual");

        LineChart lineChart = cardView.findViewById(R.id.lineChart);
        lineChart.setVisibility(View.VISIBLE);

        // Datos de ejemplo
        ArrayList<Entry> entries = new ArrayList<>();
        entries.add(new Entry(0f, 5f));
        entries.add(new Entry(1f, 8f));
        entries.add(new Entry(2f, 6f));
        entries.add(new Entry(3f, 10f));
        entries.add(new Entry(4f, 12f));
        entries.add(new Entry(5f, 9f));
        entries.add(new Entry(6f, 15f));

        LineDataSet dataSet = new LineDataSet(entries, "Triples por semana");
        dataSet.setColor(Color.parseColor("#10B981"));
        dataSet.setCircleColor(Color.parseColor("#10B981"));
        dataSet.setLineWidth(3f);
        dataSet.setCircleRadius(5f);
        dataSet.setDrawCircleHole(false);
        dataSet.setValueTextSize(10f);
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(Color.parseColor("#10B981"));
        dataSet.setFillAlpha(50);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);

        // Configuración del eje X
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(
                Arrays.asList("S1", "S2", "S3", "S4", "S5", "S6", "S7")
        ));

        lineChart.getAxisRight().setEnabled(false);
        lineChart.getAxisLeft().setAxisMinimum(0f);

        Description desc = new Description();
        desc.setText("");
        lineChart.setDescription(desc);

        lineChart.animateX(1200);
        lineChart.invalidate();

        contenedor.addView(cardView);
    }

    // GRÁFICA 3: Distribución de tiros (PieChart)
    private void agregarGraficaPastel(LinearLayout contenedor) {
        View cardView = LayoutInflater.from(getContext()).inflate(R.layout.item_alumno_estadistica_card, contenedor, false);

        ((android.widget.TextView) cardView.findViewById(R.id.text_titulo_stat)).setText("Efectividad de tiros");
        ((android.widget.TextView) cardView.findViewById(R.id.text_periodo)).setText("Este mes");

        PieChart pieChart = cardView.findViewById(R.id.pieChart);
        pieChart.setVisibility(View.VISIBLE);

        // Datos de ejemplo
        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(45f, "Canastas (45%)"));
        entries.add(new PieEntry(30f, "Triples (30%)"));
        entries.add(new PieEntry(15f, "Tiros libres (15%)"));
        entries.add(new PieEntry(10f, "Fallados (10%)"));

        PieDataSet dataSet = new PieDataSet(entries, "");

        // Colores personalizados
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.parseColor("#3B82F6")); // Azul
        colors.add(Color.parseColor("#10B981")); // Verde
        colors.add(Color.parseColor("#F59E0B")); // Amarillo
        colors.add(Color.parseColor("#EF4444")); // Rojo
        dataSet.setColors(colors);

        dataSet.setValueTextSize(12f);
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setSliceSpace(2f);

        PieData pieData = new PieData(dataSet);
        pieChart.setData(pieData);

        // Configuración visual
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setHoleRadius(40f);
        pieChart.setTransparentCircleRadius(45f);
        pieChart.setCenterText("Efectividad\nGeneral");
        pieChart.setCenterTextSize(14f);
        pieChart.setDrawEntryLabels(false);

        Legend legend = pieChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);
        legend.setTextSize(11f);

        Description desc = new Description();
        desc.setText("");
        pieChart.setDescription(desc);

        pieChart.animateY(1400);
        pieChart.invalidate();

        contenedor.addView(cardView);
    }
}