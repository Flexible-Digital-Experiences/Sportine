package com.example.sportine.ui.usuarios.modificardatosalumno;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.sportine.R;
import com.example.sportine.data.ApiService;
import com.example.sportine.data.RetrofitClient;
import com.example.sportine.ui.usuarios.dto.ActualizarUsuarioDTO;
import com.example.sportine.ui.usuarios.dto.UsuarioDetalleDTO;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ModificarDatosAlumnoFragment extends Fragment {

    private static final String TAG = "ModificarDatosAlumno";

    // ==========================================
    // COMPONENTES - DATOS ACTUALES
    // ==========================================
    private TextView tvNombreActual;
    private TextView tvApellidoActual;
    private TextView tvUsernameActual;
    private TextView tvSexoActual;
    private TextView tvEstadoActual;
    private TextView tvCiudadActual;
    private TextView tvPasswordActual;

    // ==========================================
    // ✅ COMPONENTES - NUEVOS DATOS (TODOS EDITTEXTS)
    // ==========================================
    private TextInputEditText etNombreNuevo;
    private TextInputEditText etApellidoNuevo;
    private TextInputEditText etSexoNuevo;
    private TextInputEditText etEstadoNuevo;
    private TextInputEditText etCiudadNuevo;
    private TextInputEditText etPasswordNuevo;

    // Botones
    private MaterialCardView btnBack;
    private MaterialButton btnActualizar;

    // API Service
    private ApiService apiService;

    // Datos del usuario
    private String username;
    private UsuarioDetalleDTO usuarioActual;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_alumno_modificar_datos, container, false);
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

        // 4. ✅ ELIMINADO: Ya no se configuran dropdowns

        // 5. Configurar botones
        configurarBotones();

        // 6. Cargar datos actuales
        cargarDatosActuales();
    }

    /**
     * Inicializa todas las vistas del layout
     */
    private void inicializarComponentes(View view) {
        Log.d(TAG, "Inicializando componentes...");

        // TextViews de datos actuales
        tvNombreActual = view.findViewById(R.id.tvNombreActual);
        tvApellidoActual = view.findViewById(R.id.tvApellidoActual);
        tvUsernameActual = view.findViewById(R.id.tvUsernameActual);
        tvSexoActual = view.findViewById(R.id.tvSexoActual);
        tvEstadoActual = view.findViewById(R.id.tvEstadoActual);
        tvCiudadActual = view.findViewById(R.id.tvCiudadActual);
        tvPasswordActual = view.findViewById(R.id.tvPasswordActual);

        // ✅ Campos editables (TODOS EditTexts ahora)
        etNombreNuevo = view.findViewById(R.id.etNombreNuevo);
        etApellidoNuevo = view.findViewById(R.id.etApellidoNuevo);
        etSexoNuevo = view.findViewById(R.id.etSexoNuevo);
        etEstadoNuevo = view.findViewById(R.id.etEstadoNuevo);
        etCiudadNuevo = view.findViewById(R.id.etCiudadNuevo);
        etPasswordNuevo = view.findViewById(R.id.etPasswordNuevo);

        // Botones
        btnBack = view.findViewById(R.id.btnBack);
        btnActualizar = view.findViewById(R.id.btnActualizar);

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
    private void configurarBotones() {
        btnBack.setOnClickListener(v -> requireActivity().onBackPressed());
        btnActualizar.setOnClickListener(v -> actualizarDatos());
    }

    /**
     * Carga los datos actuales del usuario
     */
    private void cargarDatosActuales() {
        if (username == null) {
            Toast.makeText(requireContext(),
                    "Error: no se pudo obtener el usuario",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "Cargando datos actuales para: " + username);

        Call<UsuarioDetalleDTO> call = apiService.obtenerUsuario(username);

        call.enqueue(new Callback<UsuarioDetalleDTO>() {
            @Override
            public void onResponse(Call<UsuarioDetalleDTO> call,
                                   Response<UsuarioDetalleDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    usuarioActual = response.body();
                    Log.d(TAG, "✓ Datos cargados exitosamente");
                    mostrarDatosActuales(usuarioActual);
                } else {
                    Log.e(TAG, "❌ Error al cargar datos: " + response.code());
                    Toast.makeText(requireContext(),
                            "Error al cargar datos",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UsuarioDetalleDTO> call, Throwable t) {
                Log.e(TAG, "❌ Error de conexión: " + t.getMessage(), t);
                Toast.makeText(requireContext(),
                        "Error de conexión",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Muestra los datos actuales en la sección de referencia
     */
    private void mostrarDatosActuales(UsuarioDetalleDTO usuario) {
        Log.d(TAG, "===== MOSTRANDO DATOS ACTUALES =====");

        tvNombreActual.setText(usuario.getNombre() != null ? usuario.getNombre() : "-");
        tvApellidoActual.setText(usuario.getApellidos() != null ? usuario.getApellidos() : "-");
        tvUsernameActual.setText(usuario.getUsuario() != null ? "@" + usuario.getUsuario() : "-");
        tvSexoActual.setText(usuario.getSexo() != null ? usuario.getSexo() : "-");
        tvEstadoActual.setText(usuario.getEstado() != null ? usuario.getEstado() : "-");
        tvCiudadActual.setText(usuario.getCiudad() != null ? usuario.getCiudad() : "-");
        tvPasswordActual.setText("••••••••");

        Log.d(TAG, "===== FIN MOSTRAR DATOS =====");
    }

    /**
     * Actualiza los datos del usuario
     */
    private void actualizarDatos() {
        Log.d(TAG, "Iniciando actualización de datos...");

        // ✅ Obtener valores de los campos (TODOS EditTexts ahora)
        String nuevoNombre = etNombreNuevo.getText().toString().trim();
        String nuevoApellido = etApellidoNuevo.getText().toString().trim();
        String nuevoSexo = etSexoNuevo.getText().toString().trim();
        String nuevoEstado = etEstadoNuevo.getText().toString().trim();
        String nuevaCiudad = etCiudadNuevo.getText().toString().trim();
        String nuevaPassword = etPasswordNuevo.getText().toString().trim();

        // Validar que al menos un campo tenga datos
        if (TextUtils.isEmpty(nuevoNombre) &&
                TextUtils.isEmpty(nuevoApellido) &&
                TextUtils.isEmpty(nuevoSexo) &&
                TextUtils.isEmpty(nuevoEstado) &&
                TextUtils.isEmpty(nuevaCiudad) &&
                TextUtils.isEmpty(nuevaPassword)) {

            Toast.makeText(requireContext(),
                    "Debes llenar al menos un campo para actualizar",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear DTO con los datos a actualizar
        ActualizarUsuarioDTO dto = new ActualizarUsuarioDTO();

        if (!TextUtils.isEmpty(nuevoNombre)) {
            dto.setNombre(nuevoNombre);
        }
        if (!TextUtils.isEmpty(nuevoApellido)) {
            dto.setApellidos(nuevoApellido);
        }
        if (!TextUtils.isEmpty(nuevoSexo)) {
            dto.setSexo(nuevoSexo);
        }
        if (!TextUtils.isEmpty(nuevoEstado)) {
            dto.setEstado(nuevoEstado);
        }
        if (!TextUtils.isEmpty(nuevaCiudad)) {
            dto.setCiudad(nuevaCiudad);
        }
        if (!TextUtils.isEmpty(nuevaPassword)) {
            dto.setPassword(nuevaPassword);
        }

        // Enviar actualización
        enviarActualizacion(dto);
    }

    /**
     * Envía la actualización al backend
     */
    private void enviarActualizacion(ActualizarUsuarioDTO dto) {
        Log.d(TAG, "Enviando actualización para: " + username);

        // Mostrar indicador de carga
        btnActualizar.setEnabled(false);
        btnActualizar.setText("Actualizando...");

        Call<Void> call = apiService.actualizarDatosUsuario(username, dto);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                btnActualizar.setEnabled(true);
                btnActualizar.setText("Actualizar");

                if (response.isSuccessful()) {
                    Log.d(TAG, "✓ Datos actualizados exitosamente");
                    Toast.makeText(requireContext(),
                            "Datos actualizados correctamente",
                            Toast.LENGTH_SHORT).show();

                    // Limpiar campos
                    limpiarCampos();

                    // Recargar datos actuales
                    cargarDatosActuales();

                } else {
                    Log.e(TAG, "Error al actualizar: " + response.code());
                    Toast.makeText(requireContext(),
                            "Error al actualizar datos: " + response.code(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                btnActualizar.setEnabled(true);
                btnActualizar.setText("Actualizar");

                Log.e(TAG, "Error de conexión: " + t.getMessage(), t);
                Toast.makeText(requireContext(),
                        "Error de conexión: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Limpia todos los campos editables
     */
    private void limpiarCampos() {
        etNombreNuevo.setText("");
        etApellidoNuevo.setText("");
        etSexoNuevo.setText("");
        etEstadoNuevo.setText("");
        etCiudadNuevo.setText("");
        etPasswordNuevo.setText("");
    }
}