package com.example.sportine.ui.entrenadores.perfil;

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
import com.example.sportine.R;
import com.example.sportine.data.ApiService;
import com.example.sportine.data.RetrofitClient;
import com.example.sportine.ui.entrenadores.dto.PerfilEntrenadorResponseDTO;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PerfilEntrenaFragment extends Fragment {

    private static final String TAG = "PerfilEntrenaFragment";

    // ==========================================
    // COMPONENTES DE LA UI
    // ==========================================
    private ImageView ivAvatarPerfilEntrenador;
    private TextView txtSaludoEntrenador;
    private TextView tvDescripcionEntrenador;
    private TextView tvNombreEntrenador;
    private TextView tvApellidoEntrenador;
    private TextView tvUsernameEntrenador;
    private TextView tvSexoEntrenador;
    private TextView tvCorreoEntrenador;
    private TextView tvEstadoEntrenador;
    private TextView tvCiudadEntrenador;
    private TextView tvContadorAlumnos;
    private TextView tvContadorAmigos;

    // Contenedor de deportes dinámico
    private LinearLayout deportesContainer;

    // Botones
    private MaterialCardView btnSettings;
    private MaterialButton btnCompletardatos;
    private MaterialButton btnGestionarDeportes; // ✅ NUEVO

    // API Service
    private ApiService apiService;

    // Datos del usuario
    private String username;

    // Mapa de deportes a íconos
    private static final Map<String, Integer> DEPORTE_ICONOS = new HashMap<>();
    private static final Map<String, String> DEPORTE_COLORES = new HashMap<>();

    static {
        // Mapeo de deportes a íconos
        DEPORTE_ICONOS.put("Fútbol", R.drawable.balon_futbol);
        DEPORTE_ICONOS.put("Basketball", R.drawable.balon_basket);
        DEPORTE_ICONOS.put("Tenis", R.drawable.pelota_tenis);
        DEPORTE_ICONOS.put("Natación", R.drawable.ic_natacion);
        DEPORTE_ICONOS.put("Running", R.drawable.ic_running);
        DEPORTE_ICONOS.put("Boxeo", R.drawable.ic_boxeo);
        DEPORTE_ICONOS.put("Gimnasio", R.drawable.ic_gimnasio);
        DEPORTE_ICONOS.put("Ciclismo", R.drawable.ic_ciclismo);
        DEPORTE_ICONOS.put("Béisbol", R.drawable.ic_beisbol);

        // Mapeo de deportes a colores de fondo
        DEPORTE_COLORES.put("Fútbol", "#E0F2F1");
        DEPORTE_COLORES.put("Basketball", "#FFF3E0");
        DEPORTE_COLORES.put("Tenis", "#FFE0E0");
        DEPORTE_COLORES.put("Natación", "#E3F2FD");
        DEPORTE_COLORES.put("Running", "#F3E5F5");
        DEPORTE_COLORES.put("Boxeo", "#FFEBEE");
        DEPORTE_COLORES.put("Gimnasio", "#E8F5E9");
        DEPORTE_COLORES.put("Ciclismo", "#FFF9C4");
        DEPORTE_COLORES.put("Béisbol", "#E0F7FA");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_entrenador_perfil, container, false);
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

        // 4. Configurar botones
        configurarBotones(view);

        // 5. Cargar datos del entrenador
        cargarDatosEntrenador();
    }

    /**
     * Inicializa todas las vistas del layout
     */
    private void inicializarComponentes(@NonNull View view) {
        Log.d(TAG, "Inicializando componentes...");

        // Avatar
        ivAvatarPerfilEntrenador = view.findViewById(R.id.iv_avatar_perfil_entrenador);

        // Datos de perfil
        txtSaludoEntrenador = view.findViewById(R.id.txtSaludoEntrenador);
        tvDescripcionEntrenador = view.findViewById(R.id.tvDescripcionEntrenador);
        tvNombreEntrenador = view.findViewById(R.id.tvNombreEntrenador);
        tvApellidoEntrenador = view.findViewById(R.id.tvApellidoEntrenador);
        tvUsernameEntrenador = view.findViewById(R.id.tvUsernameEntrenador);
        tvCorreoEntrenador = view.findViewById(R.id.tvCorreoEntrenador);
        tvSexoEntrenador = view.findViewById(R.id.tvSexoEntrenador);
        tvEstadoEntrenador = view.findViewById(R.id.tvEstadoEntrenador);
        tvCiudadEntrenador = view.findViewById(R.id.tvCiudadEntrenador);

        // Contadores
        tvContadorAlumnos = view.findViewById(R.id.tvContadorAlumnos);
        tvContadorAmigos = view.findViewById(R.id.tvContadorAmigos);

        // Contenedor de deportes
        deportesContainer = view.findViewById(R.id.deportesContainer);

        // Botones
        btnSettings = view.findViewById(R.id.btnSettings);
        btnCompletardatos = view.findViewById(R.id.btnCompletarentrena);
        btnGestionarDeportes = view.findViewById(R.id.btnGestionarDeportes); // ✅ NUEVO

        Log.d(TAG, "✓ Componentes inicializados");
    }

    /**
     * Obtiene el username del usuario desde SharedPreferences
     */
    private void obtenerDatosUsuarioLogueado() {
        SharedPreferences prefs = requireContext()
                .getSharedPreferences("SportinePrefs", Context.MODE_PRIVATE);
        username = prefs.getString("USER_USERNAME", null);
        Log.d(TAG, "Usuario logueado: " + username);
    }

    /**
     * Configura los listeners de los botones
     */
    private void configurarBotones(View view) {
        // Botón de configuración
        if (btnSettings != null) {
            btnSettings.setOnClickListener(v -> {
                Navigation.findNavController(v)
                        .navigate(R.id.action_perfilentre_to_configuracion);
            });
        }

        // Botón completar datos
        if (btnCompletardatos != null) {
            btnCompletardatos.setOnClickListener(v -> {
                Navigation.findNavController(v)
                        .navigate(R.id.action_perfilentre_to_completar_datos);
            });
        }

        // ✅ NUEVO: Botón gestionar deportes
        if (btnGestionarDeportes != null) {
            btnGestionarDeportes.setOnClickListener(v -> {
                Navigation.findNavController(v)
                        .navigate(R.id.action_perfilentre_to_gestionar_deportes);
            });
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

                // ✅ FIX: Validación para evitar el crash si el fragmento ya no existe
                if (!isAdded()) return;

                if (response.isSuccessful() && response.body() != null) {
                    PerfilEntrenadorResponseDTO perfil = response.body();
                    Log.d(TAG, "✓ Datos del entrenador cargados exitosamente");
                    mostrarDatosEntrenador(perfil);
                } else {
                    Log.e(TAG, "❌ Error al cargar datos: " + response.code());
                    Toast.makeText(requireContext(),
                            "Error al cargar datos: " + response.code(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PerfilEntrenadorResponseDTO> call, Throwable t) {
                // ✅ FIX: Validación también aquí por si acaso
                if (!isAdded()) return;

                Log.e(TAG, "❌ Error de conexión: " + t.getMessage(), t);
                Toast.makeText(requireContext(),
                        "Error de conexión: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Muestra los datos del entrenador en la UI
     */
    private void mostrarDatosEntrenador(PerfilEntrenadorResponseDTO perfil) {
        Log.d(TAG, "===== MOSTRANDO DATOS DEL ENTRENADOR =====");

        // Datos básicos
        txtSaludoEntrenador.setText("Hola " + (perfil.getNombre() != null ? perfil.getNombre() : ""));
        tvDescripcionEntrenador.setText(perfil.getDescripcionPerfil() != null ? perfil.getDescripcionPerfil() : "");
        tvNombreEntrenador.setText(perfil.getNombre() != null ? perfil.getNombre() : "-");
        tvApellidoEntrenador.setText(perfil.getApellidos() != null ? perfil.getApellidos() : "-");
        tvUsernameEntrenador.setText("@" + perfil.getUsuario());
        tvSexoEntrenador.setText(perfil.getSexo() != null ? perfil.getSexo() : "-");
        tvEstadoEntrenador.setText(perfil.getEstado() != null ? perfil.getEstado() : "-");
        tvCiudadEntrenador.setText(perfil.getCiudad() != null ? perfil.getCiudad() : "-");
        tvCorreoEntrenador.setText(perfil.getCorreo() != null ? perfil.getCorreo() : "-");

        // Contadores
        tvContadorAlumnos.setText(String.valueOf(perfil.getTotalAlumnos()));
        tvContadorAmigos.setText(String.valueOf(perfil.getTotalAmigos()));

        // Cargar foto de perfil
        cargarFotoPerfil(perfil.getFotoPerfil());

        // Mostrar deportes dinámicamente
        mostrarDeportes(perfil.getDeportes());

        Log.d(TAG, "✓ Deportes: " + perfil.getDeportes().size());
        Log.d(TAG, "✓ Alumnos: " + perfil.getTotalAlumnos());
        Log.d(TAG, "✓ Amigos: " + perfil.getTotalAmigos());
        Log.d(TAG, "===== FIN MOSTRAR DATOS =====");
    }

    /**
     * Muestra los deportes dinámicamente
     */
    private void mostrarDeportes(List<String> deportes) {
        if (deportesContainer == null) {
            Log.e(TAG, "❌ deportesContainer es null");
            return;
        }

        // Limpiar deportes anteriores
        deportesContainer.removeAllViews();

        if (deportes == null || deportes.isEmpty()) {
            Log.w(TAG, "No hay deportes para mostrar");
            return;
        }

        Log.d(TAG, "Mostrando " + deportes.size() + " deportes");

        // Crear un ícono por cada deporte
        for (String deporte : deportes) {
            deportesContainer.addView(crearIconoDeporte(deporte));
        }
    }

    /**
     * Crea un ícono de deporte dinámico
     */
    private View crearIconoDeporte(String nombreDeporte) {
        // Crear MaterialCardView
        MaterialCardView card = new MaterialCardView(requireContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1.0f
        );
        params.setMarginEnd((int) (8 * getResources().getDisplayMetrics().density));
        card.setLayoutParams(params);
        card.setRadius(12 * getResources().getDisplayMetrics().density);
        card.setCardElevation(2 * getResources().getDisplayMetrics().density);

        // Color de fondo según el deporte
        String color = DEPORTE_COLORES.getOrDefault(nombreDeporte, "#E0E0E0");
        card.setCardBackgroundColor(android.graphics.Color.parseColor(color));

        // Crear ImageView
        ImageView imageView = new ImageView(requireContext());
        int size = (int) (50 * getResources().getDisplayMetrics().density);
        int padding = (int) (10 * getResources().getDisplayMetrics().density);
        android.widget.FrameLayout.LayoutParams imgParams =
                new android.widget.FrameLayout.LayoutParams(size, size);
        imgParams.gravity = android.view.Gravity.CENTER;
        imageView.setLayoutParams(imgParams);
        imageView.setPadding(padding, padding, padding, padding);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);

        // Establecer ícono según el deporte
        Integer iconoResId = DEPORTE_ICONOS.get(nombreDeporte);
        if (iconoResId != null) {
            imageView.setImageResource(iconoResId);
        } else {
            // Ícono por defecto si no se encuentra
            imageView.setImageResource(R.drawable.ic_avatar_default);
        }

        imageView.setContentDescription(nombreDeporte);

        // Agregar ImageView al Card
        card.addView(imageView);

        return card;
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
                    .circleCrop()
                    .into(ivAvatarPerfilEntrenador);
        } else {
            ivAvatarPerfilEntrenador.setImageResource(R.drawable.ic_avatar_default);
        }
    }
}