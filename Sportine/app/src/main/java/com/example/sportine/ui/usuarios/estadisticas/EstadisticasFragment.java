package com.example.sportine.ui.usuarios.estadisticas;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.sportine.R;
import com.example.sportine.data.ApiService;
import com.example.sportine.data.RetrofitClient;
import com.example.sportine.models.SportsDistributionDTO;
import com.example.sportine.models.StatisticsOverviewDTO;
import com.example.sportine.models.TrainingFrequencyDTO;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment para mostrar las estadísticas del alumno.
 * Incluye resumen general, gráfica de frecuencia y gráfica de deportes.
 */
public class EstadisticasFragment extends Fragment {

    private static final String TAG = "EstadisticasFragment";

    // ViewModel
    private EstadisticasViewModel viewModel;

    // Views - Cards de resumen
    private TextView textTotalEntrenamientos;
    private TextView textRachaActual;
    private TextView textEntrenamientosMes;
    private TextView textTendencia;

    // Views - Gráficas
    private BarChart chartFrecuencia;
    private PieChart chartDeportes;

    // Views - Estados
    private View layoutLoading;
    private View layoutContent;
    private View layoutError;
    private TextView textError;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_alumno_estadisticas, container, false);

        // Inicializar ViewModel
        viewModel = new ViewModelProvider(this).get(EstadisticasViewModel.class);

        // Inicializar ApiService en el ViewModel
        ApiService apiService = RetrofitClient.getClient(requireContext()).create(ApiService.class);
        viewModel.inicializarApiService(apiService);

        // Inicializar vistas
        initViews(root);

        // Configurar gráficas
        setupCharts();

        // Observar LiveData
        observeViewModel();

        // Cargar datos
        viewModel.cargarEstadisticasCompletas();

        return root;
    }

    /**
     * Inicializa todas las vistas del layout.
     */
    private void initViews(View root) {
        // Cards de resumen
        textTotalEntrenamientos = root.findViewById(R.id.text_total_entrenamientos);
        textRachaActual = root.findViewById(R.id.text_racha_actual);
        textEntrenamientosMes = root.findViewById(R.id.text_entrenamientos_mes);
        textTendencia = root.findViewById(R.id.text_tendencia);

        // Gráficas
        chartFrecuencia = root.findViewById(R.id.chart_frecuencia);
        chartDeportes = root.findViewById(R.id.chart_deportes);

        // Estados
        layoutLoading = root.findViewById(R.id.layout_loading);
        layoutContent = root.findViewById(R.id.layout_content);
        layoutError = root.findViewById(R.id.layout_error);
        textError = root.findViewById(R.id.text_error);
    }

    /**
     * Configura las gráficas con estilos iniciales.
     */
    private void setupCharts() {
        // Configurar gráfica de barras (frecuencia)
        chartFrecuencia.getDescription().setEnabled(false);
        chartFrecuencia.setDrawGridBackground(false);
        chartFrecuencia.setDrawBarShadow(false);
        chartFrecuencia.setHighlightFullBarEnabled(false);
        chartFrecuencia.setPinchZoom(false);
        chartFrecuencia.setScaleEnabled(false);

        // Eje X
        XAxis xAxis = chartFrecuencia.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);

        // Eje Y izquierdo
        YAxis leftAxis = chartFrecuencia.getAxisLeft();
        leftAxis.setDrawGridLines(true);
        leftAxis.setGranularity(1f);
        leftAxis.setAxisMinimum(0f);

        // Eje Y derecho (desactivar)
        chartFrecuencia.getAxisRight().setEnabled(false);

        // Configurar gráfica de pastel (deportes)
        chartDeportes.getDescription().setEnabled(false);
        chartDeportes.setDrawHoleEnabled(true);
        chartDeportes.setHoleColor(Color.WHITE);
        chartDeportes.setHoleRadius(50f);
        chartDeportes.setTransparentCircleRadius(55f);
        chartDeportes.setDrawCenterText(true);
        chartDeportes.setCenterTextSize(16f);
        chartDeportes.setRotationEnabled(true);
        chartDeportes.setHighlightPerTapEnabled(true);

        // Leyenda
        chartDeportes.getLegend().setEnabled(true);
        chartDeportes.getLegend().setTextSize(12f);
    }

    /**
     * Observa los cambios en el ViewModel.
     */
    private void observeViewModel() {
        // Observar estado de carga
        viewModel.isLoading.observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading != null) {
                layoutLoading.setVisibility(isLoading ? View.VISIBLE : View.GONE);
                layoutContent.setVisibility(isLoading ? View.GONE : View.VISIBLE);
            }
        });

        // Observar errores
        viewModel.error.observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                mostrarError(error);
            }
        });

        // Observar resumen general
        viewModel.resumenGeneral.observe(getViewLifecycleOwner(), this::actualizarResumenGeneral);

        // Observar frecuencia de entrenamientos
        viewModel.frecuenciaEntrenamientos.observe(getViewLifecycleOwner(), this::actualizarGraficaFrecuencia);

        // Observar distribución de deportes
        viewModel.distribucionDeportes.observe(getViewLifecycleOwner(), this::actualizarGraficaDeportes);
    }

    /**
     * Actualiza los cards del resumen general.
     */
    private void actualizarResumenGeneral(StatisticsOverviewDTO resumen) {
        if (resumen == null) return;

        // Total de entrenamientos
        if (resumen.getTotalEntrenamientos() != null) {
            textTotalEntrenamientos.setText(String.valueOf(resumen.getTotalEntrenamientos()));
        }

        // Racha actual
        if (resumen.getRachaActual() != null) {
            textRachaActual.setText(resumen.getRachaActual() + " días");
        }

        // Entrenamientos del mes
        if (resumen.getEntrenamientosMesActual() != null) {
            textEntrenamientosMes.setText(String.valueOf(resumen.getEntrenamientosMesActual()));
        }

        // Tendencia
        if (resumen.getTendencia() != null) {
            String tendenciaTexto = obtenerTextoTendencia(resumen.getTendencia(), resumen.getPorcentajeCambio());
            textTendencia.setText(tendenciaTexto);

            // Cambiar color según tendencia
            int colorTendencia = obtenerColorTendencia(resumen.getTendencia());
            textTendencia.setTextColor(colorTendencia);
        }
    }

    /**
     * Actualiza la gráfica de frecuencia de entrenamientos.
     */
    private void actualizarGraficaFrecuencia(TrainingFrequencyDTO frecuencia) {
        if (frecuencia == null || frecuencia.getDataPoints() == null) return;

        List<BarEntry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        // Convertir datos a formato de MPAndroidChart
        for (int i = 0; i < frecuencia.getDataPoints().size(); i++) {
            TrainingFrequencyDTO.DataPoint point = frecuencia.getDataPoints().get(i);
            entries.add(new BarEntry(i, point.getValor() != null ? point.getValor() : 0));
            labels.add(point.getEtiqueta() != null ? point.getEtiqueta() : "");
        }

        // Crear dataset
        BarDataSet dataSet = new BarDataSet(entries, "Entrenamientos");
        dataSet.setColor(getResources().getColor(R.color.colorAccent));
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(10f);

        // Aplicar datos a la gráfica
        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.6f);
        chartFrecuencia.setData(barData);

        // Configurar etiquetas del eje X
        chartFrecuencia.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        chartFrecuencia.getXAxis().setLabelCount(labels.size());

        // Refrescar gráfica
        chartFrecuencia.animateY(1000);
        chartFrecuencia.invalidate();
    }

    /**
     * Actualiza la gráfica de distribución de deportes.
     */
    private void actualizarGraficaDeportes(SportsDistributionDTO distribucion) {
        if (distribucion == null || distribucion.getDeportes() == null || distribucion.getDeportes().isEmpty()) {
            chartDeportes.setCenterText("Sin datos");
            chartDeportes.clear();
            return;
        }

        List<PieEntry> entries = new ArrayList<>();
        List<Integer> colors = new ArrayList<>();

        // Convertir datos a formato de MPAndroidChart
        for (SportsDistributionDTO.SportData deporte : distribucion.getDeportes()) {
            if (deporte.getCantidadEntrenamientos() != null && deporte.getCantidadEntrenamientos() > 0) {
                entries.add(new PieEntry(
                        deporte.getCantidadEntrenamientos(),
                        deporte.getNombreDeporte()
                ));

                // Convertir color hex a int
                if (deporte.getColor() != null) {
                    try {
                        colors.add(Color.parseColor(deporte.getColor()));
                    } catch (Exception e) {
                        colors.add(getResources().getColor(R.color.colorAccent));
                    }
                } else {
                    colors.add(getResources().getColor(R.color.colorAccent));
                }
            }
        }

        // Crear dataset
        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(colors);
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setValueTextSize(12f);
        dataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.format("%.0f%%", (value / distribucion.getTotalEntrenamientos()) * 100);
            }
        });

        // Aplicar datos a la gráfica
        PieData pieData = new PieData(dataSet);
        chartDeportes.setData(pieData);

        // Texto central
        if (distribucion.getDeportePrincipal() != null) {
            chartDeportes.setCenterText(distribucion.getDeportePrincipal() + "\nPrincipal");
        }

        // Refrescar gráfica
        chartDeportes.animateY(1000);
        chartDeportes.invalidate();
    }

    /**
     * Obtiene el texto formateado de la tendencia.
     */
    private String obtenerTextoTendencia(String tendencia, Double porcentajeCambio) {
        if (porcentajeCambio == null) {
            return tendencia;
        }

        String simbolo = porcentajeCambio > 0 ? "↑" : porcentajeCambio < 0 ? "↓" : "→";
        return String.format("%s %s %.1f%%", simbolo, tendencia, Math.abs(porcentajeCambio));
    }

    /**
     * Obtiene el color según la tendencia.
     */
    private int obtenerColorTendencia(String tendencia) {
        switch (tendencia.toLowerCase()) {
            case "mejorando":
                return getResources().getColor(android.R.color.holo_green_dark);
            case "decreciendo":
                return getResources().getColor(android.R.color.holo_red_dark);
            case "estable":
            default:
                return getResources().getColor(android.R.color.darker_gray);
        }
    }

    /**
     * Muestra un mensaje de error.
     */
    private void mostrarError(String mensaje) {
        layoutError.setVisibility(View.VISIBLE);
        layoutContent.setVisibility(View.GONE);
        textError.setText(mensaje);

        Toast.makeText(requireContext(), mensaje, Toast.LENGTH_SHORT).show();
    }
}