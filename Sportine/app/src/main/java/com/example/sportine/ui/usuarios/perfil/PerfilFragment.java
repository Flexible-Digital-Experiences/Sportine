package com.example.sportine.ui.usuarios.perfil;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.sportine.R;
import com.example.sportine.data.ApiService;
import com.example.sportine.data.RetrofitClient;
import com.example.sportine.ui.usuarios.dto.UsuarioDetalleDTO;
import com.example.sportine.ui.usuarios.dto.PerfilAlumnoResponseDTO;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PerfilFragment extends Fragment {

    // ==========================================
    // COMPONENTES DE LA UI (IDs corregidos)
    // ==========================================
    private TextView txtSaludo;           // ID: txtSaludo
    private TextView tvNombre;            // ID: tvNombre
    private TextView tvApellido;          // ID: tvApellido
    private TextView tvUsername;          // ID: tvUsername
    private TextView tvSexo;              // ID: tvSexo
    private TextView tvEstado;            // ID: tvEstado
    private TextView tvCiudad;            // ID: tvCiudad

    // Botones
    private MaterialCardView btnSettings; // ID: btnSettings
    private MaterialButton btnCompletar;  // ID: btnCompletar

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
     * Inicializa todas las vistas del layout con los IDs CORRECTOS
     */
    private void inicializarComponentes(View view) {
        // TextViews con los IDs correctos de tu XML
        txtSaludo = view.findViewById(R.id.txtSaludo);      // ← Correcto
        tvNombre = view.findViewById(R.id.tvNombre);        // ← Correcto
        tvApellido = view.findViewById(R.id.tvApellido);    // ← Correcto
        tvUsername = view.findViewById(R.id.tvUsername);    // ← Correcto
        tvSexo = view.findViewById(R.id.tvSexo);            // ← Correcto
        tvEstado = view.findViewById(R.id.tvEstado);        // ← Correcto
        tvCiudad = view.findViewById(R.id.tvCiudad);        // ← Correcto

        // Botones
        btnSettings = view.findViewById(R.id.btnSettings);  // ← Correcto
        btnCompletar = view.findViewById(R.id.btnCompletar);// ← Correcto
    }

    /**
     * Obtiene el username y rol del usuario desde SharedPreferences
     */
    private void obtenerDatosUsuarioLogueado() {
        SharedPreferences prefs = requireContext()
                .getSharedPreferences("SportinePrefs", Context.MODE_PRIVATE);

        username = prefs.getString("USER_USERNAME", null);
        rol = prefs.getString("USER_ROL", null);

        // Validar que se haya obtenido el username
        if (username == null || username.isEmpty()) {
            Toast.makeText(requireContext(),
                    "Error: No se encontró el usuario logueado",
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Configura los listeners de los botones
     */
    private void configurarBotones(View view) {
        // Botón de configuración (el ícono de engrane)
        btnSettings.setOnClickListener(v ->
                Navigation.findNavController(view)
                        .navigate(R.id.action_perfil_to_configuracion)
        );

        // Botón "Completar mis datos"
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
            return; // No hacer nada si no hay username
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
                    if ("Alumno".equals(rol)) {
                        cargarPerfilCompleto();
                    }
                } else {
                    Toast.makeText(requireContext(),
                            "Error al cargar datos: " + response.code(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UsuarioDetalleDTO> call, Throwable t) {
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
        // Mensaje de saludo
        txtSaludo.setText("Hola " + usuario.getNombre());

        // Datos básicos
        tvNombre.setText(usuario.getNombre());
        tvApellido.setText(usuario.getApellidos());
        tvUsername.setText("@" + usuario.getUsuario());
        tvSexo.setText(usuario.getSexo());
        tvEstado.setText(usuario.getEstado());
        tvCiudad.setText(usuario.getCiudad());
    }

    /**
     * Carga el perfil completo del alumno (con estatura, peso, deportes, etc.)
     */
    private void cargarPerfilCompleto() {
        Call<PerfilAlumnoResponseDTO> call = apiService.obtenerPerfilAlumno(username);

        call.enqueue(new Callback<PerfilAlumnoResponseDTO>() {
            @Override
            public void onResponse(Call<PerfilAlumnoResponseDTO> call,
                                   Response<PerfilAlumnoResponseDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    PerfilAlumnoResponseDTO perfil = response.body();
                    mostrarDatosCompletosPerfil(perfil);
                } else if (response.code() == 404) {
                    // El alumno aún no ha completado su perfil
                    Toast.makeText(requireContext(),
                            "Completa tu perfil para ver más información",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PerfilAlumnoResponseDTO> call, Throwable t) {
                // No mostramos error aquí porque es opcional
            }
        });
    }

    /**
     * Muestra los datos completos del perfil del alumno
     * Aquí puedes agregar más TextViews en tu layout para mostrar:
     * - Estatura, peso, edad
     * - Deportes que practica
     * - Nivel, lesiones, padecimientos
     */
    private void mostrarDatosCompletosPerfil(PerfilAlumnoResponseDTO perfil) {
        // Por ahora solo mostramos un mensaje
        // Puedes agregar más TextViews en tu XML para mostrar estos datos:

        // Ejemplo (si agregas más TextViews):
        // tvEstatura.setText(String.format("%.2f m", perfil.getEstatura()));
        // tvPeso.setText(String.format("%.1f kg", perfil.getPeso()));
        // tvEdad.setText(perfil.getEdad() + " años");

        Toast.makeText(requireContext(),
                "Perfil completo cargado exitosamente",
                Toast.LENGTH_SHORT).show();
    }
}