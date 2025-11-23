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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.io.ByteArrayOutputStream;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sportine.R;
import com.example.sportine.data.ApiService;
import com.example.sportine.data.RetrofitClient;
import com.example.sportine.models.Publicacion;
import com.example.sportine.ui.usuarios.dto.PublicacionRequest;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.gson.Gson; // <-- Necesitamos Gson aquí

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreatePostBottomSheetFragment extends BottomSheetDialogFragment {

    private EditText etPostContent;
    private ImageView btnAddPhoto, btnCloseDialog;
    private Button btnPublishPost;
    private RecyclerView rvSelectedPhotos;
    private SelectedPhotosAdapter photosAdapter;

    private List<Uri> selectedPhotoUris = new ArrayList<>();

    private ActivityResultLauncher<String> requestPermissionLauncher;
    private ActivityResultLauncher<String> pickPhotosLauncher;

    private ApiService apiService;

    public interface OnPostPublishedListener {
        void onPostPublished(String content);
    }
    private OnPostPublishedListener listener;
    public void setOnPostPublishedListener(OnPostPublishedListener listener) {
        this.listener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiService = RetrofitClient.getClient(requireContext()).create(ApiService.class);

        requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) pickPhotosLauncher.launch("image/*");
            else Toast.makeText(getContext(), "Permiso denegado", Toast.LENGTH_SHORT).show();
        });

        pickPhotosLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            if (uri != null) {
                selectedPhotoUris.clear(); // Solo 1 foto
                selectedPhotoUris.add(uri);
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
        setupClickListeners();
        return view;
    }

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
    }

    private void setupClickListeners() {
        btnCloseDialog.setOnClickListener(v -> dismiss());
        btnAddPhoto.setOnClickListener(v -> checkPermissionAndOpenGallery());

        // --- ¡AQUÍ ESTÁ LA LÓGICA DE ENVÍO MULTIPART! ---
        btnPublishPost.setOnClickListener(v -> {
            String content = etPostContent.getText().toString().trim();

            if (content.isEmpty() && selectedPhotoUris.isEmpty()) {
                Toast.makeText(getContext(), "Escribe algo o sube una foto", Toast.LENGTH_SHORT).show();
                return;
            }

            btnPublishPost.setEnabled(false);
            btnPublishPost.setText("Subiendo...");

            // 1. Preparamos el JSON ("data")
            PublicacionRequest postRequest = new PublicacionRequest(content, null);
            String jsonString = new Gson().toJson(postRequest);
            RequestBody dataPart = RequestBody.create(MediaType.parse("application/json"), jsonString);

            // 2. Preparamos la Imagen ("file")
            MultipartBody.Part filePart = null;
            if (!selectedPhotoUris.isEmpty()) {
                // Convertimos Uri -> File real
                File file = getFileFromUri(selectedPhotoUris.get(0));
                if (file != null) {
                    // Creamos el cuerpo de la imagen
                    RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
                    filePart = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
                }
            }

            // 3. Enviamos todo junto
            apiService.crearPost(dataPart, filePart).enqueue(new Callback<Publicacion>() {
                @Override
                public void onResponse(Call<Publicacion> call, Response<Publicacion> response) {
                    btnPublishPost.setEnabled(true);
                    btnPublishPost.setText("Publicar");

                    if (response.isSuccessful()) {
                        Toast.makeText(getContext(), "¡Publicado con éxito!", Toast.LENGTH_SHORT).show();
                        if (listener != null) listener.onPostPublished(content);
                        dismiss();
                    } else {
                        Toast.makeText(getContext(), "Error al subir: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Publicacion> call, Throwable t) {
                    btnPublishPost.setEnabled(true);
                    btnPublishPost.setText("Publicar");
                    Toast.makeText(getContext(), "Fallo de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        });
    }

    private File getFileFromUri(Uri uri) {
        try {

            InputStream inputStream = requireContext().getContentResolver().openInputStream(uri);


            Bitmap originalBitmap = BitmapFactory.decodeStream(inputStream);
            inputStream.close();

            if (originalBitmap == null) return null;


            int maxWidth = 1024;
            int width = originalBitmap.getWidth();
            int height = originalBitmap.getHeight();

            if (width > maxWidth) {
                float ratio = (float) width / maxWidth;
                width = maxWidth;
                height = (int) (height / ratio);
                originalBitmap = Bitmap.createScaledBitmap(originalBitmap, width, height, true);
            }

            File tempFile = File.createTempFile("upload_compressed", ".jpg", requireContext().getCacheDir());
            FileOutputStream outputStream = new FileOutputStream(tempFile);


            originalBitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream);

            outputStream.flush();
            outputStream.close();

            return tempFile;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void checkPermissionAndOpenGallery() {
        String permission = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) ?
                Manifest.permission.READ_MEDIA_IMAGES : Manifest.permission.READ_EXTERNAL_STORAGE;

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