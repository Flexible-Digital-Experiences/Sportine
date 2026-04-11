package com.example.sportine.ui.entrenadores.estadisticas;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import com.example.sportine.models.DetalleEstadisticasAlumnoDTO;
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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetalleAlumnoFragment extends Fragment {

    private static final String ARG_USUARIO = "usuario";
    private static final String ARG_NOMBRE  = "nombre";
    private static final String ARG_FOTO    = "foto";

    private String usuarioAlumno, nombreAlumno, fotoAlumno;

    private EstadisticasEntrenadorViewModel viewModel;
    private ApiService apiService;

    // ── Header ────────────────────────────────────────────────────────────────
    private ImageButton btnVolver;
    private CircleImageView imgFotoPerfil;
    private TextView textNombreAlumno, textDiasJuntos, textEntrenamientosJuntos;

    // ── Resumen ───────────────────────────────────────────────────────────────
    private TextView textTotalEntrenamientos, textRachaActual;
    private TextView textEntrenamientosMes, textTendencia;

    // ── Frecuencia ────────────────────────────────────────────────────────────
    private BarChart chartFrecuencia;

    // ── Deporte chips + secciones ─────────────────────────────────────────────
    private ChipGroup chipGroupDeportes;
    private ChipGroup chipGroupVista;
    private Chip chipGraficas, chipHistorial;
    private View layoutCarrera;
    private LinearLayout layoutGraficas, layoutHistorial;

    private TextView cardEmoji1, cardTitulo1, cardValor1, cardSub1;
    private TextView cardEmoji2, cardTitulo2, cardValor2, cardSub2;
    private TextView cardEmoji3, cardTitulo3, cardValor3, cardSub3;

    // ── Estados ───────────────────────────────────────────────────────────────
    private ProgressBar progressBar;
    private View layoutContent, layoutError;
    private TextView textError;

    // ── Estado interno ────────────────────────────────────────────────────────
    private Integer idDeporteSeleccionado = null;
    private boolean historialCargado = false;

    private static final int[] COLORES_GRAFICAS = {
            0xFF6366F1, 0xFF10B981, 0xFFF59E0B, 0xFFEF4444,
            0xFF8B5CF6, 0xFF06B6D4, 0xFFF97316, 0xFF84CC16,
    };
    private static final int COLOR_COMPARADO = 0xFFE5E7EB;
    private static final int COLOR_TEXTO_SEC = 0xFF9CA3AF;

    // ── Factory ───────────────────────────────────────────────────────────────

    public static DetalleAlumnoFragment newInstance(String usuario, String nombre, String foto) {
        DetalleAlumnoFragment f = new DetalleAlumnoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USUARIO, usuario);
        args.putString(ARG_NOMBRE, nombre);
        args.putString(ARG_FOTO, foto);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            usuarioAlumno = getArguments().getString(ARG_USUARIO);
            nombreAlumno  = getArguments().getString(ARG_NOMBRE);
            fotoAlumno    = getArguments().getString(ARG_FOTO);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_alumno_detalle_estadisticas, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel  = new ViewModelProvider(requireActivity()).get(EstadisticasEntrenadorViewModel.class);
        apiService = RetrofitClient.getClient(requireContext()).create(ApiService.class);
        viewModel.inicializarApiService(apiService);

        initViews(view);
        configurarHeader();
        estilizarBarChart(chartFrecuencia, false);
        setupChipToggle();
        observarViewModel();

        btnVolver.setOnClickListener(v -> {
            if (getActivity() != null)
                getActivity().getSupportFragmentManager().popBackStack();
        });

        // Cargar resumen + frecuencia del alumno
        viewModel.cargarEstadisticasCompletasAlumno(usuarioAlumno);

        // Cargar los deportes que el entrenador imparte a este alumno
        cargarDeportesDelEntrenador();
    }

    private void initViews(View view) {
        btnVolver               = view.findViewById(R.id.btn_volver);
        imgFotoPerfil           = view.findViewById(R.id.img_foto_perfil);
        textNombreAlumno        = view.findViewById(R.id.text_nombre_alumno);
        textDiasJuntos          = view.findViewById(R.id.text_dias_juntos);
        textEntrenamientosJuntos= view.findViewById(R.id.text_entrenamientos_juntos);
        textTotalEntrenamientos = view.findViewById(R.id.text_total_entrenamientos);
        textRachaActual         = view.findViewById(R.id.text_racha_actual);
        textEntrenamientosMes   = view.findViewById(R.id.text_entrenamientos_mes);
        textTendencia           = view.findViewById(R.id.text_tendencia);
        chartFrecuencia         = view.findViewById(R.id.chart_frecuencia);
        chipGroupDeportes       = view.findViewById(R.id.chip_group_deportes);
        chipGroupVista          = view.findViewById(R.id.chip_group_vista);
        chipGraficas            = view.findViewById(R.id.chip_graficas);
        chipHistorial           = view.findViewById(R.id.chip_historial);
        layoutCarrera           = view.findViewById(R.id.layout_carrera);
        layoutGraficas          = view.findViewById(R.id.layout_graficas_deporte);
        layoutHistorial         = view.findViewById(R.id.layout_historial_deporte);
        progressBar             = view.findViewById(R.id.progress_bar);
        layoutContent           = view.findViewById(R.id.layout_content);
        layoutError             = view.findViewById(R.id.layout_error);
        textError               = view.findViewById(R.id.text_error);
        cardEmoji1  = view.findViewById(R.id.card_emoji_1);
        cardTitulo1 = view.findViewById(R.id.card_titulo_1);
        cardValor1  = view.findViewById(R.id.card_valor_1);
        cardSub1    = view.findViewById(R.id.card_sub_1);
        cardEmoji2  = view.findViewById(R.id.card_emoji_2);
        cardTitulo2 = view.findViewById(R.id.card_titulo_2);
        cardValor2  = view.findViewById(R.id.card_valor_2);
        cardSub2    = view.findViewById(R.id.card_sub_2);
        cardEmoji3  = view.findViewById(R.id.card_emoji_3);
        cardTitulo3 = view.findViewById(R.id.card_titulo_3);
        cardValor3  = view.findViewById(R.id.card_valor_3);
        cardSub3    = view.findViewById(R.id.card_sub_3);
    }

    private void configurarHeader() {
        textNombreAlumno.setText(nombreAlumno);
        if (fotoAlumno != null && !fotoAlumno.isEmpty()) {
            Picasso.get().load(fotoAlumno)
                    .placeholder(R.drawable.ic_perfil)
                    .error(R.drawable.ic_perfil)
                    .into(imgFotoPerfil);
        } else {
            imgFotoPerfil.setImageResource(R.drawable.ic_perfil);
        }
    }

    // ── Chip toggle ───────────────────────────────────────────────────────────

    private void setupChipToggle() {
        chipGraficas.setOnCheckedChangeListener((btn, isChecked) -> {
            if (isChecked) mostrarVista(true);
        });
        chipHistorial.setOnCheckedChangeListener((btn, isChecked) -> {
            if (isChecked) {
                mostrarVista(false);
                if (!historialCargado && idDeporteSeleccionado != null)
                    cargarHistorial(idDeporteSeleccionado);
            }
        });
    }

    private void mostrarVista(boolean graficas) {
        layoutGraficas.setVisibility(graficas ? View.VISIBLE : View.GONE);
        layoutHistorial.setVisibility(graficas ? View.GONE  : View.VISIBLE);
    }

    // ── Deportes del entrenador para este alumno ──────────────────────────────

    /**
     * Llama al endpoint que devuelve solo los deportes que
     * el entrenador autenticado imparte a este alumno (filtra por Entrenador_Alumno).
     */
    private void cargarDeportesDelEntrenador() {
        apiService.obtenerDeportesEntrenadorParaAlumno(usuarioAlumno)
                .enqueue(new Callback<List<DeporteAlumnoDTO>>() {
                    @Override
                    public void onResponse(Call<List<DeporteAlumnoDTO>> call,
                                           Response<List<DeporteAlumnoDTO>> response) {
                        if (response.isSuccessful() && response.body() != null
                                && !response.body().isEmpty() && isAdded()) {
                            construirChipsDeporte(response.body());
                        }
                    }
                    @Override
                    public void onFailure(Call<List<DeporteAlumnoDTO>> call, Throwable t) {
                        android.util.Log.e("DetalleAlumno", "Error deportes: " + t.getMessage());
                    }
                });
    }

    private void construirChipsDeporte(List<DeporteAlumnoDTO> deportes) {
        chipGroupDeportes.removeAllViews();
        for (DeporteAlumnoDTO deporte : deportes) {
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
                    cargarCarrera(deporte.getIdDeporte());
                    cargarMetricas(deporte.getIdDeporte());
                    layoutCarrera.setVisibility(View.VISIBLE);
                    chipGroupVista.setVisibility(View.VISIBLE);
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
        historialCargado = false;
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

    private void cargarCarrera(Integer idDeporte) {
        // Endpoint del entrenador — pasa usuarioAlumno como parámetro
        apiService.obtenerCarreraDeporteAlumno(usuarioAlumno, idDeporte)
                .enqueue(new Callback<CarreraDeporteDTO>() {
                    @Override
                    public void onResponse(Call<CarreraDeporteDTO> call,
                                           Response<CarreraDeporteDTO> response) {
                        if (response.isSuccessful() && response.body() != null
                                && isAdded() && idDeporte.equals(idDeporteSeleccionado))
                            actualizarCardsCarrera(response.body());
                    }
                    @Override
                    public void onFailure(Call<CarreraDeporteDTO> call, Throwable t) {
                        if (isAdded())
                            Toast.makeText(getContext(), "Error al cargar carrera", Toast.LENGTH_SHORT).show();
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

    private void cargarMetricas(Integer idDeporte) {
        apiService.obtenerMetricasDeporteAlumno(usuarioAlumno, idDeporte, 5)
                .enqueue(new Callback<MetricasUltimosDTO>() {
                    @Override
                    public void onResponse(Call<MetricasUltimosDTO> call,
                                           Response<MetricasUltimosDTO> response) {
                        if (response.isSuccessful() && response.body() != null
                                && isAdded() && idDeporte.equals(idDeporteSeleccionado))
                            construirGraficasDinamicas(response.body());
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
            TextView tv = new TextView(requireContext());
            tv.setText("📊 Sin datos de métricas aún");
            tv.setTextSize(13f);
            tv.setTextColor(COLOR_TEXTO_SEC);
            tv.setPadding(0, 16, 0, 32);
            layoutGraficas.addView(tv);
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

    // ── Historial ─────────────────────────────────────────────────────────────

    private void cargarHistorial(Integer idDeporte) {
        apiService.obtenerHistorialDeporteAlumno(usuarioAlumno, idDeporte, 5)
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
            TextView tv = new TextView(requireContext());
            tv.setText("📋 No hay entrenamientos finalizados aún");
            tv.setTextSize(13f);
            tv.setTextColor(COLOR_TEXTO_SEC);
            tv.setPadding(0, 16, 0, 32);
            layoutHistorial.addView(tv);
            return;
        }

        int dp = (int) requireContext().getResources().getDisplayMetrics().density;

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

            if (i > 0) {
                View divider = new View(requireContext());
                LinearLayout.LayoutParams divParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, 1);
                divParams.setMarginStart(16*dp);
                divider.setLayoutParams(divParams);
                divider.setBackgroundColor(0xFFE5E7EB);
                listaInner.addView(divider);
            }

            LinearLayout fila = new LinearLayout(requireContext());
            fila.setOrientation(LinearLayout.HORIZONTAL);
            fila.setGravity(android.view.Gravity.CENTER_VERTICAL);
            fila.setPadding(16 * dp, 12 * dp, 16 * dp, 12 * dp);

            TextView tvIcono = new TextView(requireContext());
            tvIcono.setText("🗓️");
            tvIcono.setTextSize(22f);
            LinearLayout.LayoutParams iconoParams = new LinearLayout.LayoutParams(44 * dp, 44 * dp);
            iconoParams.setMarginEnd(12 * dp);
            tvIcono.setLayoutParams(iconoParams);
            tvIcono.setGravity(android.view.Gravity.CENTER);

            LinearLayout infoCol = new LinearLayout(requireContext());
            infoCol.setOrientation(LinearLayout.VERTICAL);
            infoCol.setLayoutParams(new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

            TextView tvTitulo = new TextView(requireContext());
            tvTitulo.setText(item.getTitulo() != null ? item.getTitulo() : "Entrenamiento");
            tvTitulo.setTextSize(13f);
            tvTitulo.setTextColor(0xFF1F2937);
            tvTitulo.setTypeface(null, android.graphics.Typeface.BOLD);

            TextView tvSub = new TextView(requireContext());
            StringBuilder sub = new StringBuilder(item.getFecha() != null ? item.getFecha() : "");
            if (item.getDuracionMin() != null) sub.append(" · ").append(item.getDuracionMin()).append(" min");
            if (item.getCaloriasKcal() != null) sub.append(" · ").append(item.getCaloriasKcal()).append(" kcal");
            if (item.getDistanciaMetros() != null && item.getDistanciaMetros() > 0)
                sub.append(" · ").append(String.format("%.1f", item.getDistanciaMetros() / 1000f)).append(" km");
            tvSub.setText(sub.toString());
            tvSub.setTextSize(11f);
            tvSub.setTextColor(COLOR_TEXTO_SEC);

            infoCol.addView(tvTitulo);
            infoCol.addView(tvSub);

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
            badgeParams.setMarginStart(8*dp);
            tvBadge.setLayoutParams(badgeParams);

            fila.addView(tvIcono);
            fila.addView(infoCol);
            fila.addView(tvBadge);
            listaInner.addView(fila);
        }

        cardLista.addView(listaInner);
        layoutHistorial.addView(cardLista);
    }

    // ── Estilizar chart ───────────────────────────────────────────────────────

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

    // ── Observar ViewModel ────────────────────────────────────────────────────

    private void observarViewModel() {
        viewModel.isLoading.observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading != null) {
                progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
                if (!isLoading) layoutContent.setVisibility(View.VISIBLE);
            }
        });

        viewModel.error.observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                mostrarError(error);
                viewModel.clearError();
            }
        });

        viewModel.detalleAlumno.observe(getViewLifecycleOwner(), this::actualizarResumen);

        viewModel.frecuenciaAlumno.observe(getViewLifecycleOwner(), this::actualizarGraficaFrecuencia);
    }

    private void actualizarResumen(DetalleEstadisticasAlumnoDTO detalle) {
        if (detalle == null) return;

        if (detalle.getDiasJuntos() != null)
            textDiasJuntos.setText(detalle.getDiasJuntos() + " días juntos");
        if (detalle.getEntrenamientosJuntos() != null)
            textEntrenamientosJuntos.setText(detalle.getEntrenamientosJuntos() + " entrenamientos");

        StatisticsOverviewDTO resumen = detalle.getResumenGeneral();
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