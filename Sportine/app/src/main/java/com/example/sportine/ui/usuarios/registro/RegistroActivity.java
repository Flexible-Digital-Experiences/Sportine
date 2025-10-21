package com.example.sportine.ui.usuarios.registro;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.sportine.R;

public class RegistroActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        // Cargar el RegistroFragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new RegistroFragment())
                    .commit();
        }
    }
}