package com.example.sportine.ui.usuarios.buscar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.sportine.R;
import com.example.sportine.models.EntrenadorCardDTO;

import java.util.ArrayList;
import java.util.List;

public class ResultadosEntrenadoresAdapter extends RecyclerView.Adapter<ResultadosEntrenadoresAdapter.EntrenadorViewHolder> {

    private List<EntrenadorCardDTO> listaEntrenadores = new ArrayList<>();
    private OnEntrenadorClickListener listener;

    public interface OnEntrenadorClickListener {
        void onEntrenadorClick(EntrenadorCardDTO entrenador);
    }

    public ResultadosEntrenadoresAdapter(OnEntrenadorClickListener listener) {
        this.listener = listener;
    }

    public void setEntrenadores(List<EntrenadorCardDTO> nuevosEntrenadores) {
        this.listaEntrenadores = nuevosEntrenadores != null ? nuevosEntrenadores : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public EntrenadorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_alumno_buscar_calentrenador, parent, false);
        return new EntrenadorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EntrenadorViewHolder holder, int position) {
        EntrenadorCardDTO entrenador = listaEntrenadores.get(position);

        // Nombre del entrenador
        holder.tvNombre.setText(entrenador.getNombreCompleto());

        // Rating con estrellas
        float rating = entrenador.getRatingPromedio() != null ?
                entrenador.getRatingPromedio().floatValue() : 0f;
        holder.ratingBar.setRating(rating);
        holder.tvCalificacion.setText(String.format("%.1f", rating));

        // Foto de perfil
        if (entrenador.getFotoPerfil() != null && !entrenador.getFotoPerfil().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(entrenador.getFotoPerfil())
                    .placeholder(R.drawable.avatar_user_male)
                    .error(R.drawable.avatar_user_male)
                    .circleCrop()
                    .into(holder.ivFoto);
        } else {
            holder.ivFoto.setImageResource(R.drawable.avatar_user_male);
        }

        // Deportes/Especialidades
        if (entrenador.getEspecialidades() != null && !entrenador.getEspecialidades().isEmpty()) {
            String deportes = String.join(", ", entrenador.getEspecialidades());
            holder.tvDeportes.setText(deportes);
        } else {
            holder.tvDeportes.setText("Sin especialidades");
        }

        // NUEVO: Mostrar alumnos actuales y disponibilidad
        configurarDisponibilidad(holder, entrenador);

        // Click en la tarjeta
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEntrenadorClick(entrenador);
            }
        });
    }

    private void configurarDisponibilidad(EntrenadorViewHolder holder, EntrenadorCardDTO entrenador) {
        Integer alumnosActuales = entrenador.getAlumnosActuales();
        Integer limiteAlumnos = entrenador.getLimiteAlumnos();

        // Mostrar número de alumnos
        if (alumnosActuales != null && limiteAlumnos != null) {
            holder.tvAlumnos.setText(String.format("%d/%d alumnos", alumnosActuales, limiteAlumnos));

            int espaciosDisponibles = entrenador.getEspaciosDisponibles();
            int porcentajeOcupacion = entrenador.getPorcentajeOcupacion();

            // Mostrar badge de disponibilidad
            if (espaciosDisponibles > 0) {
                holder.badgeDisponibilidad.setVisibility(View.VISIBLE);

                if (porcentajeOcupacion >= 80) {
                    // Casi lleno - Badge amarillo
                    holder.badgeDisponibilidad.setText(espaciosDisponibles == 1 ?
                            "1 espacio" : espaciosDisponibles + " espacios");
                    holder.badgeDisponibilidad.setBackgroundResource(R.drawable.badge_casi_lleno);
                    holder.badgeDisponibilidad.setTextColor(
                            ContextCompat.getColor(holder.itemView.getContext(),
                                    android.R.color.holo_orange_dark));

                    // Indicador amarillo
                    holder.indicadorEstado.setBackgroundColor(
                            ContextCompat.getColor(holder.itemView.getContext(),
                                    android.R.color.holo_orange_light));
                } else {
                    // Disponible - Badge verde
                    holder.badgeDisponibilidad.setText(espaciosDisponibles == 1 ?
                            "1 espacio" : espaciosDisponibles + " espacios");
                    holder.badgeDisponibilidad.setBackgroundResource(R.drawable.badge_disponible);
                    holder.badgeDisponibilidad.setTextColor(
                            ContextCompat.getColor(holder.itemView.getContext(),
                                    android.R.color.holo_green_dark));

                    // Indicador verde
                    holder.indicadorEstado.setBackgroundColor(
                            ContextCompat.getColor(holder.itemView.getContext(),
                                    android.R.color.holo_green_light));
                }
            } else {
                // Sin espacios (esto no debería pasar porque el backend ya filtra)
                holder.badgeDisponibilidad.setVisibility(View.GONE);
                holder.indicadorEstado.setBackgroundColor(
                        ContextCompat.getColor(holder.itemView.getContext(),
                                android.R.color.holo_red_light));
            }
        } else {
            // Si no hay datos de disponibilidad
            holder.tvAlumnos.setText("0 alumnos");
            holder.badgeDisponibilidad.setVisibility(View.GONE);
            holder.indicadorEstado.setBackgroundColor(
                    ContextCompat.getColor(holder.itemView.getContext(),
                            android.R.color.darker_gray));
        }
    }

    @Override
    public int getItemCount() {
        return listaEntrenadores.size();
    }

    static class EntrenadorViewHolder extends RecyclerView.ViewHolder {
        ImageView ivFoto;
        TextView tvNombre;
        RatingBar ratingBar;
        TextView tvCalificacion;
        TextView tvAlumnos;
        TextView tvDeportes;
        TextView badgeDisponibilidad;
        View indicadorEstado;

        public EntrenadorViewHolder(@NonNull View itemView) {
            super(itemView);
            ivFoto = itemView.findViewById(R.id.image_entrenador);
            tvNombre = itemView.findViewById(R.id.text_nombre_entrenador);
            ratingBar = itemView.findViewById(R.id.rating_entrenador);
            tvCalificacion = itemView.findViewById(R.id.text_calificacion);
            tvAlumnos = itemView.findViewById(R.id.text_alumnos);
            tvDeportes = itemView.findViewById(R.id.text_deportes);
            badgeDisponibilidad = itemView.findViewById(R.id.badge_disponibilidad);
            indicadorEstado = itemView.findViewById(R.id.indicador_estado);
        }
    }
}