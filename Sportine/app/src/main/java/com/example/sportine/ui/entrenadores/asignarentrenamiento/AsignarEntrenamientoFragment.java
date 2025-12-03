package com.example.sportine.ui.entrenadores.asignarentrenamiento;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.sportine.R;
import com.example.sportine.data.ApiService;
import com.example.sportine.data.RetrofitClient;
import com.example.sportine.models.AsignarEjercicioDTO;
import com.example.sportine.models.CrearEntrenamientoRequestDTO;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AsignarEntrenamientoFragment extends Fragment {

    private static final String ARG_USUARIO_ALUMNO = "arg_usuario";
    private static final String ARG_NOMBRE_ALUMNO = "arg_nombre";
    private static final String ARG_FOTO_ALUMNO = "arg_foto";

    private String usuarioAlumno;
    private String nombreAlumno;
    private String fotoAlumno;

    private TextInputEditText inputTitulo, inputObjetivo, inputFecha, inputHora;
    private AutoCompleteTextView inputDificultad;
    private RecyclerView recyclerEjercicios;
    private View cardEmptyState;
    private TextView textContadorEjercicios;
    private MaterialButton btnGuardar;
    private ImageView btnBack;

    private ImageView imgAlumnoAvatar;
    private TextView textAlumnoNombre, textAlumnoDetalle;

    // CAMBIO: Guardamos las fechas directamente como String para evitar problemas de API
    private String fechaParaEnviar; // Formato yyyy-MM-dd
    private String horaParaEnviar;  // Formato HH:mm:ss

    private EjerciciosPorAsignarAdapter adapter;

    public AsignarEntrenamientoFragment() { }

    public static AsignarEntrenamientoFragment newInstance(String usuario, String nombre, String foto) {
        AsignarEntrenamientoFragment fragment = new AsignarEntrenamientoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USUARIO_ALUMNO, usuario);
        args.putString(ARG_NOMBRE_ALUMNO, nombre);
        args.putString(ARG_FOTO_ALUMNO, foto);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            usuarioAlumno = getArguments().getString(ARG_USUARIO_ALUMNO);
            nombreAlumno = getArguments().getString(ARG_NOMBRE_ALUMNO);
            fotoAlumno = getArguments().getString(ARG_FOTO_ALUMNO);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_entrenador_asignar_entrenamiento, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        inicializarVistas(view);
        configurarDatosAlumno();
        configurarPickers();
        configurarRecyclerView();

        btnBack.setOnClickListener(v -> requireActivity().onBackPressed());

        ExtendedFloatingActionButton fab = view.findViewById(R.id.fab_agregar_ejercicio);
        fab.setOnClickListener(v -> mostrarDialogoAgregarEjercicio());

        btnGuardar.setOnClickListener(v -> guardarEntrenamiento());
    }

    private void inicializarVistas(View view) {
        btnBack = view.findViewById(R.id.btnBack);

        // --- CORRECCIÓN AQUÍ ---
        View cardAlumno = view.findViewById(R.id.card_alumno);
        imgAlumnoAvatar = cardAlumno.findViewById(R.id.img_alumno_avatar);
        textAlumnoNombre = cardAlumno.findViewById(R.id.text_alumno_nombre);

        // CAMBIADO: De 'text_ultima_actividad' a 'text_alumno_descripcion'
        // que es el ID real en tu item_entrenador_alumno.xml
        textAlumnoDetalle = cardAlumno.findViewById(R.id.text_alumno_descripcion);
        // -----------------------

        inputTitulo = view.findViewById(R.id.input_titulo);
        inputObjetivo = view.findViewById(R.id.input_objetivo);
        inputFecha = view.findViewById(R.id.input_fecha);
        inputHora = view.findViewById(R.id.input_hora);
        inputDificultad = view.findViewById(R.id.input_dificultad);
        recyclerEjercicios = view.findViewById(R.id.recycler_ejercicios);
        cardEmptyState = view.findViewById(R.id.card_empty_state);
        textContadorEjercicios = view.findViewById(R.id.text_contador_ejercicios);
        btnGuardar = view.findViewById(R.id.btn_guardar_entrenamiento);
    }

    private void configurarDatosAlumno() {
        textAlumnoNombre.setText(nombreAlumno);
        textAlumnoDetalle.setText("Asignando nuevo plan");
        if (fotoAlumno != null && !fotoAlumno.isEmpty()) {
            Glide.with(this).load(fotoAlumno).circleCrop().into(imgAlumnoAvatar);
        }
    }

    // ================================================================
    // CONFIGURACIÓN DE PICKERS CORREGIDA (Sin LocalDate/LocalTime)
    // ================================================================
    private void configurarPickers() {
        inputFecha.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            new DatePickerDialog(getContext(), (view, year, month, dayOfMonth) -> {
                // Crear calendario con la fecha seleccionada
                Calendar fechaSeleccionada = Calendar.getInstance();
                fechaSeleccionada.set(year, month, dayOfMonth);

                // Formato para mostrar al usuario (dd/MM/yyyy)
                SimpleDateFormat sdfUser = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                inputFecha.setText(sdfUser.format(fechaSeleccionada.getTime()));

                // Formato para enviar al backend (yyyy-MM-dd)
                SimpleDateFormat sdfBackend = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                fechaParaEnviar = sdfBackend.format(fechaSeleccionada.getTime());

            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        inputHora.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            new TimePickerDialog(getContext(), (view, hourOfDay, minute) -> {
                // Crear calendario con la hora seleccionada
                Calendar horaSel = Calendar.getInstance();
                horaSel.set(Calendar.HOUR_OF_DAY, hourOfDay);
                horaSel.set(Calendar.MINUTE, minute);

                // Formato para mostrar (HH:mm)
                SimpleDateFormat sdfUser = new SimpleDateFormat("HH:mm", Locale.getDefault());
                inputHora.setText(sdfUser.format(horaSel.getTime()));

                // Formato para enviar (HH:mm:ss)
                SimpleDateFormat sdfBackend = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                horaParaEnviar = sdfBackend.format(horaSel.getTime());

            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
        });

        String[] dificultades = {"Facil", "Media", "Dificil"};
        ArrayAdapter<String> adapterDif = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, dificultades);
        inputDificultad.setAdapter(adapterDif);
        inputDificultad.setText("Media", false);
    }

    private void configurarRecyclerView() {
        adapter = new EjerciciosPorAsignarAdapter();
        recyclerEjercicios.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerEjercicios.setAdapter(adapter);
        adapter.setOnEliminarClickListener(position -> {
            adapter.eliminarEjercicio(position);
            actualizarEstadoLista();
        });
        actualizarEstadoLista();
    }

    private void actualizarEstadoLista() {
        int count = adapter.getItemCount();
        textContadorEjercicios.setText(count + " ejercicios");
        if (count > 0) {
            recyclerEjercicios.setVisibility(View.VISIBLE);
            cardEmptyState.setVisibility(View.GONE);
        } else {
            recyclerEjercicios.setVisibility(View.GONE);
            cardEmptyState.setVisibility(View.VISIBLE);
        }
    }

    private void mostrarDialogoAgregarEjercicio() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_agregar_ejercicio, null);
        builder.setView(view);
        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        AutoCompleteTextView spinnerTipo = view.findViewById(R.id.spinner_tipo_medida);
        TextInputEditText etNombre = view.findViewById(R.id.input_nombre_ejercicio);

        LinearLayout containerReps = view.findViewById(R.id.container_reps_peso);
        LinearLayout containerCardio = view.findViewById(R.id.container_cardio);

        TextInputEditText etSeries = view.findViewById(R.id.input_series);
        TextInputEditText etReps = view.findViewById(R.id.input_reps);
        TextInputEditText etPeso = view.findViewById(R.id.input_peso);

        TextInputEditText etDistancia = view.findViewById(R.id.input_distancia);
        TextInputEditText etTiempo = view.findViewById(R.id.input_tiempo);

        Button btnAgregar = view.findViewById(R.id.btn_agregar);
        Button btnCancelar = view.findViewById(R.id.btn_cancelar);

        String[] tipos = {"Repeticiones y Series", "Cardio / Tiempo"};
        ArrayAdapter<String> adapterTipo = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, tipos);
        spinnerTipo.setAdapter(adapterTipo);
        spinnerTipo.setText(tipos[0], false);

        spinnerTipo.setOnItemClickListener((parent, view1, position, id) -> {
            String seleccionado = adapterTipo.getItem(position);
            if ("Cardio / Tiempo".equals(seleccionado)) {
                containerReps.setVisibility(View.GONE);
                containerCardio.setVisibility(View.VISIBLE);
            } else {
                containerReps.setVisibility(View.VISIBLE);
                containerCardio.setVisibility(View.GONE);
            }
        });

        btnAgregar.setOnClickListener(v -> {
            String nombre = etNombre.getText().toString();
            if (nombre.isEmpty()) {
                etNombre.setError("Requerido");
                return;
            }

            AsignarEjercicioDTO ejercicio = new AsignarEjercicioDTO();
            ejercicio.setNombreEjercicio(nombre);
            ejercicio.setStatusEjercicio("pendiente");

            if (containerReps.getVisibility() == View.VISIBLE) {
                ejercicio.setSeries(parseInteger(etSeries));
                ejercicio.setRepeticiones(parseInteger(etReps));
                ejercicio.setPeso(parseFloat(etPeso));
                ejercicio.setDistancia(null);
                ejercicio.setDuracion(null);
            } else {
                ejercicio.setDistancia(parseFloat(etDistancia));
                ejercicio.setDuracion(parseInteger(etTiempo));
                ejercicio.setSeries(null);
                ejercicio.setRepeticiones(null);
                ejercicio.setPeso(null);
            }

            adapter.agregarEjercicio(ejercicio);
            actualizarEstadoLista();
            dialog.dismiss();
        });

        btnCancelar.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private Integer parseInteger(TextInputEditText et) {
        String s = et.getText().toString();
        return s.isEmpty() ? null : Integer.parseInt(s);
    }

    private Float parseFloat(TextInputEditText et) {
        String s = et.getText().toString();
        return s.isEmpty() ? null : Float.parseFloat(s);
    }

    private void guardarEntrenamiento() {
        // 1. Validaciones básicas
        String titulo = inputTitulo.getText().toString();
        if (titulo.isEmpty()) { inputTitulo.setError("Requerido"); return; }

        if (fechaParaEnviar == null) {
            Toast.makeText(getContext(), "Selecciona una fecha", Toast.LENGTH_SHORT).show();
            return;
        }

        if (adapter.getItemCount() == 0) {
            Toast.makeText(getContext(), "Agrega al menos un ejercicio", Toast.LENGTH_SHORT).show();
            return;
        }

        // 2. Preparar el objeto para enviar (DTO)
        CrearEntrenamientoRequestDTO request = new CrearEntrenamientoRequestDTO();
        request.setUsuarioAlumno(usuarioAlumno);
        request.setTituloEntrenamiento(titulo);
        request.setObjetivo(inputObjetivo.getText().toString());

        // Fechas ya formateadas como String
        request.setFechaEntrenamiento(fechaParaEnviar);
        request.setHoraEntrenamiento(horaParaEnviar != null ? horaParaEnviar : "12:00:00");

        request.setDificultad(inputDificultad.getText().toString().toLowerCase());
        request.setEjercicios(adapter.getEjercicios());

        // Deshabilitar botón para evitar doble click
        btnGuardar.setEnabled(false);
        btnGuardar.setText("Guardando...");

        // 3. Llamada al Backend
        ApiService api = RetrofitClient.getClient(getContext()).create(ApiService.class);

        api.crearEntrenamiento(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                // Reactivar botón por si falla algo
                btnGuardar.setEnabled(true);
                btnGuardar.setText("Guardar Entrenamiento");

                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "¡Entrenamiento asignado con éxito!", Toast.LENGTH_LONG).show();
                    // Volver atrás (al Home o Perfil)
                    requireActivity().onBackPressed();
                } else {
                    Toast.makeText(getContext(), "Error al guardar: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                btnGuardar.setEnabled(true);
                btnGuardar.setText("Guardar Entrenamiento");
                Toast.makeText(getContext(), "Fallo de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}