package com.example.sportine.ui.usuarios.detallesentrenamiento;

import com.example.sportine.R;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.bumptech.glide.Glide;
import com.example.sportine.databinding.FragmentAlumnoDetallesEntrenamientoBinding;
import com.example.sportine.models.AsignarEjercicioDTO;
import java.util.List;

public class DetallesEntrenamientoFragment extends Fragment {

    private static final String PREFS_NAME = "sportine_prefs";
    private static final String KEY_HC_CONECTADO = "hc_conectado";

    private FragmentAlumnoDetallesEntrenamientoBinding binding;
    private DetallesViewModel viewModel;
    private EjerciciosAdapter adapter;
    private Integer idEntrenamiento;

    private boolean yaGestionoHC = false;
    private boolean publicarLogroPendiente = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            idEntrenamiento = getArguments().getInt("idEntrenamiento", -1);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAlumnoDetallesEntrenamientoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupRecyclerView();
        setupViewModel();
        setupListeners();
        if (idEntrenamiento != -1) {
            viewModel.cargarDetalles(idEntrenamiento);
        } else {
            Toast.makeText(getContext(), "Error: Entrenamiento no identificado",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Al regresar de HealthConnectFragment, completar el entrenamiento
        if (yaGestionoHC) {
            yaGestionoHC = false;
            enviarCompletadoFinal(publicarLogroPendiente);
        }
    }

    // ── RecyclerView ──────────────────────────────────────────────────────────
    private void setupRecyclerView() {
        adapter = new EjerciciosAdapter();
        binding.recyclerEjercicios.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerEjercicios.setAdapter(adapter);

        adapter.setOnEjercicioCheckListener((ejercicio, isChecked) -> {
            ejercicio.setCompletado(isChecked);
            viewModel.cambiarEstadoEjercicio(ejercicio.getIdAsignado(), isChecked);
            actualizarProgresoCopa();
        });

        adapter.setOnLlenarResultadosListener((ejercicio, numeroSerie) -> {
            int idDeporte = 0;
            if (viewModel.getDetalle().getValue() != null
                    && viewModel.getDetalle().getValue().getIdDeporte() != null) {
                idDeporte = viewModel.getDetalle().getValue().getIdDeporte();
            }
            ResultadoEjercicioBottomSheet bs =
                    ResultadoEjercicioBottomSheet.newInstance(ejercicio, numeroSerie, idDeporte);
            bs.setOnResultadoGuardadoListener((idAsignado, nuevoStatus) -> {
                adapter.actualizarStatusEjercicio(idAsignado, nuevoStatus);
                adapter.avanzarSerie(idAsignado);
                actualizarProgresoCopa();
            });
            bs.show(getChildFragmentManager(), "resultado_ejercicio");
        });
    }

    // ── Copa ──────────────────────────────────────────────────────────────────
    private void actualizarProgresoCopa() {
        if (binding.imgCopaLogro.getVisibility() == View.GONE) return;
        if (viewModel.getDetalle().getValue() == null) return;
        List<AsignarEjercicioDTO> lista = viewModel.getDetalle().getValue().getEjercicios();
        if (lista == null || lista.isEmpty()) return;
        int total = lista.size();
        int completados = 0;
        for (AsignarEjercicioDTO e : lista) { if (e.isCompletado()) completados++; }
        float porcentaje = (float) completados / total;
        binding.imgCopaLogro.setAlpha(0.3f + (0.7f * porcentaje));
        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(porcentaje);
        binding.imgCopaLogro.setColorFilter(new ColorMatrixColorFilter(matrix));
        if (completados == total) {
            if (!binding.imgCopaLogro.isClickable()) {
                binding.imgCopaLogro.setClickable(true);
                binding.imgCopaLogro.animate().scaleX(1.3f).scaleY(1.3f).setDuration(200)
                        .withEndAction(() -> binding.imgCopaLogro.animate()
                                .scaleX(1f).scaleY(1f).start()).start();
                binding.imgCopaLogro.clearColorFilter();
                Toast.makeText(getContext(),
                        "¡Logro desbloqueado! Toca la copa para presumirlo 🏆",
                        Toast.LENGTH_SHORT).show();
            }
        } else { binding.imgCopaLogro.setClickable(false); }
    }

    // ── ViewModel ─────────────────────────────────────────────────────────────
    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(DetallesViewModel.class);
        viewModel.getDetalle().observe(getViewLifecycleOwner(), dto -> {
            binding.textTituloEntrenamiento.setText(dto.getTitulo());
            binding.textFecha.setText(dto.getFecha() + " " + dto.getHora());
            binding.textNombreEntrenador.setText(dto.getNombreEntrenador());
            binding.textEspecialidad.setText(dto.getEspecialidadEntrenador());
            binding.textDescripcion.setText(dto.getObjetivo());
            String dificultad = dto.getDificultad();
            if (dificultad == null) dificultad = "Medio";
            boolean esDificil = dificultad.equalsIgnoreCase("Dificil") ||
                    dificultad.equalsIgnoreCase("Avanzado") ||
                    dificultad.equalsIgnoreCase("Experto");
            binding.imgCopaLogro.setVisibility(esDificil ? View.VISIBLE : View.GONE);
            if (dto.getEjercicios() != null) {
                binding.textContadorEjercicios.setText(
                        dto.getEjercicios().size() + " ejercicios");
                adapter.setEjercicios(dto.getEjercicios());
                if (esDificil) actualizarProgresoCopa();
            }
            if (dto.getFotoEntrenador() != null && !dto.getFotoEntrenador().isEmpty()) {
                Glide.with(this).load(dto.getFotoEntrenador())
                        .placeholder(R.drawable.ic_avatar_default).circleCrop()
                        .into(binding.imgAvatarEntrenador);
            }
            if (dto.getDeporteIcono() != null) {
                binding.imgDeporteIcon.setImageResource(
                        obtenerIconoDeporte(dto.getDeporteIcono()));
            }
        });
        viewModel.getEntrenamientoCompletado().observe(getViewLifecycleOwner(), exito -> {
            if (exito) {
                Toast.makeText(getContext(), "¡Entrenamiento completado! 💪",
                        Toast.LENGTH_SHORT).show();
                if (getActivity() != null) getActivity().onBackPressed();
            }
        });
        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null) Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
        });
    }

    // ── Listeners ─────────────────────────────────────────────────────────────
    private void setupListeners() {
        binding.btnBack.setOnClickListener(v -> {
            if (getActivity() != null) getActivity().onBackPressed();
        });
        binding.sliderCansancio.addOnChangeListener((slider, value, fromUser) ->
                binding.labelCansancio.setText("Nivel de Cansancio (" + (int) value + "/10)"));
        binding.sliderDificultad.addOnChangeListener((slider, value, fromUser) ->
                binding.labelDificultad.setText("Dificultad (" + (int) value + "/10)"));
        binding.imgCopaLogro.setOnClickListener(v -> {
            Toast.makeText(getContext(), "¡Presumiendo logro! 🏆🔥", Toast.LENGTH_SHORT).show();
            iniciarFlujoCompletar(true);
        });
        binding.btnMarcarCompletado.setOnClickListener(v -> {
            boolean hayPendientes = false;
            if (viewModel.getDetalle().getValue() != null
                    && viewModel.getDetalle().getValue().getEjercicios() != null) {
                for (AsignarEjercicioDTO ejercicio :
                        viewModel.getDetalle().getValue().getEjercicios()) {
                    if (!ejercicio.isCompletado()) { hayPendientes = true; break; }
                }
            }
            if (hayPendientes) {
                new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                        .setTitle("Ejercicios pendientes")
                        .setMessage("No has marcado todos los ejercicios. "
                                + "¿Deseas finalizar de todas formas?")
                        .setPositiveButton("Sí, terminar",
                                (dialog, which) -> iniciarFlujoCompletar(false))
                        .setNegativeButton("No", null).show();
            } else { iniciarFlujoCompletar(false); }
        });
    }

    /**
     * Decide qué hacer antes de completar el entrenamiento:
     *
     * 1. Ya rechazó HC para este entrenamiento → completar directo
     * 2. Ya tiene HC conectado → ir directo al fragment sin preguntar
     * 3. Primera vez → mostrar diálogo
     */
    private void iniciarFlujoCompletar(boolean publicarLogro) {
        if (!validarFeedback()) return;
        publicarLogroPendiente = publicarLogro;

        SharedPreferences prefs = requireContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        // Caso 1: ya rechazó HC para este entrenamiento específico
        boolean rechazadoEsteEntrenamiento = prefs.getBoolean(
                "hc_rechazado_" + idEntrenamiento, false);
        if (rechazadoEsteEntrenamiento) {
            enviarCompletadoFinal(publicarLogro);
            return;
        }

        // Caso 2: ya tiene HC conectado → ir directo sin preguntar
        boolean hcConectado = prefs.getBoolean(KEY_HC_CONECTADO, false);
        if (hcConectado) {
            yaGestionoHC = true;
            navegarAHealthConnect();
            return;
        }

        // Caso 3: primera vez → mostrar diálogo
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Sincronizar con Health Connect")
                .setMessage("¿Quieres vincular tus métricas de hoy (frecuencia cardíaca, "
                        + "calorías, pasos) a este entrenamiento?")
                .setPositiveButton("Sí, vincular", (dialog, which) -> {
                    // Guardar que HC está conectado para futuros entrenamientos
                    prefs.edit().putBoolean(KEY_HC_CONECTADO, true).apply();
                    yaGestionoHC = true;
                    navegarAHealthConnect();
                })
                .setNegativeButton("No, completar sin HC", (dialog, which) -> {
                    // Guardar que rechazó HC para este entrenamiento
                    prefs.edit().putBoolean(
                            "hc_rechazado_" + idEntrenamiento, true).apply();
                    enviarCompletadoFinal(publicarLogro);
                })
                .show();
    }

    private void navegarAHealthConnect() {
        String nombreEntrenamiento = viewModel.getDetalle().getValue() != null
                ? viewModel.getDetalle().getValue().getTitulo() : "Entrenamiento";
        Bundle args = new Bundle();
        args.putInt("id_entrenamiento", idEntrenamiento);
        args.putString("nombre_entrenamiento", nombreEntrenamiento);
        Navigation.findNavController(requireView())
                .navigate(R.id.action_detalles_to_healthConnect, args);
    }

    private boolean validarFeedback() {
        String comentario = binding.inputComentario.getText() != null
                ? binding.inputComentario.getText().toString().trim() : "";
        if (comentario.length() > 255) {
            Toast.makeText(getContext(),
                    "Los comentarios no pueden exceder 255 caracteres",
                    Toast.LENGTH_LONG).show();
            binding.inputLayoutComentario.setError("Máximo 255 caracteres");
            return false;
        }
        binding.inputLayoutComentario.setError(null);
        int cansancio = (int) binding.sliderCansancio.getValue();
        int dificultad = (int) binding.sliderDificultad.getValue();
        if (cansancio < 1 || cansancio > 10) {
            Toast.makeText(getContext(),
                    "El nivel de cansancio debe ser entre 1 y 10",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        if (dificultad < 1 || dificultad > 10) {
            Toast.makeText(getContext(),
                    "La dificultad percibida debe ser entre 1 y 10",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void enviarCompletadoFinal(boolean publicarLogro) {
        String comentario = binding.inputComentario.getText() != null
                ? binding.inputComentario.getText().toString().trim() : "";
        int cansancio  = (int) binding.sliderCansancio.getValue();
        int dificultad = (int) binding.sliderDificultad.getValue();
        String animo = "Normal";
        int chipId = binding.chipGroupAnimo.getCheckedChipId();
        if (chipId != -1) {
            com.google.android.material.chip.Chip chip =
                    binding.getRoot().findViewById(chipId);
            if (chip != null) animo = chip.getText().toString();
        }
        viewModel.completarEntrenamiento(idEntrenamiento, comentario,
                cansancio, dificultad, animo, publicarLogro);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private int obtenerIconoDeporte(String nombreDeporte) {
        if (nombreDeporte == null) return R.drawable.ic_deporte_default;
        String deporte = nombreDeporte.trim().toLowerCase();
        if (deporte.contains("futbol") || deporte.contains("fútbol") || deporte.contains("soccer"))
            return R.drawable.balon_futbol;
        else if (deporte.contains("basket") || deporte.contains("basquet") || deporte.contains("baloncesto"))
            return R.drawable.balon_basket;
        else if (deporte.contains("natacion") || deporte.contains("natación") || deporte.contains("nadar"))
            return R.drawable.ic_natacion;
        else if (deporte.contains("run") || deporte.contains("correr") || deporte.contains("trotar"))
            return R.drawable.ic_running;
        else if (deporte.contains("box") || deporte.contains("pugilismo"))
            return R.drawable.ic_boxeo;
        else if (deporte.contains("tenis") || deporte.contains("tennis"))
            return R.drawable.pelota_tenis;
        else if (deporte.contains("beisbol") || deporte.contains("béisbol") || deporte.contains("baseball"))
            return R.drawable.ic_beisbol;
        else if (deporte.contains("gym") || deporte.contains("gimnasio") || deporte.contains("pesas") || deporte.contains("crossfit"))
            return R.drawable.ic_gimnasio;
        else if (deporte.contains("ciclis") || deporte.contains("bici") || deporte.contains("veloz"))
            return R.drawable.ic_ciclismo;
        else return R.drawable.ic_deporte_default;
    }
}