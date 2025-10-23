package com.example.sportine.ui.usuarios.social;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView; // ¡Asegúrate de importar ImageView!
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import com.example.sportine.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.card.MaterialCardView; // ¡Asegúrate de importar MaterialCardView!

public class CreatePostFragment extends Fragment {

    // Código de solicitud para la galería
    private static final int PICK_IMAGE_REQUEST = 1;

    // Variables para guardar lo que el usuario seleccione
    private ImageView ivSelectedPostImage;
    private Uri selectedImageUri; // La "dirección" de la foto

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alumno_create_post, container, false);

        // --- Encontrar todas las vistas ---
        MaterialToolbar toolbar = view.findViewById(R.id.toolbar_create_post);
        TextView btnPublicar = view.findViewById(R.id.btn_publicar);
        EditText etCaption = view.findViewById(R.id.et_post_caption);
        MaterialCardView cardSelectImage = view.findViewById(R.id.card_select_image);
        ivSelectedPostImage = view.findViewById(R.id.iv_selected_post_image);

        // --- Configurar los Listeners ---

        // Listener para la tarjeta que abre la galería
        cardSelectImage.setOnClickListener(v -> openGallery());

        // Listener para el botón "Publicar"
        btnPublicar.setOnClickListener(v -> {
            String caption = etCaption.getText().toString().trim();

            // Revisa si hay texto O si hay una imagen seleccionada
            if (caption.isEmpty() && selectedImageUri == null) {
                Toast.makeText(getContext(), "Escribe algo o selecciona una imagen", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Publicando...", Toast.LENGTH_SHORT).show();
                // Aquí iría la lógica para subir el post (caption y selectedImageUri)
                NavHostFragment.findNavController(this).navigateUp(); // Regresa al feed
            }
        });

        // Listener para el botón de "Atrás"
        toolbar.setNavigationOnClickListener(v -> {
            NavHostFragment.findNavController(this).navigateUp();
        });

        return view;
    }

    // --- Método para abrir la galería ---
    private void openGallery() {
        // Crea un "intent" (una solicitud) para abrir la galería
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST);
    }

    // --- Método que recibe la foto de la galería ---
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {

            // Guarda la "dirección" de la foto
            selectedImageUri = data.getData();

            try {
                // "Traduce" la dirección a una imagen (Bitmap)
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), selectedImageUri);

                // Muestra la foto en el ImageView
                ivSelectedPostImage.setImageBitmap(bitmap);

                // Quita los estilos del ícono placeholder para que la foto se vea bien
                ivSelectedPostImage.setPadding(0, 0, 0, 0);
                ivSelectedPostImage.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                ivSelectedPostImage.setImageTintList(null);

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Error al cargar la imagen", Toast.LENGTH_SHORT).show();
            }
        }
    }
}