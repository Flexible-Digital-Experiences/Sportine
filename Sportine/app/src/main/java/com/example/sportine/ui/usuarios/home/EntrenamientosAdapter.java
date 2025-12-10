package com.example.sportine.ui.usuarios.home;

import android.content.Context;
import android.graphics.Color; // ✅ Importante para usar Color.BLACK y Color.WHITE
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sportine.R;
import com.example.sportine.models.EntrenamientoDelDiaDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter OPTIMIZADO con corrección de colores en badges
 */
public class EntrenamientosAdapter extends RecyclerView.Adapter<EntrenamientosAdapter.EntrenamientoViewHolder> {

    private final Context context;
    private List<EntrenamientoDelDiaDTO> entrenamientos;
    private OnEntrenamientoClickListener listener;

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
        private final ImageView imgIcono;
        private final TextView textTitulo;
        private final TextView textObjetivo;
        private final TextView textHora;
        private final TextView textDificultad;
        private final TextView badgeEstado;

        public EntrenamientoViewHolder(@NonNull View itemView) {
            super(itemView);
            // IDs correspondientes a item_alumno_entrenamiento.xml
            cardEntrenamiento = itemView.findViewById(R.id.card_entrenamiento);
            imgIcono = itemView.findViewById(R.id.img_icono_deporte);
            textTitulo = itemView.findViewById(R.id.text_titulo_entrenamiento);
            textObjetivo = itemView.findViewById(R.id.text_objetivo);
            textHora = itemView.findViewById(R.id.text_hora);
            textDificultad = itemView.findViewById(R.id.text_dificultad);
            badgeEstado = itemView.findViewById(R.id.badge_estado);
        }

        public void bind(EntrenamientoDelDiaDTO entrenamiento) {
            // Título
            textTitulo.setText(entrenamiento.getTitulo());

            // --- LÓGICA DE ICONOS (ESTILO SWITCH) ---
            String categoria = normalizarDeporte(entrenamiento.getTitulo());

            switch (categoria) {
                case "Fútbol":
                    imgIcono.setImageResource(R.drawable.balon_futbol);
                    break;
                case "Basketball":
                    imgIcono.setImageResource(R.drawable.balon_basket);
                    break;
                case "Natación":
                    imgIcono.setImageResource(R.drawable.ic_natacion);
                    break;
                case "Boxeo":
                    imgIcono.setImageResource(R.drawable.ic_boxeo);
                    break;
                case "Tenis":
                    imgIcono.setImageResource(R.drawable.pelota_tenis);
                    break;
                case "Béisbol":
                    imgIcono.setImageResource(R.drawable.ic_beisbol);
                    break;
                case "Running":
                    imgIcono.setImageResource(R.drawable.ic_running);
                    break;
                case "Gimnasio":
                    imgIcono.setImageResource(R.drawable.ic_gimnasio);
                    break;
                case "Ciclismo":
                    imgIcono.setImageResource(R.drawable.ic_ciclismo);
                    break;
                default:
                    imgIcono.setImageResource(R.drawable.ic_deporte_default);
                    break;
            }

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

            // Dificultad y Estado
            configurarDificultad(entrenamiento.getDificultad());
            configurarBadgeEstado(entrenamiento.getEstadoEntrenamiento());

            // Click listener
            cardEntrenamiento.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEntrenamientoClick(entrenamiento);
                }
            });
        }

        private String normalizarDeporte(String titulo) {
            if (titulo == null) return "Desconocido";
            String t = titulo.toLowerCase();

            if (t.contains("futbol") || t.contains("fútbol") || t.contains("golpeo") || t.contains("balón") || t.contains("partido")) {
                return "Fútbol";
            }
            if (t.contains("basket") || t.contains("tiro") || t.contains("dribbling")) {
                return "Basketball";
            }
            if (t.contains("natación") || t.contains("agua") || t.contains("estilo libre")) {
                return "Natación";
            }
            if (t.contains("box") || t.contains("saco") || t.contains("round")) {
                return "Boxeo";
            }
            if (t.contains("tenis") || t.contains("raqueta") || t.contains("set")) {
                return "Tenis";
            }
            if (t.contains("beisbol") || t.contains("béisbol") || t.contains("bateo")) {
                return "Béisbol";
            }
            if (t.contains("run") || t.contains("correr") || t.contains("5k") || t.contains("velocidad")) {
                return "Running";
            }
            if (t.contains("gym") || t.contains("gimnasio") || t.contains("pesas") || t.contains("fuerza") || t.contains("brazo") || t.contains("pierna") || t.contains("pecho")) {
                return "Gimnasio";
            }
            if (t.contains("ciclismo") || t.contains("bici")) {
                return "Ciclismo";
            }

            return "Desconocido";
        }

        private void configurarDificultad(String dificultad) {
            if (dificultad == null) {
                textDificultad.setVisibility(View.GONE);
                return;
            }
            textDificultad.setVisibility(View.VISIBLE);

            switch (dificultad.toLowerCase()) {
                case "facil": textDificultad.setText("Fácil"); break;
                case "media": textDificultad.setText("Media"); break;
                case "dificil": textDificultad.setText("Difícil"); break;
                default: textDificultad.setText(dificultad); break;
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
                    // Cambio: Texto negro para contrastar con el amarillo/crema
                    badgeEstado.setTextColor(Color.WHITE);
                    break;
                case "en_progreso":
                    badgeEstado.setText("En progreso");
                    badgeEstado.setBackgroundResource(R.drawable.badge_casi_lleno);
                    // Reset a blanco (importante por el reciclaje de vistas)
                    badgeEstado.setTextColor(Color.BLACK);
                    break;
                case "finalizado":
                    badgeEstado.setText("Completado");
                    badgeEstado.setBackgroundResource(R.drawable.badge_disponible);
                    // Reset a blanco
                    badgeEstado.setTextColor(Color.WHITE);
                    break;
                default:
                    badgeEstado.setText(estado);
                    badgeEstado.setTextColor(Color.WHITE);
                    break;
            }
        }
    }
}