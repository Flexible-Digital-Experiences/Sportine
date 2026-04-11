package com.example.sportine.ui.usuarios.estadisticas;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.sportine.R;
import com.example.sportine.data.ApiService;
import com.example.sportine.data.RetrofitClient;
import com.example.sportine.models.CarreraDeporteDTO;
import com.example.sportine.models.DeporteAlumnoDTO;
import com.example.sportine.models.HistorialEntrenamientoDTO;
import com.example.sportine.models.MetricasUltimosDTO;
import com.example.sportine.models.StatisticsOverviewDTO;
import com.example.sportine.models.TrainingFrequencyDTO;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EstadisticasFragment extends Fragment {

    private EstadisticasViewModel viewModel;
    private ApiService apiService;

    // ── Views ─────────────────────────────────────────────────────────────────
    private TextView textTotalEntrenamientos, textRachaActual;
    private TextView textEntrenamientosMes, textTendencia;
    private ChipGroup chipGroupDeportes;
    private ChipGroup chipGroupVista;
    private Chip chipGraficas, chipHistorial;
    private View layoutCarrera;
    private LinearLayout layoutGraficas;
    private LinearLayout layoutHistorial;

    private TextView cardEmoji1, cardTitulo1, cardValor1, cardSub1;
    private TextView cardEmoji2, cardTitulo2, cardValor2, cardSub2;
    private TextView cardEmoji3, cardTitulo3, cardValor3, cardSub3;

    private BarChart chartFrecuencia;
    private View layoutLoading, layoutContent, layoutError;
    private TextView textError;

    // Estado
    private List<DeporteAlumnoDTO> deportesAlumno = new ArrayList<>();
    private Integer idDeporteSeleccionado = null;
    private boolean graficasCargadas   = false;
    private boolean historialCargado   = false;

    // Paleta de colores
    private static final int[] COLORES_GRAFICAS = {
            0xFF6366F1, 0xFF10B981, 0xFFF59E0B, 0xFFEF4444,
            0xFF8B5CF6, 0xFF06B6D4, 0xFFF97316, 0xFF84CC16,
    };
    private static final int COLOR_COMPARADO = 0xFFE5E7EB;
    private static final int COLOR_TEXTO_SEC = 0xFF9CA3AF;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_alumno_estadisticas, container, false);

        viewModel  = new ViewModelProvider(this).get(EstadisticasViewModel.class);
        apiService = RetrofitClient.getClient(requireContext()).create(ApiService.class);
        viewModel.inicializarApiService(apiService);

        initViews(root);
        estilizarBarChart(chartFrecuencia, false);
        setupChipToggle();
        observarViewModel();

        viewModel.cargarResumenGeneral();
        viewModel.cargarFrecuenciaEntrenamientos("MONTH");
        cargarDeportesAlumno();

        return root;
    }

    private void initViews(View root) {
        textTotalEntrenamientos = root.findViewById(R.id.text_total_entrenamientos);
        textRachaActual         = root.findViewById(R.id.text_racha_actual);
        textEntrenamientosMes   = root.findViewById(R.id.text_entrenamientos_mes);
        textTendencia           = root.findViewById(R.id.text_tendencia);
        chipGroupDeportes       = root.findViewById(R.id.chip_group_deportes);
        chipGroupVista          = root.findViewById(R.id.chip_group_vista);
        chipGraficas            = root.findViewById(R.id.chip_graficas);
        chipHistorial           = root.findViewById(R.id.chip_historial);
        layoutCarrera           = root.findViewById(R.id.layout_carrera);
        layoutGraficas          = root.findViewById(R.id.layout_graficas_deporte);
        layoutHistorial         = root.findViewById(R.id.layout_historial_deporte);
        chartFrecuencia         = root.findViewById(R.id.chart_frecuencia);
        layoutLoading           = root.findViewById(R.id.layout_loading);
        layoutContent           = root.findViewById(R.id.layout_content);
        layoutError             = root.findViewById(R.id.layout_error);
        textError               = root.findViewById(R.id.text_error);
        cardEmoji1  = root.findViewById(R.id.card_emoji_1);
        cardTitulo1 = root.findViewById(R.id.card_titulo_1);
        cardValor1  = root.findViewById(R.id.card_valor_1);
        cardSub1    = root.findViewById(R.id.card_sub_1);
        cardEmoji2  = root.findViewById(R.id.card_emoji_2);
        cardTitulo2 = root.findViewById(R.id.card_titulo_2);
        cardValor2  = root.findViewById(R.id.card_valor_2);
        cardSub2    = root.findViewById(R.id.card_sub_2);
        cardEmoji3  = root.findViewById(R.id.card_emoji_3);
        cardTitulo3 = root.findViewById(R.id.card_titulo_3);
        cardValor3  = root.findViewById(R.id.card_valor_3);
        cardSub3    = root.findViewById(R.id.card_sub_3);
    }

    // ── Chip toggle Gráficas / Historial ─────────────────────────────────────

    private void setupChipToggle() {
        chipGraficas.setOnCheckedChangeListener((btn, isChecked) -> {
            if (isChecked) mostrarVista(true);
        });
        chipHistorial.setOnCheckedChangeListener((btn, isChecked) -> {
            if (isChecked) {
                mostrarVista(false);
                // Cargar historial solo si no está cargado aún
                if (!historialCargado && idDeporteSeleccionado != null)
                    cargarHistorialDeporte(idDeporteSeleccionado);
            }
        });
    }

    /** true = gráficas, false = historial */
    private void mostrarVista(boolean graficas) {
        layoutGraficas.setVisibility(graficas ? View.VISIBLE : View.GONE);
        layoutHistorial.setVisibility(graficas ? View.GONE  : View.VISIBLE);
    }

    // ── Estilizar BarChart ────────────────────────────────────────────────────

    private void estilizarBarChart(BarChart chart, boolean conLeyenda) {
        chart.getDescription().setEnabled(false);
        chart.setDrawGridBackground(false);
        chart.setDrawBarShadow(false);
        chart.setPinchZoom(false);
        chart.setScaleEnabled(false);
        chart.getAxisRight().setEnabled(false);
        chart.setExtraBottomOffset(6f);
        chart.setExtraTopOffset(6f);

        XAxis x = chart.getXAxis();
        x.setPosition(XAxis.XAxisPosition.BOTTOM);
        x.setDrawGridLines(false);
        x.setGranularity(1f);
        x.setTextColor(COLOR_TEXTO_SEC);
        x.setTextSize(11f);
        x.setDrawAxisLine(false);

        chart.getAxisLeft().setAxisMinimum(0f);
        chart.getAxisLeft().setTextColor(COLOR_TEXTO_SEC);
        chart.getAxisLeft().setTextSize(11f);
        chart.getAxisLeft().setGridColor(0xFFEEEEEE);
        chart.getAxisLeft().setDrawAxisLine(false);

        Legend legend = chart.getLegend();
        legend.setEnabled(conLeyenda);
        legend.setTextColor(COLOR_TEXTO_SEC);
        legend.setTextSize(12f);
        legend.setForm(Legend.LegendForm.SQUARE);
        legend.setFormSize(10f);
        legend.setXEntrySpace(16f);
    }

    // ── Chips de deporte ──────────────────────────────────────────────────────

    private void cargarDeportesAlumno() {
        apiService.obtenerDeportesAlumno().enqueue(new Callback<List<DeporteAlumnoDTO>>() {
            @Override
            public void onResponse(Call<List<DeporteAlumnoDTO>> call,
                                   Response<List<DeporteAlumnoDTO>> response) {
                if (response.isSuccessful() && response.body() != null
                        && !response.body().isEmpty() && isAdded()) {
                    deportesAlumno = response.body();
                    construirChips();
                }
            }
            @Override
            public void onFailure(Call<List<DeporteAlumnoDTO>> call, Throwable t) {
                android.util.Log.e("Deportes", "Error: " + t.getMessage());
            }
        });
    }

    private void construirChips() {
        chipGroupDeportes.removeAllViews();
        for (DeporteAlumnoDTO deporte : deportesAlumno) {
            Chip chip = new Chip(requireContext());
            chip.setText(deporte.getEmoji() + " " + deporte.getNombreDeporte());
            chip.setCheckable(true);
            chip.setCheckedIconVisible(false);
            chip.setTextSize(13f);
            chip.setEnsureMinTouchTargetSize(false);

            chip.setOnCheckedChangeListener((btn, isChecked) -> {
                if (isChecked) {
                    idDeporteSeleccionado = deporte.getIdDeporte();
                    limpiarSeccionDeporte();
                    cargarCarreraDeporte(deporte.getIdDeporte());
                    cargarMetricasDeporte(deporte.getIdDeporte());
                    // Mostrar controles
                    layoutCarrera.setVisibility(View.VISIBLE);
                    chipGroupVista.setVisibility(View.VISIBLE);
                    // Resetear al chip de gráficas
                    chipGraficas.setChecked(true);
                    mostrarVista(true);
                }
            });
            chipGroupDeportes.addView(chip);
        }
        if (chipGroupDeportes.getChildCount() > 0)
            ((Chip) chipGroupDeportes.getChildAt(0)).setChecked(true);
    }

    private void limpiarSeccionDeporte() {
        graficasCargadas = false;
        historialCargado = false;

        // Limpiar cards
        TextView[] valores = {cardValor1, cardValor2, cardValor3};
        TextView[] subs    = {cardSub1,   cardSub2,   cardSub3};
        TextView[] emojis  = {cardEmoji1, cardEmoji2, cardEmoji3};
        TextView[] titulos = {cardTitulo1,cardTitulo2,cardTitulo3};
        for (int i = 0; i < 3; i++) {
            valores[i].setText("...");
            subs[i].setText("");
            emojis[i].setText("⏳");
            titulos[i].setText("");
        }
        layoutGraficas.removeAllViews();
        layoutHistorial.removeAllViews();
    }

    // ── Cards de carrera ──────────────────────────────────────────────────────

    private void cargarCarreraDeporte(Integer idDeporte) {
        apiService.obtenerCarreraDeporte(idDeporte).enqueue(new Callback<CarreraDeporteDTO>() {
            @Override
            public void onResponse(Call<CarreraDeporteDTO> call,
                                   Response<CarreraDeporteDTO> response) {
                if (response.isSuccessful() && response.body() != null
                        && isAdded() && idDeporte.equals(idDeporteSeleccionado)) {
                    actualizarCardsCarrera(response.body());
                }
            }
            @Override
            public void onFailure(Call<CarreraDeporteDTO> call, Throwable t) {
                if (isAdded())
                    Toast.makeText(getContext(), "Error al cargar estadísticas", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void actualizarCardsCarrera(CarreraDeporteDTO dto) {
        List<CarreraDeporteDTO.CardCarreraDTO> cards = dto.getCards();
        if (cards == null || cards.isEmpty()) return;

        TextView[] emojis  = {cardEmoji1,  cardEmoji2,  cardEmoji3};
        TextView[] titulos = {cardTitulo1, cardTitulo2, cardTitulo3};
        TextView[] valores = {cardValor1,  cardValor2,  cardValor3};
        TextView[] subs    = {cardSub1,    cardSub2,    cardSub3};

        for (int i = 0; i < Math.min(cards.size(), 3); i++) {
            CarreraDeporteDTO.CardCarreraDTO card = cards.get(i);
            emojis[i].setText(card.getEmoji());
            titulos[i].setText(card.getEtiqueta());
            double val = card.getValorTotal();
            valores[i].setText(val % 1 == 0
                    ? String.valueOf((int) val)
                    : String.format("%.1f", val));
            subs[i].setText("Mejor: " + formatearValor(card.getMejorSesion())
                    + " " + card.getUnidad());
        }
    }

    // ── Gráficas dinámicas ────────────────────────────────────────────────────

    private void cargarMetricasDeporte(Integer idDeporte) {
        apiService.obtenerMetricasDeporte(idDeporte, 5).enqueue(new Callback<MetricasUltimosDTO>() {
            @Override
            public void onResponse(Call<MetricasUltimosDTO> call,
                                   Response<MetricasUltimosDTO> response) {
                if (response.isSuccessful() && response.body() != null
                        && isAdded() && idDeporte.equals(idDeporteSeleccionado)) {
                    graficasCargadas = true;
                    construirGraficasDinamicas(response.body());
                }
            }
            @Override
            public void onFailure(Call<MetricasUltimosDTO> call, Throwable t) {
                if (isAdded())
                    Toast.makeText(getContext(), "Error al cargar métricas", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void construirGraficasDinamicas(MetricasUltimosDTO dto) {
        layoutGraficas.removeAllViews();

        List<MetricasUltimosDTO.GraficaMetricaDTO> graficas = dto.getGraficas();

        if (graficas == null || graficas.isEmpty()) {
            TextView tvVacio = new TextView(requireContext());
            tvVacio.setText("📊 Completa más entrenamientos para ver tus gráficas aquí");
            tvVacio.setTextSize(13f);
            tvVacio.setTextColor(COLOR_TEXTO_SEC);
            tvVacio.setPadding(0, 16, 0, 32);
            layoutGraficas.addView(tvVacio);
            return;
        }

        int dp = (int) requireContext().getResources().getDisplayMetrics().density;

        for (int i = 0; i < graficas.size(); i++) {
            MetricasUltimosDTO.GraficaMetricaDTO grafica = graficas.get(i);
            int color = COLORES_GRAFICAS[i % COLORES_GRAFICAS.length];

            MaterialCardView card = new MaterialCardView(requireContext());
            LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            cardParams.bottomMargin = 12 * dp;
            card.setLayoutParams(cardParams);
            card.setRadius(16 * dp);
            card.setCardElevation(0);
            card.setStrokeWidth(dp);
            card.setStrokeColor(0xFFE5E7EB);
            card.setCardBackgroundColor(Color.WHITE);

            LinearLayout inner = new LinearLayout(requireContext());
            inner.setOrientation(LinearLayout.VERTICAL);
            int pad = 16 * dp;
            inner.setPadding(pad, pad, pad, pad);

            TextView label = new TextView(requireContext());
            String titulo = grafica.getEtiqueta()
                    + (grafica.getUnidad() != null && !grafica.getUnidad().isEmpty()
                    ? " (" + grafica.getUnidad() + ")" : "");
            label.setText(titulo);
            label.setTextSize(14f);
            label.setTextColor(0xFF1F2937);
            label.setTypeface(null, android.graphics.Typeface.BOLD);
            LinearLayout.LayoutParams labelParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            labelParams.bottomMargin = 8 * dp;
            label.setLayoutParams(labelParams);

            BarChart chart = new BarChart(requireContext());
            LinearLayout.LayoutParams chartParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, 180 * dp);
            chart.setLayoutParams(chartParams);
            estilizarBarChart(chart, true);

            inner.addView(label);
            inner.addView(chart);
            card.addView(inner);
            layoutGraficas.addView(card);

            poblarBarChart(chart, grafica, color);
        }
    }

    private void poblarBarChart(BarChart chart,
                                MetricasUltimosDTO.GraficaMetricaDTO grafica,
                                int colorPrincipal) {
        List<MetricasUltimosDTO.GraficaMetricaDTO.PuntoDTO> puntos = grafica.getPuntos();
        if (puntos == null || puntos.isEmpty()) return;

        List<String> fechas = new ArrayList<>();
        List<BarEntry> entriesValor     = new ArrayList<>();
        List<BarEntry> entriesComparado = new ArrayList<>();
        boolean tieneComparado = false;

        for (int i = 0; i < puntos.size(); i++) {
            MetricasUltimosDTO.GraficaMetricaDTO.PuntoDTO p = puntos.get(i);
            fechas.add(p.getFecha());
            entriesValor.add(new BarEntry(i, p.getValor().floatValue()));
            if (p.tieneComparado()) {
                entriesComparado.add(new BarEntry(i, p.getValorComparado().floatValue()));
                tieneComparado = true;
            }
        }

        BarData barData;

        if (tieneComparado && !entriesComparado.isEmpty()) {
            BarDataSet dsComparado = new BarDataSet(entriesComparado, "Intentados");
            dsComparado.setColor(COLOR_COMPARADO);
            dsComparado.setValueTextColor(COLOR_TEXTO_SEC);
            dsComparado.setValueTextSize(10f);
            dsComparado.setDrawValues(true);

            BarDataSet dsValor = new BarDataSet(entriesValor, "Anotados / Completados");
            dsValor.setColor(colorPrincipal);
            dsValor.setValueTextColor(colorPrincipal);
            dsValor.setValueTextSize(10f);
            dsValor.setDrawValues(true);

            float groupSpace = 0.3f, barSpace = 0.05f, barWidth = 0.3f;
            barData = new BarData(dsComparado, dsValor);
            barData.setBarWidth(barWidth);
            chart.setData(barData);
            chart.getXAxis().setAxisMinimum(0f);
            chart.getXAxis().setAxisMaximum(barData.getGroupWidth(groupSpace, barSpace) * puntos.size());
            chart.getXAxis().setCenterAxisLabels(true);
            chart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(fechas));
            chart.getXAxis().setLabelCount(fechas.size());
            chart.groupBars(0f, groupSpace, barSpace);
        } else {
            BarDataSet dsValor = new BarDataSet(entriesValor, grafica.getEtiqueta());
            dsValor.setColor(colorPrincipal);
            dsValor.setValueTextColor(colorPrincipal);
            dsValor.setValueTextSize(10f);
            dsValor.setDrawValues(true);
            barData = new BarData(dsValor);
            barData.setBarWidth(0.5f);
            chart.setData(barData);
            chart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(fechas));
            chart.getXAxis().setLabelCount(fechas.size());
        }

        chart.animateY(600);
        chart.invalidate();
    }

    // ── Historial de entrenamientos ───────────────────────────────────────────

    private void cargarHistorialDeporte(Integer idDeporte) {
        apiService.obtenerHistorialDeporte(idDeporte, 5)
                .enqueue(new Callback<List<HistorialEntrenamientoDTO>>() {
                    @Override
                    public void onResponse(Call<List<HistorialEntrenamientoDTO>> call,
                                           Response<List<HistorialEntrenamientoDTO>> response) {
                        if (response.isSuccessful() && response.body() != null
                                && isAdded() && idDeporte.equals(idDeporteSeleccionado)) {
                            historialCargado = true;
                            construirHistorial(response.body());
                        }
                    }
                    @Override
                    public void onFailure(Call<List<HistorialEntrenamientoDTO>> call, Throwable t) {
                        if (isAdded())
                            Toast.makeText(getContext(), "Error al cargar historial", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void construirHistorial(List<HistorialEntrenamientoDTO> historial) {
        layoutHistorial.removeAllViews();

        if (historial == null || historial.isEmpty()) {
            TextView tvVacio = new TextView(requireContext());
            tvVacio.setText("📋 No hay entrenamientos finalizados aún");
            tvVacio.setTextSize(13f);
            tvVacio.setTextColor(COLOR_TEXTO_SEC);
            tvVacio.setPadding(0, 16, 0, 32);
            layoutHistorial.addView(tvVacio);
            return;
        }

        int dp = (int) requireContext().getResources().getDisplayMetrics().density;

        // Card contenedora que agrupa toda la lista
        MaterialCardView cardLista = new MaterialCardView(requireContext());
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        cardLista.setLayoutParams(cardParams);
        cardLista.setRadius(16 * dp);
        cardLista.setCardElevation(0);
        cardLista.setStrokeWidth(dp);
        cardLista.setStrokeColor(0xFFE5E7EB);
        cardLista.setCardBackgroundColor(Color.WHITE);

        LinearLayout listaInner = new LinearLayout(requireContext());
        listaInner.setOrientation(LinearLayout.VERTICAL);

        for (int i = 0; i < historial.size(); i++) {
            HistorialEntrenamientoDTO item = historial.get(i);

            // Fila del entrenamiento
            LinearLayout fila = new LinearLayout(requireContext());
            fila.setOrientation(LinearLayout.HORIZONTAL);
            fila.setGravity(Gravity.CENTER_VERTICAL);
            int padH = 16 * dp, padV = 12 * dp;
            fila.setPadding(padH, padV, padH, padV);

            // Separador entre filas
            if (i > 0) {
                View divider = new View(requireContext());
                LinearLayout.LayoutParams divParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, 1);
                divParams.leftMargin = padH;
                divider.setLayoutParams(divParams);
                divider.setBackgroundColor(0xFFE5E7EB);
                listaInner.addView(divider);
            }

            // Ícono de fecha
            TextView tvIcono = new TextView(requireContext());
            tvIcono.setText("🗓️");
            tvIcono.setTextSize(22f);
            LinearLayout.LayoutParams iconoParams = new LinearLayout.LayoutParams(
                    44 * dp, 44 * dp);
            iconoParams.topMargin = 12 * dp;
            tvIcono.setLayoutParams(iconoParams);
            tvIcono.setGravity(Gravity.CENTER);

            // Info central
            LinearLayout infoCol = new LinearLayout(requireContext());
            infoCol.setOrientation(LinearLayout.VERTICAL);
            infoCol.setLayoutParams(new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

            TextView tvTitulo = new TextView(requireContext());
            tvTitulo.setText(item.getTitulo() != null ? item.getTitulo() : "Entrenamiento");
            tvTitulo.setTextSize(13f);
            tvTitulo.setTextColor(0xFF1F2937);
            tvTitulo.setTypeface(null, Typeface.BOLD);

            TextView tvSubtitulo = new TextView(requireContext());
            StringBuilder sub = new StringBuilder(item.getFecha() != null ? item.getFecha() : "");
            if (item.getDuracionMin() != null) sub.append(" · ").append(item.getDuracionMin()).append(" min");
            if (item.getCaloriasKcal() != null) sub.append(" · ").append(item.getCaloriasKcal()).append(" kcal");
            if (item.getDistanciaMetros() != null && item.getDistanciaMetros() > 0)
                sub.append(" · ").append(String.format("%.1f", item.getDistanciaMetros() / 1000f)).append(" km");
            tvSubtitulo.setText(sub.toString());
            tvSubtitulo.setTextSize(11f);
            tvSubtitulo.setTextColor(COLOR_TEXTO_SEC);

            infoCol.addView(tvTitulo);
            infoCol.addView(tvSubtitulo);

            // Badge HC / manual
            TextView tvBadge = new TextView(requireContext());
            if (item.getTieneHc()) {
                tvBadge.setText("HC");
                tvBadge.setBackgroundColor(0xFFDCFCE7);
                tvBadge.setTextColor(0xFF166534);
            } else {
                tvBadge.setText("manual");
                tvBadge.setBackgroundColor(0xFFF3F4F6);
                tvBadge.setTextColor(0xFF6B7280);
            }
            tvBadge.setTextSize(10f);
            tvBadge.setPadding(8 * dp, 4 * dp, 8 * dp, 4 * dp);
            LinearLayout.LayoutParams badgeParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            badgeParams.leftMargin = 8 * dp;
            tvBadge.setLayoutParams(badgeParams);

            fila.addView(tvIcono);
            fila.addView(infoCol);
            fila.addView(tvBadge);
            listaInner.addView(fila);
        }

        cardLista.addView(listaInner);
        layoutHistorial.addView(cardLista);
    }

    // ── Frecuencia ────────────────────────────────────────────────────────────

    private void actualizarGraficaFrecuencia(TrainingFrequencyDTO frecuencia) {
        if (frecuencia == null || frecuencia.getDataPoints() == null) return;
        List<BarEntry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        for (int i = 0; i < frecuencia.getDataPoints().size(); i++) {
            TrainingFrequencyDTO.DataPoint p = frecuencia.getDataPoints().get(i);
            entries.add(new BarEntry(i, p.getValor() != null ? p.getValor() : 0));
            labels.add(p.getEtiqueta() != null ? p.getEtiqueta() : "");
        }

        BarDataSet ds = new BarDataSet(entries, "Entrenamientos");
        ds.setColor(0xFF6366F1);
        ds.setValueTextColor(0xFF6366F1);
        ds.setValueTextSize(11f);

        BarData data = new BarData(ds);
        data.setBarWidth(0.5f);
        chartFrecuencia.setData(data);
        chartFrecuencia.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        chartFrecuencia.getXAxis().setLabelCount(labels.size());
        chartFrecuencia.animateY(600);
        chartFrecuencia.invalidate();
    }

    // ── Observar ViewModel ────────────────────────────────────────────────────

    private void observarViewModel() {
        viewModel.isLoading.observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading != null) {
                layoutLoading.setVisibility(isLoading ? View.VISIBLE : View.GONE);
                if (!isLoading) layoutContent.setVisibility(View.VISIBLE);
            }
        });
        viewModel.error.observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) mostrarError(error);
        });
        viewModel.resumenGeneral.observe(getViewLifecycleOwner(), this::actualizarResumen);
        viewModel.frecuenciaEntrenamientos.observe(getViewLifecycleOwner(), this::actualizarGraficaFrecuencia);
    }

    private void actualizarResumen(StatisticsOverviewDTO resumen) {
        if (resumen == null) return;
        if (resumen.getTotalEntrenamientos() != null)
            textTotalEntrenamientos.setText(String.valueOf(resumen.getTotalEntrenamientos()));
        if (resumen.getRachaActual() != null)
            textRachaActual.setText(resumen.getRachaActual() + " días");
        if (resumen.getEntrenamientosMesActual() != null)
            textEntrenamientosMes.setText(String.valueOf(resumen.getEntrenamientosMesActual()));
        if (resumen.getTendencia() != null) {
            Double pct = resumen.getPorcentajeCambio();
            String simbolo = pct != null && pct > 0 ? "↑" : pct != null && pct < 0 ? "↓" : "→";
            textTendencia.setText(simbolo + " " + resumen.getTendencia());
            switch (resumen.getTendencia().toLowerCase()) {
                case "mejorando":   textTendencia.setTextColor(0xFF10B981); break;
                case "decreciendo": textTendencia.setTextColor(0xFFEF4444); break;
                default:            textTendencia.setTextColor(0xFF6B7280); break;
            }
        }
    }

    private String formatearValor(Double val) {
        if (val == null) return "0";
        return val % 1 == 0 ? String.valueOf(val.intValue()) : String.format("%.1f", val);
    }

    private void mostrarError(String mensaje) {
        if (layoutError != null) layoutError.setVisibility(View.VISIBLE);
        if (layoutContent != null) layoutContent.setVisibility(View.GONE);
        if (textError != null) textError.setText(mensaje);
    }
}