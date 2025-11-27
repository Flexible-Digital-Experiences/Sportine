package com.example.sportine.ui.usuarios.perfil;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PerfilFragment extends Fragment {

    private static final String TAG = "PerfilFragment";

    // ==========================================
    // COMPONENTES DE LA UI
    // ==========================================
    private ImageView ivAvatarPerfil;     // Foto de perfil
    private TextView txtSaludo;
    private TextView tvNombre;
    private TextView tvApellido;
    private TextView tvUsername;
    private TextView tvSexo;
    private TextView tvEstado;
    private TextView tvCiudad;
    private TextView tvTotalAmigos;
    private TextView tvTotalEntrenadores;

    // Contenedor de deportes
    private LinearLayout deportesContainer;

    // Botones
    private MaterialCardView btnSettings;
    private MaterialButton btnCompletar;

    // API Service
    private ApiService apiService;

    // Datos del usuario
    private String username;
    private String rol;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_alumno_perfil, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Inicializar componentes
        inicializarComponentes(view);

        // 2. Inicializar Retrofit
        apiService = RetrofitClient.getClient(requireContext()).create(ApiService.class);

        // 3. Obtener datos del usuario logueado
        obtenerDatosUsuarioLogueado();

        // 4. Configurar botones de navegación
        configurarBotones(view);

        // 5. Cargar datos del perfil desde el backend
        cargarDatosPerfil();
    }

    /**
     * Inicializa todas las vistas del layout
     */
    private void inicializarComponentes(View view) {
        // Foto de perfil
        ivAvatarPerfil = view.findViewById(R.id.iv_avatar_perfil);

        // TextViews
        txtSaludo = view.findViewById(R.id.txtSaludo);
        tvNombre = view.findViewById(R.id.tvNombre);
        tvApellido = view.findViewById(R.id.tvApellido);
        tvUsername = view.findViewById(R.id.tvUsername);
        tvSexo = view.findViewById(R.id.tvSexo);
        tvEstado = view.findViewById(R.id.tvEstado);
        tvCiudad = view.findViewById(R.id.tvCiudad);
        tvTotalAmigos = view.findViewById(R.id.tvTotalAmigos);
        tvTotalEntrenadores = view.findViewById(R.id.tvTotalEntrenadores);

        // Contenedor de deportes (el LinearLayout horizontal que contiene las cards)
        deportesContainer = view.findViewById(R.id.deportesContainer);

        // Botones
        btnSettings = view.findViewById(R.id.btnSettings);
        btnCompletar = view.findViewById(R.id.btnCompletar);
    }

    /**
     * Obtiene el username y rol del usuario desde SharedPreferences
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
        btnSettings.setOnClickListener(v ->
                Navigation.findNavController(view)
                        .navigate(R.id.action_perfil_to_configuracion)
        );

        btnCompletar.setOnClickListener(v ->
                Navigation.findNavController(view)
                        .navigate(R.id.action_perfil_to_completar_datos)
        );
    }

    /**
     * Carga los datos del perfil desde el backend
     */
    private void cargarDatosPerfil() {
        if (username == null) {
            return;
        }

        // Primero obtener datos básicos del usuario
        Call<UsuarioDetalleDTO> call = apiService.obtenerUsuario(username);

        call.enqueue(new Callback<UsuarioDetalleDTO>() {
            @Override
            public void onResponse(Call<UsuarioDetalleDTO> call,
                                   Response<UsuarioDetalleDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UsuarioDetalleDTO usuario = response.body();
                    mostrarDatosBasicos(usuario);

                    // Si es alumno, intentar cargar perfil completo
                    if ("alumno".equalsIgnoreCase(rol)) {
                        cargarPerfilCompleto();
                    }
                } else {
                    Log.e(TAG, "Error al cargar datos: " + response.code());
                    Toast.makeText(requireContext(),
                            "Error al cargar datos: " + response.code(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UsuarioDetalleDTO> call, Throwable t) {
                Log.e(TAG, "Error de conexión: " + t.getMessage(), t);
                Toast.makeText(requireContext(),
                        "Error de conexión: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Muestra los datos básicos del usuario en la UI
     */
    private void mostrarDatosBasicos(UsuarioDetalleDTO usuario) {
        txtSaludo.setText("Hola " + usuario.getNombre());
        tvNombre.setText(usuario.getNombre());
        tvApellido.setText(usuario.getApellidos());
        tvUsername.setText("@" + usuario.getUsuario());
        tvSexo.setText(usuario.getSexo());
        tvEstado.setText(usuario.getEstado());
        tvCiudad.setText(usuario.getCiudad());
    }

    /**
     * Carga el perfil completo del alumno
     * VERSIÓN CON DEBUG COMPLETO
     */
    private void cargarPerfilCompleto() {
        Log.d(TAG, "→ Cargando perfil completo para usuario: " + username);

        Call<PerfilAlumnoResponseDTO> call = apiService.obtenerPerfilAlumno(username);

        call.enqueue(new Callback<PerfilAlumnoResponseDTO>() {
            @Override
            public void onResponse(Call<PerfilAlumnoResponseDTO> call,
                                   Response<PerfilAlumnoResponseDTO> response) {

                Log.d(TAG, "Response code: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    PerfilAlumnoResponseDTO perfil = response.body();
                    Log.d(TAG, "✓ Perfil recibido exitosamente");
                    Log.d(TAG, "  - Usuario: " + perfil.getUsuario());
                    Log.d(TAG, "  - Nombre: " + perfil.getNombre());
                    Log.d(TAG, "  - Foto: " + perfil.getFotoPerfil());
                    Log.d(TAG, "  - Deportes: " + perfil.getDeportes());

                    mostrarDatosCompletosPerfil(perfil);

                } else if (response.code() == 404) {
                    Log.w(TAG, "⚠ Perfil no completado (404)");
                    Toast.makeText(requireContext(),
                            "Completa tu perfil para ver más información",
                            Toast.LENGTH_SHORT).show();

                } else if (response.code() == 403) {
                    Log.e(TAG, "❌ Error 403 Forbidden - Problema con el token");
                    Toast.makeText(requireContext(),
                            "Error de autenticación (403)",
                            Toast.LENGTH_SHORT).show();

                } else {
                    Log.e(TAG, "❌ Error al cargar perfil: " + response.code());
                    try {
                        Log.e(TAG, "Error body: " + response.errorBody().string());
                    } catch (Exception e) {
                        Log.e(TAG, "No se pudo leer el error body", e);
                    }
                }
            }

            @Override
            public void onFailure(Call<PerfilAlumnoResponseDTO> call, Throwable t) {
                Log.e(TAG, "❌ Error de conexión al cargar perfil: " + t.getMessage(), t);
                Toast.makeText(requireContext(),
                        "Error de conexión: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Muestra los datos completos del perfil del alumno
     * VERSIÓN CON DEBUG COMPLETO
     */
    private void mostrarDatosCompletosPerfil(PerfilAlumnoResponseDTO perfil) {
        Log.d(TAG, "===== MOSTRANDO DATOS COMPLETOS DEL PERFIL =====");
        Log.d(TAG, "Foto perfil URL: " + perfil.getFotoPerfil());
        Log.d(TAG, "Deportes: " + perfil.getDeportes());
        Log.d(TAG, "Cantidad deportes: " + (perfil.getDeportes() != null ? perfil.getDeportes().size() : "NULL"));
        Log.d(TAG, "Total amigos: " + perfil.getTotalAmigos());
        Log.d(TAG, "Total entrenadores: " + perfil.getTotalEntrenadores());

        // 1. Cargar foto de perfil
        cargarFotoPerfil(perfil.getFotoPerfil());

        // 2. Mostrar deportes dinámicamente
        mostrarDeportes(perfil.getDeportes());

        // 3. Mostrar contadores
        mostrarContadores(perfil);

        Log.d(TAG, "===== FIN DATOS COMPLETOS =====");
    }

    /**
     * Carga la foto de perfil desde Cloudinary usando Glide
     */
    private void cargarFotoPerfil(String urlFotoPerfil) {
        if (urlFotoPerfil != null && !urlFotoPerfil.isEmpty()) {
            Log.d(TAG, "Cargando foto de perfil: " + urlFotoPerfil);

            Glide.with(this)
                    .load(urlFotoPerfil)
                    .placeholder(R.drawable.ic_avatar_default)  // Imagen mientras carga
                    .error(R.drawable.ic_avatar_default)         // Imagen si falla
                    .diskCacheStrategy(DiskCacheStrategy.ALL)    // Cachear la imagen
                    .circleCrop()                                // Hacer circular
                    .into(ivAvatarPerfil);
        } else {
            Log.w(TAG, "URL de foto de perfil vacía, usando imagen por defecto");
            ivAvatarPerfil.setImageResource(R.drawable.ic_avatar_default);
        }
    }

    /**
     * Muestra los deportes que practica el usuario
     * VERSIÓN CON DEBUG COMPLETO
     */
    private void mostrarDeportes(List<String> deportes) {
        Log.d(TAG, "===== MOSTRANDO DEPORTES =====");
        Log.d(TAG, "Deportes recibidos: " + deportes);

        // Limpiar el contenedor primero
        deportesContainer.removeAllViews();

        if (deportes == null) {
            Log.e(TAG, "❌ La lista de deportes es NULL");
            return;
        }

        if (deportes.isEmpty()) {
            Log.w(TAG, "⚠ La lista de deportes está VACÍA");
            return;
        }

        Log.d(TAG, "✓ Se van a mostrar " + deportes.size() + " deportes");

        // Crear una card por cada deporte
        for (int i = 0; i < deportes.size(); i++) {
            String deporte = deportes.get(i);
            Log.d(TAG, "Procesando deporte [" + i + "]: '" + deporte + "'");
            agregarCardDeporte(deporte);
        }

        Log.d(TAG, "✓ Deportes agregados al contenedor: " + deportesContainer.getChildCount());
    }

    /**
     * Agrega una card de deporte al contenedor
     */
    private void agregarCardDeporte(String deporte) {
        // Crear la card programáticamente
        MaterialCardView card = new MaterialCardView(requireContext());

        // Configurar dimensiones y márgenes
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                0,  // width = 0 para usar weight
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.weight = 1;  // Distribuir equitativamente
        params.setMarginEnd(dpToPx(8));  // Margen derecho
        card.setLayoutParams(params);

        // Configurar estilo de la card
        card.setRadius(dpToPx(12));
        card.setCardElevation(dpToPx(2));
        card.setCardBackgroundColor(obtenerColorDeporte(deporte));

        // Crear el ImageView
        ImageView imageView = new ImageView(requireContext());
        imageView.setLayoutParams(new LinearLayout.LayoutParams(
                dpToPx(50),
                dpToPx(50)
        ));
        imageView.setPadding(dpToPx(10), dpToPx(10), dpToPx(10), dpToPx(10));
        imageView.setImageResource(obtenerImagenDeporte(deporte));
        imageView.setContentDescription(deporte);

        // Agregar ImageView a la card
        card.addView(imageView);

        // Agregar card al contenedor
        deportesContainer.addView(card);
    }

    /**
     * Mapea el nombre del deporte a su imagen correspondiente
     * ACTUALIZADO: Maneja correctamente nombres con tildes y mayúsculas
     */
    private int obtenerImagenDeporte(String deporte) {
        if (deporte == null || deporte.isEmpty()) {
            Log.w(TAG, "Deporte null o vacío");
            return R.drawable.ic_deporte_default;
        }

        // Normalizar: quitar tildes y convertir a minúsculas
        String deporteNormalizado = deporte.toLowerCase()
                .trim()
                .replace("á", "a")
                .replace("é", "e")
                .replace("í", "i")
                .replace("ó", "o")
                .replace("ú", "u");

        Log.d(TAG, "Buscando imagen para deporte: '" + deporte + "' (normalizado: '" + deporteNormalizado + "')");

        switch (deporteNormalizado) {
            case "futbol":
            case "football":
            case "soccer":
                Log.d(TAG, "✓ Usando balon_futbol");
                return R.drawable.balon_futbol;

            case "basketball":
            case "basquet":
            case "baloncesto":
                Log.d(TAG, "✓ Usando balon_basket");
                return R.drawable.balon_basket;

            case "tenis":
            case "tennis":
                Log.d(TAG, "✓ Usando pelota_tenis");
                return R.drawable.pelota_tenis;

            case "gimnasio":
            case "gym":
                return R.drawable.ic_gimnasio;  // Si tienes esta imagen

            case "natacion":
            case "swimming":
                return R.drawable.ic_natacion;  // Si tienes esta imagen

            case "running":
            case "correr":
                return R.drawable.ic_running;

            case "boxeo":
            case "boxing":
                return R.drawable.ic_boxeo;

            case "ciclismo":
            case "cycling":
                return R.drawable.ic_ciclismo;

            case "beisbol":
            case "baseball":
                return R.drawable.ic_beisbol;

            default:
                Log.w(TAG, "⚠ Deporte NO reconocido: '" + deporte + "', usando imagen por defecto");
                return R.drawable.ic_deporte_default;
        }
    }

    /**
     * Obtiene el color de fondo según el deporte
     * ACTUALIZADO: Maneja correctamente nombres con tildes y mayúsculas
     */
    private int obtenerColorDeporte(String deporte) {
        if (deporte == null || deporte.isEmpty()) {
            return 0xFFF5F5F5;  // Gris claro por defecto
        }

        // Normalizar: quitar tildes y convertir a minúsculas
        String deporteNormalizado = deporte.toLowerCase()
                .trim()
                .replace("á", "a")
                .replace("é", "e")
                .replace("í", "i")
                .replace("ó", "o")
                .replace("ú", "u");

        switch (deporteNormalizado) {
            case "futbol":
            case "football":
            case "soccer":
                return 0xFFFFF3E0;  // Naranja claro

            case "basketball":
            case "basquet":
            case "baloncesto":
                return 0xFFFFE0E0;  // Rojo claro

            case "tenis":
            case "tennis":
                return 0xFFE0F2F1;  // Verde claro

            case "gimnasio":
            case "gym":
                return 0xFFE3F2FD;  // Azul claro

            case "natacion":
            case "swimming":
                return 0xFFE1F5FE;  // Azul agua

            case "running":
            case "correr":
                return 0xFFFFFAEE;  // Amarillo pálido

            case "boxeo":
            case "boxing":
                return 0xFFF0FFF0;  // Verde menta pálido

            case "ciclismo":
            case "cycling":
                return 0xFFE6E6FA;  // Lavanda pálido

            case "beisbol":
            case "baseball":
                return 0xFFFAE0E6;  // Rosa pálido

            default:
                return 0xFFF5F5F5;  // Gris claro
        }
    }

    /**
     * Convierte dp a pixeles
     */
    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    /**
     * Muestra los contadores de amigos y entrenadores
     */
    private void mostrarContadores(PerfilAlumnoResponseDTO perfil) {
        int amigos = perfil.getTotalAmigos();
        int entrenadores = perfil.getTotalEntrenadores();

        Log.d(TAG, "Total amigos: " + amigos);
        Log.d(TAG, "Total entrenadores: " + entrenadores);

        tvTotalAmigos.setText(String.valueOf(amigos));
        tvTotalEntrenadores.setText(String.valueOf(entrenadores));
    }
}