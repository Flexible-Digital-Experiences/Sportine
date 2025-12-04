package com.example.sportine.ui.usuarios.configuracion;

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

    // ==========================================
    // COMPONENTES DE LA UI
    // ==========================================
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

    // API Service
    private ApiService apiService;

    // Datos del usuario
    private String username;
    private String rol;
    private UsuarioDetalleDTO usuarioActual;

    // Launcher para seleccionar imagen
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private Uri selectedImageUri;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_alumno_configuracion, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Inicializar componentes
        inicializarComponentes(view);

        // 2. Configurar launcher de imágenes
        configurarImagePicker();

        // 3. Inicializar Retrofit
        apiService = RetrofitClient.getClient(requireContext()).create(ApiService.class);

        // 4. Obtener username
        obtenerDatosUsuarioLogueado();

        // 5. Configurar botones
        configurarBotones(view);

        // 6. Cargar datos del usuario
        cargarDatosUsuario();
    }

    /**
     * Inicializa todas las vistas del layout
     */
    private void inicializarComponentes(View view) {
        Log.d(TAG, "Inicializando componentes...");

        // Avatar y card
        ivAvatarConfig = view.findViewById(R.id.iv_avatar_config);
        cardAvatarConfig = view.findViewById(R.id.card_avatar_config);
        btnEditarFoto = view.findViewById(R.id.iv_edit_foto);

        // TextViews de datos
        tvNombre = view.findViewById(R.id.tvNombre);
        tvApellido = view.findViewById(R.id.tvApellido);
        tvUsername = view.findViewById(R.id.tvUsername);
        tvSexo = view.findViewById(R.id.tvSexo);
        tvEstado = view.findViewById(R.id.tvEstado);
        tvCiudad = view.findViewById(R.id.tvCiudad);
        tvPassword = view.findViewById(R.id.tvPassword);

        // Botones
        btnTogglePassword = view.findViewById(R.id.btnTogglePassword);
        btnModificar = view.findViewById(R.id.btnModificar);
        btnBack = view.findViewById(R.id.btnBack);
        btnCerrarSesion = view.findViewById(R.id.btnCerrarSesion);

        Log.d(TAG, "✓ Componentes inicializados");
    }

    /**
     * Configura el launcher para seleccionar imágenes de la galería
     */
    private void configurarImagePicker() {
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        if (selectedImageUri != null) {
                            Log.d(TAG, "📸 Imagen seleccionada: " + selectedImageUri);

                            // Mostrar imagen seleccionada temporalmente
                            Glide.with(this)
                                    .load(selectedImageUri)
                                    .circleCrop()
                                    .into(ivAvatarConfig);

                            // ✅ Subir imagen a Cloudinary
                            subirFotoPerfilACloudinary(selectedImageUri);
                        }
                    }
                }
        );
    }

    /**
     * Obtiene el username del usuario desde SharedPreferences
     */
    private void obtenerDatosUsuarioLogueado() {
        SharedPreferences prefs = requireContext()
                .getSharedPreferences("SportinePrefs", Context.MODE_PRIVATE);
        username = prefs.getString("USER_USERNAME", null);
        rol = prefs.getString("USER_ROL", null);
        Log.d(TAG, "Usuario logueado: " + username + ", Rol: " + rol);
    }

    /**
     * Configura los listeners de los botones
     */
    private void configurarBotones(View view) {
        // Botón volver
        btnBack.setOnClickListener(v -> requireActivity().onBackPressed());

        // Botón modificar - Navegar al fragment de modificar
        btnModificar.setOnClickListener(v ->
                Navigation.findNavController(view)
                        .navigate(R.id.action_configuracion_to_modificar)
        );

        // Ocultar botón de toggle de contraseña
        if (btnTogglePassword != null) {
            btnTogglePassword.setVisibility(View.GONE);
        }

        // Click en el botón de editar foto
        if (btnEditarFoto != null) {
            btnEditarFoto.setOnClickListener(v -> abrirSelectorImagen());
        } else {
            Log.w(TAG, "⚠️ btnEditarFoto no encontrado en el layout");
        }

        // ✅ Botón cerrar sesión
        if (btnCerrarSesion != null) {
            btnCerrarSesion.setOnClickListener(v -> mostrarDialogoCerrarSesion());
            Log.d(TAG, "✓ Botón cerrar sesión configurado");
        } else {
            Log.e(TAG, "❌ btnCerrarSesion no encontrado");
        }
    }

    /**
     * Carga los datos del usuario desde el backend
     */
    private void cargarDatosUsuario() {
        if (username == null) {
            Toast.makeText(requireContext(),
                    "Error: no se pudo obtener el usuario",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "Cargando datos del usuario: " + username);

        Call<UsuarioDetalleDTO> call = apiService.obtenerUsuario(username);

        call.enqueue(new Callback<UsuarioDetalleDTO>() {
            @Override
            public void onResponse(Call<UsuarioDetalleDTO> call,
                                   Response<UsuarioDetalleDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    usuarioActual = response.body();
                    Log.d(TAG, "✓ Datos del usuario cargados exitosamente");
                    mostrarDatosUsuario(usuarioActual);

                    // Si es alumno, cargar foto de perfil
                    if ("alumno".equalsIgnoreCase(rol)) {
                        cargarFotoPerfilAlumno();
                    }
                } else {
                    Log.e(TAG, "❌ Error al cargar datos: " + response.code());
                    Toast.makeText(requireContext(),
                            "Error al cargar datos: " + response.code(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UsuarioDetalleDTO> call, Throwable t) {
                Log.e(TAG, "❌ Error de conexión: " + t.getMessage(), t);
                Toast.makeText(requireContext(),
                        "Error de conexión: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Muestra los datos del usuario en la UI
     */
    private void mostrarDatosUsuario(UsuarioDetalleDTO usuario) {
        Log.d(TAG, "===== MOSTRANDO DATOS DEL USUARIO =====");

        tvNombre.setText(usuario.getNombre() != null ? usuario.getNombre() : "-");
        tvApellido.setText(usuario.getApellidos() != null ? usuario.getApellidos() : "-");
        tvUsername.setText("@" + usuario.getUsuario());
        tvSexo.setText(usuario.getSexo() != null ? usuario.getSexo() : "-");
        tvEstado.setText(usuario.getEstado() != null ? usuario.getEstado() : "-");
        tvCiudad.setText(usuario.getCiudad() != null ? usuario.getCiudad() : "-");
        tvPassword.setText("••••••••");

        Log.d(TAG, "===== FIN MOSTRAR DATOS =====");
    }

    /**
     * Carga la foto de perfil del alumno desde Cloudinary
     */
    private void cargarFotoPerfilAlumno() {
        Call<PerfilAlumnoResponseDTO> call = apiService.obtenerPerfilAlumno(username);

        call.enqueue(new Callback<PerfilAlumnoResponseDTO>() {
            @Override
            public void onResponse(Call<PerfilAlumnoResponseDTO> call,
                                   Response<PerfilAlumnoResponseDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String fotoPerfil = response.body().getFotoPerfil();
                    cargarFotoPerfil(fotoPerfil);
                }
            }

            @Override
            public void onFailure(Call<PerfilAlumnoResponseDTO> call, Throwable t) {
                Log.w(TAG, "No se pudo cargar foto de perfil: " + t.getMessage());
            }
        });
    }

    /**
     * Abre el selector de imágenes
     */
    private void abrirSelectorImagen() {
        Log.d(TAG, "Abriendo selector de imágenes...");
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    /**
     * ✅ NUEVO: Sube la foto de perfil a Cloudinary vía backend
     */
    private void subirFotoPerfilACloudinary(Uri imageUri) {
        Log.d(TAG, "📤 Iniciando subida de foto a Cloudinary...");

        // Mostrar indicador de carga
        Toast.makeText(requireContext(), "Subiendo imagen...", Toast.LENGTH_SHORT).show();

        try {
            // 1. Convertir URI a File
            File file = uriToFile(imageUri);
            if (file == null) {
                Toast.makeText(requireContext(), "Error al procesar la imagen", Toast.LENGTH_SHORT).show();
                return;
            }

            // 2. Crear RequestBody para el archivo
            RequestBody requestFile = RequestBody.create(
                    MediaType.parse(requireContext().getContentResolver().getType(imageUri)),
                    file
            );

            // 3. Crear MultipartBody.Part
            MultipartBody.Part body = MultipartBody.Part.createFormData(
                    "foto",
                    file.getName(),
                    requestFile
            );

            // 4. Hacer la llamada al backend
            Call<Map<String, String>> call = apiService.actualizarFotoPerfil(username, body);

            call.enqueue(new Callback<Map<String, String>>() {
                @Override
                public void onResponse(Call<Map<String, String>> call,
                                       Response<Map<String, String>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        String nuevaUrl = response.body().get("fotoPerfil");
                        Log.d(TAG, "✅ Foto subida exitosamente: " + nuevaUrl);

                        Toast.makeText(requireContext(),
                                "Foto actualizada correctamente",
                                Toast.LENGTH_SHORT).show();

                        // Recargar la foto de perfil
                        cargarFotoPerfil(nuevaUrl);

                    } else {
                        Log.e(TAG, "❌ Error al subir foto: " + response.code());
                        Toast.makeText(requireContext(),
                                "Error al actualizar la foto: " + response.code(),
                                Toast.LENGTH_SHORT).show();

                        // Recargar la foto anterior
                        cargarFotoPerfilAlumno();
                    }
                }

                @Override
                public void onFailure(Call<Map<String, String>> call, Throwable t) {
                    Log.e(TAG, "❌ Error de conexión: " + t.getMessage(), t);
                    Toast.makeText(requireContext(),
                            "Error de conexión: " + t.getMessage(),
                            Toast.LENGTH_LONG).show();

                    // Recargar la foto anterior
                    cargarFotoPerfilAlumno();
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "❌ Error al preparar imagen: " + e.getMessage(), e);
            Toast.makeText(requireContext(),
                    "Error al procesar la imagen",
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Convierte un URI de imagen a un File temporal
     */
    private File uriToFile(Uri uri) {
        try {
            InputStream inputStream = requireContext().getContentResolver().openInputStream(uri);
            if (inputStream == null) return null;

            // Obtener el nombre del archivo
            String fileName = getFileName(uri);
            if (fileName == null) {
                fileName = "temp_image_" + System.currentTimeMillis() + ".jpg";
            }

            // Crear archivo temporal
            File file = new File(requireContext().getCacheDir(), fileName);
            FileOutputStream outputStream = new FileOutputStream(file);

            // Copiar datos
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.close();
            inputStream.close();

            return file;

        } catch (Exception e) {
            Log.e(TAG, "Error al convertir URI a File: " + e.getMessage(), e);
            return null;
        }
    }

    /**
     * Obtiene el nombre del archivo desde el URI
     */
    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = requireContext().getContentResolver()
                    .query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (index >= 0) {
                        result = cursor.getString(index);
                    }
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    /**
     * Carga la foto de perfil desde Cloudinary
     */
    private void cargarFotoPerfil(String urlFoto) {
        if (urlFoto != null && !urlFoto.isEmpty()) {
            Log.d(TAG, "Cargando foto de perfil: " + urlFoto);
            Glide.with(this)
                    .load(urlFoto)
                    .placeholder(R.drawable.ic_avatar_default)
                    .error(R.drawable.ic_avatar_default)
                    .diskCacheStrategy(DiskCacheStrategy.NONE) // No cachear para ver cambios inmediatos
                    .skipMemoryCache(true)
                    .circleCrop()
                    .into(ivAvatarConfig);
        } else {
            ivAvatarConfig.setImageResource(R.drawable.ic_avatar_default);
        }
    }
    /**
     * ✅ NUEVO: Muestra un diálogo de confirmación antes de cerrar sesión
     */
    private void mostrarDialogoCerrarSesion() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Cerrar Sesión")
                .setMessage("¿Estás seguro de que deseas cerrar sesión?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("Sí, cerrar sesión", (dialog, which) -> cerrarSesion())
                .setNegativeButton("Cancelar", null)
                .show();
    }

    /**
     * ✅ NUEVO: Cierra la sesión del usuario
     */
    private void cerrarSesion() {
        Log.d(TAG, "Cerrando sesión del usuario: " + username);

        // 1. Limpiar SharedPreferences
        SharedPreferences prefs = requireContext()
                .getSharedPreferences("SportinePrefs", Context.MODE_PRIVATE);
        prefs.edit().clear().apply();

        Log.d(TAG, "✓ SharedPreferences limpiadas");

        // 2. Mostrar mensaje
        Toast.makeText(requireContext(),
                "Sesión cerrada correctamente",
                Toast.LENGTH_SHORT).show();

        // 3. Redirigir a LoginActivity
        Intent intent = new Intent(requireContext(), com.example.sportine.ui.usuarios.login.LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

        // 4. Cerrar la actividad actual
        requireActivity().finish();

        Log.d(TAG, "✓ Redirigido a LoginActivity");
    }
}