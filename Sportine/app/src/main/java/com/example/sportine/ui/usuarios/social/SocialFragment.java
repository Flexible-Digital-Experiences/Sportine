package com.example.sportine.ui.usuarios.social; // Asegúrate que el paquete sea el correcto

// Se quitaron las importaciones de AlertDialog, EditText, Toast que ya no se usan
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation; // ¡Importante para la navegación!
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sportine.R; // Asegúrate de importar tu R
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;


public class SocialFragment extends Fragment {

    private RecyclerView recyclerView;
    private SocialFeedAdapter adapter;
    private List<Post> postList;
    // Las variables globales ya no son necesarias aquí

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alumno_social, container, false);

        recyclerView = view.findViewById(R.id.rv_social_feed);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        postList = new ArrayList<>();
        // --- INICIO DE TU LÓGICA DE POSTS (Se queda igual) ---
        // ¡Solo asegúrate de que los archivos de imagen existan en res/drawable!
        postList.add(new Post(
                "Ana",
                "Logro conseguido\n¡Felicidades Ana! Ha superado el récord:\n¡160kg en sentadilla!",
                R.drawable.avatar_ana, // Reemplazar con el avatar de Ana
                "Sportine ● Hace 10m"
        ));
        postList.add(new Post(
                "Ana",
                "¡Felicidades Ana! Enhorabuena, sigue así 💪",
                R.drawable.avatar_ana, // Reemplazar con el avatar de Ana
                "Sportine ● Hace 5m"
        ));
        postList.add(new Post(
                "Usuario 3",
                "Hoy 5km, ¡rompí récord!\n#Running #Cardio",
                R.drawable.avatar_user_female, // Reemplazar con un avatar
                R.drawable.post_running, // Reemplazar con una imagen de running
                "Sportine ● Hace 5m"
        ));
        postList.add(new Post(
                "Usuario 4",
                "¡Felicidades! Enhorabuena, sigue así 👍",
                R.drawable.avatar_user_male, // Reemplazar con un avatar
                "Sportine ● Hace 5m"
        ));
        // Agreguemos más para probar el scroll
        postList.add(new Post("David", "Nueva rutina de pecho, ¡a darle!", R.drawable.avatar_user_male, "Sportine ● Hace 2h"));
        postList.add(new Post("Laura", "¿Alguien para una reta de basket mañana?", R.drawable.avatar_user_female, "Sportine ● Hace 3h"));
        // --- FIN DE TU LÓGICA DE POSTS ---


        adapter = new SocialFeedAdapter(postList);
        recyclerView.setAdapter(adapter);

        // --- INICIO DE LA LÓGICA MODIFICADA ---

        // Configurar el CardView para NAVEGAR a la pantalla CreatePostFragment
        MaterialCardView cardSharePost = view.findViewById(R.id.card_share_post);
        cardSharePost.setOnClickListener(v -> {
            Navigation.findNavController(view).navigate(R.id.action_social_to_create_post);
        });

        // Configurar el ícono de la cámara para NAVEGAR a la pantalla CreatePostFragment
        ImageView cameraIcon = view.findViewById(R.id.iv_camera_icon);
        cameraIcon.setOnClickListener(v -> {
            Navigation.findNavController(view).navigate(R.id.action_social_to_create_post);
        });

        // Configurar el ícono de "Agregar Amigo" para NAVEGAR a la pantalla BuscarAmigoFragment
        ImageView addFriendIcon = view.findViewById(R.id.iv_add_friend);
        addFriendIcon.setOnClickListener(v -> {
            // ¡ESTE ES EL CAMBIO! Ya no llama a showAddFriendDialog()
            Navigation.findNavController(view).navigate(R.id.action_social_to_buscar_amigo);
        });

        // --- FIN DE LA LÓGICA MODIFICADA ---

        return view;
    }

    // --- ¡EL MÉTODO showAddFriendDialog() FUE ELIMINADO! ---

    // --- Método para añadir tus posts de ejemplo ---
    // (Asegúrate de que los nombres de drawable existan en tu proyecto)
    // Este método es necesario si lo llamas en onCreateView
    private void addSamplePosts() {
        // Pega aquí tu lógica para añadir los posts a postList
        // Ejemplo:
        // postList.add(new Post("Ana", "Mensaje...", R.drawable.avatar_ana, "Timestamp"));
        // ...añade todos tus posts...
    }

} // Fin de la clase SocialFragment