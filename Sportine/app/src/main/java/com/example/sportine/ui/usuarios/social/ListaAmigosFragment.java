package com.example.sportine.ui.usuarios.social;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.sportine.R;
import com.google.android.material.appbar.MaterialToolbar;
import java.util.ArrayList;
import java.util.List;

public class ListaAmigosFragment extends Fragment implements ListaAmigosAdapter.OnAmigoEliminarListener {

    private RecyclerView rvListaAmigos;
    private ListaAmigosAdapter adapter;
    private List<Post> listaDeAmigosDeEjemplo;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alumno_lista_amigos, container, false);

        // 1. Configurar la barra de herramientas (Toolbar)
        MaterialToolbar toolbar = view.findViewById(R.id.toolbar_lista_amigos);
        toolbar.setNavigationOnClickListener(v -> {
            // Lógica para regresar a la pantalla anterior (SocialFragment)
            NavHostFragment.findNavController(this).navigateUp();
        });

        // 2. Configurar el RecyclerView
        rvListaAmigos = view.findViewById(R.id.rv_lista_amigos);
        rvListaAmigos.setLayoutManager(new LinearLayoutManager(getContext()));

        // 3. Cargar los datos de ejemplo
        cargarAmigosDeEjemplo();

        // 4. Crear y setear el adaptador
        adapter = new ListaAmigosAdapter(listaDeAmigosDeEjemplo, this);
        rvListaAmigos.setAdapter(adapter);

        return view;
    }

    // --- Este método se ejecuta cuando le das clic a "Eliminar" en el adaptador ---
    @Override
    public void onAmigoEliminar(Post amigo) {
        // ¡AQUÍ ESTÁ EL POP-UP QUE PEDISTE!
        new AlertDialog.Builder(getContext())
                .setTitle("Eliminar Amigo")
                .setMessage("¿Estás seguro de que deseas eliminar a " + amigo.getUserName() + " de tus amigos?")
                .setPositiveButton("Sí, eliminar", (dialog, which) -> {
                    // Lógica real de borrado (aquí llamarías a Spring Boot)
                    Toast.makeText(getContext(), amigo.getUserName() + " ha sido eliminado.", Toast.LENGTH_SHORT).show();
                    // (Aquí también tendrías que recargar la lista)
                })
                .setNegativeButton("Cancelar", null) // "null" significa que solo cierra el diálogo
                .show();
    }

    // --- Método de ejemplo para llenar la lista ---
    private void cargarAmigosDeEjemplo() {
        listaDeAmigosDeEjemplo = new ArrayList<>();
        // Reusamos la clase Post para simular amigos
        listaDeAmigosDeEjemplo.add(new Post("Ana", "", R.drawable.avatar_ana, ""));
        listaDeAmigosDeEjemplo.add(new Post("Carlos", "", R.drawable.avatar_user_male, ""));
        listaDeAmigosDeEjemplo.add(new Post("David", "", R.drawable.ic_launcher_background, ""));
        listaDeAmigosDeEjemplo.add(new Post("Laura", "", R.drawable.avatar_user_female, ""));
    }
}