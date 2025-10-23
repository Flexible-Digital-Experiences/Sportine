package com.example.sportine.ui.usuarios.estadisticas;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
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
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.Arrays;

public class EstadisticasFragment extends Fragment {

    private LinearLayout contenedorGraficas;
    private MaterialCardView cardFutbol, cardBasket, cardTenis;
    private String deporteSeleccionado = "futbol"; // Deporte por defecto

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_alumno_estadisticas, container, false);

        // Referencias a las tarjetas de deportes
        cardFutbol = root.findViewById(R.id.card_deporte_futbol);
        cardBasket = root.findViewById(R.id.card_deporte_basket);
        cardTenis = root.findViewById(R.id.card_deporte_voleibol);

        // Contenedor donde agregaremos las tarjetas
        contenedorGraficas = root.findViewById(R.id.contenedor_graficas);

        // Configurar listeners para las tarjetas de deportes
        configurarClickDeportes();

        // Cargar gráficas del deporte por defecto (fútbol)
        if (contenedorGraficas != null) {
            cargarGraficasDeporte(deporteSeleccionado);
        }

        return root;
    }

    private void configurarClickDeportes() {
        cardFutbol.setOnClickListener(v -> {
            deporteSeleccionado = "futbol";
            actualizarSeleccionDeporte();
            cargarGraficasDeporte(deporteSeleccionado);
        });

        cardBasket.setOnClickListener(v -> {
            deporteSeleccionado = "basket";
            actualizarSeleccionDeporte();
            cargarGraficasDeporte(deporteSeleccionado);
        });

        cardTenis.setOnClickListener(v -> {
            deporteSeleccionado = "tenis";
            actualizarSeleccionDeporte();
            cargarGraficasDeporte(deporteSeleccionado);
        });
    }

    private void actualizarSeleccionDeporte() {
        // Resetear todas las tarjetas
        resetearEstiloTarjeta(cardFutbol);
        resetearEstiloTarjeta(cardBasket);
        resetearEstiloTarjeta(cardTenis);

        // Aplicar estilo a la tarjeta seleccionada
        switch (deporteSeleccionado) {
            case "futbol":
                aplicarEstiloSeleccionado(cardFutbol);
                break;
            case "basket":
                aplicarEstiloSeleccionado(cardBasket);
                break;
            case "tenis":
                aplicarEstiloSeleccionado(cardTenis);
                break;
        }
    }

    private void resetearEstiloTarjeta(MaterialCardView card) {
        card.setStrokeColor(Color.parseColor("#E5E7EB"));
        card.setStrokeWidth(getResources().getDimensionPixelSize(R.dimen.stroke_width_normal));
    }

    private void aplicarEstiloSeleccionado(MaterialCardView card) {
        card.setStrokeColor(Color.parseColor("#3B82F6"));
        card.setStrokeWidth(getResources().getDimensionPixelSize(R.dimen.stroke_width_selected));
    }

    private void cargarGraficasDeporte(String deporte) {
        // Limpiar contenedor
        contenedorGraficas.removeAllViews();

        // Cargar gráficas según el deporte
        switch (deporte) {
            case "futbol":
                agregarGraficasFutbol();
                break;
            case "basket":
                agregarGraficasBasket();
                break;
            case "tenis":
                agregarGraficasTenis();
                break;
        }
    }

    // ========== GRÁFICAS DE FÚTBOL ==========
    private void agregarGraficasFutbol() {
        agregarGraficaBarrasFutbol(contenedorGraficas);
        agregarGraficaLineasFutbol(contenedorGraficas);
        agregarGraficaPastelFutbol(contenedorGraficas);
    }

    private void agregarGraficaBarrasFutbol(LinearLayout contenedor) {
        View cardView = LayoutInflater.from(getContext()).inflate(R.layout.item_alumno_estadistica_card, contenedor, false);

        ((android.widget.TextView) cardView.findViewById(R.id.text_titulo_stat)).setText("Goles anotados");
        ((android.widget.TextView) cardView.findViewById(R.id.text_periodo)).setText("Esta semana");

        BarChart barChart = cardView.findViewById(R.id.barChart);
        barChart.setVisibility(View.VISIBLE);

        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0f, 2f));
        entries.add(new BarEntry(1f, 3f));
        entries.add(new BarEntry(2f, 1f));
        entries.add(new BarEntry(3f, 4f));
        entries.add(new BarEntry(4f, 2f));
        entries.add(new BarEntry(5f, 5f));
        entries.add(new BarEntry(6f, 3f));

        BarDataSet dataSet = new BarDataSet(entries, "Goles");
        dataSet.setColor(Color.parseColor("#10B981"));
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(10f);

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.8f);

        barChart.setData(barData);
        barChart.setFitBars(true);

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

    private void agregarGraficaLineasFutbol(LinearLayout contenedor) {
        View cardView = LayoutInflater.from(getContext()).inflate(R.layout.item_alumno_estadistica_card, contenedor, false);

        ((android.widget.TextView) cardView.findViewById(R.id.text_titulo_stat)).setText("Asistencias");
        ((android.widget.TextView) cardView.findViewById(R.id.text_periodo)).setText("Mensual");

        LineChart lineChart = cardView.findViewById(R.id.lineChart);
        lineChart.setVisibility(View.VISIBLE);

        ArrayList<Entry> entries = new ArrayList<>();
        entries.add(new Entry(0f, 3f));
        entries.add(new Entry(1f, 5f));
        entries.add(new Entry(2f, 4f));
        entries.add(new Entry(3f, 7f));
        entries.add(new Entry(4f, 6f));
        entries.add(new Entry(5f, 8f));
        entries.add(new Entry(6f, 9f));

        LineDataSet dataSet = new LineDataSet(entries, "Asistencias por semana");
        dataSet.setColor(Color.parseColor("#F59E0B"));
        dataSet.setCircleColor(Color.parseColor("#F59E0B"));
        dataSet.setLineWidth(3f);
        dataSet.setCircleRadius(5f);
        dataSet.setDrawCircleHole(false);
        dataSet.setValueTextSize(10f);
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(Color.parseColor("#F59E0B"));
        dataSet.setFillAlpha(50);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);

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

    private void agregarGraficaPastelFutbol(LinearLayout contenedor) {
        View cardView = LayoutInflater.from(getContext()).inflate(R.layout.item_alumno_estadistica_card, contenedor, false);

        ((android.widget.TextView) cardView.findViewById(R.id.text_titulo_stat)).setText("Distribución de tiros");
        ((android.widget.TextView) cardView.findViewById(R.id.text_periodo)).setText("Este mes");

        PieChart pieChart = cardView.findViewById(R.id.pieChart);
        pieChart.setVisibility(View.VISIBLE);

        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(50f, "Efectivos (50%)"));
        entries.add(new PieEntry(30f, "Atajados (30%)"));
        entries.add(new PieEntry(20f, "Fuera (20%)"));

        PieDataSet dataSet = new PieDataSet(entries, "");

        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.parseColor("#10B981"));
        colors.add(Color.parseColor("#F59E0B"));
        colors.add(Color.parseColor("#EF4444"));
        dataSet.setColors(colors);

        dataSet.setValueTextSize(12f);
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setSliceSpace(2f);

        PieData pieData = new PieData(dataSet);
        pieChart.setData(pieData);

        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setHoleRadius(40f);
        pieChart.setTransparentCircleRadius(45f);
        pieChart.setCenterText("Precisión\nde Tiro");
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

    // ========== GRÁFICAS DE BASKET ==========
    private void agregarGraficasBasket() {
        agregarGraficaBarrasBasket(contenedorGraficas);
        agregarGraficaLineasBasket(contenedorGraficas);
        agregarGraficaPastelBasket(contenedorGraficas);
    }

    private void agregarGraficaBarrasBasket(LinearLayout contenedor) {
        View cardView = LayoutInflater.from(getContext()).inflate(R.layout.item_alumno_estadistica_card, contenedor, false);

        ((android.widget.TextView) cardView.findViewById(R.id.text_titulo_stat)).setText("Canastas encestadas");
        ((android.widget.TextView) cardView.findViewById(R.id.text_periodo)).setText("Esta semana");

        BarChart barChart = cardView.findViewById(R.id.barChart);
        barChart.setVisibility(View.VISIBLE);

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

    private void agregarGraficaLineasBasket(LinearLayout contenedor) {
        View cardView = LayoutInflater.from(getContext()).inflate(R.layout.item_alumno_estadistica_card, contenedor, false);

        ((android.widget.TextView) cardView.findViewById(R.id.text_titulo_stat)).setText("Triples convertidos");
        ((android.widget.TextView) cardView.findViewById(R.id.text_periodo)).setText("Mensual");

        LineChart lineChart = cardView.findViewById(R.id.lineChart);
        lineChart.setVisibility(View.VISIBLE);

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

    private void agregarGraficaPastelBasket(LinearLayout contenedor) {
        View cardView = LayoutInflater.from(getContext()).inflate(R.layout.item_alumno_estadistica_card, contenedor, false);

        ((android.widget.TextView) cardView.findViewById(R.id.text_titulo_stat)).setText("Efectividad de tiros");
        ((android.widget.TextView) cardView.findViewById(R.id.text_periodo)).setText("Este mes");

        PieChart pieChart = cardView.findViewById(R.id.pieChart);
        pieChart.setVisibility(View.VISIBLE);

        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(45f, "Canastas (45%)"));
        entries.add(new PieEntry(30f, "Triples (30%)"));
        entries.add(new PieEntry(15f, "Tiros libres (15%)"));
        entries.add(new PieEntry(10f, "Fallados (10%)"));

        PieDataSet dataSet = new PieDataSet(entries, "");

        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.parseColor("#3B82F6"));
        colors.add(Color.parseColor("#10B981"));
        colors.add(Color.parseColor("#F59E0B"));
        colors.add(Color.parseColor("#EF4444"));
        dataSet.setColors(colors);

        dataSet.setValueTextSize(12f);
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setSliceSpace(2f);

        PieData pieData = new PieData(dataSet);
        pieChart.setData(pieData);

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

    // ========== GRÁFICAS DE TENIS ==========
    private void agregarGraficasTenis() {
        agregarGraficaBarrasTenis(contenedorGraficas);
        agregarGraficaLineasTenis(contenedorGraficas);
        agregarGraficaPastelTenis(contenedorGraficas);
    }

    private void agregarGraficaBarrasTenis(LinearLayout contenedor) {
        View cardView = LayoutInflater.from(getContext()).inflate(R.layout.item_alumno_estadistica_card, contenedor, false);

        ((android.widget.TextView) cardView.findViewById(R.id.text_titulo_stat)).setText("Aces por partido");
        ((android.widget.TextView) cardView.findViewById(R.id.text_periodo)).setText("Esta semana");

        BarChart barChart = cardView.findViewById(R.id.barChart);
        barChart.setVisibility(View.VISIBLE);

        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0f, 8f));
        entries.add(new BarEntry(1f, 12f));
        entries.add(new BarEntry(2f, 10f));
        entries.add(new BarEntry(3f, 15f));
        entries.add(new BarEntry(4f, 11f));
        entries.add(new BarEntry(5f, 14f));
        entries.add(new BarEntry(6f, 13f));

        BarDataSet dataSet = new BarDataSet(entries, "Aces");
        dataSet.setColor(Color.parseColor("#8B5CF6"));
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(10f);

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.8f);

        barChart.setData(barData);
        barChart.setFitBars(true);

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

    private void agregarGraficaLineasTenis(LinearLayout contenedor) {
        View cardView = LayoutInflater.from(getContext()).inflate(R.layout.item_alumno_estadistica_card, contenedor, false);

        ((android.widget.TextView) cardView.findViewById(R.id.text_titulo_stat)).setText("Partidos ganados");
        ((android.widget.TextView) cardView.findViewById(R.id.text_periodo)).setText("Mensual");

        LineChart lineChart = cardView.findViewById(R.id.lineChart);
        lineChart.setVisibility(View.VISIBLE);

        ArrayList<Entry> entries = new ArrayList<>();
        entries.add(new Entry(0f, 2f));
        entries.add(new Entry(1f, 3f));
        entries.add(new Entry(2f, 2f));
        entries.add(new Entry(3f, 4f));
        entries.add(new Entry(4f, 5f));
        entries.add(new Entry(5f, 4f));
        entries.add(new Entry(6f, 6f));

        LineDataSet dataSet = new LineDataSet(entries, "Victorias por semana");
        dataSet.setColor(Color.parseColor("#EC4899"));
        dataSet.setCircleColor(Color.parseColor("#EC4899"));
        dataSet.setLineWidth(3f);
        dataSet.setCircleRadius(5f);
        dataSet.setDrawCircleHole(false);
        dataSet.setValueTextSize(10f);
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(Color.parseColor("#EC4899"));
        dataSet.setFillAlpha(50);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);

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

    private void agregarGraficaPastelTenis(LinearLayout contenedor) {
        View cardView = LayoutInflater.from(getContext()).inflate(R.layout.item_alumno_estadistica_card, contenedor, false);

        ((android.widget.TextView) cardView.findViewById(R.id.text_titulo_stat)).setText("Tipos de golpe");
        ((android.widget.TextView) cardView.findViewById(R.id.text_periodo)).setText("Este mes");

        PieChart pieChart = cardView.findViewById(R.id.pieChart);
        pieChart.setVisibility(View.VISIBLE);

        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(35f, "Derechas (35%)"));
        entries.add(new PieEntry(30f, "Revés (30%)"));
        entries.add(new PieEntry(20f, "Volea (20%)"));
        entries.add(new PieEntry(15f, "Remate (15%)"));

        PieDataSet dataSet = new PieDataSet(entries, "");

        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.parseColor("#8B5CF6"));
        colors.add(Color.parseColor("#EC4899"));
        colors.add(Color.parseColor("#F59E0B"));
        colors.add(Color.parseColor("#10B981"));
        dataSet.setColors(colors);

        dataSet.setValueTextSize(12f);
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setSliceSpace(2f);

        PieData pieData = new PieData(dataSet);
        pieChart.setData(pieData);

        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setHoleRadius(40f);
        pieChart.setTransparentCircleRadius(45f);
        pieChart.setCenterText("Distribución\nde Golpes");
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