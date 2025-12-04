package com.example.sportine.ui.entrenadores.estadisticas;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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
import com.example.sportine.models.DetalleEstadisticasAlumnoDTO;
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
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Fragment que muestra el detalle completo de estadísticas de un alumno.
 * Incluye resumen general, gráficas de frecuencia y distribución de deportes.
 */
public class DetalleAlumnoFragment extends Fragment {

    private static final String TAG = "DetalleAlumnoFragment";
    private static final String ARG_USUARIO = "usuario";
    private static final String ARG_NOMBRE = "nombre";
    private static final String ARG_FOTO = "foto";

    // Parámetros del alumno
    private String usuarioAlumno;
    private String nombreAlumno;
    private String fotoAlumno;

    // ViewModel
    private EstadisticasEntrenadorViewModel viewModel;

    // Vistas - Header
    private ImageButton btnVolver;
    private CircleImageView imgFotoPerfil;
    private TextView textNombreAlumno;
    private TextView textDiasJuntos;
    private TextView textEntrenamientosJuntos;

    // Vistas - Resumen General
    private TextView textTotalEntrenamientos;
    private TextView textRachaActual;
    private TextView textEntrenamientosMes;
    private TextView textTendencia;

    // Vistas - Gráficas
    private BarChart chartFrecuencia;
    private PieChart chartDeportes;

    // Vistas - Estados
    private ProgressBar progressBar;
    private View layoutContent;
    private View layoutError;
    private TextView textError;

    /**
     * Crea una nueva instancia del fragment con los datos del alumno.
     */
    public static DetalleAlumnoFragment newInstance(String usuario, String nombre, String foto) {
        DetalleAlumnoFragment fragment = new DetalleAlumnoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USUARIO, usuario);
        args.putString(ARG_NOMBRE, nombre);
        args.putString(ARG_FOTO, foto);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            usuarioAlumno = getArguments().getString(ARG_USUARIO);
            nombreAlumno = getArguments().getString(ARG_NOMBRE);
            fotoAlumno = getArguments().getString(ARG_FOTO);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_alumno_detalle_estadisticas, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Inicializar vistas
        inicializarVistas(view);

        // Inicializar ViewModel
        viewModel = new ViewModelProvider(requireActivity()).get(EstadisticasEntrenadorViewModel.class);

        // Inicializar ApiService si no está inicializado
        ApiService apiService = RetrofitClient.getClient(requireContext()).create(ApiService.class);
        viewModel.inicializarApiService(apiService);

        // Configurar header
        configurarHeader();

        // Configurar gráficas
        configurarGraficas();

        // Configurar botón volver
        btnVolver.setOnClickListener(v -> {
            if (getActivity() != null) {
                // Navegar de regreso al fragment de estadísticas
                EstadisticasEntrenadorFragment estadisticasFragment = new EstadisticasEntrenadorFragment();
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.nav_host_fragment_activity_main, estadisticasFragment)
                        .commit();
            }
        });

        // Observar ViewModel
        observarViewModel();

        // Cargar datos del alumno
        viewModel.cargarEstadisticasCompletasAlumno(usuarioAlumno);
    }

    /**
     * Inicializa todas las vistas del layout.
     */
    private void inicializarVistas(View view) {
        // Header
        btnVolver = view.findViewById(R.id.btn_volver);
        imgFotoPerfil = view.findViewById(R.id.img_foto_perfil);
        textNombreAlumno = view.findViewById(R.id.text_nombre_alumno);
        textDiasJuntos = view.findViewById(R.id.text_dias_juntos);
        textEntrenamientosJuntos = view.findViewById(R.id.text_entrenamientos_juntos);

        // Resumen General
        textTotalEntrenamientos = view.findViewById(R.id.text_total_entrenamientos);
        textRachaActual = view.findViewById(R.id.text_racha_actual);
        textEntrenamientosMes = view.findViewById(R.id.text_entrenamientos_mes);
        textTendencia = view.findViewById(R.id.text_tendencia);

        // Gráficas
        chartFrecuencia = view.findViewById(R.id.chart_frecuencia);
        chartDeportes = view.findViewById(R.id.chart_deportes);

        // Estados
        progressBar = view.findViewById(R.id.progress_bar);
        layoutContent = view.findViewById(R.id.layout_content);
        layoutError = view.findViewById(R.id.layout_error);
        textError = view.findViewById(R.id.text_error);
    }

    /**
     * Configura el header con la información básica del alumno.
     */
    private void configurarHeader() {
        textNombreAlumno.setText(nombreAlumno);

        if (fotoAlumno != null && !fotoAlumno.isEmpty()) {
            Picasso.get()
                    .load(fotoAlumno)
                    .placeholder(R.drawable.ic_perfil)
                    .error(R.drawable.ic_perfil)
                    .into(imgFotoPerfil);
        } else {
            imgFotoPerfil.setImageResource(R.drawable.ic_perfil);
        }
    }

    /**
     * Configura las gráficas (BarChart y PieChart).
     */
    private void configurarGraficas() {
        // Configurar BarChart (Frecuencia)
        chartFrecuencia.setDrawGridBackground(false);
        chartFrecuencia.getDescription().setEnabled(false);
        chartFrecuencia.setTouchEnabled(true);
        chartFrecuencia.setDragEnabled(true);
        chartFrecuencia.setScaleEnabled(false);
        chartFrecuencia.setPinchZoom(false);
        chartFrecuencia.setDrawBarShadow(false);
        chartFrecuencia.setDrawValueAboveBar(true);
        chartFrecuencia.animateY(1000);

        // XAxis (abajo)
        XAxis xAxis = chartFrecuencia.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);

        // YAxis izquierdo
        YAxis leftAxis = chartFrecuencia.getAxisLeft();
        leftAxis.setDrawGridLines(true);
        leftAxis.setGranularity(1f);
        leftAxis.setAxisMinimum(0f);

        // YAxis derecho (deshabilitado)
        chartFrecuencia.getAxisRight().setEnabled(false);

        // Configurar PieChart (Deportes)
        chartDeportes.setUsePercentValues(false);
        chartDeportes.getDescription().setEnabled(false);
        chartDeportes.setDrawHoleEnabled(true);
        chartDeportes.setHoleRadius(50f);
        chartDeportes.setTransparentCircleRadius(55f);
        chartDeportes.setRotationEnabled(true);
        chartDeportes.setHighlightPerTapEnabled(true);
        chartDeportes.animateY(1000);

        // Leyenda
        chartDeportes.getLegend().setEnabled(true);
        chartDeportes.getLegend().setTextSize(12f);
    }

    /**
     * Observa los cambios en el ViewModel.
     */
    private void observarViewModel() {
        // Observar detalle del alumno
        viewModel.detalleAlumno.observe(getViewLifecycleOwner(), detalle -> {
            if (detalle != null) {
                actualizarUI(detalle);
            }
        });

        // Observar frecuencia
        viewModel.frecuenciaAlumno.observe(getViewLifecycleOwner(), frecuencia -> {
            if (frecuencia != null) {
                actualizarGraficaFrecuencia(frecuencia);
            }
        });

        // Observar distribución de deportes
        viewModel.distribucionAlumno.observe(getViewLifecycleOwner(), distribucion -> {
            if (distribucion != null) {
                actualizarGraficaDeportes(distribucion);
            }
        });

        // Observar estado de carga
        viewModel.isLoading.observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading != null) {
                if (isLoading) {
                    progressBar.setVisibility(View.VISIBLE);
                    layoutContent.setVisibility(View.GONE);
                    layoutError.setVisibility(View.GONE);
                } else {
                    progressBar.setVisibility(View.GONE);
                    layoutContent.setVisibility(View.VISIBLE);
                }
            }
        });

        // Observar errores
        viewModel.error.observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                mostrarError(error);
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
                viewModel.clearError();
            }
        });
    }

    /**
     * Actualiza la UI con los datos del detalle del alumno.
     */
    private void actualizarUI(DetalleEstadisticasAlumnoDTO detalle) {
        // Información de relación
        if (detalle.getDiasJuntos() != null) {
            textDiasJuntos.setText(detalle.getDiasJuntos() + " días juntos");
        }
        if (detalle.getEntrenamientosJuntos() != null) {
            textEntrenamientosJuntos.setText(detalle.getEntrenamientosJuntos() + " entrenamientos");
        }

        // Resumen general
        StatisticsOverviewDTO resumen = detalle.getResumenGeneral();
        if (resumen != null) {
            textTotalEntrenamientos.setText(String.valueOf(
                    resumen.getTotalEntrenamientos() != null ? resumen.getTotalEntrenamientos() : 0
            ));

            String rachaTexto = (resumen.getRachaActual() != null ? resumen.getRachaActual() : 0) + " días";
            textRachaActual.setText(rachaTexto);

            textEntrenamientosMes.setText(String.valueOf(
                    resumen.getEntrenamientosMesActual() != null ? resumen.getEntrenamientosMesActual() : 0
            ));

            // Tendencia
            String tendenciaTexto = obtenerTextoTendencia(resumen.getTendencia(), resumen.getPorcentajeCambio());
            textTendencia.setText(tendenciaTexto);
            textTendencia.setTextColor(obtenerColorTendencia(resumen.getTendencia()));
        }
    }

    /**
     * Actualiza la gráfica de frecuencia de entrenamientos.
     */
    private void actualizarGraficaFrecuencia(TrainingFrequencyDTO frecuencia) {
        if (frecuencia.getDataPoints() == null || frecuencia.getDataPoints().isEmpty()) {
            chartFrecuencia.clear();
            chartFrecuencia.setNoDataText("Sin datos de frecuencia");
            return;
        }

        List<BarEntry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        for (int i = 0; i < frecuencia.getDataPoints().size(); i++) {
            TrainingFrequencyDTO.DataPoint punto = frecuencia.getDataPoints().get(i);
            entries.add(new BarEntry(i, punto.getValor() != null ? punto.getValor() : 0));
            labels.add(punto.getEtiqueta() != null ? punto.getEtiqueta() : "");
        }

        BarDataSet dataSet = new BarDataSet(entries, "Entrenamientos");
        dataSet.setColor(Color.parseColor("#4F46E5"));
        dataSet.setValueTextSize(10f);
        dataSet.setValueTextColor(Color.BLACK);

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.8f);

        chartFrecuencia.setData(barData);
        chartFrecuencia.getXAxis().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int index = (int) value;
                return index >= 0 && index < labels.size() ? labels.get(index) : "";
            }
        });
        chartFrecuencia.invalidate();
    }

    /**
     * Actualiza la gráfica de distribución de deportes.
     */
    private void actualizarGraficaDeportes(SportsDistributionDTO distribucion) {
        if (distribucion.getDeportes() == null || distribucion.getDeportes().isEmpty()) {
            chartDeportes.clear();
            chartDeportes.setNoDataText("Sin datos de deportes");
            return;
        }

        List<PieEntry> entries = new ArrayList<>();
        List<Integer> colors = new ArrayList<>();

        for (SportsDistributionDTO.SportData deporte : distribucion.getDeportes()) {
            entries.add(new PieEntry(
                    deporte.getCantidadEntrenamientos() != null ? deporte.getCantidadEntrenamientos() : 0,
                    deporte.getNombreDeporte()
            ));

            // Color del deporte
            if (deporte.getColor() != null) {
                try {
                    colors.add(Color.parseColor(deporte.getColor()));
                } catch (Exception e) {
                    colors.add(Color.parseColor("#6B7280"));
                }
            } else {
                colors.add(Color.parseColor("#6B7280"));
            }
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(colors);
        dataSet.setValueTextSize(12f);
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return ((int) value) + "";
            }
        });

        PieData pieData = new PieData(dataSet);
        chartDeportes.setData(pieData);

        // Texto central
        if (distribucion.getDeportePrincipal() != null) {
            chartDeportes.setCenterText(distribucion.getDeportePrincipal());
            chartDeportes.setCenterTextSize(14f);
        }

        chartDeportes.invalidate();
    }

    /**
     * Obtiene el texto formateado de la tendencia.
     */
    private String obtenerTextoTendencia(String tendencia, Double porcentaje) {
        if (tendencia == null) return "→ Estable";

        String simbolo;
        switch (tendencia.toLowerCase()) {
            case "mejorando":
                simbolo = "↑";
                break;
            case "decreciendo":
                simbolo = "↓";
                break;
            default:
                simbolo = "→";
        }

        if (porcentaje != null && porcentaje != 0) {
            return simbolo + " " + String.format("%.1f%%", Math.abs(porcentaje));
        }
        return simbolo + " " + tendencia;
    }

    /**
     * Obtiene el color según la tendencia.
     */
    private int obtenerColorTendencia(String tendencia) {
        if (tendencia == null) return Color.parseColor("#6B7280");

        switch (tendencia.toLowerCase()) {
            case "mejorando":
                return Color.parseColor("#10B981");
            case "decreciendo":
                return Color.parseColor("#EF4444");
            default:
                return Color.parseColor("#6B7280");
        }
    }

    /**
     * Muestra el estado de error.
     */
    private void mostrarError(String mensaje) {
        layoutContent.setVisibility(View.GONE);
        layoutError.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        textError.setText(mensaje);
    }
}