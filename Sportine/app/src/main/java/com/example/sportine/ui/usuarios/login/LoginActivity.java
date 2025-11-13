package com.example.sportine.ui.usuarios.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
// import android.widget.EditText; // <-- Ya no usamos esta importación
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sportine.R;
import com.example.sportine.MainActivity;
import com.example.sportine.data.ApiService;
import com.example.sportine.ui.usuarios.dto.LoginRequest;
import com.example.sportine.ui.usuarios.dto.LoginResponse;
import com.example.sportine.ui.usuarios.registro.RegistroActivity;

// --- ¡IMPORTACIÓN NUEVA Y CLAVE! ---
import com.google.android.material.textfield.TextInputLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {

    // true = Usa login falso (para probar la UI)
    // false = Usa login real (para conectar a Spring Boot)
    private final boolean MODO_PRUEBA = true;

    private ApiService apiService;

    private TextInputLayout tilEmail;
    private TextInputLayout tilPassword;

    // IP especial para conectar el Emulador al "localhost" de tu PC
    public static final String BASE_URL = "http://10.0.2.2:8080/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        Button loginBoton = findViewById(R.id.loginBoton);
        Button registroBoton = findViewById(R.id.registroBoton);


        tilEmail = findViewById(R.id.usernameInputLayout);
        tilPassword = findViewById(R.id.passwordInputLayout);


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);


        registroBoton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegistroActivity.class);
            startActivity(intent);
        });

        // --- 4. Lógica del Botón de Login (CON MODO DE PRUEBA) ---
        loginBoton.setOnClickListener(v -> {

            if (MODO_PRUEBA) {
                // --- MODO PRUEBA (LOGIN FALSO) ---
                Toast.makeText(this, "MODO PRUEBA ACTIVADO", Toast.LENGTH_SHORT).show();

                // CAMBIA ESTO PARA PROBAR "ALUMNO" O "ENTRENADOR"
                String rolFalso = "ALUMNO";

                SharedPreferences prefs = getSharedPreferences("SportinePrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("USER_ROL", rolFalso);
                editor.putString("USER_TOKEN", "token_de_prueba_123");
                editor.apply();

                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();

            } else {
                // --- MODO REAL (CON RETROFIT) ---

                String email = tilEmail.getEditText().getText().toString().trim();
                String password = tilPassword.getEditText().getText().toString().trim();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Por favor, llena todos los campos", Toast.LENGTH_SHORT).show();
                    return;
                }

                LoginRequest loginRequest = new LoginRequest(email, password);
                hacerLogin(loginRequest);
            }
        });
    }

    // --- 5. Método que hace la llamada a la API (Modo Real) ---
    private void hacerLogin(LoginRequest loginRequest) {
        // (Este método se queda igual)
        Call<LoginResponse> call = apiService.login(loginRequest);

        call.enqueue(new Callback<LoginResponse>() {

            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();
                    String rol = loginResponse.getRol();
                    String token = loginResponse.getToken();

                    SharedPreferences prefs = getSharedPreferences("SportinePrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("USER_ROL", rol);
                    editor.putString("USER_TOKEN", token);
                    editor.apply();

                    Toast.makeText(LoginActivity.this, "¡Bienvenido " + loginResponse.getNombreUsuario() + "!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Email o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Fallo de conexión. Revisa el servidor o tu internet.", Toast.LENGTH_LONG).show();
            }
        });
    }
}