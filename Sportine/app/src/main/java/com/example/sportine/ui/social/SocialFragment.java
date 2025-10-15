package com.example.sportine.ui.social;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.sportine.R; // Asegúrate de que importe tu archivo R

public class SocialFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Esta línea es la más importante: le dice a Java qué archivo XML dibujar en la pantalla.
        View view = inflater.inflate(R.layout.fragment_social, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // En el futuro, aquí es donde encontraremos el RecyclerView
        // y le pondremos los datos de las publicaciones.
        // Por ahora, lo dejamos así de simple.
    }
}
