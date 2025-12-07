package com.example.sportine.ui.entrenadores.gestionardeportes;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.sportine.R;
import com.example.sportine.data.ApiService;
import com.example.sportine.data.RetrofitClient;
import com.example.sportine.ui.entrenadores.dto.DeporteRequestDTO;
import com.example.sportine.ui.entrenadores.dto.PerfilEntrenadorResponseDTO;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GestionarDeportesFragment extends Fragment {

    private static final String TAG = "GestionarDeportesFragment";

    // ==========================================
    // COMPONENTES DE LA UI
    // ==========================================
    private View btnBack;

    // Deportes actuales
    private LinearLayout deportesActualesContainer;
    private TextView tvSinDeportes;

    // Agregar deportes
    private AutoCompleteTextView spinnerDeportesDisponibles;
    private MaterialButton btnAgregarDeporte;

    // Eliminar deportes
    private TextInputLayout layoutEliminarDeporte;
    private AutoCompleteTextView spinnerDeportesActuales;
    private MaterialButton btnEliminarDeporte;

    // API Service
    private ApiService apiService;

    // Datos del usuario
    private String username;
    private List<String> deportesActuales = new ArrayList<>();
    private List<String> todosLosDeportes = new ArrayList<>();

    // Mapa de deportes a íconos (igual que en PerfilEntrenaFragment)
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
        return inflater.inflate(R.layout.fragment_entrenador_gestionar_deportes, container, false);
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
        configurarListeners();

        // 5. Cargar catálogo de deportes
        cargarCatalogoDeportes();

        // 6. Cargar deportes actuales del entrenador
        cargarDeportesActuales();
    }

    private void inicializarComponentes(@NonNull View view) {
        Log.d(TAG, "Inicializando componentes...");

        btnBack = view.findViewById(R.id.btnBack);

        deportesActualesContainer = view.findViewById(R.id.deportesActualesContainer);
        tvSinDeportes = view.findViewById(R.id.tvSinDeportes);

        spinnerDeportesDisponibles = view.findViewById(R.id.spinnerDeportesDisponibles);
        btnAgregarDeporte = view.findViewById(R.id.btnAgregarDeporte);

        layoutEliminarDeporte = view.findViewById(R.id.layoutEliminarDeporte);
        spinnerDeportesActuales = view.findViewById(R.id.spinnerDeportesActuales);
        btnEliminarDeporte = view.findViewById(R.id.btnEliminarDeporte);

        Log.d(TAG, "✓ Componentes inicializados");
    }

    private void obtenerDatosUsuarioLogueado() {
        SharedPreferences prefs = requireContext()
                .getSharedPreferences("SportinePrefs", Context.MODE_PRIVATE);
        username = prefs.getString("USER_USERNAME", null);
        Log.d(TAG, "Usuario logueado: " + username);
    }

    private void configurarListeners() {
        // Botón volver
        btnBack.setOnClickListener(v -> requireActivity().onBackPressed());

        // Botón agregar deporte
        btnAgregarDeporte.setOnClickListener(v -> agregarDeporte());

        // Botón eliminar deporte
        btnEliminarDeporte.setOnClickListener(v -> mostrarDialogoEliminar());
    }

    /**
     * Carga el catálogo completo de deportes desde el backend
     */
    private void cargarCatalogoDeportes() {
        Log.d(TAG, "Cargando catálogo de deportes...");

        Call<List<String>> call = apiService.obtenerCatalogoDeportes();

        call.enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call,
                                   Response<List<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    todosLosDeportes = response.body();
                    Log.d(TAG, "✓ Catálogo cargado: " + todosLosDeportes.size() + " deportes");
                    actualizarSpinnerDisponibles();
                } else {
                    Log.e(TAG, "❌ Error al cargar catálogo: " + response.code());
                    Toast.makeText(requireContext(),
                            "Error al cargar catálogo de deportes",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                Log.e(TAG, "❌ Error de conexión: " + t.getMessage(), t);
                Toast.makeText(requireContext(),
                        "Error de conexión al cargar deportes",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Carga los deportes actuales del entrenador
     */
    private void cargarDeportesActuales() {
        if (username == null) {
            Toast.makeText(requireContext(),
                    "Error: no se pudo obtener el usuario",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "Cargando deportes del entrenador: " + username);

        Call<PerfilEntrenadorResponseDTO> call = apiService.obtenerMiPerfilEntrenador(username);

        call.enqueue(new Callback<PerfilEntrenadorResponseDTO>() {
            @Override
            public void onResponse(Call<PerfilEntrenadorResponseDTO> call,
                                   Response<PerfilEntrenadorResponseDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    deportesActuales = response.body().getDeportes();
                    Log.d(TAG, "✓ Deportes actuales cargados: " + deportesActuales.size());
                    mostrarDeportesActuales();
                    actualizarSpinnerDisponibles();
                    actualizarSpinnerActuales();
                } else {
                    Log.e(TAG, "❌ Error al cargar deportes: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<PerfilEntrenadorResponseDTO> call, Throwable t) {
                Log.e(TAG, "❌ Error de conexión: " + t.getMessage(), t);
            }
        });
    }

    /**
     * Muestra los deportes actuales en la UI
     */
    private void mostrarDeportesActuales() {
        deportesActualesContainer.removeAllViews();

        if (deportesActuales == null || deportesActuales.isEmpty()) {
            tvSinDeportes.setVisibility(View.VISIBLE);
            deportesActualesContainer.setVisibility(View.GONE);
            return;
        }

        tvSinDeportes.setVisibility(View.GONE);
        deportesActualesContainer.setVisibility(View.VISIBLE);

        for (String deporte : deportesActuales) {
            deportesActualesContainer.addView(crearIconoDeporte(deporte));
        }
    }

    /**
     * Crea un ícono de deporte dinámico
     */
    private View crearIconoDeporte(String nombreDeporte) {
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

        String color = DEPORTE_COLORES.getOrDefault(nombreDeporte, "#E0E0E0");
        card.setCardBackgroundColor(android.graphics.Color.parseColor(color));

        ImageView imageView = new ImageView(requireContext());
        int size = (int) (50 * getResources().getDisplayMetrics().density);
        int padding = (int) (10 * getResources().getDisplayMetrics().density);
        android.widget.FrameLayout.LayoutParams imgParams =
                new android.widget.FrameLayout.LayoutParams(size, size);
        imgParams.gravity = android.view.Gravity.CENTER;
        imageView.setLayoutParams(imgParams);
        imageView.setPadding(padding, padding, padding, padding);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);

        Integer iconoResId = DEPORTE_ICONOS.get(nombreDeporte);
        if (iconoResId != null) {
            imageView.setImageResource(iconoResId);
        } else {
            imageView.setImageResource(R.drawable.ic_avatar_default);
        }

        imageView.setContentDescription(nombreDeporte);
        card.addView(imageView);

        return card;
    }

    /**
     * Actualiza el spinner de deportes disponibles (excluyendo los que ya tiene)
     */
    private void actualizarSpinnerDisponibles() {
        List<String> deportesDisponibles = new ArrayList<>();

        for (String deporte : todosLosDeportes) {
            if (!deportesActuales.contains(deporte)) {
                deportesDisponibles.add(deporte);
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                deportesDisponibles
        );
        spinnerDeportesDisponibles.setAdapter(adapter);

        Log.d(TAG, "✓ Spinner disponibles actualizado: " + deportesDisponibles.size() + " deportes");
    }

    /**
     * Actualiza el spinner de deportes actuales
     */
    private void actualizarSpinnerActuales() {
        if (deportesActuales.isEmpty()) {
            layoutEliminarDeporte.setEnabled(false);
            spinnerDeportesActuales.setText("");
            btnEliminarDeporte.setEnabled(false);
        } else {
            layoutEliminarDeporte.setEnabled(true);
            btnEliminarDeporte.setEnabled(true);

            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    requireContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    deportesActuales
            );
            spinnerDeportesActuales.setAdapter(adapter);
        }

        Log.d(TAG, "✓ Spinner actuales actualizado: " + deportesActuales.size() + " deportes");
    }

    /**
     * Agrega un deporte al perfil del entrenador
     */
    private void agregarDeporte() {
        String deporteSeleccionado = spinnerDeportesDisponibles.getText().toString().trim();

        if (deporteSeleccionado.isEmpty()) {
            Toast.makeText(requireContext(),
                    "Selecciona un deporte para agregar",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "Agregando deporte: " + deporteSeleccionado);

        // ✅ Crear el DTO
        DeporteRequestDTO request = new DeporteRequestDTO(deporteSeleccionado);

        // ✅ Enviar el DTO
        Call<Void> call = apiService.agregarDeporteEntrenador(username, request);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "✓ Deporte agregado exitosamente");
                    Toast.makeText(requireContext(),
                            "Deporte agregado correctamente",
                            Toast.LENGTH_SHORT).show();

                    deportesActuales.add(deporteSeleccionado);
                    spinnerDeportesDisponibles.setText("");

                    mostrarDeportesActuales();
                    actualizarSpinnerDisponibles();
                    actualizarSpinnerActuales();
                } else {
                    Log.e(TAG, "❌ Error al agregar deporte: " + response.code());
                    Toast.makeText(requireContext(),
                            "Error al agregar deporte: " + response.code(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "❌ Error de conexión: " + t.getMessage(), t);
                Toast.makeText(requireContext(),
                        "Error de conexión: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Muestra diálogo de confirmación antes de eliminar
     */
    private void mostrarDialogoEliminar() {
        String deporteSeleccionado = spinnerDeportesActuales.getText().toString().trim();

        if (deporteSeleccionado.isEmpty()) {
            Toast.makeText(requireContext(),
                    "Selecciona un deporte para eliminar",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(requireContext())
                .setTitle("Eliminar Deporte")
                .setMessage("¿Estás seguro de eliminar " + deporteSeleccionado + " de tu perfil?\n\nEsta acción no se puede deshacer.")
                .setPositiveButton("Eliminar", (dialog, which) -> eliminarDeporte(deporteSeleccionado))
                .setNegativeButton("Cancelar", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    /**
     * Elimina un deporte del perfil del entrenador
     */
    private void eliminarDeporte(String deporte) {
        Log.d(TAG, "Eliminando deporte: " + deporte);

        Call<Void> call = apiService.eliminarDeporteEntrenador(username, deporte);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "✓ Deporte eliminado exitosamente");
                    Toast.makeText(requireContext(),
                            "Deporte eliminado correctamente",
                            Toast.LENGTH_SHORT).show();

                    // Actualizar listas
                    deportesActuales.remove(deporte);
                    spinnerDeportesActuales.setText("");

                    // Refrescar UI
                    mostrarDeportesActuales();
                    actualizarSpinnerDisponibles();
                    actualizarSpinnerActuales();
                } else {
                    Log.e(TAG, "❌ Error al eliminar deporte: " + response.code());
                    Toast.makeText(requireContext(),
                            "Error al eliminar deporte: " + response.code(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "❌ Error de conexión: " + t.getMessage(), t);
                Toast.makeText(requireContext(),
                        "Error de conexión: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}