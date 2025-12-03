package com.example.sportine.ui.entrenadores.feedback;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.sportine.R;
import com.example.sportine.data.ApiService;
import com.example.sportine.data.RetrofitClient;
import com.example.sportine.models.FeedbackResumenDTO;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FeedbackFragment extends Fragment {

    private RecyclerView recyclerView;
    private FeedbackAdapter adapter;
    private View layoutVacio;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_entrenador_feedback_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Botón atrás
        view.findViewById(R.id.btn_back).setOnClickListener(v -> {
            if (getActivity() != null) getActivity().onBackPressed();
        });

        layoutVacio = view.findViewById(R.id.layout_vacio);
        recyclerView = view.findViewById(R.id.recycler_feedback);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new FeedbackAdapter();
        recyclerView.setAdapter(adapter);

        cargarDatos();
    }

    private void cargarDatos() {
        ApiService api = RetrofitClient.getClient(getContext()).create(ApiService.class);

        api.obtenerFeedbackEntrenador().enqueue(new Callback<List<FeedbackResumenDTO>>() {
            @Override
            public void onResponse(Call<List<FeedbackResumenDTO>> call, Response<List<FeedbackResumenDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<FeedbackResumenDTO> datos = response.body();

                    if (datos.isEmpty()) {
                        layoutVacio.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    } else {
                        layoutVacio.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        adapter.setList(datos);
                    }
                } else {
                    Toast.makeText(getContext(), "Error al cargar comentarios", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<FeedbackResumenDTO>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }
}