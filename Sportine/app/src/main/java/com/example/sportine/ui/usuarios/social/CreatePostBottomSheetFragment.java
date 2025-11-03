package com.example.sportine.ui.usuarios.social;

import android.Manifest;
import android.app.TimePickerDialog;
import android.content.pm.PackageManager;
import android.location.Location;
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
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CreatePostBottomSheetFragment extends BottomSheetDialogFragment {

    private EditText etPostContent;
    private ImageView btnAddPhoto, btnAddLocation, btnAddTimer, btnCloseDialog;
    private Button btnPublishPost;
    private RecyclerView rvSelectedPhotos;
    private SelectedPhotosAdapter photosAdapter;
    private List<Uri> selectedPhotoUris = new ArrayList<>();

    private FusedLocationProviderClient fusedLocationClient;

    private ActivityResultLauncher<String[]> requestPermissionLauncher;
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
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        // Launcher para permisos
        requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), permissions -> {
            if (Boolean.TRUE.equals(permissions.get(Manifest.permission.ACCESS_FINE_LOCATION))) {
                getCurrentLocation();
            } else {
                Toast.makeText(getContext(), "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show();
            }
        });

        // Launcher para el selector de fotos
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
        btnAddLocation = view.findViewById(R.id.btn_add_location);
        btnAddTimer = view.findViewById(R.id.btn_add_timer);
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

        btnAddPhoto.setOnClickListener(v -> pickPhotosLauncher.launch("image/*"));

        btnAddLocation.setOnClickListener(v -> requestLocationPermission());

        btnAddTimer.setOnClickListener(v -> showTimePicker());

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

    private void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
        } else {
            requestPermissionLauncher.launch(new String[]{Manifest.permission.ACCESS_FINE_LOCATION});
        }
    }

    private void getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return; // Permiso no concedido
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(requireActivity(), location -> {
                    if (location != null) {
                        String locationText = String.format("\n[Ubicación: %.5f, %.5f]", location.getLatitude(), location.getLongitude());
                        etPostContent.append(locationText);
                    } else {
                        Toast.makeText(getContext(), "No se pudo obtener la ubicación", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showTimePicker() {
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(requireContext(),
                (view, hourOfDay, minuteOfHour) -> {
                    String timeText = String.format("\n[Duración: %02d:%02d:00]", hourOfDay, minuteOfHour);
                    etPostContent.append(timeText);
                }, hour, minute, true);
        timePickerDialog.show();
    }

    @Override
    public int getTheme() {
        return R.style.BottomSheetDialogTheme;
    }
}
