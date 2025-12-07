package com.example.sportine.ui.entrenadores.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide; // Asegúrate de tener Glide para las imágenes
import com.example.sportine.R;
import com.example.sportine.models.AlumnoProgresoDTO;

import java.util.ArrayList;
import java.util.List;

public class AlumnosAdapter extends RecyclerView.Adapter<AlumnosAdapter.AlumnoViewHolder> {

    private List<AlumnoProgresoDTO> alumnos = new ArrayList<>();
    private Context context;

    // Interfaz para manejar clicks
    public interface OnAlumnoClickListener {
        void onAlumnoClick(AlumnoProgresoDTO alumno);
    }

    private OnAlumnoClickListener listener;

    public void setOnAlumnoClickListener(OnAlumnoClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public AlumnoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        // Nota: Asegúrate de que este layout (item_entrenador_alumno_home) sea el que modificamos
        // con los IDs nuevos (text_deporte, icon_deporte, layout_sport_info).
        View view = LayoutInflater.from(context).inflate(R.layout.item_entrenador_alumno_home, parent, false);
        return new AlumnoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlumnoViewHolder holder, int position) {
        AlumnoProgresoDTO alumno = alumnos.get(position);

        // --- 1. Lógica de Márgenes (Tu código original) ---
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) holder.itemView.getLayoutParams();
        if (position == 0) {
            params.topMargin = 0;
        } else {
            params.topMargin = (int) (12 * context.getResources().getDisplayMetrics().density);
        }
        holder.itemView.setLayoutParams(params);

        // --- 2. Textos Básicos ---
        holder.nombre.setText(alumno.getNombre() + " " + (alumno.getApellidos() != null ? alumno.getApellidos() : ""));
        holder.ultimaActividad.setText(alumno.getDescripcionActividad());

        // --- 3. Contadores ---
        int completados = alumno.getEntrenamientosCompletadosSemana();
        int pendientes = alumno.getEntrenamientosPendientes();
        holder.textCompletados.setText(completados + (completados == 1 ? " Completado" : " Completados"));
        holder.textPendientes.setText(pendientes + (pendientes == 1 ? " Pendiente" : " Pendientes"));

        // --- 4. Indicador de Activo ---
        if (Boolean.TRUE.equals(alumno.getActivo())) { // Boolean.TRUE para evitar NullPointer
            holder.indicatorActivo.setVisibility(View.VISIBLE);
        } else {
            holder.indicatorActivo.setVisibility(View.GONE);
        }

        // --- 5. LÓGICA DE DEPORTE (AGREGADO) ---
        String deporte = alumno.getDeporte().trim();

        // Verificamos que el deporte sea válido y que el holder tenga las vistas (por seguridad)
        if (holder.layoutSportInfo != null && deporte != null && !deporte.isEmpty() && !deporte.equalsIgnoreCase("Sin asignar")) {
            holder.layoutSportInfo.setVisibility(View.VISIBLE);
            holder.textDeporte.setText(deporte);

            // Switch para iconos locales
            // Mapeo del String que viene de Spring Boot a tus Drawables locales
            // En AlumnosAdapter.java -> onBindViewHolder

            switch (deporte) {
                case "Fútbol": case "Futbol":
                    holder.iconDeporte.setImageResource(R.drawable.balon_futbol);
                    break;
                case "Basketball": case "Basquetbol":
                    holder.iconDeporte.setImageResource(R.drawable.balon_basket);
                    break;
                case "Natación": case "Natacion":
                    holder.iconDeporte.setImageResource(R.drawable.ic_natacion);
                    break;
                case "Boxeo": case "Box":
                    holder.iconDeporte.setImageResource(R.drawable.ic_boxeo);
                    break;
                case "Tenis":
                    holder.iconDeporte.setImageResource(R.drawable.pelota_tenis);
                    break;
                case "Béisbol": case "Beisbol":
                    holder.iconDeporte.setImageResource(R.drawable.ic_beisbol);
                    break;
                case "Running":
                    holder.iconDeporte.setImageResource(R.drawable.ic_running);
                    break;

                // --- ASEGÚRATE DE QUE ESTOS ESTÉN ---
                case "Gimnasio": case "Gym":
                    holder.iconDeporte.setImageResource(R.drawable.ic_gimnasio);
                    break;
                case "Ciclismo":
                    holder.iconDeporte.setImageResource(R.drawable.ic_ciclismo);
                    break;
                // ------------------------------------

                default:
                    holder.iconDeporte.setImageResource(R.drawable.ic_ejercicio);
                    break;
            }
        } else if (holder.layoutSportInfo != null) {
            // Ocultar si no hay deporte
            holder.layoutSportInfo.setVisibility(View.GONE);
        }

        // --- 6. Cargar Foto con Glide ---
        if (alumno.getFotoPerfil() != null && !alumno.getFotoPerfil().isEmpty()) {
            Glide.with(context)
                    .load(alumno.getFotoPerfil())
                    .placeholder(R.drawable.ic_avatar_default)
                    .circleCrop() // Hace la imagen redonda
                    .into(holder.avatar);
        } else {
            holder.avatar.setImageResource(R.drawable.ic_avatar_default);
        }

        // --- 7. Click Listener ---
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onAlumnoClick(alumno);
        });
    }

    @Override
    public int getItemCount() {
        return alumnos != null ? alumnos.size() : 0;
    }

    public void setAlumnos(List<AlumnoProgresoDTO> nuevosAlumnos) {
        this.alumnos = nuevosAlumnos;
        notifyDataSetChanged();
    }

    static class AlumnoViewHolder extends RecyclerView.ViewHolder {
        ImageView avatar;
        View indicatorActivo;
        TextView nombre, ultimaActividad, textCompletados, textPendientes;

        // Nuevos campos para el deporte
        TextView textDeporte;
        ImageView iconDeporte;
        LinearLayout layoutSportInfo;

        public AlumnoViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.img_alumno_avatar);
            indicatorActivo = itemView.findViewById(R.id.indicator_activo);
            nombre = itemView.findViewById(R.id.text_alumno_nombre);
            ultimaActividad = itemView.findViewById(R.id.text_ultima_actividad);
            textCompletados = itemView.findViewById(R.id.text_completados);
            textPendientes = itemView.findViewById(R.id.text_pendientes);

            // Inicializamos los nuevos IDs del XML modificado
            textDeporte = itemView.findViewById(R.id.text_deporte);
            iconDeporte = itemView.findViewById(R.id.icon_deporte);
            layoutSportInfo = itemView.findViewById(R.id.layout_sport_info);
        }
    }
}