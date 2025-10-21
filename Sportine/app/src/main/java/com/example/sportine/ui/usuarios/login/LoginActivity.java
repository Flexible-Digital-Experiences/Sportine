package com.example.sportine.ui.usuarios.login;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sportine.R;
import com.example.sportine.MainActivity;
import com.example.sportine.ui.usuarios.registro.RegistroActivity;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Obtenemos los botones directamente del layout
        Button loginBoton = findViewById(R.id.loginBoton);
        Button registroBoton = findViewById(R.id.registroBoton);

        // Login Button - ir a MainActivity
        loginBoton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        // Registro Button - ir a RegistroActivity
        registroBoton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegistroActivity.class);
            startActivity(intent);
        });
    }
}