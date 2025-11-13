package com.example.sportine.ui.usuarios.social; // Asegúrate que el paquete sea el correcto

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast; // ¡AÑADIDO! Para mostrar el mensaje de "Publicado"

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation; // Se mantiene para "Buscar Amigo"
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sportine.R; // Asegúrate de importar tu R
import com.google.android.material.card.MaterialCardView; // Para el disparador

import java.util.ArrayList;
import java.util.List;

// --- CAMBIO #1: Implementamos la interfaz del BottomSheet ---
public class SocialFragment extends Fragment implements CreatePostBottomSheetFragment.OnPostPublishedListener {

    private RecyclerView recyclerView;
    private SocialFeedAdapter adapter;
    private List<Post> postList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alumno_social, container, false);

        // --- Configuración del RecyclerView (Se queda igual) ---
        recyclerView = view.findViewById(R.id.rv_social_feed);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        postList = new ArrayList<>();
        addSamplePosts(); // Llamamos al método que tiene tus posts de ejemplo

        adapter = new SocialFeedAdapter(postList);
        recyclerView.setAdapter(adapter);

        // --- CAMBIO #2: Lógica para ABRIR EL DIÁLOGO FLOTANTE ---

        // Buscamos el CardView modernizado (¡CON EL NUEVO ID!)
        MaterialCardView cardCreatePostTrigger = view.findViewById(R.id.card_share_post);

        cardCreatePostTrigger.setOnClickListener(v -> {
            // Ya no navega, ahora llama al método para mostrar el diálogo
            showCreatePostDialog();
        });

        // (Se elimina la lógica vieja de cardSharePost y cameraIcon)

        // --- FIN DE LA LÓGICA MODIFICADA ---


        // --- LÓGICA DE AGREGAR AMIGO (Esta se queda igual) ---
        ImageView addFriendIcon = view.findViewById(R.id.iv_add_friend);
        addFriendIcon.setOnClickListener(v -> {
            // Esto sigue navegando a la pantalla de "Buscar Amigo"
            Navigation.findNavController(view).navigate(R.id.action_social_to_buscar_amigo);
        });
        // --- FIN DE LÓGICA DE AGREGAR AMIGO ---

        return view;
    }

    // --- CAMBIO #3: NUEVO MÉTODO para mostrar el diálogo flotante ---
    private void showCreatePostDialog() {
        CreatePostBottomSheetFragment bottomSheet = new CreatePostBottomSheetFragment();
        // Nos "suscribimos" para saber cuándo el usuario presione "Publicar"
        bottomSheet.setOnPostPublishedListener(this);
        // Mostramos el diálogo
        bottomSheet.show(getParentFragmentManager(), "CreatePostBottomSheet");
    }

    // --- CAMBIO #4: NUEVO MÉTODO que se ejecuta cuando el diálogo avisa que publicó ---
    @Override
    public void onPostPublished(String content) {
        // Por ahora, solo muestra un mensaje.
        Toast.makeText(getContext(), "¡Publicado con éxito!", Toast.LENGTH_LONG).show();

        // (Opcional) Aquí podrías añadir el nuevo post a la 'postList' localmente
        // Post newPost = new Post("Tú", content, R.drawable.avatar_user_male, "Ahora mismo");
        // postList.add(0, newPost);
        // adapter.notifyItemInserted(0);
        // recyclerView.scrollToPosition(0);
    }


    // --- CAMBIO #5: Rellenamos tu método 'addSamplePosts' ---
    private void addSamplePosts() {
        // --- INICIO DE TU LÓGICA DE POSTS (La movimos aquí) ---
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
        // --- FIN DE TU LÓGICA DE POSTS ---
    }

} // Fin de la clase SocialFragment