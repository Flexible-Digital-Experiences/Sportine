package com.example.sportine.ui.usuarios.social;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sportine.R;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class SocialFragment extends Fragment {

    private RecyclerView recyclerView;
    private SocialFeedAdapter adapter;
    private List<Post> postList;
    // Las variables 'cardSharePost' y 'cameraIcon' ya no necesitan ser globales,
    // las declararemos dentro de onCreateView para un código más limpio.

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alumno_social, container, false);

        recyclerView = view.findViewById(R.id.rv_social_feed);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        postList = new ArrayList<>();
        // --- TUS PUBLICACIONES ESTÁN AQUÍ, INTACTAS ---
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
        postList.add(new Post("David", "Nueva rutina de pecho, ¡a darle!", R.drawable.ic_launcher_background, "Sportine ● Hace 2h"));
        postList.add(new Post("Laura", "¿Alguien para una reta de basket mañana?", R.drawable.ic_launcher_background, "Sportine ● Hace 3h"));


        adapter = new SocialFeedAdapter(postList);
        recyclerView.setAdapter(adapter);

        // Encontrar y configurar el CardView y el ícono para crear nuevas publicaciones
        MaterialCardView cardSharePost = view.findViewById(R.id.card_share_post);
        cardSharePost.setOnClickListener(v -> showCreatePostDialog());

        ImageView cameraIcon = view.findViewById(R.id.iv_camera_icon);
        cameraIcon.setOnClickListener(v -> showCreatePostDialog());


        // --- AQUÍ EMPIEZA EL CÓDIGO NUEVO ---

        // Encuentra el ImageView del ícono de agregar amigo por su ID
        ImageView addFriendIcon = view.findViewById(R.id.iv_add_friend);

        // Le asigna un OnClickListener para que reaccione al clic
        addFriendIcon.setOnClickListener(v -> {
            // Llama al nuevo método que crearemos para mostrar el diálogo
            showAddFriendDialog();
        });

        // --- AQUÍ TERMINA EL CÓDIGO NUEVO ---

        return view;
    }

    // Método para mostrar el diálogo de crear publicación
    private void showCreatePostDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Crear nueva publicación");

        final EditText input = new EditText(getContext());
        input.setHint("Escribe tu publicación aquí...");
        builder.setView(input);

        builder.setPositiveButton("Publicar", (dialog, which) -> {
            String postMessage = input.getText().toString().trim();
            if (!postMessage.isEmpty()) {
                // Simular que el usuario actual es "Yo"
                String userName = "Emmanuel";
                int userAvatar = R.drawable.ic_launcher_background; // TODO: Cambia por tu avatar

                // Un formato más consistente con el resto de la app
                String timestamp = "Sportine ● Ahora";

                Post newPost = new Post(userName, postMessage, userAvatar, timestamp);
                adapter.addPost(newPost);

                // --- MEJORA AÑADIDA ---
                // Mueve el scroll hasta arriba para que veas tu nueva publicación al instante
                recyclerView.scrollToPosition(0);

                Toast.makeText(getContext(), "¡Publicado!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "La publicación no puede estar vacía", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());

        builder.show();
    }
    private void showAddFriendDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Buscar Amigo");
        builder.setMessage("Ingresa el nombre de usuario para buscarlo.");

        // Creamos un EditText para que el usuario escriba
        final EditText input = new EditText(getContext());
        input.setHint("Nombre de usuario...");
        builder.setView(input);

        // Configuramos el botón "Buscar"
        builder.setPositiveButton("Buscar", (dialog, which) -> {
            String friendName = input.getText().toString().trim();
            if (!friendName.isEmpty()) {
                // Por ahora, solo mostramos un mensaje de confirmación
                Toast.makeText(getContext(), "Buscando a: " + friendName, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show();
            }
        });

        // Configuramos el botón "Cancelar"
        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());

        // Mostramos el diálogo
        builder.show();
    }

}