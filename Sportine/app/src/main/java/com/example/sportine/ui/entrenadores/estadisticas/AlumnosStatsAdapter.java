package com.example.sportine.ui.entrenadores.estadisticas;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sportine.R;
import com.example.sportine.models.AlumnoCardStatsDTO;
import com.google.android.material.card.MaterialCardView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Adapter para mostrar la lista de alumnos con sus estadísticas resumidas.
 * Cada item muestra: foto, nombre, deporte, métricas y nivel de compromiso.
 */
public class AlumnosStatsAdapter extends RecyclerView.Adapter<AlumnosStatsAdapter.AlumnoViewHolder> {

    private List<AlumnoCardStatsDTO> alumnos = new ArrayList<>();
    private OnAlumnoClickListener listener;

    /**
     * Interface para manejar clicks en los items.
     */
    public interface OnAlumnoClickListener {
        void onAlumnoClick(AlumnoCardStatsDTO alumno);
    }

    /**
     * Constructor del adapter.
     *
     * @param listener Listener para clicks en los items
     */
    public AlumnosStatsAdapter(OnAlumnoClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public AlumnoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_alumno_stats_card, parent, false);
        return new AlumnoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlumnoViewHolder holder, int position) {
        AlumnoCardStatsDTO alumno = alumnos.get(position);
        holder.bind(alumno, listener);
    }

    @Override
    public int getItemCount() {
        return alumnos.size();
    }

    /**
     * Actualiza la lista de alumnos.
     *
     * @param nuevosAlumnos Nueva lista de alumnos
     */
    public void setAlumnos(List<AlumnoCardStatsDTO> nuevosAlumnos) {
        this.alumnos = nuevosAlumnos != null ? nuevosAlumnos : new ArrayList<>();
        notifyDataSetChanged();
    }

    /**
     * ViewHolder para cada item de alumno.
     */
    static class AlumnoViewHolder extends RecyclerView.ViewHolder {

        private final MaterialCardView cardView;
        private final CircleImageView imgFotoPerfil;
        private final View indicadorEntrenoHoy;
        private final TextView textNombre;
        private final TextView textDeportePrincipal;
        private final TextView textUltimaActividad;
        private final TextView badgeCompromiso;
        private final TextView textTotalEntrenamientos;
        private final TextView textRacha;
        private final TextView textEntrenamientosMes;

        public AlumnoViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = (MaterialCardView) itemView;
            imgFotoPerfil = itemView.findViewById(R.id.img_foto_perfil);
            indicadorEntrenoHoy = itemView.findViewById(R.id.indicator_entreno_hoy);
            textNombre = itemView.findViewById(R.id.text_nombre_alumno);
            textDeportePrincipal = itemView.findViewById(R.id.text_deporte_principal);
            textUltimaActividad = itemView.findViewById(R.id.text_ultima_actividad);
            badgeCompromiso = itemView.findViewById(R.id.badge_compromiso);
            textTotalEntrenamientos = itemView.findViewById(R.id.text_total_entrenamientos);
            textRacha = itemView.findViewById(R.id.text_racha);
            textEntrenamientosMes = itemView.findViewById(R.id.text_entrenamientos_mes);
        }

        public void bind(AlumnoCardStatsDTO alumno, OnAlumnoClickListener listener) {
            // Foto de perfil
            if (alumno.getFotoPerfil() != null && !alumno.getFotoPerfil().isEmpty()) {
                Picasso.get()
                        .load(alumno.getFotoPerfil())
                        .placeholder(R.drawable.ic_perfil)
                        .error(R.drawable.ic_perfil)
                        .into(imgFotoPerfil);
            } else {
                imgFotoPerfil.setImageResource(R.drawable.ic_perfil);
            }

            // Indicador de "Entrenó hoy"
            if (alumno.getEntrenoHoy() != null && alumno.getEntrenoHoy()) {
                indicadorEntrenoHoy.setVisibility(View.VISIBLE);
            } else {
                indicadorEntrenoHoy.setVisibility(View.GONE);
            }

            // Nombre completo
            textNombre.setText(alumno.getNombreCompleto());

            // Deporte principal con emoji
            String deporteTexto = obtenerEmojiDeporte(alumno.getDeportePrincipal()) + " " +
                    (alumno.getDeportePrincipal() != null ? alumno.getDeportePrincipal() : "Sin deporte");
            textDeportePrincipal.setText(deporteTexto);

            // Última actividad
            if (alumno.getUltimaActividad() != null) {
                textUltimaActividad.setText(alumno.getUltimaActividad());
            } else {
                textUltimaActividad.setText("Sin actividad");
            }

            // Badge de compromiso
            if (alumno.getNivelCompromiso() != null) {
                badgeCompromiso.setText(alumno.getNivelCompromiso().toUpperCase());

                // Color según nivel de compromiso
                if (alumno.getColorCompromiso() != null) {
                    try {
                        int color = Color.parseColor(alumno.getColorCompromiso());
                        badgeCompromiso.setBackgroundTintList(
                                android.content.res.ColorStateList.valueOf(color)
                        );
                    } catch (Exception e) {
                        // Color por defecto si falla el parsing
                        badgeCompromiso.setBackgroundTintList(
                                android.content.res.ColorStateList.valueOf(Color.parseColor("#6B7280"))
                        );
                    }
                }
            } else {
                badgeCompromiso.setVisibility(View.GONE);
            }

            // Métricas
            textTotalEntrenamientos.setText(String.valueOf(
                    alumno.getTotalEntrenamientos() != null ? alumno.getTotalEntrenamientos() : 0
            ));

            textRacha.setText(String.valueOf(
                    alumno.getRachaActual() != null ? alumno.getRachaActual() : 0
            ));

            textEntrenamientosMes.setText(String.valueOf(
                    alumno.getEntrenamientosMesActual() != null ? alumno.getEntrenamientosMesActual() : 0
            ));

            // Click listener
            cardView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onAlumnoClick(alumno);
                }
            });
        }

        /**
         * Obtiene el emoji correspondiente al deporte.
         */
        private String obtenerEmojiDeporte(String deporte) {
            if (deporte == null) return "⚽";

            switch (deporte.toLowerCase()) {
                case "fútbol":
                case "futbol":
                    return "⚽";
                case "basketball":
                case "basquetbol":
                    return "🏀";
                case "natación":
                case "natacion":
                    return "🏊";
                case "running":
                case "correr":
                    return "🏃";
                case "boxeo":
                    return "🥊";
                case "tenis":
                    return "🎾";
                case "gimnasio":
                case "gym":
                    return "💪";
                case "ciclismo":
                    return "🚴";
                case "béisbol":
                case "beisbol":
                    return "⚾";
                default:
                    return "⚽";
            }
        }
    }
}