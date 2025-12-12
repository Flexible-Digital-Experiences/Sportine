package com.example.sportine.ui.usuarios.registro;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.sportine.R;
import com.example.sportine.data.ApiService;
import com.example.sportine.data.RetrofitClient;
import com.example.sportine.models.Usuario;
import com.example.sportine.models.RespuestaRegistro;
import com.example.sportine.ui.usuarios.login.LoginActivity;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistroFragment extends Fragment {

    private TextInputEditText nombreInput, apellidoInput, usernameInput, passwordInput,
            ciudadInput;
    private AutoCompleteTextView sexoSpinner, rolSpinner, estadoSpinner;
    private Button registroBoton;
    private ApiService apiService;
    private ImageView btnBack;

    // Array de estados como variable de clase
    private String[] estados;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_registro, container, false);

        // Inicializar Retrofit
        apiService = RetrofitClient.getClient(requireContext()).create(ApiService.class);

        // Referencias a vistas (USAR view.findViewById EN FRAGMENT)
        nombreInput = view.findViewById(R.id.nombreInput);
        apellidoInput = view.findViewById(R.id.apellidoInput);
        sexoSpinner = view.findViewById(R.id.sexoSpinner);
        usernameInput = view.findViewById(R.id.usernameInput);
        passwordInput = view.findViewById(R.id.passwordInput);
        rolSpinner = view.findViewById(R.id.rolSpinner);
        registroBoton = view.findViewById(R.id.registroBoton);
        estadoSpinner = view.findViewById(R.id.estadoSpinner);
        ciudadInput = view.findViewById(R.id.ciudadInput);
        btnBack = view.findViewById(R.id.btnBack);

        // Configurar botón de regresar
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            requireActivity().finish();
        });

        // Llenar spinner Sexo
        String[] sexos = {"Masculino", "Femenino"};
        sexoSpinner.setAdapter(new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, sexos));

        // Llenar spinner Rol
        String[] roles = {"Alumno", "Entrenador"};
        rolSpinner.setAdapter(new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, roles));

        // Inicializar array de estados (32 estados de México en orden)
        estados = new String[]{
                "Ciudad de México",      // ID 1
                "Aguascalientes",        // ID 2
                "Baja California",       // ID 3
                "Baja California Sur",   // ID 4
                "Campeche",              // ID 5
                "Chiapas",               // ID 6
                "Chihuahua",             // ID 7
                "Coahuila",              // ID 8
                "Colima",                // ID 9
                "Durango",               // ID 10
                "Guanajuato",            // ID 11
                "Guerrero",              // ID 12
                "Hidalgo",               // ID 13
                "Jalisco",               // ID 14
                "México",                // ID 15
                "Michoacán",             // ID 16
                "Morelos",               // ID 17
                "Nayarit",               // ID 18
                "Nuevo León",            // ID 19
                "Oaxaca",                // ID 20
                "Puebla",                // ID 21
                "Querétaro",             // ID 22
                "Quintana Roo",          // ID 23
                "San Luis Potosí",       // ID 24
                "Sinaloa",               // ID 25
                "Sonora",                // ID 26
                "Tabasco",               // ID 27
                "Tamaulipas",            // ID 28
                "Tlaxcala",              // ID 29
                "Veracruz",              // ID 30
                "Yucatán",               // ID 31
                "Zacatecas"              // ID 32
        };

        // Llenar spinner Estados
        estadoSpinner.setAdapter(new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, estados));

        // Click del botón
        registroBoton.setOnClickListener(v -> registrarUsuario());

        return view;
    }


    private void registrarUsuario() {

        // Capturar datos
        String nombre = nombreInput.getText().toString().trim();
        String apellidos = apellidoInput.getText().toString().trim();
        String sexo = sexoSpinner.getText().toString().trim();
        String usuario = usernameInput.getText().toString().trim();
        String contrasena = passwordInput.getText().toString().trim();
        String rol = rolSpinner.getText().toString().toLowerCase().trim();
        String estadoSeleccionado = estadoSpinner.getText().toString();
        Integer idEstado = null;
        for (int i = 0; i < estados.length; i++) {
            if (estadoSeleccionado.equals(estados[i])) {
                idEstado = i+1;
                break;
            }
        }
        String ciudad = ciudadInput.getText().toString().trim();

        // Validaciones
        if (nombre.isEmpty() || apellidos.isEmpty() || sexo.isEmpty() ||
                usuario.isEmpty() || contrasena.isEmpty() || rol.isEmpty() ||
                ciudad.isEmpty() || estadoSeleccionado.isEmpty()) {
            Toast.makeText(requireContext(), "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validar que el estado sea válido
        if (idEstado == null) {
            Toast.makeText(requireContext(), "Selecciona un estado válido", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validar longitud de contraseña
        if (contrasena.length() < 6) {
            Toast.makeText(requireContext(), "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear objeto Usuario EXACTO al JSON
        Usuario u = new Usuario(
                usuario,
                nombre,
                apellidos,
                sexo,
                idEstado,
                ciudad,
                rol,
                contrasena
        );

        // Llamada Retrofit
        Call<RespuestaRegistro> call = apiService.registrarUsuario(u);

        registroBoton.setEnabled(false);

        call.enqueue(new Callback<RespuestaRegistro>() {
            @Override
            public void onResponse(Call<RespuestaRegistro> call, Response<RespuestaRegistro> response) {
                registroBoton.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(requireContext(),
                            "Registro exitoso: " + response.body().getMensaje(),
                            Toast.LENGTH_LONG).show();

                    // Limpiar campos
                    nombreInput.setText("");
                    apellidoInput.setText("");
                    sexoSpinner.setText("", false);
                    usernameInput.setText("");
                    passwordInput.setText("");
                    rolSpinner.setText("", false);
                    estadoSpinner.setText("", false);
                    ciudadInput.setText("");

                    // Volver al LoginActivity
                    Intent intent = new Intent(requireActivity(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    requireActivity().finish();

                } else {
                    // ✅ CAMBIO ÚNICO: Leer los errores del backend
                    mostrarErroresValidacion(response);
                }
            }

            @Override
            public void onFailure(Call<RespuestaRegistro> call, Throwable t) {
                registroBoton.setEnabled(true);
                Toast.makeText(requireContext(),
                        "Error de conexión: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * ✅ MÉTODO NUEVO: Muestra los errores de validación del backend
     * Agrega este método DESPUÉS de registrarUsuario()
     */
    private void mostrarErroresValidacion(Response<RespuestaRegistro> response) {
        try {
            if (response.errorBody() != null) {
                String errorBody = response.errorBody().string();

                // Parsear el JSON de errores
                org.json.JSONObject jsonError = new org.json.JSONObject(errorBody);

                // Construir mensaje con todos los errores
                StringBuilder mensajeError = new StringBuilder();

                // Iterar sobre todos los campos con error
                org.json.JSONArray nombres = jsonError.names();
                if (nombres != null) {
                    for (int i = 0; i < nombres.length(); i++) {
                        String campo = nombres.getString(i);
                        String mensaje = jsonError.getString(campo);
                        mensajeError.append("• ").append(mensaje).append("\n");
                    }
                }

                // Mostrar los errores en un Toast largo O en AlertDialog
                if (mensajeError.length() > 0) {
                    // OPCIÓN 1: Toast largo (más simple)
                    Toast.makeText(requireContext(),
                            mensajeError.toString().trim(),
                            Toast.LENGTH_LONG).show();

                    // OPCIÓN 2: AlertDialog (más bonito) - Descomenta si prefieres esta
                    /*
                    new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                            .setTitle("Errores de validación")
                            .setMessage(mensajeError.toString().trim())
                            .setPositiveButton("Entendido", null)
                            .show();
                    */
                } else {
                    Toast.makeText(requireContext(),
                            "Error en el registro: " + response.code(),
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(requireContext(),
                        "Error en el registro: " + response.code(),
                        Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(requireContext(),
                    "Error en el registro: " + response.code(),
                    Toast.LENGTH_SHORT).show();
        }
    }
}