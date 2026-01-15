package com.example.sportine.ui.entrenadores.estadisticas;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.sportine.R;
import com.example.sportine.data.ApiService;
import com.example.sportine.data.RetrofitClient;
import com.example.sportine.models.AlumnoCardStatsDTO;

/**
 * Fragment que muestra la lista de alumnos del entrenador con sus estadísticas.
 * Permite ver métricas resumidas y navegar al detalle de cada alumno.
 */
public class EstadisticasEntrenadorFragment extends Fragment implements AlumnosStatsAdapter.OnAlumnoClickListener {

    private static final String TAG = "EstadisticasEntrenador";

    // ViewModel
    private EstadisticasEntrenadorViewModel viewModel;

    // Vistas
    private RecyclerView recyclerView;
    private AlumnosStatsAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;
    private View layoutEmpty;
    private View layoutError;
    private TextView textError;
    private TextView textTotalAlumnos;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_entrenador_estadisticas, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Inicializar vistas
        inicializarVistas(view);

        // Inicializar ViewModel
        viewModel = new ViewModelProvider(this).get(EstadisticasEntrenadorViewModel.class);

        // Inicializar ApiService
        ApiService apiService = RetrofitClient.getClient(requireContext()).create(ApiService.class);
        viewModel.inicializarApiService(apiService);

        // Configurar RecyclerView
        configurarRecyclerView();

        // Configurar SwipeRefreshLayout
        configurarSwipeRefresh();

        // Observar LiveData
        observarViewModel();

        // Cargar datos iniciales
        viewModel.cargarListaAlumnos();
    }

    /**
     * Inicializa todas las vistas del layout.
     */
    private void inicializarVistas(View view) {
        recyclerView = view.findViewById(R.id.recycler_alumnos);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
        progressBar = view.findViewById(R.id.progress_bar);
        layoutEmpty = view.findViewById(R.id.layout_empty);
        layoutError = view.findViewById(R.id.layout_error);
        textError = view.findViewById(R.id.text_error);
        textTotalAlumnos = view.findViewById(R.id.text_total_alumnos);
    }

    /**
     * Configura el RecyclerView con el adapter.
     */
    private void configurarRecyclerView() {
        adapter = new AlumnosStatsAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
    }

    /**
     * Configura el SwipeRefreshLayout para refrescar la lista.
     */
    private void configurarSwipeRefresh() {
        swipeRefreshLayout.setColorSchemeResources(
                R.color.colorAccent,
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light
        );

        swipeRefreshLayout.setOnRefreshListener(() -> {
            viewModel.refrescarListaAlumnos();
        });
    }

    /**
     * Observa los cambios en el ViewModel.
     */
    private void observarViewModel() {
        // Observar lista de alumnos
        viewModel.listaAlumnos.observe(getViewLifecycleOwner(), alumnos -> {
            if (alumnos != null && !alumnos.isEmpty()) {
                adapter.setAlumnos(alumnos);
                mostrarLista();
                actualizarContadorAlumnos(alumnos.size());
            } else {
                mostrarEstadoVacio();
            }
        });

        // Observar estado de carga
        viewModel.isLoading.observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading != null) {
                if (isLoading && !swipeRefreshLayout.isRefreshing()) {
                    progressBar.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                    layoutEmpty.setVisibility(View.GONE);
                    layoutError.setVisibility(View.GONE);
                } else {
                    progressBar.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(false);
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
     * Muestra la lista de alumnos (oculta otros estados).
     */
    private void mostrarLista() {
        recyclerView.setVisibility(View.VISIBLE);
        layoutEmpty.setVisibility(View.GONE);
        layoutError.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
    }

    /**
     * Muestra el estado vacío (sin alumnos).
     */
    private void mostrarEstadoVacio() {
        recyclerView.setVisibility(View.GONE);
        layoutEmpty.setVisibility(View.VISIBLE);
        layoutError.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        actualizarContadorAlumnos(0);
    }

    /**
     * Muestra el estado de error.
     */
    private void mostrarError(String mensaje) {
        recyclerView.setVisibility(View.GONE);
        layoutEmpty.setVisibility(View.GONE);
        layoutError.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        textError.setText(mensaje);
    }

    /**
     * Actualiza el contador de alumnos en el header.
     */
    private void actualizarContadorAlumnos(int total) {
        String texto = total + (total == 1 ? " alumno" : " alumnos");
        textTotalAlumnos.setText(texto);
    }

    /**
     * Maneja el click en un alumno de la lista.
     * Navega al detalle del alumno.
     */


    /*
    @Override
    public void onAlumnoClick(AlumnoCardStatsDTO alumno) {
        // Crear el fragment de detalle
        DetalleAlumnoFragment detalleFragment = DetalleAlumnoFragment.newInstance(
                alumno.getUsuario(),
                alumno.getNombreCompleto(),
                alumno.getFotoPerfil()
        );

        // Navegar al detalle
        if (getActivity() != null) {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.nav_host_fragment_activity_main, detalleFragment)
                    .addToBackStack(null)
                    .commit();
        }
    }
    */


    @Override
    public void onAlumnoClick(AlumnoCardStatsDTO alumno) {

        Bundle args = new Bundle();
        args.putString("usuario", alumno.getUsuario());
        args.putString("nombre", alumno.getNombreCompleto());
        args.putString("foto", alumno.getFotoPerfil());

        NavHostFragment.findNavController(this)
                .navigate(R.id.action_estadisticas_to_detalleAlumno, args);
    }



    @Override
    public void onResume() {
        super.onResume();
        // Refrescar datos al volver de la pantalla de detalle
        if (viewModel != null) {
            viewModel.limpiarDetalleAlumno();
        }
    }
}