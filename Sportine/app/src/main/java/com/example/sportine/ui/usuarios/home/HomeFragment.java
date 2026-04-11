package com.example.sportine.ui.usuarios.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.sportine.R;
import com.example.sportine.data.ApiService;
import com.example.sportine.data.RetrofitClient;
import com.example.sportine.models.EntrenamientoDelDiaDTO;
import com.example.sportine.models.LogroDTO;
import com.google.android.material.card.MaterialCardView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private HomeViewModel viewModel;
    private ApiService apiService;

    private TextView textSaludo;
    private TextView textMensaje;
    private TextView textFecha;
    private TextView textEntrenamientos;
    private RecyclerView recyclerDeportes;
    private SwipeRefreshLayout swipeRefreshLayout;
    private MaterialCardView cardBienvenida;

    private EntrenamientosAdapter adapter;

    // Evitar mostrar el bottom sheet de logros más de una vez por sesión de la pantalla
    private boolean logrosYaMostrados = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_alumno_home, container, false);

        viewModel  = new ViewModelProvider(this).get(HomeViewModel.class);
        apiService = RetrofitClient.getClient(requireContext()).create(ApiService.class);

        inicializarVistas(root);
        configurarRecyclerView();
        configurarFecha();
        observarViewModel();
        configurarSwipeRefresh();

        viewModel.cargarHomeAlumno();
        return root;
    }

    private void inicializarVistas(View root) {
        cardBienvenida     = root.findViewById(R.id.card_bienvenida);
        textSaludo         = root.findViewById(R.id.text_saludo);
        textMensaje        = root.findViewById(R.id.text_mensaje);
        textFecha          = root.findViewById(R.id.text_fecha);
        textEntrenamientos = root.findViewById(R.id.text_entrenamientos);
        recyclerDeportes   = root.findViewById(R.id.recycler_deportes);
    }

    private void configurarRecyclerView() {
        adapter = new EntrenamientosAdapter(requireContext());
        recyclerDeportes.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerDeportes.setAdapter(adapter);
        recyclerDeportes.setHasFixedSize(true);
        adapter.setOnEntrenamientoClickListener(this::navegarADetalle);
    }

    private void configurarFecha() {
        SimpleDateFormat fmt = new SimpleDateFormat(
                "EEEE, d 'de' MMMM 'de' yyyy", new Locale("es", "ES"));
        String fecha = fmt.format(new Date());
        fecha = fecha.substring(0, 1).toUpperCase() + fecha.substring(1);
        textFecha.setText(fecha);
    }

    private void observarViewModel() {
        viewModel.getSaludo().observe(getViewLifecycleOwner(), s -> {
            if (s != null) textSaludo.setText(s);
        });
        viewModel.getMensajeDinamico().observe(getViewLifecycleOwner(), m -> {
            if (m != null) textMensaje.setText(m);
        });
        viewModel.getEntrenamientos().observe(getViewLifecycleOwner(), lista -> {
            if (lista != null) adapter.setEntrenamientos(lista);
        });
        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty())
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
        });
    }

    private void configurarSwipeRefresh() {
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setOnRefreshListener(() -> {
                logrosYaMostrados = false; // permitir volver a mostrar logros al refrescar
                viewModel.refrescarDatos();
            });
        }
    }

    // ── Logros ────────────────────────────────────────────────────────────────

    @Override
    public void onResume() {
        super.onResume();
        if (viewModel != null) viewModel.cargarHomeAlumno();
        // Consultar logros pendientes solo una vez por visita al Home
        if (!logrosYaMostrados) {
            consultarLogrosPendientes();
        }
    }

    /**
     * Consulta logros no vistos. Si hay alguno, muestra el BottomSheet.
     * Solo se ejecuta una vez por visita para no interrumpir al usuario.
     */
    private void consultarLogrosPendientes() {
        apiService.obtenerLogrosPendientes().enqueue(new Callback<List<LogroDTO>>() {
            @Override
            public void onResponse(Call<List<LogroDTO>> call,
                                   Response<List<LogroDTO>> response) {
                if (response.isSuccessful()
                        && response.body() != null
                        && !response.body().isEmpty()
                        && isAdded()) {

                    logrosYaMostrados = true;
                    List<LogroDTO> logros = response.body();

                    // Pequeño delay para que el Home termine de cargar antes de mostrar el sheet
                    requireView().postDelayed(() -> {
                        if (isAdded() && getChildFragmentManager()
                                .findFragmentByTag("logros") == null) {
                            LogroBottomSheet.newInstance(logros)
                                    .show(getChildFragmentManager(), "logros");
                        }
                    }, 800);
                }
            }

            @Override
            public void onFailure(Call<List<LogroDTO>> call, Throwable t) {
                // Silencioso — los logros no son críticos
            }
        });
    }

    // ── Navegación ────────────────────────────────────────────────────────────

    private void navegarADetalle(EntrenamientoDelDiaDTO entrenamiento) {
        Bundle args = new Bundle();
        args.putInt("idEntrenamiento", entrenamiento.getIdEntrenamiento());
        args.putString("titulo", entrenamiento.getTitulo());
        try {
            Navigation.findNavController(requireView())
                    .navigate(R.id.detallesEntrenamientoFragment, args);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(requireContext(),
                    "Error al navegar", Toast.LENGTH_SHORT).show();
        }
    }
}