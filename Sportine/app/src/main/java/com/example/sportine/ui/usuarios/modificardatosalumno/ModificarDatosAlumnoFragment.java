package com.example.sportine.ui.usuarios.modificardatosalumno;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.sportine.R;

public class ModificarDatosAlumnoFragment extends Fragment {

    private Spinner spinnerSexo, spinnerEstado, spinnerCiudad;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_alumno_modificar_datos, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Botón para volver atrás
        ImageView btnBack = view.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().onBackPressed();
            }
        });

        // Inicializar Spinners
        spinnerSexo = view.findViewById(R.id.spinnerSexoNuevo);
        spinnerEstado = view.findViewById(R.id.spinnerEstadoNuevo);
        spinnerCiudad = view.findViewById(R.id.spinnerCiudadNuevo);

        // Configurar Spinner de Sexo
        ArrayAdapter<CharSequence> adapterSexo = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.sexo_options,
                android.R.layout.simple_spinner_item
        );
        adapterSexo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSexo.setAdapter(adapterSexo);

        // Configurar Spinner de Estado
        ArrayAdapter<CharSequence> adapterEstado = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.estado_options,
                android.R.layout.simple_spinner_item
        );
        adapterEstado.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEstado.setAdapter(adapterEstado);

        // Configurar Spinner de Ciudad
        ArrayAdapter<CharSequence> adapterCiudad = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.ciudad_options,
                android.R.layout.simple_spinner_item
        );
        adapterCiudad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCiudad.setAdapter(adapterCiudad);
    }
}