package com.example.sportine.ui.usuarios.home;

import android.content.Intent;
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
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.sportine.R;
import com.example.sportine.models.EntrenamientoDelDiaDTO;
import com.google.android.material.card.MaterialCardView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Fragment para la pantalla de inicio del alumno
 * Muestra:
 * - Saludo personalizado
 * - Fecha actual
 * - Lista de entrenamientos del día
 */
public class HomeFragment extends Fragment {

    // ViewModel
    private HomeViewModel viewModel;

    // Views
    private TextView textSaludo;
    private TextView textMensaje;
    private TextView textFecha;
    private TextView textEntrenamientos;
    private RecyclerView recyclerDeportes;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private MaterialCardView cardBienvenida;
    private View layoutEmpty;
    private TextView textEmpty;

    // Adapter
    private EntrenamientosAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_alumno_home, container, false);

        // Inicializar ViewModel
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        // Inicializar vistas
        inicializarVistas(root);

        // Configurar RecyclerView
        configurarRecyclerView();

        // Configurar fecha actual
        configurarFecha();

        // Observar cambios en el ViewModel
        observarViewModel();

        // Configurar SwipeRefreshLayout
        configurarSwipeRefresh();

        // Cargar datos
        viewModel.cargarHomeAlumno();

        return root;
    }

    private void inicializarVistas(View root) {
        cardBienvenida = root.findViewById(R.id.card_bienvenida);
        textSaludo = root.findViewById(R.id.text_saludo);
        textMensaje = root.findViewById(R.id.text_mensaje);
        textFecha = root.findViewById(R.id.text_fecha);
        textEntrenamientos = root.findViewById(R.id.text_entrenamientos);
        recyclerDeportes = root.findViewById(R.id.recycler_deportes);

        // Opcional: si tienes un ProgressBar para loading
        // progressBar = root.findViewById(R.id.progress_bar);

        // Opcional: si tienes SwipeRefreshLayout
        // swipeRefreshLayout = root.findViewById(R.id.swipe_refresh);
    }

    private void configurarRecyclerView() {
        adapter = new EntrenamientosAdapter(requireContext());

        recyclerDeportes.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerDeportes.setAdapter(adapter);
        recyclerDeportes.setHasFixedSize(true);

        // Configurar click listener
        adapter.setOnEntrenamientoClickListener(entrenamiento -> {
            // Navegar al detalle del entrenamiento
            navegarADetalle(entrenamiento);
        });
    }

    private void configurarFecha() {
        // Formatear fecha actual en español
        SimpleDateFormat formatoFecha = new SimpleDateFormat(
                "EEEE, d 'de' MMMM 'de' yyyy",
                new Locale("es", "ES")
        );
        String fechaActual = formatoFecha.format(new Date());

        // Capitalizar primera letra del día
        fechaActual = fechaActual.substring(0, 1).toUpperCase() + fechaActual.substring(1);

        textFecha.setText(fechaActual);
    }

    private void observarViewModel() {
        // Observar saludo
        viewModel.getSaludo().observe(getViewLifecycleOwner(), saludo -> {
            if (saludo != null) {
                textSaludo.setText(saludo);
            }
        });

        // Observar mensaje dinámico
        viewModel.getMensajeDinamico().observe(getViewLifecycleOwner(), mensaje -> {
            if (mensaje != null) {
                textMensaje.setText(mensaje);
            }
        });

        // Observar entrenamientos
        viewModel.getEntrenamientos().observe(getViewLifecycleOwner(), entrenamientos -> {
            if (entrenamientos != null) {
                adapter.setEntrenamientos(entrenamientos);

                // Mostrar mensaje si no hay entrenamientos
                if (entrenamientos.isEmpty()) {
                    mostrarMensajeVacio();
                } else {
                    ocultarMensajeVacio();
                }
            }
        });

        // Observar estado de carga
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading != null) {
                // Mostrar/ocultar indicador de carga
                // if (progressBar != null) {
                //     progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
                // }

                // Si tienes SwipeRefreshLayout
                // if (swipeRefreshLayout != null) {
                //     swipeRefreshLayout.setRefreshing(isLoading);
                // }
            }
        });

        // Observar errores
        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void configurarSwipeRefresh() {
        // Si tienes SwipeRefreshLayout, descomenta esto:
        /*
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setOnRefreshListener(() -> {
                viewModel.refrescarDatos();
            });
        }
        */
    }

    // En HomeFragment.java

    private void navegarADetalle(EntrenamientoDelDiaDTO entrenamiento) {
        // 1. Crear el paquete de datos (Bundle)
        Bundle args = new Bundle();
        // Pasamos el ID con la clave exacta que espera el otro fragmento
        args.putInt("idEntrenamiento", entrenamiento.getIdEntrenamiento());

        // Opcional: Pasar título para mostrarlo mientras carga
        args.putString("titulo", entrenamiento.getTitulo());

        // 2. Ejecutar la navegación real
        try {
            Navigation.findNavController(requireView()).navigate(
                    R.id.detallesEntrenamientoFragment, // El ID de tu fragmento destino en mobile_navigation.xml
                    args
            );
        } catch (Exception e) {
            // Por si el ID del fragmento no coincide en tu navigation graph
            e.printStackTrace();
            Toast.makeText(requireContext(), "Error al navegar: Verifica tu mobile_navigation.xml", Toast.LENGTH_LONG).show();
        }
    }

    private void mostrarMensajeVacio() {
        // Opcional: mostrar un mensaje cuando no hay entrenamientos
        // layoutEmpty.setVisibility(View.VISIBLE);
        // recyclerDeportes.setVisibility(View.GONE);
    }

    private void ocultarMensajeVacio() {
        // layoutEmpty.setVisibility(View.GONE);
        // recyclerDeportes.setVisibility(View.VISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refrescar datos cuando el usuario vuelve a esta pantalla
        // viewModel.refrescarDatos();
    }
}