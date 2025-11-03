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
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class SocialFragment extends Fragment implements CreatePostBottomSheetFragment.OnPostPublishedListener {

    private RecyclerView recyclerView;
    private SocialFeedAdapter adapter;
    private List<Post> postList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alumno_social, container, false);

        recyclerView = view.findViewById(R.id.rv_social_feed);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        postList = new ArrayList<>();
        addSamplePosts();

        adapter = new SocialFeedAdapter(postList);
        recyclerView.setAdapter(adapter);

        MaterialCardView cardCreatePostTrigger = view.findViewById(R.id.card_create_post_trigger);
        cardCreatePostTrigger.setOnClickListener(v -> showCreatePostDialog());

        ImageView addFriendIcon = view.findViewById(R.id.iv_add_friend);
        addFriendIcon.setOnClickListener(v -> Navigation.findNavController(view).navigate(R.id.action_social_to_buscar_amigo));

        ImageView removeFriendIcon = view.findViewById(R.id.iv_remove_friend);
        removeFriendIcon.setOnClickListener(v -> showFriendListDialog());

        return view;
    }

    private void showCreatePostDialog() {
        CreatePostBottomSheetFragment bottomSheet = new CreatePostBottomSheetFragment();
        bottomSheet.setOnPostPublishedListener(this);
        bottomSheet.show(getParentFragmentManager(), "CreatePostBottomSheet");
    }

    private void showFriendListDialog() {
        FriendListDialogFragment dialogFragment = new FriendListDialogFragment();
        dialogFragment.show(getParentFragmentManager(), "FriendListDialog");
    }

    @Override
    public void onPostPublished(String content) {
        Toast.makeText(getContext(), "¡Publicado con éxito!", Toast.LENGTH_LONG).show();
        // Opcional: añadir el nuevo post a la lista y notificar al adaptador
        // Post newPost = new Post("Tú", content, R.drawable.avatar_user_male, "Ahora mismo");
        // postList.add(0, newPost);
        // adapter.notifyItemInserted(0);
        // recyclerView.scrollToPosition(0);
    }

    private void addSamplePosts() {
        postList.add(new Post(
                "Ana",
                "Logro conseguido\n¡Felicidades Ana! Ha superado el récord:\n¡160kg en sentadilla!",
                R.drawable.avatar_ana, 
                "Sportine ● Hace 10m"
        ));
        postList.add(new Post(
                "Ana",
                "¡Felicidades Ana! Enhorabuena, sigue así 💪",
                R.drawable.avatar_ana, 
                "Sportine ● Hace 5m"
        ));
        postList.add(new Post(
                "Usuario 3",
                "Hoy 5km, ¡rompí récord!\n#Running #Cardio",
                R.drawable.avatar_user_female, 
                R.drawable.post_running, 
                "Sportine ● Hace 5m"
        ));
        postList.add(new Post(
                "Usuario 4",
                "¡Felicidades! Enhorabuena, sigue así 👍",
                R.drawable.avatar_user_male, 
                "Sportine ● Hace 5m"
        ));
        postList.add(new Post("David", "Nueva rutina de pecho, ¡a darle!", R.drawable.avatar_user_male, "Sportine ● Hace 2h"));
        postList.add(new Post("Laura", "¿Alguien para una reta de basket mañana?", R.drawable.avatar_user_female, "Sportine ● Hace 3h"));
    }

}