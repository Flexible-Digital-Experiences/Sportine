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

    private TextInputEditText nombreInput, apellidoInput, usernameInput, passwordInput,
                                estadoInput, ciudadInput;
    private AutoCompleteTextView sexoSpinner, rolSpinner;
    private Button registroBoton;
    private ApiService apiService;
    private ImageView btnBack;


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
        estadoInput = view.findViewById(R.id.estadoInput);
        ciudadInput = view.findViewById(R.id.ciudadInput);
        btnBack = view.findViewById(R.id.btnBack);  // Agregar esta línea

        // Configurar botón de regresar - Agregar este bloque
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
        String estado = estadoInput.getText().toString().trim();
        String ciudad = ciudadInput.getText().toString().trim();


        // Validaciones
        if (nombre.isEmpty() || apellidos.isEmpty() || sexo.isEmpty() ||
                usuario.isEmpty() || contrasena.isEmpty() || rol.isEmpty()) {
            Toast.makeText(requireContext(), "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear objeto Usuario EXACTO al JSON
        Usuario u = new Usuario(
                usuario,
                nombre,
                apellidos,
                sexo,
                estado,
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
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(requireContext(),
                            "Registro exitoso: " + response.body().getMensaje(),
                            Toast.LENGTH_LONG).show();
                    nombreInput.setText("");
                    apellidoInput.setText("");
                    sexoSpinner.setText("");
                    usernameInput.setText("");
                    passwordInput.setText("");
                    rolSpinner.setText("");
                    estadoInput.setText("");
                    ciudadInput.setText("");

                    // Volver al LoginActivity
                    Intent intent = new Intent(requireActivity(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    requireActivity().finish();
                } else {
                    Toast.makeText(requireContext(),
                            "Error en el registro", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<RespuestaRegistro> call, Throwable t) {
                Toast.makeText(requireContext(),
                        "Error de conexión: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}
