package com.example.sportine.ui.Configuracion;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.sportine.R;
public class ConfiguracionFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflar el layout de configuración
        return inflater.inflate(R.layout.fragment_configuracion, container, false);
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

        Button btnModificar = view.findViewById(R.id.btnModificar);
        btnModificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view)
                        .navigate(R.id.action_configuracion_to_modificar);
            }
        });

    // Botón Agregar forma de pago
    Button btnAgregarPago = view.findViewById(R.id.btnAgregarPago);
        btnAgregarPago.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Navigation.findNavController(view)
                    .navigate(R.id.action_configuracion_to_agregar_pago);
        }
    });
        configurarBotonesEditar(view);
    }
        private void configurarBotonesEditar(View view) {


           Button btnEditarTarjeta1 = view.findViewById(R.id.Botonedita1);
           Button btnEditarTarjeta2 = view.findViewById(R.id.Botonedita2);
           Button btnEditarTarjeta3 = view.findViewById(R.id.Botonedita3);
           btnEditarTarjeta1.setOnClickListener(new View.OnClickListener() {
            @Override
              public void onClick(View v) {
                Navigation.findNavController(view)
                   .navigate(R.id.action_configuracion_to_editar_tarjeta);
              }
          });
           btnEditarTarjeta2.setOnClickListener(new View.OnClickListener() {
            @Override
              public void onClick(View v) {
                Navigation.findNavController(view)
                   .navigate(R.id.action_configuracion_to_editar_tarjeta);
              }
          });
           btnEditarTarjeta3.setOnClickListener(new View.OnClickListener() {
            @Override
              public void onClick(View v) {
                Navigation.findNavController(view)
                   .navigate(R.id.action_configuracion_to_editar_tarjeta);
              }
          });

    }
}