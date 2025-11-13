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
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;

public class CreatePostBottomSheetFragment extends BottomSheetDialogFragment {

    private EditText etPostContent;
    private ImageView btnAddPhoto, btnCloseDialog;
    private Button btnPublishPost;
    private RecyclerView rvSelectedPhotos;
    private SelectedPhotosAdapter photosAdapter;
    private List<Uri> selectedPhotoUris = new ArrayList<>();

    private ActivityResultLauncher<String> requestPermissionLauncher;
    private ActivityResultLauncher<String> pickPhotosLauncher;

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

        // 1. Preparamos el launcher para PEDIR PERMISO
        requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {
                pickPhotosLauncher.launch("image/*");
            } else {
                Toast.makeText(getContext(), "Permiso denegado para leer imágenes", Toast.LENGTH_SHORT).show();
            }
        });

        // 2. Preparamos el launcher para ABRIR LA GALERÍA
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
        rvSelectedPhotos.setVisibility(selectedPhotoUris.isEmpty() ? View.GONE : View.VISIBLE);
    }

    private void setupClickListeners() {
        btnCloseDialog.setOnClickListener(v -> dismiss());

        btnAddPhoto.setOnClickListener(v -> {
            checkPermissionAndOpenGallery();
        });

        btnPublishPost.setOnClickListener(v -> {
            String content = etPostContent.getText().toString().trim();
            if (!content.isEmpty() || !selectedPhotoUris.isEmpty()) {
                if (listener != null) {
                    listener.onPostPublished(content);
                }
                dismiss();
            } else {
                Toast.makeText(getContext(), "El contenido no puede estar vacío", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkPermissionAndOpenGallery() {

        String permission;

        // 1. Decide qué permiso pedir basado en la versión de Android
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            // Android 13 (API 33) o superior
            permission = Manifest.permission.READ_MEDIA_IMAGES;
        } else {
            // Android 12 (API 32) o inferior
            permission = Manifest.permission.READ_EXTERNAL_STORAGE;
        }

        // 2. Revisa si ya tiene el permiso
        if (ContextCompat.checkSelfPermission(requireContext(), permission) == PackageManager.PERMISSION_GRANTED) {
            // Si ya tenemos permiso, abre la galería
            pickPhotosLauncher.launch("image/*");
        } else {
            // Si no, pide el permiso
            requestPermissionLauncher.launch(permission);
        }
    }

    @Override
    public int getTheme() {
        return R.style.BottomSheetDialogTheme;
    }
}