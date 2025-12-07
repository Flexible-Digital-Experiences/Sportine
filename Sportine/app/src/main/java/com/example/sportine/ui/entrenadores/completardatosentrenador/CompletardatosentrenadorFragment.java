package com.example.sportine.ui.entrenadores.completardatosentrenador;

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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.example.sportine.R;
import com.example.sportine.data.ApiService;
import com.example.sportine.data.RetrofitClient;
import com.example.sportine.ui.entrenadores.dto.ActualizarPerfilEntrenadorDTO;
import com.example.sportine.ui.entrenadores.dto.PerfilEntrenadorResponseDTO;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CompletardatosentrenadorFragment extends Fragment {

    private static final String TAG = "CompletarDatosFragment";

    // ==========================================
    // COMPONENTES DE LA UI
    // ==========================================
    private View btnBack;
    private ImageView ivAvatarCompletar;
    // Componentes Premium
    private MaterialCardView cardHaztePremium;
    private MaterialButton btnHaztePremium;
    private MaterialCardView cardCancelarSuscripcion;
    private MaterialButton btnGestionarSuscripcion;

    // Datos Actuales (Solo Lectura)
    private TextView tvCostoActual;
    private TextView tvTipoCuentaActual;
    private TextView tvDeportesActual;
    private TextView tvLimiteAlumnosActual;
    private TextView tvDescripcionActual;
    private TextView tvCorreoActual;
    private TextView tvTelefonoActual;

    // Datos Nuevos (Editables)
    private TextInputEditText etCostoNuevo;
    private TextInputLayout layoutLimiteAlumnos;
    private AutoCompleteTextView spinnerLimiteAlumnos;
    private TextInputEditText etDescripcionNuevo;
    private TextInputEditText etCorreoNuevo;
    private TextInputEditText etTelefonoNuevo;

    private MaterialButton btnActualizar;

    // API Service
    private ApiService apiService;

    // Datos del usuario
    private String username;
    private PerfilEntrenadorResponseDTO perfilActual;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_entrenador_completar_datos, container, false);
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

        // 5. Cargar datos actuales
        cargarDatosActuales();
    }

    private void inicializarComponentes(@NonNull View view) {
        Log.d(TAG, "Inicializando componentes...");

        // Header
        btnBack = view.findViewById(R.id.btnBack);
        ivAvatarCompletar = view.findViewById(R.id.iv_avatar_completar);

        // Datos Actuales
        tvCostoActual = view.findViewById(R.id.tvCostoActual);
        tvTipoCuentaActual = view.findViewById(R.id.tvTipoCuentaActual);
        tvCorreoActual = view.findViewById(R.id.tvCorreoActual);           // ✅ NUEVO
        tvTelefonoActual = view.findViewById(R.id.tvTelefonoActual);       // ✅ NUEVO
        tvDeportesActual = view.findViewById(R.id.tvDeportesActual);
        tvLimiteAlumnosActual = view.findViewById(R.id.tvLimiteAlumnosActual);
        tvDescripcionActual = view.findViewById(R.id.tvDescripcionActual);

        // Datos Nuevos
        etCostoNuevo = view.findViewById(R.id.etCostoNuevo);
        etCorreoNuevo = view.findViewById(R.id.etCorreoNuevo);             // ✅ NUEVO
        etTelefonoNuevo = view.findViewById(R.id.etTelefonoNuevo);         // ✅ NUEVO
        layoutLimiteAlumnos = view.findViewById(R.id.layoutLimiteAlumnos);
        spinnerLimiteAlumnos = view.findViewById(R.id.spinnerLimiteAlumnos);
        etDescripcionNuevo = view.findViewById(R.id.etDescripcionNuevo);

        // Botón
        btnActualizar = view.findViewById(R.id.btnActualizar);

        // ✅ NUEVOS: Componentes Premium
        cardHaztePremium = view.findViewById(R.id.cardHaztePremium);
        btnHaztePremium = view.findViewById(R.id.btnHaztePremium);

        cardCancelarSuscripcion = view.findViewById(R.id.cardCancelarSuscripcion);
        btnGestionarSuscripcion = view.findViewById(R.id.btnGestionarSuscripcion);

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

        // Botón actualizar
        btnActualizar.setOnClickListener(v -> actualizarDatos());

        // ✅ NUEVO: Botón Hazte Premium
        btnHaztePremium.setOnClickListener(v -> mostrarPagoPremium());

        btnGestionarSuscripcion.setOnClickListener(v -> gestionarSuscripcion());
    }

    /**
     * Carga los datos actuales del entrenador desde el backend
     */
    private void cargarDatosActuales() {
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
                    perfilActual = response.body();
                    Log.d(TAG, "✓ Datos cargados exitosamente");
                    mostrarDatosActuales(perfilActual);
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
     * Muestra los datos actuales en la UI
     */
    private void mostrarDatosActuales(PerfilEntrenadorResponseDTO perfil) {
        Log.d(TAG, "===== MOSTRANDO DATOS ACTUALES =====");

        // Costo
        tvCostoActual.setText(perfil.getCostoMensualidad() != null
                ? "$" + perfil.getCostoMensualidad()
                : "-");

        // Tipo de cuenta
        String tipoCuenta = perfil.getTipoCuenta() != null
                ? perfil.getTipoCuenta().toUpperCase()
                : "GRATIS";
        tvTipoCuentaActual.setText(tipoCuenta);

        // ✅ Correo
        tvCorreoActual.setText(perfil.getCorreo() != null && !perfil.getCorreo().isEmpty()
                ? perfil.getCorreo()
                : "-");

        // ✅ Teléfono
        tvTelefonoActual.setText(perfil.getTelefono() != null && !perfil.getTelefono().isEmpty()
                ? perfil.getTelefono()
                : "-");

        // Deportes
        String deportes = perfil.getDeportes() != null && !perfil.getDeportes().isEmpty()
                ? String.join(", ", perfil.getDeportes())
                : "-";
        tvDeportesActual.setText(deportes);

        // Límite de alumnos
        tvLimiteAlumnosActual.setText(perfil.getLimiteAlumnos() != null
                ? String.valueOf(perfil.getLimiteAlumnos())
                : "-");

        // Descripción
        tvDescripcionActual.setText(perfil.getDescripcionPerfil() != null
                ? perfil.getDescripcionPerfil()
                : "-");

        // Foto
        cargarFotoPerfil(perfil.getFotoPerfil());

        // Configurar spinner de límite SOLO si es Premium
        configurarSpinnerLimite(tipoCuenta);

        Log.d(TAG, "===== FIN MOSTRAR DATOS =====");
    }

    /**
     * ✅ Configura el spinner de límite de alumnos SOLO si es Premium
     */
    private void configurarSpinnerLimite(String tipoCuenta) {
        if ("PREMIUM".equalsIgnoreCase(tipoCuenta)) {
            // Mostrar spinner
            layoutLimiteAlumnos.setVisibility(View.VISIBLE);

            // ✅ Ocultar botón Premium
            cardHaztePremium.setVisibility(View.GONE);
            cardCancelarSuscripcion.setVisibility(View.VISIBLE);

            // Opciones del spinner (5 a 50 alumnos)
            List<String> opciones = new ArrayList<>();
            for (int i = 5; i <= 50; i += 5) {
                opciones.add(String.valueOf(i));
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    requireContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    opciones
            );
            spinnerLimiteAlumnos.setAdapter(adapter);

            Log.d(TAG, "✓ Spinner de límite habilitado (Premium)");
        } else {
            // Ocultar spinner
            layoutLimiteAlumnos.setVisibility(View.GONE);

            // ✅ MOSTRAR botón Premium
            cardHaztePremium.setVisibility(View.VISIBLE);

            Log.d(TAG, "✓ Botón Premium visible (Cuenta gratuita)");
        }
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
                    .into(ivAvatarCompletar);
        } else {
            ivAvatarCompletar.setImageResource(R.drawable.ic_avatar_default);
        }
    }

    /**
     * Actualiza los datos del entrenador
     */
    private void actualizarDatos() {
        Log.d(TAG, "Iniciando actualización de datos...");

        // 1. Recoger datos
        String costoStr = etCostoNuevo.getText().toString().trim();
        String correo = etCorreoNuevo.getText().toString().trim();          // ✅ NUEVO
        String telefono = etTelefonoNuevo.getText().toString().trim();      // ✅ NUEVO
        String limiteStr = spinnerLimiteAlumnos.getText().toString().trim();
        String descripcion = etDescripcionNuevo.getText().toString().trim();

        // 2. Validar que al menos un campo esté lleno
        if (costoStr.isEmpty() && correo.isEmpty() && telefono.isEmpty() &&
                limiteStr.isEmpty() && descripcion.isEmpty()) {
            Toast.makeText(requireContext(),
                    "Ingresa al menos un campo para actualizar",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // ✅ 2.5. Validar correo
        if (!correo.isEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            Toast.makeText(requireContext(),
                    "Ingresa un correo válido",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // ✅ 2.6. Validar teléfono
        if (!telefono.isEmpty() && telefono.length() != 10) {
            Toast.makeText(requireContext(),
                    "El teléfono debe tener 10 dígitos",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // 3. Crear DTO
        ActualizarPerfilEntrenadorDTO dto = new ActualizarPerfilEntrenadorDTO();

        if (!costoStr.isEmpty()) {
            dto.setCostoMensualidad(Integer.parseInt(costoStr));
        }

        // ✅ AGREGAR correo y teléfono
        if (!correo.isEmpty()) {
            dto.setCorreo(correo);
        }

        if (!telefono.isEmpty()) {
            dto.setTelefono(telefono);
        }

        if (!limiteStr.isEmpty()) {
            dto.setLimiteAlumnos(Integer.parseInt(limiteStr));
        }

        if (!descripcion.isEmpty()) {
            dto.setDescripcionPerfil(descripcion);
        }

        // 4. Enviar al backend
        Log.d(TAG, "Enviando datos al backend...");

        Call<PerfilEntrenadorResponseDTO> call = apiService
                .actualizarPerfilEntrenador(username, dto);

        call.enqueue(new Callback<PerfilEntrenadorResponseDTO>() {
            @Override
            public void onResponse(Call<PerfilEntrenadorResponseDTO> call,
                                   Response<PerfilEntrenadorResponseDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "✓ Perfil actualizado exitosamente");
                    Toast.makeText(requireContext(),
                            "Perfil actualizado correctamente",
                            Toast.LENGTH_SHORT).show();

                    // Recargar datos
                    perfilActual = response.body();
                    mostrarDatosActuales(perfilActual);

                    // Limpiar campos
                    etCostoNuevo.setText("");
                    etCorreoNuevo.setText("");                 // ✅ NUEVO
                    etTelefonoNuevo.setText("");               // ✅ NUEVO
                    spinnerLimiteAlumnos.setText("");
                    etDescripcionNuevo.setText("");

                } else {
                    Log.e(TAG, "❌ Error al actualizar: " + response.code());
                    Toast.makeText(requireContext(),
                            "Error al actualizar: " + response.code(),
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

    private void gestionarSuscripcion() {
        Log.d(TAG, "Navegando a gestión de suscripción...");
        NavHostFragment.findNavController(this)
                .navigate(R.id.action_completardatosentrenadorFragment_to_suscription_Fragment);
    }

    private void mostrarPagoPremium() {
        Log.d(TAG, "Iniciando proceso de pago Premium...");
        NavHostFragment.findNavController(this)
                .navigate(R.id.action_completardatosentrenadorFragment_to_suscription_Fragment);
    }
}