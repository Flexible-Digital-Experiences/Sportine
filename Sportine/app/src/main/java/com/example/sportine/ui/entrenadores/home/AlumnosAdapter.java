package com.example.sportine.ui.entrenadores.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.sportine.R;
import com.example.sportine.models.AlumnoProgresoDTO;
import java.util.ArrayList;
import java.util.List;

public class AlumnosAdapter extends RecyclerView.Adapter<AlumnosAdapter.AlumnoViewHolder> {

    private List<AlumnoProgresoDTO> alumnos = new ArrayList<>();

    public void setAlumnos(List<AlumnoProgresoDTO> nuevosAlumnos) {
        this.alumnos = nuevosAlumnos != null ? nuevosAlumnos : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AlumnoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Asegúrate de que el nombre del layout sea el correcto (item_entrenador_alumno_home)
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_entrenador_alumno_home, parent, false);
        return new AlumnoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlumnoViewHolder holder, int position) {
        AlumnoProgresoDTO alumno = alumnos.get(position);

        // 1. Nombre
        holder.txtNombre.setText(alumno.getNombre() + " " + alumno.getApellidos());

        // 2. Última actividad
        holder.txtUltimaActividad.setText(alumno.getDescripcionActividad());

        // 3. Estadísticas (Separadas según tu XML)
        holder.txtCompletados.setText(alumno.getEntrenamientosCompletadosSemana() + " completados");

        if (alumno.getEntrenamientosPendientes() > 0) {
            holder.txtPendientes.setText(alumno.getEntrenamientosPendientes() + " pendientes");
            holder.txtPendientes.setVisibility(View.VISIBLE);
            // Opcional: Mostrar icono de pendientes si es necesario
        } else {
            // Si no hay pendientes, podrías poner "Al día" o ocultarlo
            holder.txtPendientes.setText("Al día");
        }

        // 4. Indicador de Actividad (Puntito verde)
        if (alumno.isActivo()) {
            holder.indicatorActivo.setVisibility(View.VISIBLE);
        } else {
            holder.indicatorActivo.setVisibility(View.GONE);
        }

        // 5. Foto de Perfil
        if (alumno.getFotoPerfil() != null && !alumno.getFotoPerfil().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(alumno.getFotoPerfil())
                    .placeholder(R.drawable.avatar_user_male) // Imagen por defecto mientras carga
                    .error(R.drawable.avatar_user_male)       // Imagen si falla
                    .into(holder.imgAvatar);
        } else {
            holder.imgAvatar.setImageResource(R.drawable.avatar_user_male);
        }

        // 6. Configurar iconos de estadísticas (si no están fijos en XML o si quieres cambiarlos dinámicamente)
        holder.iconCompletados.setImageResource(R.drawable.ic_checkbox_checked); // Asegúrate de tener este drawable
        holder.iconPendientes.setImageResource(R.drawable.ic_clock);     // O el icono que uses para pendientes
    }

    @Override
    public int getItemCount() {
        return alumnos.size();
    }

    static class AlumnoViewHolder extends RecyclerView.ViewHolder {
        ImageView imgAvatar, iconCompletados, iconPendientes;
        View indicatorActivo;
        TextView txtNombre, txtUltimaActividad, txtCompletados, txtPendientes;

        public AlumnoViewHolder(@NonNull View itemView) {
            super(itemView);

            // Mapeo exacto con tu XML
            imgAvatar = itemView.findViewById(R.id.img_alumno_avatar);
            indicatorActivo = itemView.findViewById(R.id.indicator_activo);
            txtNombre = itemView.findViewById(R.id.text_alumno_nombre);
            txtUltimaActividad = itemView.findViewById(R.id.text_ultima_actividad);

            iconCompletados = itemView.findViewById(R.id.icon_completados);
            txtCompletados = itemView.findViewById(R.id.text_completados);

            iconPendientes = itemView.findViewById(R.id.icon_pendientes);
            txtPendientes = itemView.findViewById(R.id.text_pendientes);
        }
    }
}