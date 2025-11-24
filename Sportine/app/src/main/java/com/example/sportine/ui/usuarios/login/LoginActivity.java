package com.example.sportine.ui.usuarios.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sportine.R;
import com.example.sportine.MainActivity;
import com.example.sportine.data.ApiService;
import com.example.sportine.data.RetrofitClient; // <-- ¡Importante!
import com.example.sportine.ui.usuarios.dto.LoginRequest;
import com.example.sportine.ui.usuarios.dto.LoginResponse;
import com.example.sportine.ui.usuarios.registro.RegistroActivity;
import com.google.android.material.textfield.TextInputLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
// Quitamos 'retrofit2.Retrofit' y 'converter.gson.GsonConverterFactory'
// porque ya no los creamos aquí.

public class LoginActivity extends AppCompatActivity {

    private final boolean MODO_PRUEBA = false; // ¡Modo real activado!
    private ApiService apiService;
    private TextInputLayout tilUsuario;
    private TextInputLayout tilPassword;

    // La BASE_URL ahora se define solo en RetrofitClient
    // public static final String BASE_URL = "..."; // <-- Ya no es necesaria aquí

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button loginBoton = findViewById(R.id.loginBoton);
        Button registroBoton = findViewById(R.id.registroBoton);

        tilUsuario = findViewById(R.id.usernameInputLayout);
        tilPassword = findViewById(R.id.passwordInputLayout);

        // --- ¡CAMBIO CLAVE! ---
        // Obtenemos la instancia de Retrofit desde nuestro cliente
        // Le pasamos 'this' (el Context) para que pueda crear el Interceptor
        apiService = RetrofitClient.getClient(this).create(ApiService.class);

        // Botón de Registro (sin cambios)
        registroBoton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegistroActivity.class);
            startActivity(intent);
        });

        // Lógica del Botón de Login (Modo Real)
        loginBoton.setOnClickListener(v -> {

            if (MODO_PRUEBA) {
                // ... (modo prueba) ...
            } else {
                // --- MODO REAL (CON RETROFIT) ---
                String usuario = tilUsuario.getEditText().getText().toString().trim();
                String contrasena = tilPassword.getEditText().getText().toString().trim();

                if (usuario.isEmpty() || contrasena.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Por favor, llena todos los campos", Toast.LENGTH_SHORT).show();
                    return;
                }

                LoginRequest loginRequest = new LoginRequest(usuario, contrasena);
                hacerLogin(loginRequest);
            }
        });
    }


    private void hacerLogin(LoginRequest loginRequest) {

        Call<LoginResponse> call = apiService.login(loginRequest);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {

                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();

                    if (loginResponse.isSuccess()) {

                        // --- ¡CAMBIO CLAVE! RECIBIMOS EL TOKEN ---
                        String rol = loginResponse.getRol();
                        String nombreUsuario = loginResponse.getNombre();
                        String username = loginResponse.getUsuario();
                        String token = loginResponse.getToken(); // <-- ¡Lo leemos!

                        // Validamos que el token no sea nulo
                        if (token == null || token.isEmpty()) {
                            Toast.makeText(LoginActivity.this, "Error: El servidor no devolvió un token.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // --- GUARDADO EN PREFERENCIAS ---
                        SharedPreferences prefs = getSharedPreferences("SportinePrefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();

                        editor.putString("USER_ROL", response.body().getRol());
                        editor.putString("USER_NOMBRE", nombreUsuario);
                        editor.putString("USER_USERNAME", username);
                        editor.putString("USER_TOKEN", token); // <-- ¡Lo guardamos!

                        editor.apply();

                        Toast.makeText(LoginActivity.this, "¡Bienvenido " + nombreUsuario + "!", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();

                    } else {
                        // Si 'success' es false (ej. contraseña mal)
                        Toast.makeText(LoginActivity.this, loginResponse.getMensaje(), Toast.LENGTH_SHORT).show();
                    }

                } else {
                    // Si el servidor regresa 401, 404, 500, etc.
                    Toast.makeText(LoginActivity.this, "Usuario o contraseña incorrectos (Error " + response.code() + ")", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                // Si no hay internet o el servidor está caído
                Toast.makeText(LoginActivity.this, "Fallo de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}