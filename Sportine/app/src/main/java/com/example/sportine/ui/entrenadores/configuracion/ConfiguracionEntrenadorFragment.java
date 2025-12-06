package com.example.sportine.ui.entrenadores.configuracion;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.sportine.R;
import com.example.sportine.data.ApiService;
import com.example.sportine.data.RetrofitClient;
import com.example.sportine.ui.entrenadores.dto.PerfilEntrenadorResponseDTO;
import com.google.android.material.button.MaterialButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConfiguracionEntrenadorFragment extends Fragment {

    private static final String TAG = "ConfiguracionFragment";

    // ==========================================
    // COMPONENTES DE LA UI
    // ==========================================
    private View btnBack;
    private ImageView ivAvatarConfig;

    // Datos Personales
    private TextView tvNombre;
    private TextView tvApellido;
    private TextView tvUsername;
    private TextView tvSexo;
    private TextView tvEstado;
    private TextView tvCiudad;
    private TextView tvTipoCuenta;
    private TextView tvPassword;

    // Detalles de Clases
    private TextView tvCostoMensualidad;
    private TextView tvAlumnosInscritos;
    private TextView tvDineroGanado;

    // Botones
    private MaterialButton btnModificar;
    private MaterialButton btnCerrarSesion; // ✅ NUEVO

    // API Service
    private ApiService apiService;

    // Datos del usuario
    private String username;

    // Launcher para seleccionar imagen
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    // Launcher para pedir permisos
    private ActivityResultLauncher<String> permissionLauncher;

    // Referencia al botón de editar foto
    private View btnEditarFoto;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inicializar launchers ANTES de inflar el layout
        inicializarLaunchers();

        return inflater.inflate(R.layout.fragment_entrenador_configuracion, container, false);
    }

    private void inicializarLaunchers() {
        // Launcher para seleccionar imagen
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        if (imageUri != null) {
                            subirFotoPerfil(imageUri);
                        }
                    }
                }
        );

        // Launcher para pedir permisos
        permissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        abrirGaleria();
                    } else {
                        Toast.makeText(requireContext(),
                                "Permiso de almacenamiento denegado",
                                Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Inicializar componentes
        inicializarComponentes(view);

        // 2. Inicializar Retrofit
        apiService = RetrofitClient.getClient(requireContext()).create(ApiService.class);

        // 3. Obtener username
        obtenerDatosUsuarioLogueado();

        // 4. Configurar listeners
        configurarListeners(view);

        // 5. Cargar datos desde backend
        cargarDatosEntrenador();
    }

    private void inicializarComponentes(@NonNull View view) {
        Log.d(TAG, "Inicializando componentes...");

        // Header
        btnBack = view.findViewById(R.id.btnBack);
        ivAvatarConfig = view.findViewById(R.id.iv_avatar_config);

        // Datos Personales
        tvNombre = view.findViewById(R.id.tvNombre);
        tvApellido = view.findViewById(R.id.tvApellido);
        tvUsername = view.findViewById(R.id.tvUsername);
        tvSexo = view.findViewById(R.id.tvSexo);
        tvEstado = view.findViewById(R.id.tvEstado);
        tvCiudad = view.findViewById(R.id.tvCiudad);
        tvTipoCuenta = view.findViewById(R.id.tvTipoCuenta);
        tvPassword = view.findViewById(R.id.tvPassword);

        // Detalles de Clases
        tvCostoMensualidad = view.findViewById(R.id.tvCostoMensualidad);
        tvAlumnosInscritos = view.findViewById(R.id.tvAlumnosInscritos);
        tvDineroGanado = view.findViewById(R.id.tvDineroGanado);

        // Botones
        btnModificar = view.findViewById(R.id.btnModificarentrena);
        btnEditarFoto = view.findViewById(R.id.btnEditarFoto);
        btnCerrarSesion = view.findViewById(R.id.btnCerrarSesion); // ✅ NUEVO

        Log.d(TAG, "✓ Componentes inicializados");
    }

    private void obtenerDatosUsuarioLogueado() {
        SharedPreferences prefs = requireContext()
                .getSharedPreferences("SportinePrefs", Context.MODE_PRIVATE);
        username = prefs.getString("USER_USERNAME", null);
        Log.d(TAG, "Usuario logueado: " + username);
    }

    private void configurarListeners(@NonNull View view) {
        // Botón volver
        btnBack.setOnClickListener(v -> requireActivity().onBackPressed());

        // Botón modificar (navegar a completar datos)
        btnModificar.setOnClickListener(v -> {
            Navigation.findNavController(v)
                    .navigate(R.id.action_configuracionentre_to_modificar);
        });

        // Botón editar foto
        btnEditarFoto.setOnClickListener(v -> verificarPermisoYAbrirGaleria());

        // ✅ NUEVO: Botón cerrar sesión
        if (btnCerrarSesion != null) {
            btnCerrarSesion.setOnClickListener(v -> mostrarDialogoCerrarSesion());
            Log.d(TAG, "✓ Botón cerrar sesión configurado");
        } else {
            Log.e(TAG, "❌ btnCerrarSesion no encontrado");
        }
    }

    /**
     * Carga los datos del entrenador desde el backend
     */
    private void cargarDatosEntrenador() {
        if (username == null) {
            Toast.makeText(requireContext(),
                    "Error: no se pudo obtener el usuario",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "Cargando datos del entrenador: " + username);

        Call<PerfilEntrenadorResponseDTO> call = apiService.obtenerMiPerfilEntrenador(username);

        call.enqueue(new Callback<PerfilEntrenadorResponseDTO>() {
            @Override
            public void onResponse(Call<PerfilEntrenadorResponseDTO> call,
                                   Response<PerfilEntrenadorResponseDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    PerfilEntrenadorResponseDTO perfil = response.body();
                    Log.d(TAG, "✓ Datos cargados exitosamente");
                    mostrarDatos(perfil);
                } else {
                    Log.e(TAG, "❌ Error al cargar datos: " + response.code());
                    Toast.makeText(requireContext(),
                            "Error al cargar datos: " + response.code(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PerfilEntrenadorResponseDTO> call, Throwable t) {
                Log.e(TAG, "❌ Error de conexión: " + t.getMessage(), t);
                Toast.makeText(requireContext(),
                        "Error de conexión: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Muestra los datos en la UI
     */
    private void mostrarDatos(PerfilEntrenadorResponseDTO perfil) {
        Log.d(TAG, "===== MOSTRANDO DATOS =====");

        // Datos Personales
        tvNombre.setText(perfil.getNombre() != null ? perfil.getNombre() : "-");
        tvApellido.setText(perfil.getApellidos() != null ? perfil.getApellidos() : "-");
        tvUsername.setText(perfil.getUsuario() != null ? perfil.getUsuario() : "-");
        tvSexo.setText(perfil.getSexo() != null ? perfil.getSexo() : "-");
        tvEstado.setText(perfil.getEstado() != null ? perfil.getEstado() : "-");
        tvCiudad.setText(perfil.getCiudad() != null ? perfil.getCiudad() : "-");

        // Tipo de Cuenta (con formato)
        String tipoCuenta = perfil.getTipoCuenta() != null
                ? perfil.getTipoCuenta().toUpperCase()
                : "GRATIS";
        tvTipoCuenta.setText(tipoCuenta);

        // Cambiar color según el tipo
        if ("PREMIUM".equalsIgnoreCase(tipoCuenta)) {
            tvTipoCuenta.setTextColor(getResources().getColor(android.R.color.holo_orange_dark));
        } else {
            tvTipoCuenta.setTextColor(getResources().getColor(android.R.color.holo_blue_dark));
        }

        // Contraseña (siempre oculta)
        tvPassword.setText("••••••••");

        // Detalles de Clases
        tvCostoMensualidad.setText(perfil.getCostoMensualidad() != null
                ? "$" + perfil.getCostoMensualidad()
                : "-");

        int alumnos = perfil.getTotalAlumnos();
        tvAlumnosInscritos.setText(String.valueOf(alumnos));

        // Calcular dinero ganado (costo × alumnos)
        if (perfil.getCostoMensualidad() != null && alumnos > 0) {
            int dineroGanado = perfil.getCostoMensualidad() * alumnos;
            tvDineroGanado.setText("$" + String.format("%,d", dineroGanado));
        } else {
            tvDineroGanado.setText("$0");
        }

        // Foto de perfil
        cargarFotoPerfil(perfil.getFotoPerfil());

        Log.d(TAG, "===== FIN MOSTRAR DATOS =====");
    }

    /**
     * Carga la foto de perfil
     */
    private void cargarFotoPerfil(String urlFoto) {
        if (urlFoto != null && !urlFoto.isEmpty()) {
            Glide.with(this)
                    .load(urlFoto)
                    .placeholder(R.drawable.ic_avatar_default)
                    .error(R.drawable.ic_avatar_default)
                    .circleCrop()
                    .into(ivAvatarConfig);
        } else {
            ivAvatarConfig.setImageResource(R.drawable.ic_avatar_default);
        }
    }

    /**
     * Verifica permisos y abre la galería
     */
    private void verificarPermisoYAbrirGaleria() {
        // Android 13+ (API 33+) no requiere READ_EXTERNAL_STORAGE para galería
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            abrirGaleria();
        } else {
            // Android 12 y anteriores
            if (ContextCompat.checkSelfPermission(requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                abrirGaleria();
            } else {
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        }
    }

    /**
     * Abre la galería para seleccionar una imagen
     */
    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    /**
     * Sube la foto de perfil al backend
     */
    private void subirFotoPerfil(Uri imageUri) {
        Log.d(TAG, "Iniciando subida de foto...");

        try {
            // 1. Convertir Uri a File
            File file = convertirUriAFile(imageUri);

            // 2. Crear RequestBody
            RequestBody requestBody = RequestBody.create(
                    MediaType.parse("image/*"),
                    file
            );

            // 3. Crear MultipartBody.Part
            MultipartBody.Part imagePart = MultipartBody.Part.createFormData(
                    "file",
                    file.getName(),
                    requestBody
            );

            // 4. Llamar al API
            Call<PerfilEntrenadorResponseDTO> call = apiService
                    .actualizarFotoPerfilEntrenador(username, imagePart);

            call.enqueue(new Callback<PerfilEntrenadorResponseDTO>() {
                @Override
                public void onResponse(Call<PerfilEntrenadorResponseDTO> call,
                                       Response<PerfilEntrenadorResponseDTO> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Log.d(TAG, "✓ Foto actualizada exitosamente");
                        Toast.makeText(requireContext(),
                                "Foto de perfil actualizada",
                                Toast.LENGTH_SHORT).show();

                        // Recargar datos para mostrar nueva foto
                        mostrarDatos(response.body());

                    } else {
                        Log.e(TAG, "❌ Error al actualizar foto: " + response.code());
                        Toast.makeText(requireContext(),
                                "Error al actualizar foto: " + response.code(),
                                Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<PerfilEntrenadorResponseDTO> call, Throwable t) {
                    Log.e(TAG, "❌ Error de conexión: " + t.getMessage(), t);
                    Toast.makeText(requireContext(),
                            "Error de conexión: " + t.getMessage(),
                            Toast.LENGTH_LONG).show();
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "❌ Error al procesar imagen: " + e.getMessage(), e);
            Toast.makeText(requireContext(),
                    "Error al procesar imagen",
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Convierte Uri a File
     */
    private File convertirUriAFile(Uri uri) throws Exception {
        InputStream inputStream = requireContext().getContentResolver().openInputStream(uri);
        File file = new File(requireContext().getCacheDir(), "temp_image.jpg");
        FileOutputStream outputStream = new FileOutputStream(file);

        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, length);
        }

        outputStream.close();
        inputStream.close();

        return file;
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
        Log.d(TAG, "Cerrando sesión del entrenador: " + username);

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