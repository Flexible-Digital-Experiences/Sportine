package com.example.sportine;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sportine.ui.usuarios.login.LoginActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ImageView logo = findViewById(R.id.iv_splash_logo);
        TextView text = findViewById(R.id.tv_splash_name);

        // --- ANIMACIÓN ---
        // 1. Estado inicial: El texto está ahí pero invisible (alpha 0)
        //    El logo está en su lugar.

        // Vamos a mover el logo un poco a la izquierda Y aparecer el texto
        // Pero primero, un pequeño delay para que se aprecie el logo solo
        new Handler(Looper.getMainLooper()).postDelayed(() -> {

            // Animación del Texto: Fade In (Aparecer)
            text.animate()
                    .alpha(1f) // Se vuelve visible
                    .setDuration(800) // Tarda 0.8 segundos
                    .setInterpolator(new DecelerateInterpolator())
                    .start();

            // (Opcional) Si quieres que el logo se mueva, podrías jugar con translationX
            // pero con el LinearLayout centrado, solo aparecer el texto
            // hace que se vea como que se expande. ¡Se ve elegante!

        }, 500); // Espera medio segundo antes de empezar

        // --- NAVEGACIÓN ---
        // Esperamos 3 segundos en total y nos vamos al Login
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(intent);
            finish(); // Matamos el Splash para que no puedan volver con "Atrás"
        }, 3000);
    }
}