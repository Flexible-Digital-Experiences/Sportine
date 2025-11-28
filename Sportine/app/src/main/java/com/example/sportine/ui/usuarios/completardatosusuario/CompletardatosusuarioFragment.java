package com.example.sportine.ui.usuarios.completardatosusuario;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.sportine.R;
import com.example.sportine.data.ApiService;
import com.example.sportine.data.RetrofitClient;
import com.example.sportine.ui.usuarios.dto.PerfilAlumnoResponseDTO;
import com.example.sportine.ui.usuarios.dto.ActualizarDatosAlumnoDTO;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CompletardatosusuarioFragment extends Fragment {

    private static final String TAG = "CompletarDatosFragment";

    // ==========================================
    // COMPONENTES DE LA UI - DATOS ACTUALES
    // ==========================================
    private ImageView ivAvatarCompletar;
    private TextView tvLesionesActual;
    private TextView tvPadecimientosActual;
    private TextView tvGeneroActual;
    private TextView tvEstaturaActual;
    private TextView tvPesoActual;
    private TextView tvEdadActual;

    // ==========================================
    // COMPONENTES DE LA UI - CAMPOS EDITABLES
    // ==========================================
    private EditText etLesiones;
    private EditText etPadecimientos;
    private Spinner spinnerGenero;
    private EditText etEstatura;
    private EditText etPeso;
    private Spinner spinnerEdad;

    // Botones
    private MaterialCardView btnBack;
    private MaterialButton btnActualizar;

    // API Service
    private ApiService apiService;

    // Datos del usuario
    private String username;
    private PerfilAlumnoResponseDTO perfilActual;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_alumno_completar_datos, container, false);
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

        // 4. Configurar Spinners
        setupSpinners();

        // 5. Configurar botones
        configurarBotones();

        // 6. Cargar datos actuales del perfil
        cargarDatosActuales();
    }

    /**
     * Inicializa todas las vistas del layout
     */
    private void inicializarComponentes(View view) {
        // Foto de perfil
        ivAvatarCompletar = view.findViewById(R.id.iv_avatar_completar);

        // TextViews de datos actuales (los que NO se pueden editar)
        tvLesionesActual = obtenerTextViewEnCard(view, "Lesiones", false);
        tvPadecimientosActual = obtenerTextViewEnCard(view, "Padecimientos", false);
        tvGeneroActual = obtenerTextViewEnCard(view, "Género", false);
        tvEstaturaActual = obtenerTextViewEnCard(view, "Estatura (m)", false);
        tvPesoActual = obtenerTextViewEnCard(view, "Peso (Kg)", false);
        tvEdadActual = obtenerTextViewEnCard(view, "Edad", false);

        // EditTexts y Spinners (campos editables)
        etLesiones = view.findViewById(R.id.etLesiones);
        etPadecimientos = view.findViewById(R.id.etPadecimientos);
        spinnerGenero = view.findViewById(R.id.spinnerGenero);
        etEstatura = view.findViewById(R.id.etEstatura);
        etPeso = view.findViewById(R.id.etPeso);
        spinnerEdad = view.findViewById(R.id.spinnerEdad);

        // Botones
        btnBack = view.findViewById(R.id.btnBack);
        btnActualizar = view.findViewById(R.id.btnActualizar);
    }

    /**
     * Método auxiliar para obtener TextViews dentro de las cards
     * (Busca el segundo TextView dentro de cada LinearLayout que contenga el label)
     */
    private TextView obtenerTextViewEnCard(View parentView, String labelText, boolean esEditable) {
        // Este método es un helper para encontrar los TextViews de "datos actuales"
        // En tu XML, cada campo tiene un TextView con el label y otro con el valor "-"

        // Para simplificar, vamos a usar los IDs directos si los defines en el XML
        // Si no, este método puede buscar por el texto del label

        // Por ahora, devolvemos null y los asignamos manualmente
        return null;
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
     * Configura los Spinners de Edad y Género
     */
    private void setupSpinners() {
        // Spinner de Edad
        String[] edades = new String[83];
        edades[0] = "Selecciona edad";
        for (int i = 1; i < 83; i++) {
            edades[i] = String.valueOf(i + 17); // De 18 a 100
        }
        ArrayAdapter<String> adapterEdad = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                edades
        );
        adapterEdad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEdad.setAdapter(adapterEdad);

        // Spinner de Género
        String[] generos = {
                "Selecciona género",
                "Masculino",
                "Femenino",
                "No Binario",
                "Género fluido",
                "Agénero",
                "Bigénero",
                "Demigénero",
                "Transgenero",
                "Cisgenero",
                "Prefiero no decir"
        };
        ArrayAdapter<String> adapterGenero = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                generos
        );
        adapterGenero.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGenero.setAdapter(adapterGenero);
    }

    /**
     * Configura los listeners de los botones
     */
    private void configurarBotones() {
        btnBack.setOnClickListener(v -> requireActivity().onBackPressed());

        btnActualizar.setOnClickListener(v -> actualizarDatos());
    }

    /**
     * Carga los datos actuales del perfil del alumno
     */
    private void cargarDatosActuales() {
        if (username == null) {
            Toast.makeText(requireContext(),
                    "Error: no se pudo obtener el usuario",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "Cargando datos actuales para: " + username);

        Call<PerfilAlumnoResponseDTO> call = apiService.obtenerPerfilAlumno(username);

        call.enqueue(new Callback<PerfilAlumnoResponseDTO>() {
            @Override
            public void onResponse(Call<PerfilAlumnoResponseDTO> call,
                                   Response<PerfilAlumnoResponseDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    perfilActual = response.body();
                    mostrarDatosActuales(perfilActual);
                } else if (response.code() == 404) {
                    Log.w(TAG, "Perfil no completado aún");
                    // Dejar los campos vacíos para que el usuario los llene
                } else {
                    Log.e(TAG, "Error al cargar datos: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<PerfilAlumnoResponseDTO> call, Throwable t) {
                Log.e(TAG, "Error de conexión: " + t.getMessage(), t);
                Toast.makeText(requireContext(),
                        "Error de conexión",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Muestra los datos actuales en la sección "Datos Actuales"
     */
    private void mostrarDatosActuales(PerfilAlumnoResponseDTO perfil) {
        Log.d(TAG, "Mostrando datos actuales del perfil");

        // Cargar foto de perfil
        if (perfil.getFotoPerfil() != null && !perfil.getFotoPerfil().isEmpty()) {
            Glide.with(this)
                    .load(perfil.getFotoPerfil())
                    .placeholder(R.drawable.ic_avatar_default)
                    .error(R.drawable.ic_avatar_default)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .circleCrop()
                    .into(ivAvatarCompletar);
        }

        // Mostrar datos en la sección "Datos Actuales"
        // NOTA: Necesitas agregar IDs específicos a estos TextViews en tu XML
        // Por ahora, los dejamos comentados hasta que tengas los IDs

        /*
        tvLesionesActual.setText(
                perfil.getLesiones() != null ? perfil.getLesiones() : "-"
        );
        tvPadecimientosActual.setText(
                perfil.getPadecimientos() != null ? perfil.getPadecimientos() : "-"
        );
        tvGeneroActual.setText(
                perfil.getSexo() != null ? perfil.getSexo() : "-"
        );
        tvEstaturaActual.setText(
                perfil.getEstatura() != null ?
                String.format(Locale.getDefault(), "%.2f", perfil.getEstatura()) : "-"
        );
        tvPesoActual.setText(
                perfil.getPeso() != null ?
                String.format(Locale.getDefault(), "%.2f", perfil.getPeso()) : "-"
        );
        tvEdadActual.setText(
                perfil.getEdad() != null ? String.valueOf(perfil.getEdad()) : "-"
        );
        */
    }

    /**
     * Actualiza los datos del alumno en el backend
     */
    private void actualizarDatos() {
        Log.d(TAG, "Iniciando actualización de datos");

        // Obtener valores de los campos
        String lesiones = etLesiones.getText().toString().trim();
        String padecimientos = etPadecimientos.getText().toString().trim();
        String generoSeleccionado = spinnerGenero.getSelectedItem().toString();
        String estaturaStr = etEstatura.getText().toString().trim();
        String pesoStr = etPeso.getText().toString().trim();
        String edadSeleccionada = spinnerEdad.getSelectedItem().toString();

        // Validar que al menos un campo tenga datos
        if (TextUtils.isEmpty(lesiones) &&
                TextUtils.isEmpty(padecimientos) &&
                generoSeleccionado.equals("Selecciona género") &&
                TextUtils.isEmpty(estaturaStr) &&
                TextUtils.isEmpty(pesoStr) &&
                edadSeleccionada.equals("Selecciona edad")) {

            Toast.makeText(requireContext(),
                    "Debes llenar al menos un campo para actualizar",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear DTO con los datos a actualizar
        ActualizarDatosAlumnoDTO dto = new ActualizarDatosAlumnoDTO();

        // Solo agregar campos que tengan valores
        if (!TextUtils.isEmpty(lesiones)) {
            dto.setLesiones(lesiones);
        }

        if (!TextUtils.isEmpty(padecimientos)) {
            dto.setPadecimientos(padecimientos);
        }

        if (!generoSeleccionado.equals("Selecciona género")) {
            dto.setSexo(generoSeleccionado);
        }

        if (!TextUtils.isEmpty(estaturaStr)) {
            try {
                float estatura = Float.parseFloat(estaturaStr);
                if (estatura > 0 && estatura < 3) { // Validación básica
                    dto.setEstatura(estatura);
                } else {
                    Toast.makeText(requireContext(),
                            "Estatura inválida (debe estar entre 0 y 3 metros)",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (NumberFormatException e) {
                Toast.makeText(requireContext(),
                        "Formato de estatura inválido",
                        Toast.LENGTH_SHORT).show();
                return;
            }
        }

        if (!TextUtils.isEmpty(pesoStr)) {
            try {
                float peso = Float.parseFloat(pesoStr);
                if (peso > 0 && peso < 500) { // Validación básica
                    dto.setPeso(peso);
                } else {
                    Toast.makeText(requireContext(),
                            "Peso inválido (debe estar entre 0 y 500 kg)",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (NumberFormatException e) {
                Toast.makeText(requireContext(),
                        "Formato de peso inválido",
                        Toast.LENGTH_SHORT).show();
                return;
            }
        }

        if (!edadSeleccionada.equals("Selecciona edad")) {
            try {
                int edad = Integer.parseInt(edadSeleccionada);
                // Convertir edad a fecha de nacimiento aproximada
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.YEAR, -edad);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                dto.setFechaNacimiento(sdf.format(cal.getTime()));
            } catch (NumberFormatException e) {
                Toast.makeText(requireContext(),
                        "Edad inválida",
                        Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // Enviar actualización al backend
        enviarActualizacion(dto);
    }

    /**
     * Envía la actualización al backend
     */
    private void enviarActualizacion(ActualizarDatosAlumnoDTO dto) {
        Log.d(TAG, "Enviando actualización para: " + username);

        // Mostrar indicador de carga
        btnActualizar.setEnabled(false);
        btnActualizar.setText("Actualizando...");

        Call<Void> call = apiService.actualizarDatosAlumno(username, dto);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                btnActualizar.setEnabled(true);
                btnActualizar.setText("Actualizar datos");

                if (response.isSuccessful()) {
                    Log.d(TAG, "✓ Datos actualizados exitosamente");
                    Toast.makeText(requireContext(),
                            "Datos actualizados correctamente",
                            Toast.LENGTH_SHORT).show();

                    // Limpiar campos
                    limpiarCampos();

                    // Recargar datos actuales
                    cargarDatosActuales();

                    // Opcional: volver atrás automáticamente
                    // requireActivity().onBackPressed();

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
                btnActualizar.setText("Actualizar datos");

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
        etLesiones.setText("");
        etPadecimientos.setText("");
        etEstatura.setText("");
        etPeso.setText("");
        spinnerGenero.setSelection(0);
        spinnerEdad.setSelection(0);
    }
}