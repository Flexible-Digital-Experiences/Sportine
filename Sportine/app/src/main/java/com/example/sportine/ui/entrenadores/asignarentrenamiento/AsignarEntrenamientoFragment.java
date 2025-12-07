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

    // ARGUMENTOS NUEVOS PARA DEPORTE Y ACTIVIDAD
    private static final String ARG_DEPORTE_ALUMNO = "arg_deporte";
    private static final String ARG_ACTIVIDAD_ALUMNO = "arg_actividad";

    private String usuarioAlumno;
    private String nombreAlumno;
    private String fotoAlumno;
    private String deporteAlumno;
    private String actividadAlumno;

    private TextInputEditText inputTitulo, inputObjetivo, inputFecha, inputHora;
    private AutoCompleteTextView inputDificultad;
    private RecyclerView recyclerEjercicios;
    private View cardEmptyState;
    private TextView textContadorEjercicios;
    private MaterialButton btnGuardar;
    private ImageView btnBack;

    // Vistas de la tarjeta de alumno
    private ImageView imgAlumnoAvatar;
    private TextView textAlumnoNombre, textAlumnoDetalle;

    // Vistas para el deporte (Dentro de la tarjeta)
    private TextView textDeporte;
    private ImageView iconDeporte;
    private LinearLayout layoutSportInfo;

    private String fechaParaEnviar;
    private String horaParaEnviar;

    private EjerciciosPorAsignarAdapter adapter;

    public AsignarEntrenamientoFragment() { }

    // MÉTODO NEWINSTANCE ACTUALIZADO (Recibe 5 parámetros)
    public static AsignarEntrenamientoFragment newInstance(String usuario, String nombre, String foto, String deporte, String actividad) {
        AsignarEntrenamientoFragment fragment = new AsignarEntrenamientoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USUARIO_ALUMNO, usuario);
        args.putString(ARG_NOMBRE_ALUMNO, nombre);
        args.putString(ARG_FOTO_ALUMNO, foto);
        args.putString(ARG_DEPORTE_ALUMNO, deporte);
        args.putString(ARG_ACTIVIDAD_ALUMNO, actividad);
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
            deporteAlumno = getArguments().getString(ARG_DEPORTE_ALUMNO);
            actividadAlumno = getArguments().getString(ARG_ACTIVIDAD_ALUMNO);
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

        View cardAlumno = view.findViewById(R.id.card_alumno);
        imgAlumnoAvatar = cardAlumno.findViewById(R.id.img_alumno_avatar);
        textAlumnoNombre = cardAlumno.findViewById(R.id.text_alumno_nombre);
        textAlumnoDetalle = cardAlumno.findViewById(R.id.text_alumno_descripcion);

        // VINCULACIÓN DE VISTAS DEL DEPORTE
        textDeporte = cardAlumno.findViewById(R.id.text_deporte);
        iconDeporte = cardAlumno.findViewById(R.id.icon_deporte);
        layoutSportInfo = cardAlumno.findViewById(R.id.layout_sport_info);

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

        // Asignar descripción de actividad (Ej: "Completó entrenamiento hoy")
        if (actividadAlumno != null && !actividadAlumno.isEmpty()) {
            textAlumnoDetalle.setText(actividadAlumno);
        } else {
            textAlumnoDetalle.setText("Sin actividad reciente");
        }

        if (fotoAlumno != null && !fotoAlumno.isEmpty()) {
            Glide.with(this).load(fotoAlumno).circleCrop().into(imgAlumnoAvatar);
        }

        // LÓGICA VISUAL DEL DEPORTE
        if (layoutSportInfo != null && deporteAlumno != null && !deporteAlumno.isEmpty() && !deporteAlumno.equalsIgnoreCase("Sin asignar")) {
            layoutSportInfo.setVisibility(View.VISIBLE);
            textDeporte.setText(deporteAlumno);

            switch (deporteAlumno) {
                case "Fútbol": case "Futbol":
                    iconDeporte.setImageResource(R.drawable.balon_futbol);
                    break;
                case "Basketball": case "Basquetbol":
                    iconDeporte.setImageResource(R.drawable.balon_basket);
                    break;
                case "Natación": case "Natacion":
                    iconDeporte.setImageResource(R.drawable.ic_natacion);
                    break;
                case "Boxeo": case "Box":
                    iconDeporte.setImageResource(R.drawable.ic_boxeo);
                    break;
                case "Tenis":
                    iconDeporte.setImageResource(R.drawable.pelota_tenis);
                    break;
                case "Béisbol": case "Beisbol":
                    iconDeporte.setImageResource(R.drawable.ic_beisbol);
                    break;
                case "Running":
                    iconDeporte.setImageResource(R.drawable.ic_running);
                    break;
                case "Gimnasio": case "Gym":
                    iconDeporte.setImageResource(R.drawable.ic_gimnasio);
                    break;
                case "Ciclismo":
                    iconDeporte.setImageResource(R.drawable.ic_ciclismo);
                    break;
                default:
                    iconDeporte.setImageResource(R.drawable.ic_ejercicio);
                    break;
            }
        } else if (layoutSportInfo != null) {
            layoutSportInfo.setVisibility(View.GONE);
        }
    }

    private void configurarPickers() {
        inputFecha.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            new DatePickerDialog(getContext(), (view, year, month, dayOfMonth) -> {
                Calendar fechaSeleccionada = Calendar.getInstance();
                fechaSeleccionada.set(year, month, dayOfMonth);
                SimpleDateFormat sdfUser = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                inputFecha.setText(sdfUser.format(fechaSeleccionada.getTime()));
                SimpleDateFormat sdfBackend = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                fechaParaEnviar = sdfBackend.format(fechaSeleccionada.getTime());
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        inputHora.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            new TimePickerDialog(getContext(), (view, hourOfDay, minute) -> {
                Calendar horaSel = Calendar.getInstance();
                horaSel.set(Calendar.HOUR_OF_DAY, hourOfDay);
                horaSel.set(Calendar.MINUTE, minute);
                SimpleDateFormat sdfUser = new SimpleDateFormat("HH:mm", Locale.getDefault());
                inputHora.setText(sdfUser.format(horaSel.getTime()));
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
        recyclerEjercicios.setVisibility(count > 0 ? View.VISIBLE : View.GONE);
        cardEmptyState.setVisibility(count > 0 ? View.GONE : View.VISIBLE);
    }

    private void mostrarDialogoAgregarEjercicio() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_agregar_ejercicio, null);
        builder.setView(view);
        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

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
            boolean isCardio = "Cardio / Tiempo".equals(adapterTipo.getItem(position));
            containerReps.setVisibility(isCardio ? View.GONE : View.VISIBLE);
            containerCardio.setVisibility(isCardio ? View.VISIBLE : View.GONE);
        });

        btnAgregar.setOnClickListener(v -> {
            String nombre = etNombre.getText().toString();
            if (nombre.isEmpty()) { etNombre.setError("Requerido"); return; }

            AsignarEjercicioDTO ejercicio = new AsignarEjercicioDTO();
            ejercicio.setNombreEjercicio(nombre);
            ejercicio.setStatusEjercicio("pendiente");

            if (containerReps.getVisibility() == View.VISIBLE) {
                ejercicio.setSeries(parseInteger(etSeries));
                ejercicio.setRepeticiones(parseInteger(etReps));
                ejercicio.setPeso(parseFloat(etPeso));
            } else {
                ejercicio.setDistancia(parseFloat(etDistancia));
                ejercicio.setDuracion(parseInteger(etTiempo));
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
        String titulo = inputTitulo.getText().toString();
        if (titulo.isEmpty()) { inputTitulo.setError("Requerido"); return; }
        if (fechaParaEnviar == null) { Toast.makeText(getContext(), "Selecciona una fecha", Toast.LENGTH_SHORT).show(); return; }
        if (adapter.getItemCount() == 0) { Toast.makeText(getContext(), "Agrega al menos un ejercicio", Toast.LENGTH_SHORT).show(); return; }

        CrearEntrenamientoRequestDTO request = new CrearEntrenamientoRequestDTO();
        request.setUsuarioAlumno(usuarioAlumno);
        request.setTituloEntrenamiento(titulo);
        request.setObjetivo(inputObjetivo.getText().toString());
        request.setFechaEntrenamiento(fechaParaEnviar);
        request.setHoraEntrenamiento(horaParaEnviar != null ? horaParaEnviar : "12:00:00");
        request.setDificultad(inputDificultad.getText().toString().toLowerCase());
        request.setEjercicios(adapter.getEjercicios());

        btnGuardar.setEnabled(false);
        btnGuardar.setText("Guardando...");

        ApiService api = RetrofitClient.getClient(getContext()).create(ApiService.class);
        api.crearEntrenamiento(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                btnGuardar.setEnabled(true);
                btnGuardar.setText("Guardar Entrenamiento");
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "¡Entrenamiento asignado!", Toast.LENGTH_LONG).show();
                    requireActivity().onBackPressed();
                } else {
                    Toast.makeText(getContext(), "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                btnGuardar.setEnabled(true);
                btnGuardar.setText("Guardar Entrenamiento");
                Toast.makeText(getContext(), "Fallo de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }
}