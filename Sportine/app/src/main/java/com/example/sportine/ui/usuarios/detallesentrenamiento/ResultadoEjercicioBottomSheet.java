package com.example.sportine.ui.usuarios.detallesentrenamiento;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.sportine.R;
import com.example.sportine.data.ApiService;
import com.example.sportine.data.RetrofitClient;
import com.example.sportine.models.AsignarEjercicioDTO;
import com.example.sportine.models.ResultadoSerieRequest;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResultadoEjercicioBottomSheet extends BottomSheetDialogFragment {

    public interface OnResultadoGuardadoListener {
        void onGuardado(int idAsignado, String nuevoStatus);
    }

    private AsignarEjercicioDTO ejercicio;
    private int numeroSerie;
    private int idDeporte;
    private OnResultadoGuardadoListener listener;

    private String statusActual = "completado";
    private int valRepsIntentadas = 0;
    private float valPesoUsado = 0f;
    private int valDuracionMin = 0;
    private int valDistanciaM = 0;
    private int valExitosos = 0;
    private boolean mostrarExitosos = false;

    private TextView tvNombre, tvEsperado, tvNumeroSerie, tvOmitidoMensaje;
    private LinearLayout layoutIntentados, layoutExitosos;
    private LinearLayout layoutReps, layoutPeso, layoutDuracion, layoutDistancia;
    private TextView tvReps, tvPeso, tvDuracion, tvDistancia, tvExitososValor;
    private MaterialButton btnRepsMenos, btnRepsMas, btnPesoMenos, btnPesoMas;
    private MaterialButton btnDuracionMenos, btnDuracionMas, btnDistanciaMenos, btnDistanciaMas;
    private MaterialButton btnExitososMenos, btnExitososMas;
    private ChipGroup chipGroupStatus;
    private TextInputEditText inputNotas;
    private MaterialButton btnGuardar, btnCancelar;

    public static ResultadoEjercicioBottomSheet newInstance(
            AsignarEjercicioDTO ejercicio, int numeroSerie, int idDeporte) {
        ResultadoEjercicioBottomSheet bs = new ResultadoEjercicioBottomSheet();
        bs.ejercicio = ejercicio;
        bs.numeroSerie = numeroSerie;
        bs.idDeporte = idDeporte;
        return bs;
    }

    public void setOnResultadoGuardadoListener(OnResultadoGuardadoListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_resultados_ejercicio, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        inicializarVistas(view);
        configurarEncabezado();
        configurarChips();
        configurarBotonesNumericos();
        aplicarModoCompletado();
    }

    private void inicializarVistas(View view) {
        tvNombre         = view.findViewById(R.id.tv_nombre_ejercicio_bs);
        tvEsperado       = view.findViewById(R.id.tv_valores_esperados_bs);
        tvNumeroSerie    = view.findViewById(R.id.tv_numero_serie_bs);
        tvOmitidoMensaje = view.findViewById(R.id.tv_omitido_mensaje);
        layoutIntentados = view.findViewById(R.id.layout_intentados);
        layoutExitosos   = view.findViewById(R.id.layout_exitosos);
        layoutReps       = view.findViewById(R.id.layout_reps_resultado);
        layoutPeso       = view.findViewById(R.id.layout_peso_resultado);
        layoutDuracion   = view.findViewById(R.id.layout_duracion_resultado);
        layoutDistancia  = view.findViewById(R.id.layout_distancia_resultado);
        tvReps           = view.findViewById(R.id.tv_reps_valor);
        tvPeso           = view.findViewById(R.id.tv_peso_valor);
        tvDuracion       = view.findViewById(R.id.tv_duracion_valor);
        tvDistancia      = view.findViewById(R.id.tv_distancia_valor);
        tvExitososValor  = view.findViewById(R.id.tv_exitosos_valor);
        btnRepsMenos     = view.findViewById(R.id.btn_reps_menos);
        btnRepsMas       = view.findViewById(R.id.btn_reps_mas);
        btnPesoMenos     = view.findViewById(R.id.btn_peso_menos);
        btnPesoMas       = view.findViewById(R.id.btn_peso_mas);
        btnDuracionMenos = view.findViewById(R.id.btn_duracion_menos);
        btnDuracionMas   = view.findViewById(R.id.btn_duracion_mas);
        btnDistanciaMenos= view.findViewById(R.id.btn_distancia_menos);
        btnDistanciaMas  = view.findViewById(R.id.btn_distancia_mas);
        btnExitososMenos = view.findViewById(R.id.btn_exitosos_menos);
        btnExitososMas   = view.findViewById(R.id.btn_exitosos_mas);
        chipGroupStatus  = view.findViewById(R.id.chip_group_status);
        inputNotas       = view.findViewById(R.id.input_notas_bs);
        btnGuardar       = view.findViewById(R.id.btn_guardar_bs);
        btnCancelar      = view.findViewById(R.id.btn_cancelar_bs);
    }

    private void configurarEncabezado() {
        tvNombre.setText(ejercicio.getNombreEjercicio());
        tvNumeroSerie.setText("Serie " + numeroSerie);

        StringBuilder esperado = new StringBuilder("Esperado: ");
        boolean esCardio = ejercicio.esCardio();

        if (!esCardio) {
            if (ejercicio.getRepeticiones() != null) {
                esperado.append(ejercicio.getRepeticiones()).append(" reps");
                valRepsIntentadas = ejercicio.getRepeticiones();
            }
            if (ejercicio.getPeso() != null && ejercicio.getPeso() > 0) {
                esperado.append(" · ").append(ejercicio.getPeso()).append(" kg");
                valPesoUsado = ejercicio.getPeso();
            }
        } else {
            if (ejercicio.getDuracion() != null) {
                esperado.append(ejercicio.getDuracion()).append(" min");
                valDuracionMin = ejercicio.getDuracion();
            }
            if (ejercicio.getDistancia() != null && ejercicio.getDistancia() > 0) {
                esperado.append(" · ").append((int)(ejercicio.getDistancia() * 1000)).append(" m");
                valDistanciaM = (int)(ejercicio.getDistancia() * 1000);
            }
        }

        tvEsperado.setText(esperado.toString());

        layoutReps.setVisibility(!esCardio && ejercicio.getRepeticiones() != null ? View.VISIBLE : View.GONE);
        layoutPeso.setVisibility(!esCardio && ejercicio.getPeso() != null && ejercicio.getPeso() > 0 ? View.VISIBLE : View.GONE);
        layoutDuracion.setVisibility(esCardio && ejercicio.getDuracion() != null ? View.VISIBLE : View.GONE);
        layoutDistancia.setVisibility(esCardio && ejercicio.getDistancia() != null && ejercicio.getDistancia() > 0 ? View.VISIBLE : View.GONE);
        android.util.Log.d("Exitosos", "tieneExitosos = " + ejercicio.isTieneExitosos()
                + " | nombre = " + ejercicio.getNombreEjercicio());
        // Exitosos solo aplica en deportes de puntaje/anotación, no en gym ni cardio puro
        mostrarExitosos = ejercicio.isTieneExitosos();
        layoutExitosos.setVisibility(mostrarExitosos ? View.VISIBLE : View.GONE);

        actualizarTextos();
    }

    private void configurarChips() {
        chipGroupStatus.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;
            int id = checkedIds.get(0);
            if      (id == R.id.chip_completado) { statusActual = "completado"; aplicarModoCompletado(); }
            else if (id == R.id.chip_parcial)    { statusActual = "parcial";    aplicarModoParcial(); }
            else if (id == R.id.chip_omitido)    { statusActual = "omitido";    aplicarModoOmitido(); }
        });
    }

    private void aplicarModoCompletado() {
        resetearAValoresEsperados();
        actualizarTextos();
        setControlesIntentadosHabilitados(false);
        layoutIntentados.setVisibility(View.VISIBLE);
        layoutExitosos.setVisibility(mostrarExitosos ? View.VISIBLE : View.GONE);
        tvOmitidoMensaje.setVisibility(View.GONE);
    }

    private void aplicarModoParcial() {
        setControlesIntentadosHabilitados(true);
        layoutIntentados.setVisibility(View.VISIBLE);
        layoutExitosos.setVisibility(mostrarExitosos ? View.VISIBLE : View.GONE);
        tvOmitidoMensaje.setVisibility(View.GONE);
    }

    private void aplicarModoOmitido() {
        layoutIntentados.setVisibility(View.GONE);
        layoutExitosos.setVisibility(View.GONE);
        tvOmitidoMensaje.setVisibility(View.VISIBLE);
    }

    private void setControlesIntentadosHabilitados(boolean habilitados) {
        float alpha = habilitados ? 1.0f : 0.5f;
        btnRepsMenos.setEnabled(habilitados);    btnRepsMas.setEnabled(habilitados);
        btnPesoMenos.setEnabled(habilitados);    btnPesoMas.setEnabled(habilitados);
        btnDuracionMenos.setEnabled(habilitados);btnDuracionMas.setEnabled(habilitados);
        btnDistanciaMenos.setEnabled(habilitados);btnDistanciaMas.setEnabled(habilitados);
        btnRepsMenos.setAlpha(alpha);    btnRepsMas.setAlpha(alpha);
        btnPesoMenos.setAlpha(alpha);    btnPesoMas.setAlpha(alpha);
        btnDuracionMenos.setAlpha(alpha);btnDuracionMas.setAlpha(alpha);
        btnDistanciaMenos.setAlpha(alpha);btnDistanciaMas.setAlpha(alpha);
    }

    private void resetearAValoresEsperados() {
        valRepsIntentadas = ejercicio.getRepeticiones() != null ? ejercicio.getRepeticiones() : 0;
        valPesoUsado      = ejercicio.getPeso() != null ? ejercicio.getPeso() : 0f;
        valDuracionMin    = ejercicio.getDuracion() != null ? ejercicio.getDuracion() : 0;
        valDistanciaM     = ejercicio.getDistancia() != null ? (int)(ejercicio.getDistancia() * 1000) : 0;
    }

    private void configurarBotonesNumericos() {
        btnRepsMenos.setOnClickListener(v -> { if (valRepsIntentadas > 0) valRepsIntentadas--; actualizarTextos(); });
        btnRepsMas.setOnClickListener(v   -> { valRepsIntentadas++; actualizarTextos(); });
        btnPesoMenos.setOnClickListener(v -> { if (valPesoUsado >= 0.5f) valPesoUsado -= 0.5f; actualizarTextos(); });
        btnPesoMas.setOnClickListener(v   -> { valPesoUsado += 0.5f; actualizarTextos(); });
        btnDuracionMenos.setOnClickListener(v -> { if (valDuracionMin > 0) valDuracionMin--; actualizarTextos(); });
        btnDuracionMas.setOnClickListener(v   -> { valDuracionMin++; actualizarTextos(); });
        btnDistanciaMenos.setOnClickListener(v -> { if (valDistanciaM >= 50) valDistanciaM -= 50; actualizarTextos(); });
        btnDistanciaMas.setOnClickListener(v   -> { valDistanciaM += 50; actualizarTextos(); });

        btnExitososMenos.setOnClickListener(v -> {
            if (valExitosos > 0) { valExitosos--; tvExitososValor.setText(String.valueOf(valExitosos)); }
        });
        btnExitososMas.setOnClickListener(v -> {
            int max = valRepsIntentadas > 0 ? valRepsIntentadas : Integer.MAX_VALUE;
            if (valExitosos < max) { valExitosos++; tvExitososValor.setText(String.valueOf(valExitosos)); }
        });

        btnCancelar.setOnClickListener(v -> dismiss());
        btnGuardar.setOnClickListener(v  -> guardarResultado());
    }

    private void actualizarTextos() {
        tvReps.setText(String.valueOf(valRepsIntentadas));
        tvPeso.setText(valPesoUsado % 1 == 0 ? String.valueOf((int) valPesoUsado) : String.valueOf(valPesoUsado));
        tvDuracion.setText(String.valueOf(valDuracionMin));
        tvDistancia.setText(String.valueOf(valDistanciaM));
        if (valExitosos > valRepsIntentadas && valRepsIntentadas > 0) {
            valExitosos = valRepsIntentadas;
            tvExitososValor.setText(String.valueOf(valExitosos));
        }
    }

    private void guardarResultado() {
        String notas = inputNotas.getText() != null ? inputNotas.getText().toString().trim() : "";

        ResultadoSerieRequest request = new ResultadoSerieRequest();
        request.setNumeroSerie(numeroSerie);
        request.setStatus(statusActual);
        request.setNotas(notas.isEmpty() ? null : notas);

        if ("omitido".equals(statusActual)) {
            request.setExitosos(0);
        } else if (!ejercicio.esCardio()) {
            request.setRepsCompletadas(valRepsIntentadas);
            request.setPesoUsado(valPesoUsado);
            request.setExitosos(mostrarExitosos ? valExitosos : null);
        } else {
            request.setDuracionCompletadaSeg(valDuracionMin * 60);
            request.setDistanciaCompletadaMetros((double) valDistanciaM);
            request.setExitosos(null);
        }

        btnGuardar.setEnabled(false);
        btnGuardar.setText("Guardando...");

        ApiService api = RetrofitClient.getClient(requireContext()).create(ApiService.class);
        api.guardarResultadoSerie(ejercicio.getIdAsignado(), request)
                .enqueue(new Callback<Map<String, Object>>() {
                    @Override
                    public void onResponse(@NonNull Call<Map<String, Object>> call,
                                           @NonNull Response<Map<String, Object>> response) {
                        if (response.isSuccessful()) {
                            if (listener != null) listener.onGuardado(ejercicio.getIdAsignado(), statusActual);
                            dismiss();
                        } else {
                            btnGuardar.setEnabled(true);
                            btnGuardar.setText("Guardar serie");
                            Toast.makeText(getContext(), "Error (" + response.code() + ")", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(@NonNull Call<Map<String, Object>> call, @NonNull Throwable t) {
                        btnGuardar.setEnabled(true);
                        btnGuardar.setText("Guardar serie");
                        Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}