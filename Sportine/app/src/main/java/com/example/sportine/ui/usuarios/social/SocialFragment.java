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

import com.example.sportine.R;
import com.example.sportine.data.ApiService;
import com.example.sportine.data.RetrofitClient;
// --- ¡CAMBIO! Importamos el "Súper-Paquete" ---
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

    // --- ¡CAMBIO! La lista ahora es del tipo DTO ---
    private List<PublicacionFeedDTO> publicacionList;

    private ApiService apiService;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alumno_social, container, false);

        apiService = RetrofitClient.getClient(requireContext()).create(ApiService.class);

        recyclerView = view.findViewById(R.id.rv_social_feed);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Inicializamos la lista VACÍA
        publicacionList = new ArrayList<>();

        // Le pasamos la lista vacía al adaptador
        adapter = new SocialFeedAdapter(publicacionList, requireContext(), apiService);
        recyclerView.setAdapter(adapter);

        // --- Lógica de botones (se queda igual) ---
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
        cargarFeed(); // Llamamos a la API
    }

    // --- ¡CAMBIO! Este método ahora espera 'PublicacionFeedDTO' ---
    private void cargarFeed() {

        apiService.getSocialFeed().enqueue(new Callback<List<PublicacionFeedDTO>>() {
            @Override
            public void onResponse(Call<List<PublicacionFeedDTO>> call, Response<List<PublicacionFeedDTO>> response) {

                if (response.isSuccessful() && response.body() != null) {
                    List<PublicacionFeedDTO> posts = response.body();
                    Collections.reverse(posts); // Los más nuevos primero
                    adapter.setPublicaciones(posts); // ¡Actualiza el adapter!
                } else {
                    Toast.makeText(getContext(), "Error al cargar el feed: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<PublicacionFeedDTO>> call, Throwable t) {
                Toast.makeText(getContext(), "Fallo de conexión al cargar feed: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    // (showCreatePostDialog se queda igual)
    private void showCreatePostDialog() {
        CreatePostBottomSheetFragment bottomSheet = new CreatePostBottomSheetFragment();
        bottomSheet.setOnPostPublishedListener(this);
        bottomSheet.show(getParentFragmentManager(), "CreatePostBottomSheet");
    }

    // ('onPostPublished' se queda igual, ¡sigue funcionando!)
    @Override
    public void onPostPublished(String content) {
        Toast.makeText(getContext(), "¡Publicado con éxito!", Toast.LENGTH_SHORT).show();
        cargarFeed(); // Refresca el feed para mostrar el post nuevo
    }
}