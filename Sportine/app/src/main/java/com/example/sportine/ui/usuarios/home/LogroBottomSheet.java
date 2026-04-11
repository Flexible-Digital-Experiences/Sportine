package com.example.sportine.ui.usuarios.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.sportine.R;
import com.example.sportine.data.ApiService;
import com.example.sportine.data.RetrofitClient;
import com.example.sportine.models.LogroDTO;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Bottom sheet que muestra los logros desbloqueados pendientes de ver.
 * Permite navegar entre múltiples logros y decidir si publicarlos.
 * Se dispara desde HomeFragment.onResume cuando hay logros pendientes.
 */
public class LogroBottomSheet extends BottomSheetDialogFragment {

    private static final String TAG = "LogroBottomSheet";
    private static final String ARG_LOGROS = "logros";

    private List<LogroDTO> logros;
    private int indiceActual = 0;

    private TextView tvMensaje;
    private TextView tvContador;
    private TextView tvPaginacion;
    private MaterialButton btnPublicar;
    private MaterialButton btnGuardarDespues;
    private MaterialButton btnAnterior;
    private MaterialButton btnSiguiente;
    private View layoutNavegacion;

    private ApiService apiService;

    public static LogroBottomSheet newInstance(List<LogroDTO> logros) {
        LogroBottomSheet sheet = new LogroBottomSheet();
        Bundle args = new Bundle();
        args.putSerializable(ARG_LOGROS, new ArrayList<>(logros));
        sheet.setArguments(args);
        return sheet;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //noinspection unchecked
            logros = (List<LogroDTO>) getArguments().getSerializable(ARG_LOGROS);
        }
        if (logros == null) logros = new ArrayList<>();
        apiService = RetrofitClient.getClient(requireContext()).create(ApiService.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_logro, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvMensaje         = view.findViewById(R.id.tv_mensaje_logro);
        tvContador        = view.findViewById(R.id.tv_contador_logros);
        tvPaginacion      = view.findViewById(R.id.tv_paginacion);
        btnPublicar       = view.findViewById(R.id.btn_publicar_logro);
        btnGuardarDespues = view.findViewById(R.id.btn_guardar_despues);
        btnAnterior       = view.findViewById(R.id.btn_anterior);
        btnSiguiente      = view.findViewById(R.id.btn_siguiente);
        layoutNavegacion  = view.findViewById(R.id.layout_navegacion);

        configurarUI();
        mostrarLogro(indiceActual);

        btnPublicar.setOnClickListener(v -> publicarLogroActual());
        btnGuardarDespues.setOnClickListener(v -> guardarParaDespues());
        btnAnterior.setOnClickListener(v -> {
            if (indiceActual > 0) { indiceActual--; mostrarLogro(indiceActual); }
        });
        btnSiguiente.setOnClickListener(v -> {
            if (indiceActual < logros.size() - 1) { indiceActual++; mostrarLogro(indiceActual); }
        });
    }

    private void configurarUI() {
        if (logros.size() > 1) {
            tvContador.setVisibility(View.VISIBLE);
            tvContador.setText("🎉 +" + logros.size() + " logros nuevos");
            layoutNavegacion.setVisibility(View.VISIBLE);
        } else {
            tvContador.setVisibility(View.GONE);
            layoutNavegacion.setVisibility(View.GONE);
        }
    }

    private void mostrarLogro(int indice) {
        if (indice < 0 || indice >= logros.size()) return;
        LogroDTO logro = logros.get(indice);
        tvMensaje.setText(logro.getMensaje());

        if (logros.size() > 1) {
            tvPaginacion.setText((indice + 1) + " / " + logros.size());
            btnAnterior.setEnabled(indice > 0);
            btnSiguiente.setEnabled(indice < logros.size() - 1);
        }

        // Si este logro ya fue publicado, cambiar el botón
        boolean yaPublicado = logro.getPublicado() != null && logro.getPublicado();
        btnPublicar.setEnabled(!yaPublicado);
        btnPublicar.setText(yaPublicado ? "Ya publicado ✓" : "Publicar en el feed 🏆");
    }

    private void publicarLogroActual() {
        if (indiceActual >= logros.size()) return;
        LogroDTO logro = logros.get(indiceActual);

        apiService.publicarLogro(logro.getIdLogro()).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call,
                                   Response<Map<String, Object>> response) {
                if (response.isSuccessful()) {
                    logro.setPublicado(true);
                    Toast.makeText(getContext(),
                            "¡Logro publicado en el feed! 🏆", Toast.LENGTH_SHORT).show();
                    mostrarLogro(indiceActual);
                    avanzarOCerrar();
                } else {
                    Toast.makeText(getContext(),
                            "Error al publicar", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Toast.makeText(getContext(),
                        "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void guardarParaDespues() {
        // Solo marcar como visto el logro actual y avanzar
        marcarVistoYAvanzar();
    }

    private void avanzarOCerrar() {
        if (indiceActual < logros.size() - 1) {
            indiceActual++;
            mostrarLogro(indiceActual);
        } else {
            marcarTodosVistos();
            dismiss();
        }
    }

    private void marcarVistoYAvanzar() {
        // Marcar solo el actual como visto
        List<Integer> ids = List.of(logros.get(indiceActual).getIdLogro());
        marcarVistos(ids);

        if (indiceActual < logros.size() - 1) {
            indiceActual++;
            mostrarLogro(indiceActual);
        } else {
            dismiss();
        }
    }

    private void marcarTodosVistos() {
        List<Integer> ids = logros.stream()
                .map(LogroDTO::getIdLogro)
                .collect(Collectors.toList());
        marcarVistos(ids);
    }

    private void marcarVistos(List<Integer> ids) {
        Map<String, List<Integer>> body = new HashMap<>();
        body.put("ids", ids);
        apiService.marcarLogrosVistos(body).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call,
                                   Response<Map<String, Object>> response) {
                Log.d(TAG, "Logros marcados como vistos: " + ids);
            }
            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Log.w(TAG, "No se pudieron marcar logros como vistos: " + t.getMessage());
            }
        });
    }

    @Override
    public void onDismiss(@NonNull android.content.DialogInterface dialog) {
        super.onDismiss(dialog);
        // Al cerrar sin interactuar, marcar todos como vistos
        marcarTodosVistos();
    }
}