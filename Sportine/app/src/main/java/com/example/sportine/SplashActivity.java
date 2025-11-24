package com.example.sportine;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
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

        logo.setScaleX(0f);
        logo.setScaleY(0f);
        logo.setAlpha(0f);

        logo.animate()
                .scaleX(1f)
                .scaleY(1f)
                .alpha(1f)
                .setDuration(800)
                .setInterpolator(new OvershootInterpolator(1.5f))
                .start();

        text.setTranslationY(100f);
        text.setAlpha(0f);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            text.animate()
                    .translationY(0f)
                    .alpha(1f)
                    .setDuration(800)
                    .setInterpolator(new DecelerateInterpolator(2f))
                    .start();
        }, 400);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            logo.animate()
                    .scaleX(0.9f)
                    .scaleY(0.9f)
                    .alpha(0f)
                    .setDuration(300)
                    .setInterpolator(new AccelerateDecelerateInterpolator())
                    .start();

            text.animate()
                    .translationY(-50f)
                    .alpha(0f)
                    .setDuration(300)
                    .setInterpolator(new AccelerateDecelerateInterpolator())
                    .withEndAction(() -> {
                        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                        startActivity(intent);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        finish();
                    })
                    .start();
        }, 2500);
    }
}