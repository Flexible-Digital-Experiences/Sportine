package com.example.sportine.ui.usuarios.configuracion;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.sportine.R;
import com.example.sportine.data.ApiService;
import com.example.sportine.data.RetrofitClient;
import com.example.sportine.ui.usuarios.dto.UsuarioDetalleDTO;
import com.example.sportine.ui.usuarios.dto.PerfilAlumnoResponseDTO;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConfiguracionFragment extends Fragment {

    private static final String TAG = "ConfiguracionFragment";

    // Componentes UI
    private ImageView ivAvatarConfig;
    private MaterialCardView cardAvatarConfig;
    private MaterialCardView btnEditarFoto;
    private TextView tvNombre;
    private TextView tvApellido;
    private TextView tvUsername;
    private TextView tvSexo;
    private TextView tvEstado;
    private TextView tvCiudad;
    private TextView tvPassword;
    private ImageView btnTogglePassword;
    private MaterialButton btnModificar, btnCerrarSesion;
    private MaterialCardView btnBack;

    private ApiService apiService;
    private String username;
    private String rol;
    private UsuarioDetalleDTO usuarioActual;

    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private Uri selectedImageUri;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_alumno_configuracion, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        inicializarComponentes(view);
        configurarImagePicker();
        apiService = RetrofitClient.getClient(requireContext()).create(ApiService.class);
        obtenerDatosUsuarioLogueado();
        configurarBotones(view);
        cargarDatosUsuario();
    }

    private void inicializarComponentes(View view) {
        ivAvatarConfig = view.findViewById(R.id.iv_avatar_config);
        cardAvatarConfig = view.findViewById(R.id.card_avatar_config);
        btnEditarFoto = view.findViewById(R.id.iv_edit_foto);
        tvNombre = view.findViewById(R.id.tvNombre);
        tvApellido = view.findViewById(R.id.tvApellido);
        tvUsername = view.findViewById(R.id.tvUsername);
        tvSexo = view.findViewById(R.id.tvSexo);
        tvEstado = view.findViewById(R.id.tvEstado);
        tvCiudad = view.findViewById(R.id.tvCiudad);
        tvPassword = view.findViewById(R.id.tvPassword);
        btnTogglePassword = view.findViewById(R.id.btnTogglePassword);
        btnModificar = view.findViewById(R.id.btnModificar);
        btnBack = view.findViewById(R.id.btnBack);
        btnCerrarSesion = view.findViewById(R.id.btnCerrarSesion);
    }

    private void configurarImagePicker() {
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        if (selectedImageUri != null) {
                            Log.d(TAG, "📸 Imagen seleccionada: " + selectedImageUri);
                            Glide.with(this).load(selectedImageUri).circleCrop().into(ivAvatarConfig);
                            subirFotoPerfilACloudinary(selectedImageUri);
                        }
                    }
                }
        );
    }

    private void obtenerDatosUsuarioLogueado() {
        if (!isAdded()) return;
        SharedPreferences prefs = requireContext().getSharedPreferences("SportinePrefs", Context.MODE_PRIVATE);
        username = prefs.getString("USER_USERNAME", null);
        rol = prefs.getString("USER_ROL", null);
    }

    private void configurarBotones(View view) {
        btnBack.setOnClickListener(v -> requireActivity().onBackPressed());
        btnModificar.setOnClickListener(v -> Navigation.findNavController(view).navigate(R.id.action_configuracion_to_modificar));
        if (btnTogglePassword != null) btnTogglePassword.setVisibility(View.GONE);
        if (btnEditarFoto != null) btnEditarFoto.setOnClickListener(v -> abrirSelectorImagen());
        if (btnCerrarSesion != null) btnCerrarSesion.setOnClickListener(v -> mostrarDialogoCerrarSesion());
    }

    private void cargarDatosUsuario() {
        if (username == null) return;
        Call<UsuarioDetalleDTO> call = apiService.obtenerUsuario(username);
        call.enqueue(new Callback<UsuarioDetalleDTO>() {
            @Override
            public void onResponse(Call<UsuarioDetalleDTO> call, Response<UsuarioDetalleDTO> response) {
                if (!isAdded()) return; // ✅ BLINDAJE
                if (response.isSuccessful() && response.body() != null) {
                    usuarioActual = response.body();
                    mostrarDatosUsuario(usuarioActual);
                    if ("alumno".equalsIgnoreCase(rol)) cargarFotoPerfilAlumno();
                } else {
                    Toast.makeText(requireContext(), "Error al cargar datos", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<UsuarioDetalleDTO> call, Throwable t) {
                if (!isAdded()) return; // ✅ BLINDAJE
                Toast.makeText(requireContext(), "Error de conexión", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void mostrarDatosUsuario(UsuarioDetalleDTO usuario) {
        tvNombre.setText(usuario.getNombre() != null ? usuario.getNombre() : "-");
        tvApellido.setText(usuario.getApellidos() != null ? usuario.getApellidos() : "-");
        tvUsername.setText("@" + usuario.getUsuario());
        tvSexo.setText(usuario.getSexo() != null ? usuario.getSexo() : "-");
        tvEstado.setText(usuario.getEstado() != null ? usuario.getEstado() : "-");
        tvCiudad.setText(usuario.getCiudad() != null ? usuario.getCiudad() : "-");
        tvPassword.setText("••••••••");
    }

    private void cargarFotoPerfilAlumno() {
        Call<PerfilAlumnoResponseDTO> call = apiService.obtenerPerfilAlumno(username);
        call.enqueue(new Callback<PerfilAlumnoResponseDTO>() {
            @Override
            public void onResponse(Call<PerfilAlumnoResponseDTO> call, Response<PerfilAlumnoResponseDTO> response) {
                if (!isAdded()) return; // ✅ BLINDAJE
                if (response.isSuccessful() && response.body() != null) {
                    cargarFotoPerfil(response.body().getFotoPerfil());
                }
            }
            @Override
            public void onFailure(Call<PerfilAlumnoResponseDTO> call, Throwable t) {
                if (!isAdded()) return; // ✅ BLINDAJE
            }
        });
    }

    private void abrirSelectorImagen() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    private void subirFotoPerfilACloudinary(Uri imageUri) {
        if (!isAdded()) return;
        Toast.makeText(requireContext(), "Subiendo imagen...", Toast.LENGTH_SHORT).show();

        try {
            File file = uriToFile(imageUri);
            if (file == null) return;

            RequestBody requestFile = RequestBody.create(MediaType.parse(requireContext().getContentResolver().getType(imageUri)), file);
            MultipartBody.Part body = MultipartBody.Part.createFormData("foto", file.getName(), requestFile);

            apiService.actualizarFotoPerfil(username, body).enqueue(new Callback<Map<String, String>>() {
                @Override
                public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                    if (!isAdded()) return; // ✅ BLINDAJE
                    if (response.isSuccessful() && response.body() != null) {
                        String nuevaUrl = response.body().get("fotoPerfil");
                        Toast.makeText(requireContext(), "Foto actualizada", Toast.LENGTH_SHORT).show();
                        cargarFotoPerfil(nuevaUrl);
                    } else {
                        Toast.makeText(requireContext(), "Error al actualizar", Toast.LENGTH_SHORT).show();
                        cargarFotoPerfilAlumno();
                    }
                }
                @Override
                public void onFailure(Call<Map<String, String>> call, Throwable t) {
                    if (!isAdded()) return; // ✅ BLINDAJE
                    Toast.makeText(requireContext(), "Error de conexión", Toast.LENGTH_LONG).show();
                    cargarFotoPerfilAlumno();
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "Error: " + e.getMessage());
        }
    }

    private File uriToFile(Uri uri) {
        try {
            InputStream inputStream = requireContext().getContentResolver().openInputStream(uri);
            if (inputStream == null) return null;
            String fileName = getFileName(uri);
            if (fileName == null) fileName = "temp_image_" + System.currentTimeMillis() + ".jpg";
            File file = new File(requireContext().getCacheDir(), fileName);
            FileOutputStream outputStream = new FileOutputStream(file);
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) outputStream.write(buffer, 0, bytesRead);
            outputStream.close();
            inputStream.close();
            return file;
        } catch (Exception e) {
            return null;
        }
    }

    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = requireContext().getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (index >= 0) result = cursor.getString(index);
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) result = result.substring(cut + 1);
        }
        return result;
    }

    private void cargarFotoPerfil(String urlFoto) {
        if (!isAdded()) return; // ✅ BLINDAJE
        if (urlFoto != null && !urlFoto.isEmpty()) {
            Glide.with(this).load(urlFoto).placeholder(R.drawable.ic_avatar_default).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).circleCrop().into(ivAvatarConfig);
        } else {
            ivAvatarConfig.setImageResource(R.drawable.ic_avatar_default);
        }
    }

    private void mostrarDialogoCerrarSesion() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Cerrar Sesión")
                .setMessage("¿Estás seguro de que deseas cerrar sesión?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("Sí, cerrar sesión", (dialog, which) -> cerrarSesion())
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void cerrarSesion() {
        if (!isAdded()) return; // ✅ BLINDAJE
        SharedPreferences prefs = requireContext().getSharedPreferences("SportinePrefs", Context.MODE_PRIVATE);
        prefs.edit().clear().apply();
        Toast.makeText(requireContext(), "Sesión cerrada correctamente", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(requireContext(), com.example.sportine.ui.usuarios.login.LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }
}