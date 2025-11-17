package com.example.sportine.ui.usuarios.social;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sportine.R;
// --- ¡CAMBIOS! Imports de Retrofit y DTOs ---
import com.example.sportine.data.ApiService;
import com.example.sportine.data.RetrofitClient;
import com.example.sportine.models.Publicacion;
import com.example.sportine.ui.usuarios.dto.PublicacionRequest;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
// --- Fin de Imports ---

public class CreatePostBottomSheetFragment extends BottomSheetDialogFragment {

    // (Variables de UI sin cambios)
    private EditText etPostContent;
    private ImageView btnAddPhoto, btnCloseDialog;
    private Button btnPublishPost;
    private RecyclerView rvSelectedPhotos;
    private SelectedPhotosAdapter photosAdapter;
    private List<Uri> selectedPhotoUris = new ArrayList<>();

    private ActivityResultLauncher<String> requestPermissionLauncher;
    private ActivityResultLauncher<String> pickPhotosLauncher;

    // --- ¡CAMBIO 1: Añadir ApiService! ---
    private ApiService apiService;

    // (Listener sin cambios)
    public interface OnPostPublishedListener {
        void onPostPublished(String content);
    }

    private OnPostPublishedListener listener;

    public void setOnPostPublishedListener(OnPostPublishedListener listener) {
        this.listener = listener;
    }

    public CreatePostBottomSheetFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // --- ¡CAMBIO 2: Inicializar ApiService! ---
        // (Usamos requireContext() para obtener el contexto para el Interceptor)
        apiService = RetrofitClient.getClient(requireContext()).create(ApiService.class);

        // (Launchers de permisos y fotos se quedan igual)
        requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {
                pickPhotosLauncher.launch("image/*");
            } else {
                Toast.makeText(getContext(), "Permiso denegado", Toast.LENGTH_SHORT).show();
            }
        });

        pickPhotosLauncher = registerForActivityResult(new ActivityResultContracts.GetMultipleContents(), uris -> {
            if (uris != null && !uris.isEmpty()) {
                selectedPhotoUris.addAll(uris);
                photosAdapter.notifyDataSetChanged();
                rvSelectedPhotos.setVisibility(View.VISIBLE);
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_create_post, container, false);

        initViews(view);
        setupRecyclerView();
        setupClickListeners(); // <-- ¡Aquí es donde ocurre la magia!

        return view;
    }

    // (initViews y setupRecyclerView se quedan igual)
    private void initViews(View view) {
        etPostContent = view.findViewById(R.id.et_post_content);
        btnAddPhoto = view.findViewById(R.id.btn_add_photo);
        btnCloseDialog = view.findViewById(R.id.btn_close_dialog);
        btnPublishPost = view.findViewById(R.id.btn_publish_post);
        rvSelectedPhotos = view.findViewById(R.id.rv_selected_photos);
    }

    private void setupRecyclerView() {
        rvSelectedPhotos.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        photosAdapter = new SelectedPhotosAdapter(selectedPhotoUris);
        rvSelectedPhotos.setAdapter(photosAdapter);
        rvSelectedPhotos.setVisibility(selectedPhotoUris.isEmpty() ? View.GONE : View.VISIBLE);
    }

    // --- ¡CAMBIO 3: Lógica de publicación CON RETROFIT! ---
    private void setupClickListeners() {
        btnCloseDialog.setOnClickListener(v -> dismiss());

        btnAddPhoto.setOnClickListener(v -> {
            checkPermissionAndOpenGallery();
        });

        btnPublishPost.setOnClickListener(v -> {
            String content = etPostContent.getText().toString().trim();

            // (Por ahora no manejamos la subida de fotos, solo el texto)
            // TODO: Implementar lógica de subida de imágenes (selectedPhotoUris)
            if (content.isEmpty() && selectedPhotoUris.isEmpty()) {
                Toast.makeText(getContext(), "El contenido no puede estar vacío", Toast.LENGTH_SHORT).show();
                return;
            }

            // Deshabilitamos el botón para evitar doble click
            btnPublishPost.setEnabled(false);
            btnPublishPost.setText("Publicando...");

            // Creamos el DTO
            // (Enviamos 'null' en la imagen por ahora)
            PublicacionRequest request = new PublicacionRequest(content, null);

            // ¡Hacemos la llamada a la API!
            // (El Token se inyecta solo gracias al AuthInterceptor)
            apiService.crearPost(request).enqueue(new Callback<Publicacion>() {
                @Override
                public void onResponse(Call<Publicacion> call, Response<Publicacion> response) {
                    // Volvemos a habilitar el botón
                    btnPublishPost.setEnabled(true);
                    btnPublishPost.setText("Publicar");

                    if (response.isSuccessful() && response.body() != null) {
                        // ¡ÉXITO!
                        Toast.makeText(getContext(), "Publicado con éxito", Toast.LENGTH_SHORT).show();

                        // Avisamos al SocialFragment para que refresque
                        if (listener != null) {
                            listener.onPostPublished(content);
                        }
                        // Cerramos el diálogo
                        dismiss();
                    } else {
                        // Error del servidor (ej. 403 Forbidden, 500 Error)
                        Toast.makeText(getContext(), "Error al publicar: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Publicacion> call, Throwable t) {
                    // Error de red (sin internet, servidor caído)
                    btnPublishPost.setEnabled(true);
                    btnPublishPost.setText("Publicar");
                    Toast.makeText(getContext(), "Fallo de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        });
    }

    // (checkPermissionAndOpenGallery y getTheme se quedan igual)
    private void checkPermissionAndOpenGallery() {
        String permission;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            permission = Manifest.permission.READ_MEDIA_IMAGES;
        } else {
            permission = Manifest.permission.READ_EXTERNAL_STORAGE;
        }

        if (ContextCompat.checkSelfPermission(requireContext(), permission) == PackageManager.PERMISSION_GRANTED) {
            pickPhotosLauncher.launch("image/*");
        } else {
            requestPermissionLauncher.launch(permission);
        }
    }

    @Override
    public int getTheme() {
        return R.style.BottomSheetDialogTheme;
    }
}
