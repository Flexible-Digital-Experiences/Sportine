package com.example.sportine.ui.usuarios.social;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.sportine.R;
import com.example.sportine.data.ApiService;
import com.example.sportine.data.RetrofitClient;
import com.example.sportine.models.PublicacionFeedDTO;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SocialFragment extends Fragment implements CreatePostBottomSheetFragment.OnPostPublishedListener {

    private RecyclerView recyclerView;
    private SocialFeedAdapter adapter;
    private List<PublicacionFeedDTO> publicacionList;
    private ApiService apiService;

    private SwipeRefreshLayout swipeRefreshLayout;

    private LinearLayout llEmptyState;
    private Button btnBuscarAmigosEmpty;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alumno_social, container, false);

        apiService = RetrofitClient.getClient(requireContext()).create(ApiService.class);

        // 1. Vincular Vistas
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        llEmptyState = view.findViewById(R.id.ll_empty_state);
        btnBuscarAmigosEmpty = view.findViewById(R.id.btn_buscar_amigos_empty);

        recyclerView = view.findViewById(R.id.rv_social_feed);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // 2. Configurar Adapter
        publicacionList = new ArrayList<>();
        adapter = new SocialFeedAdapter(publicacionList, requireContext(), apiService);
        recyclerView.setAdapter(adapter);

        // 3. Configurar SwipeRefresh (Jalar para recargar)
        swipeRefreshLayout.setOnRefreshListener(this::cargarFeed);

        // 4. Configurar Botón del Empty State
        btnBuscarAmigosEmpty.setOnClickListener(v ->
                Navigation.findNavController(view).navigate(R.id.action_social_to_buscar_amigo)
        );

        MaterialCardView cardCreatePostTrigger = view.findViewById(R.id.card_create_post_trigger);
        cardCreatePostTrigger.setOnClickListener(v -> showCreatePostDialog());

        ImageView addFriendIcon = view.findViewById(R.id.iv_add_friend);
        addFriendIcon.setOnClickListener(v -> Navigation.findNavController(view).navigate(R.id.action_social_to_buscar_amigo));

        ImageView removeFriendIcon = view.findViewById(R.id.iv_remove_friend);
        removeFriendIcon.setOnClickListener(v -> Navigation.findNavController(view).navigate(R.id.action_social_to_lista_amigos));

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Cargar feed automáticamente al entrar
        swipeRefreshLayout.setRefreshing(true);
        cargarFeed();
    }

    private void cargarFeed() {
        apiService.getSocialFeed().enqueue(new Callback<List<PublicacionFeedDTO>>() {
            @Override
            public void onResponse(Call<List<PublicacionFeedDTO>> call, Response<List<PublicacionFeedDTO>> response) {
                // Apagar la ruedita de carga
                swipeRefreshLayout.setRefreshing(false);

                if (response.isSuccessful() && response.body() != null) {
                    List<PublicacionFeedDTO> posts = response.body();

                    // --- AQUÍ ESTÁ EL REVERSE ---
                    // Como en tu backend usaste ASC (viejos primero),
                    // aquí los volteamos para ver los NUEVOS primero.
                    Collections.reverse(posts);

                    adapter.setPublicaciones(posts);

                    // --- LÓGICA DEL EMPTY STATE ---
                    if (posts.isEmpty()) {
                        // No hay posts: Muestra dibujo, oculta lista
                        llEmptyState.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    } else {
                        // Sí hay posts: Oculta dibujo, muestra lista
                        llEmptyState.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }

                } else {
                    Toast.makeText(getContext(), "Error al cargar: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<PublicacionFeedDTO>> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(getContext(), "Sin conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showCreatePostDialog() {
        CreatePostBottomSheetFragment bottomSheet = new CreatePostBottomSheetFragment();
        bottomSheet.setOnPostPublishedListener(this);
        bottomSheet.show(getParentFragmentManager(), "CreatePostBottomSheet");
    }

    @Override
    public void onPostPublished(String content) {
        Toast.makeText(getContext(), "Publicado", Toast.LENGTH_SHORT).show();
        swipeRefreshLayout.setRefreshing(true);
        cargarFeed();
    }
}