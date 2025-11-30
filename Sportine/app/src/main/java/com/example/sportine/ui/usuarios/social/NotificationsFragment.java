package com.example.sportine.ui.usuarios.social;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation; // Importante para el popBackStack
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.sportine.R;
import com.example.sportine.data.ApiService;
import com.example.sportine.data.RetrofitClient;
import com.example.sportine.models.Notificacion;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationsFragment extends Fragment {

    private RecyclerView recyclerView;
    private NotificationsAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout emptyStateLayout;
    private ApiService apiService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflamos el layout
        View view = inflater.inflate(R.layout.fragment_alumno_notifications, container, false);

        // Inicializamos API
        apiService = RetrofitClient.getClient(requireContext()).create(ApiService.class);

        // Vinculamos vistas
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_notifications);
        emptyStateLayout = view.findViewById(R.id.ll_empty_notifications);
        recyclerView = view.findViewById(R.id.rv_notifications);

        // Configurar RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new NotificationsAdapter(new ArrayList<>(), getContext());
        recyclerView.setAdapter(adapter);

        // Configurar Refresh (Jalar para actualizar)
        swipeRefreshLayout.setOnRefreshListener(this::cargarNotificaciones);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // --- CONFIGURACIÓN DE LA TOOLBAR ---
        MaterialToolbar toolbar = view.findViewById(R.id.toolbar_notifications);

        // Nota: No ponemos .setNavigationIcon() porque ya lo pusiste en el XML (@drawable/ic_flechaatras)

        // Configuramos la acción al dar clic en la flecha:
        toolbar.setNavigationOnClickListener(v ->
                Navigation.findNavController(view).popBackStack() // Regresa a la pantalla anterior
        );

        // Cargar datos al iniciar
        swipeRefreshLayout.setRefreshing(true);
        cargarNotificaciones();
    }

    private void cargarNotificaciones() {
        apiService.obtenerNotificaciones().enqueue(new Callback<List<Notificacion>>() {
            @Override
            public void onResponse(Call<List<Notificacion>> call, Response<List<Notificacion>> response) {
                swipeRefreshLayout.setRefreshing(false);

                if (response.isSuccessful() && response.body() != null) {
                    List<Notificacion> lista = response.body();

                    if (lista.isEmpty()) {
                        // Si no hay notificaciones: Muestra Empty State
                        recyclerView.setVisibility(View.GONE);
                        emptyStateLayout.setVisibility(View.VISIBLE);
                    } else {
                        // Si hay datos: Muestra lista
                        recyclerView.setVisibility(View.VISIBLE);
                        emptyStateLayout.setVisibility(View.GONE);
                        adapter.setLista(lista);
                    }
                } else {
                    Toast.makeText(getContext(), "Error al cargar notificaciones", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Notificacion>> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(getContext(), "Sin conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }
}