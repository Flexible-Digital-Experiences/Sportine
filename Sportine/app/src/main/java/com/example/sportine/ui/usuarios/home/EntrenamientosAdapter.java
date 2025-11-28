package com.example.sportine.ui.usuarios.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sportine.R;
import com.example.sportine.models.EntrenamientoDelDiaDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter SIMPLIFICADO para mostrar la lista de entrenamientos del alumno
 * Usa solo los elementos que ya existen en tu XML
 */
public class EntrenamientosAdapter extends RecyclerView.Adapter<EntrenamientosAdapter.EntrenamientoViewHolder> {

    private final Context context;
    private List<EntrenamientoDelDiaDTO> entrenamientos;
    private OnEntrenamientoClickListener listener;

    // Interface para manejar clicks
    public interface OnEntrenamientoClickListener {
        void onEntrenamientoClick(EntrenamientoDelDiaDTO entrenamiento);
    }

    public EntrenamientosAdapter(Context context) {
        this.context = context;
        this.entrenamientos = new ArrayList<>();
    }

    public void setOnEntrenamientoClickListener(OnEntrenamientoClickListener listener) {
        this.listener = listener;
    }

    public void setEntrenamientos(List<EntrenamientoDelDiaDTO> entrenamientos) {
        this.entrenamientos = entrenamientos != null ? entrenamientos : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public EntrenamientoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(
                R.layout.item_alumno_entrenamiento,
                parent,
                false
        );
        return new EntrenamientoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EntrenamientoViewHolder holder, int position) {
        EntrenamientoDelDiaDTO entrenamiento = entrenamientos.get(position);
        holder.bind(entrenamiento);
    }

    @Override
    public int getItemCount() {
        return entrenamientos.size();
    }

    class EntrenamientoViewHolder extends RecyclerView.ViewHolder {

        private final CardView cardEntrenamiento;
        private final TextView textTitulo;
        private final TextView textObjetivo;
        private final TextView textHora;
        private final TextView textDificultad;
        private final TextView badgeEstado;

        public EntrenamientoViewHolder(@NonNull View itemView) {
            super(itemView);

            // SOLO los IDs que existen en tu XML
            cardEntrenamiento = itemView.findViewById(R.id.card_entrenamiento);
            textTitulo = itemView.findViewById(R.id.text_titulo_entrenamiento);
            textObjetivo = itemView.findViewById(R.id.text_objetivo);
            textHora = itemView.findViewById(R.id.text_hora);
            textDificultad = itemView.findViewById(R.id.text_dificultad);
            badgeEstado = itemView.findViewById(R.id.badge_estado);
        }

        public void bind(EntrenamientoDelDiaDTO entrenamiento) {
            // Título
            textTitulo.setText(entrenamiento.getTitulo());

            // Objetivo
            if (entrenamiento.getObjetivo() != null && !entrenamiento.getObjetivo().isEmpty()) {
                textObjetivo.setText(entrenamiento.getObjetivo());
                textObjetivo.setVisibility(View.VISIBLE);
            } else {
                textObjetivo.setVisibility(View.GONE);
            }

            // Hora
            if (entrenamiento.getHoraEntrenamiento() != null) {
                textHora.setText(entrenamiento.getHoraEntrenamiento());
            }

            // Dificultad
            configurarDificultad(entrenamiento.getDificultad());

            // Badge de Estado
            configurarBadgeEstado(entrenamiento.getEstadoEntrenamiento());

            // Click listener
            cardEntrenamiento.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEntrenamientoClick(entrenamiento);
                }
            });
        }

        private void configurarDificultad(String dificultad) {
            if (dificultad == null) {
                textDificultad.setVisibility(View.GONE);
                return;
            }

            textDificultad.setVisibility(View.VISIBLE);

            switch (dificultad.toLowerCase()) {
                case "facil":
                    textDificultad.setText("Fácil");
                    break;
                case "media":
                    textDificultad.setText("Media");
                    break;
                case "dificil":
                    textDificultad.setText("Difícil");
                    break;
                default:
                    textDificultad.setText(dificultad);
                    break;
            }
        }

        private void configurarBadgeEstado(String estado) {
            if (estado == null) {
                badgeEstado.setVisibility(View.GONE);
                return;
            }

            badgeEstado.setVisibility(View.VISIBLE);

            switch (estado.toLowerCase()) {
                case "pendiente":
                    badgeEstado.setText("Pendiente");
                    badgeEstado.setBackgroundResource(R.drawable.bg_badge_rounded);
                    break;
                case "en_progreso":
                    badgeEstado.setText("En progreso");
                    badgeEstado.setBackgroundResource(R.drawable.badge_casi_lleno);
                    break;
                case "finalizado":
                    badgeEstado.setText("Completado");
                    badgeEstado.setBackgroundResource(R.drawable.badge_disponible);
                    break;
                default:
                    badgeEstado.setText(estado);
                    break;
            }
        }
    }
}