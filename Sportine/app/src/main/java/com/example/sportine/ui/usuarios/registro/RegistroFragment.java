package com.example.sportine.ui.usuarios.registro;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.example.sportine.R;
import com.example.sportine.ui.usuarios.login.LoginActivity;
import com.google.android.material.textfield.TextInputEditText;

public class RegistroFragment extends Fragment {

    private TextInputEditText nombreInput;
    private TextInputEditText apellidoInput;
    private AutoCompleteTextView sexoSpinner;
    private TextInputEditText usernameInput;
    private TextInputEditText passwordInput;
    private AutoCompleteTextView rolSpinner;
    private Button registroBoton;

    public RegistroFragment() {
        // Constructor vacío requerido
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        // Inflamos el layout fragment_registro.xml
        View view = inflater.inflate(R.layout.fragment_registro, container, false);

        // Inicializar vistas
        nombreInput = view.findViewById(R.id.nombreInput);
        apellidoInput = view.findViewById(R.id.apellidoInput);
        sexoSpinner = view.findViewById(R.id.sexoSpinner);
        usernameInput = view.findViewById(R.id.usernameInput);
        passwordInput = view.findViewById(R.id.passwordInput);
        rolSpinner = view.findViewById(R.id.rolSpinner);
        registroBoton = view.findViewById(R.id.registroBoton);

        // Configurar spinner de sexo
        String[] sexos = {"Masculino", "Femenino"};
        ArrayAdapter<String> sexoAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                sexos
        );
        sexoSpinner.setAdapter(sexoAdapter);

        // Configurar spinner de rol
        String[] roles = {"Alumno", "Entrenador"};
        ArrayAdapter<String> rolAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                roles
        );
        rolSpinner.setAdapter(rolAdapter);

        // Configurar botón de registro
        registroBoton.setOnClickListener(v -> realizarRegistro());

        return view;
    }

    private void realizarRegistro() {
        // Obtener valores de los campos
        String nombre = nombreInput.getText().toString().trim();
        String apellido = apellidoInput.getText().toString().trim();
        String sexo = sexoSpinner.getText().toString().trim();
        String username = usernameInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String rol = rolSpinner.getText().toString().trim();

        // Validación básica
        if (nombre.isEmpty()) {
            nombreInput.setError("El nombre es requerido");
            nombreInput.requestFocus();
            return;
        }

        if (apellido.isEmpty()) {
            apellidoInput.setError("El apellido es requerido");
            apellidoInput.requestFocus();
            return;
        }

        if (sexo.isEmpty()) {
            Toast.makeText(requireContext(), "Por favor selecciona un sexo",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (username.isEmpty()) {
            usernameInput.setError("El nombre de usuario es requerido");
            usernameInput.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            passwordInput.setError("La contraseña es requerida");
            passwordInput.requestFocus();
            return;
        }

        if (password.length() < 6) {
            passwordInput.setError("La contraseña debe tener al menos 6 caracteres");
            passwordInput.requestFocus();
            return;
        }

        if (rol.isEmpty()) {
            Toast.makeText(requireContext(), "Por favor selecciona un rol",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // Aquí harías el registro en tu base de datos o API
        // Por ejemplo: registrarUsuario(nombre, apellido, sexo, username, password, rol);

        // Mostrar mensaje de éxito
        Toast.makeText(requireContext(), "Registro exitoso", Toast.LENGTH_SHORT).show();

        // Regresar al LoginActivity
        Intent intent = new Intent(requireActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        requireActivity().finish();
    }
}