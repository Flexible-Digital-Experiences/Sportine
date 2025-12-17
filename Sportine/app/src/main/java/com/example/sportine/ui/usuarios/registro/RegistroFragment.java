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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistroFragment extends Fragment {

    private TextInputEditText nombreInput, correoInput, apellidoInput, usernameInput, passwordInput,
            ciudadInput;
    private AutoCompleteTextView sexoSpinner, rolSpinner, estadoSpinner;
    private Button registroBoton;
    private ApiService apiService;
    private ImageView btnBack;

    private String[] estados;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_registro, container, false);

        apiService = RetrofitClient.getClient(requireContext()).create(ApiService.class);

        nombreInput = view.findViewById(R.id.nombreInput);
        apellidoInput = view.findViewById(R.id.apellidoInput);
        correoInput = view.findViewById(R.id.mailInput);
        sexoSpinner = view.findViewById(R.id.sexoSpinner);
        usernameInput = view.findViewById(R.id.usernameInput);
        passwordInput = view.findViewById(R.id.passwordInput);
        rolSpinner = view.findViewById(R.id.rolSpinner);
        registroBoton = view.findViewById(R.id.registroBoton);
        estadoSpinner = view.findViewById(R.id.estadoSpinner);
        ciudadInput = view.findViewById(R.id.ciudadInput);
        btnBack = view.findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            requireActivity().finish();
        });

        String[] sexos = {"Masculino", "Femenino"};
        sexoSpinner.setAdapter(new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, sexos));

        String[] roles = {"Alumno", "Entrenador"};
        rolSpinner.setAdapter(new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, roles));

        estados = new String[]{
                "Ciudad de México", "Aguascalientes", "Baja California", "Baja California Sur",
                "Campeche", "Chiapas", "Chihuahua", "Coahuila", "Colima", "Durango",
                "Guanajuato", "Guerrero", "Hidalgo", "Jalisco", "México", "Michoacán",
                "Morelos", "Nayarit", "Nuevo León", "Oaxaca", "Puebla", "Querétaro",
                "Quintana Roo", "San Luis Potosí", "Sinaloa", "Sonora", "Tabasco",
                "Tamaulipas", "Tlaxcala", "Veracruz", "Yucatán", "Zacatecas"
        };

        estadoSpinner.setAdapter(new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, estados));

        registroBoton.setOnClickListener(v -> registrarUsuario());

        return view;
    }

    private void registrarUsuario() {
        String nombre = nombreInput.getText().toString().trim();
        String apellidos = apellidoInput.getText().toString().trim();
        String sexo = sexoSpinner.getText().toString().trim();
        String correo = correoInput.getText().toString().trim();
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

        if (nombre.isEmpty() || apellidos.isEmpty() || sexo.isEmpty() ||
                usuario.isEmpty() || contrasena.isEmpty() || rol.isEmpty() ||
                ciudad.isEmpty() || estadoSeleccionado.isEmpty() || correo.isEmpty()) {
            Toast.makeText(requireContext(), "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            Toast.makeText(requireContext(), "Ingresa un correo válido", Toast.LENGTH_SHORT).show();
            return;
        }

        if (idEstado == null) {
            Toast.makeText(requireContext(), "Selecciona un estado válido", Toast.LENGTH_SHORT).show();
            return;
        }

        if (contrasena.length() < 6) {
            Toast.makeText(requireContext(), "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
            return;
        }

        Usuario u = new Usuario(
                usuario, nombre, apellidos, sexo,
                idEstado, ciudad, rol, contrasena, correo
        );

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

                    nombreInput.setText("");
                    apellidoInput.setText("");
                    sexoSpinner.setText("", false);
                    correoInput.setText("");
                    usernameInput.setText("");
                    passwordInput.setText("");
                    rolSpinner.setText("", false);
                    estadoSpinner.setText("", false);
                    ciudadInput.setText("");

                    Intent intent = new Intent(requireActivity(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    requireActivity().finish();
                } else {
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

    private void mostrarErroresValidacion(Response<RespuestaRegistro> response) {
        try {
            if (response.errorBody() != null) {
                String errorBody = response.errorBody().string();
                org.json.JSONObject jsonError = new org.json.JSONObject(errorBody);
                StringBuilder mensajeError = new StringBuilder();

                org.json.JSONArray nombres = jsonError.names();
                if (nombres != null) {
                    for (int i = 0; i < nombres.length(); i++) {
                        String campo = nombres.getString(i);
                        String mensaje = jsonError.getString(campo);
                        mensajeError.append("• ").append(mensaje).append("\n");
                    }
                }

                if (mensajeError.length() > 0) {
                    Toast.makeText(requireContext(),
                            mensajeError.toString().trim(),
                            Toast.LENGTH_LONG).show();
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