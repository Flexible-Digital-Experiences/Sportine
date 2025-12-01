package com.example.sportine.ui.entrenadores.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

    // Interfaz para manejar clicks en el futuro (para dejar tarea)
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
        View view = LayoutInflater.from(context).inflate(R.layout.item_entrenador_alumno_home, parent, false);
        return new AlumnoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlumnoViewHolder holder, int position) {
        AlumnoProgresoDTO alumno = alumnos.get(position);
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) holder.itemView.getLayoutParams();

        if (position == 0) {
            params.topMargin = 0; // Quita margen superior del primer item
        } else {
            params.topMargin = (int) (12 * context.getResources().getDisplayMetrics().density); // 12dp para el resto
        }
        holder.itemView.setLayoutParams(params);


        // 1. Nombre completo
        holder.nombre.setText(alumno.getNombre() + " " + alumno.getApellidos());

        // 2. Descripción actividad (Ej: "Completó entrenamiento hoy")
        // Si quieres darle un toque extra, puedes cambiar el color del texto si está inactivo
        holder.ultimaActividad.setText(alumno.getDescripcionActividad());

        // 3. Contadores CON ETIQUETAS
        // Aquí concatenamos el número con la palabra para que el usuario entienda
        int completados = alumno.getEntrenamientosCompletadosSemana();
        int pendientes = alumno.getEntrenamientosPendientes();

        holder.textCompletados.setText(completados + (completados == 1 ? " Completado" : " Completados"));
        holder.textPendientes.setText(pendientes + (pendientes == 1 ? " Pendiente" : " Pendientes"));

        // 4. Indicador de activo (El punto verde sobre el avatar)
        if (Boolean.TRUE.equals(alumno.getActivo())) {
            holder.indicatorActivo.setVisibility(View.VISIBLE);
        } else {
            holder.indicatorActivo.setVisibility(View.GONE);
        }

        // 5. Cargar Foto con Glide
        if (alumno.getFotoPerfil() != null && !alumno.getFotoPerfil().isEmpty()) {
            Glide.with(context)
                    .load(alumno.getFotoPerfil())
                    .placeholder(R.drawable.ic_avatar_default)
                    .circleCrop()
                    .into(holder.avatar);
        } else {
            holder.avatar.setImageResource(R.drawable.ic_avatar_default);
        }

        // Click Listener
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onAlumnoClick(alumno);
        });
    }

    @Override
    public int getItemCount() {
        return alumnos.size();
    }

    public void setAlumnos(List<AlumnoProgresoDTO> nuevosAlumnos) {
        this.alumnos = nuevosAlumnos;
        notifyDataSetChanged();
    }

    static class AlumnoViewHolder extends RecyclerView.ViewHolder {
        ImageView avatar;
        View indicatorActivo;
        TextView nombre, ultimaActividad, textCompletados, textPendientes;

        public AlumnoViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.img_alumno_avatar);
            indicatorActivo = itemView.findViewById(R.id.indicator_activo);
            nombre = itemView.findViewById(R.id.text_alumno_nombre);
            ultimaActividad = itemView.findViewById(R.id.text_ultima_actividad);
            textCompletados = itemView.findViewById(R.id.text_completados);
            textPendientes = itemView.findViewById(R.id.text_pendientes);
        }
    }
}