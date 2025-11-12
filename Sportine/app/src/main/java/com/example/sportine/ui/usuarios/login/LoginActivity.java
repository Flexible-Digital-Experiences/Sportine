package com.example.sportine.ui.usuarios.login;

import android.content.Intent;
import android.content.SharedPreferences; // Import para guardar el rol
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText; // Import para los campos de texto
import android.widget.Toast; // Import para mostrar mensajes

import androidx.appcompat.app.AppCompatActivity;

import com.example.sportine.R;
import com.example.sportine.MainActivity;
import com.example.sportine.data.ApiService; // Import de tu "menú" de API
import com.example.sportine.ui.usuarios.dto.LoginRequest; // Import del DTO de envío
import com.example.sportine.ui.usuarios.dto.LoginResponse; // Import del DTO de respuesta
import com.example.sportine.ui.usuarios.registro.RegistroActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {

    private ApiService apiService;
    private EditText etEmail;
    private EditText etPassword;

    // Esta es la IP especial que usa el emulador de Android para
    // conectarse al "localhost" de tu computadora (donde corre Spring Boot)
    public static final String BASE_URL = "http://10.0.2.2:8080/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // --- 1. Encontramos TODAS las vistas ---
        Button loginBoton = findViewById(R.id.loginBoton);
        Button registroBoton = findViewById(R.id.registroBoton);

        // !!! OJO AQUÍ: Asegúrate de que los IDs de tu XML coincidan !!!
        etEmail = findViewById(R.id.usernameInput); // Reemplaza si tu ID es diferente
        etPassword = findViewById(R.id.passwordInput); // Reemplaza si tu ID es diferente

        // --- 2. Construimos el motor de Retrofit ---
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL) // Le dice a dónde apuntar
                .addConverterFactory(GsonConverterFactory.create()) // Le dice cómo "traducir" JSON
                .build();

        apiService = retrofit.create(ApiService.class); // Crea el "mesero"

        // --- 3. Lógica del Botón de Registro (Esta se queda igual) ---
        registroBoton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegistroActivity.class);
            startActivity(intent);
        });

        // --- 4. Lógica del Botón de Login (¡AHORA ES REAL!) ---
        loginBoton.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            // Validación simple
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Por favor, llena todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            // Crea el "paquete" (DTO) para enviar al backend
            LoginRequest loginRequest = new LoginRequest(email, password);

            // Llama al método que hace la magia
            hacerLogin(loginRequest);
        });
    }

    // --- 5. Método que hace la llamada a la API ---
    private void hacerLogin(LoginRequest loginRequest) {
        // Muestra un indicador de carga (¡buena práctica!)
        // (Aquí podrías poner un ProgressBar visible)

        Call<LoginResponse> call = apiService.login(loginRequest);

        // enqueue() hace la llamada en un hilo separado (para no trabar la app)
        call.enqueue(new Callback<LoginResponse>() {

            // --- ¡ÉXITO! El servidor respondió ---
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                // (Aquí quitas el ProgressBar)

                if (response.isSuccessful() && response.body() != null) {

                    LoginResponse loginResponse = response.body();
                    String rol = loginResponse.getRol(); // "ALUMNO" o "ENTRENADOR"
                    String token = loginResponse.getToken();

                    // --- ¡LA CLAVE! Guardamos el ROL y el TOKEN ---
                    SharedPreferences prefs = getSharedPreferences("SportinePrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("USER_ROL", rol);
                    editor.putString("USER_TOKEN", token);
                    editor.apply();

                    // Ahora sí, mandamos al usuario a la app principal
                    Toast.makeText(LoginActivity.this, "¡Bienvenido " + loginResponse.getNombreUsuario() + "!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish(); // Cierra la pantalla de Login

                } else {
                    // El servidor respondió un error (ej. 401 No Autorizado, 404 No Encontrado)
                    Toast.makeText(LoginActivity.this, "Email o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                }
            }

            // --- ¡FALLO! No se pudo conectar ---
            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                // (Aquí quitas el ProgressBar)
                // Esto pasa si el servidor está caído o no hay internet
                Toast.makeText(LoginActivity.this, "Fallo de conexión. Revisa el servidor o tu internet.", Toast.LENGTH_LONG).show();
            }
        });
    }
}