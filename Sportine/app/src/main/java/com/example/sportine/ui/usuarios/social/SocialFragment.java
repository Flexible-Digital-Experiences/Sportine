package com.example.sportine.ui.usuarios.social;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
// --- ¡IMPORTANTE! ---
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alumno_social, container, false);

        apiService = RetrofitClient.getClient(requireContext()).create(ApiService.class);

        // --- ¡ENLAZAMOS LA VISTA DEL REFRESH! ---
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);

        recyclerView = view.findViewById(R.id.rv_social_feed);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        publicacionList = new ArrayList<>();
        adapter = new SocialFeedAdapter(publicacionList, requireContext(), apiService);
        recyclerView.setAdapter(adapter);

        // --- ¡CONFIGURAMOS EL LISTENER! ---
        swipeRefreshLayout.setOnRefreshListener(() -> {
            // Esto se ejecuta cuando deslizas hacia abajo
            cargarFeed();
        });

        // Botones
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
        // Cargamos al inicio (mostrando la animación)
        swipeRefreshLayout.setRefreshing(true);
        cargarFeed();
    }

    private void cargarFeed() {
        apiService.getSocialFeed().enqueue(new Callback<List<PublicacionFeedDTO>>() {
            @Override
            public void onResponse(Call<List<PublicacionFeedDTO>> call, Response<List<PublicacionFeedDTO>> response) {
                // --- ¡APAGAMOS LA ANIMACIÓN! ---
                swipeRefreshLayout.setRefreshing(false);

                if (response.isSuccessful() && response.body() != null) {
                    List<PublicacionFeedDTO> posts = response.body();
                    Collections.reverse(posts);
                    adapter.setPublicaciones(posts);
                } else {
                    Toast.makeText(getContext(), "Error al cargar el feed: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<PublicacionFeedDTO>> call, Throwable t) {

                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(getContext(), "Fallo de conexión al cargar feed: " + t.getMessage(), Toast.LENGTH_LONG).show();
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
        Toast.makeText(getContext(), "¡Publicado con éxito!", Toast.LENGTH_SHORT).show();
        // Al publicar, mostramos el refresh y recargamos
        swipeRefreshLayout.setRefreshing(true);
        cargarFeed();
    }
}