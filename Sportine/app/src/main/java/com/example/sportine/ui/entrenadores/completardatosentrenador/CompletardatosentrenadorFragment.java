package com.example.sportine.ui.entrenadores.completardatosentrenador;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.sportine.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class CompletardatosentrenadorFragment extends Fragment {

    // Componentes de la UI
    private View btnBack;
    private ImageView ivAvatarCompletar;

    // Datos Actuales (Solo Lectura)
    private TextView tvCostoActual;
    private TextView tvDeportesActual;
    private TextView tvDescripcionActual;

    // Datos Nuevos (Editables)
    private TextInputEditText etCostoNuevo;
    private TextInputEditText etDeportesNuevo;
    private TextInputEditText etDescripcionNuevo;

    private MaterialButton btnActualizar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Asegúrate de que este R.layout corresponde al XML que modernizamos
        return inflater.inflate(R.layout.fragment_entrenador_completar_datos, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Inicializar Componentes
        inicializarComponentes(view);

        // 2. Cargar Datos Actuales
        cargarDatosActuales();

        // 3. Configurar Listeners
        configurarListeners(view);
    }

    // --- Métodos de Ayuda ---

    private void inicializarComponentes(@NonNull View view) {
        // Encabezado y Avatar
        btnBack = view.findViewById(R.id.btnBack);
        ivAvatarCompletar = view.findViewById(R.id.iv_avatar_completar);

        // Datos Actuales
        tvCostoActual = view.findViewById(R.id.tvCostoActual);
        tvDeportesActual = view.findViewById(R.id.tvDeportesActual);
        tvDescripcionActual = view.findViewById(R.id.tvDescripcionActual);

        // Datos Nuevos (Editables)
        etCostoNuevo = view.findViewById(R.id.etCostoNuevo);
        etDeportesNuevo = view.findViewById(R.id.etDeportesNuevo);
        etDescripcionNuevo = view.findViewById(R.id.etDescripcionNuevo);

        // Botón
        btnActualizar = view.findViewById(R.id.btnActualizar);
    }

    private void configurarListeners(@NonNull View view) {
        // Botón Volver Atrás
        btnBack.setOnClickListener(v -> requireActivity().onBackPressed());

        // Botón Actualizar
        btnActualizar.setOnClickListener(v -> actualizarDatos());

        // Listener para la foto de perfil (para abrir la galería/cámara)
        ivAvatarCompletar.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Abriendo selector de imágenes...", Toast.LENGTH_SHORT).show();
            // ** Lógica real para abrir la galería/cámara aquí **
        });
    }

    /**
     * Simula la carga de datos del perfil actual del entrenador.
     * Estos datos se muestran como referencia para el usuario.
     */
    private void cargarDatosActuales() {
        // ** Reemplazar con la lógica real de obtención de datos (BD/API) **

        // Datos simulados
        double costoActual = 800.00;
        String deportesActuales = "Basketball, Fútbol, Atletismo";
        String descripcionActual = "Instructor con 15 años de experiencia, especializado en entrenamiento funcional y deportes de equipo.";

        // Poblar TextViews de datos actuales
        tvCostoActual.setText(String.format("$ %,.2f", costoActual));
        tvDeportesActual.setText(deportesActuales);
        tvDescripcionActual.setText(descripcionActual);

        // Aquí también podrías cargar la foto de perfil (ivAvatarCompletar) si tuvieras la URL.
    }

    /**
     * Recoge los datos de los campos editables y simula el proceso de actualización.
     */
    private void actualizarDatos() {
        String costoNuevo = etCostoNuevo.getText().toString().trim();
        String deportesNuevo = etDeportesNuevo.getText().toString().trim();
        String descripcionNueva = etDescripcionNuevo.getText().toString().trim();

        // 1. Validaciones básicas (se pueden expandir)
        if (costoNuevo.isEmpty() && deportesNuevo.isEmpty() && descripcionNueva.isEmpty()) {
            Toast.makeText(getContext(), "Ingresa al menos un campo para actualizar", Toast.LENGTH_LONG).show();
            return;
        }

        // 2. Simulación de actualización
        StringBuilder mensaje = new StringBuilder("Datos a actualizar:\n");

        if (!costoNuevo.isEmpty()) {
            mensaje.append("Costo: $").append(costoNuevo).append("\n");
        }
        if (!deportesNuevo.isEmpty()) {
            mensaje.append("Deportes: ").append(deportesNuevo).append("\n");
        }
        if (!descripcionNueva.isEmpty()) {
            mensaje.append("Descripción: ").append(descripcionNueva).append("\n");
        }

        Toast.makeText(getContext(), "Actualizando datos...\n" + mensaje.toString(), Toast.LENGTH_LONG).show();

        // ** Lógica real de guardado en la BD/API aquí **

        // 3. (Opcional) Navegar de vuelta a la configuración o recargar datos
        // Navigation.findNavController(requireView()).popBackStack();
    }
}