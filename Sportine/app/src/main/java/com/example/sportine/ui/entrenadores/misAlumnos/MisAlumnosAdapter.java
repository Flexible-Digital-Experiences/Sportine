package com.example.sportine.ui.entrenadores.misAlumnos;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.sportine.R;
import com.example.sportine.models.AlumnoEntrenadorDTO;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;
import java.util.List;

public class MisAlumnosAdapter extends RecyclerView.Adapter<MisAlumnosAdapter.AlumnoViewHolder> {

    private List<AlumnoEntrenadorDTO> alumnos = new ArrayList<>();
    private OnAlumnoClickListener listener;

    public interface OnAlumnoClickListener {
        void onAlumnoClick(AlumnoEntrenadorDTO alumno);
    }

    public MisAlumnosAdapter(OnAlumnoClickListener listener) {
        this.listener = listener;
    }

    public void setAlumnos(List<AlumnoEntrenadorDTO> alumnos) {
        this.alumnos = alumnos != null ? alumnos : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AlumnoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_entrenador_mialumno, parent, false);
        return new AlumnoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlumnoViewHolder holder, int position) {
        holder.bind(alumnos.get(position));
    }

    @Override
    public int getItemCount() {
        return alumnos.size();
    }

    class AlumnoViewHolder extends RecyclerView.ViewHolder {
        private MaterialCardView cardAlumno;
        private ShapeableImageView imageAlumno;
        private TextView textNombreAlumno;
        private TextView textEdad;
        private TextView textDeportes;
        private View indicadorEstado;

        public AlumnoViewHolder(@NonNull View itemView) {
            super(itemView);
            cardAlumno = itemView.findViewById(R.id.item_alumno_card);
            imageAlumno = itemView.findViewById(R.id.image_alumno);
            textNombreAlumno = itemView.findViewById(R.id.text_nombre_alumno);
            textEdad = itemView.findViewById(R.id.text_edad);
            textDeportes = itemView.findViewById(R.id.text_deportes);
            indicadorEstado = itemView.findViewById(R.id.indicador_estado);
        }

        public void bind(AlumnoEntrenadorDTO alumno) {
            // Nombre
            textNombreAlumno.setText(alumno.getNombreCompleto());

            // Edad
            if (alumno.getEdad() != null) {
                textEdad.setText(alumno.getEdad() + " años");
            }

            // Deportes
            textDeportes.setText(alumno.getDeportes());

            // Foto de perfil
            if (alumno.getFotoPerfil() != null && !alumno.getFotoPerfil().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(alumno.getFotoPerfil())
                        .placeholder(R.drawable.avatar_user_male)
                        .error(R.drawable.avatar_user_male)
                        .circleCrop()
                        .into(imageAlumno);
            } else {
                imageAlumno.setImageResource(R.drawable.avatar_user_male);
            }

            // Indicador de estado
            String status = alumno.getStatusRelacion();
            if ("activo".equalsIgnoreCase(status)) {
                indicadorEstado.setBackgroundColor(itemView.getContext().getColor(R.color.green_600));
            } else if ("pendiente".equalsIgnoreCase(status)) {
                indicadorEstado.setBackgroundColor(itemView.getContext().getColor(android.R.color.holo_orange_dark));
            } else {
                indicadorEstado.setBackgroundColor(itemView.getContext().getColor(R.color.button_reject));
            }

            // Click listener
            cardAlumno.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onAlumnoClick(alumno);
                }
            });
        }
    }
}