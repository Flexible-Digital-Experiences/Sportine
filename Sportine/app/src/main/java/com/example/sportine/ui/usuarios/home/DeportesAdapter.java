package com.example.sportine.ui.usuarios.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sportine.R;

import java.util.List;

public class DeportesAdapter extends RecyclerView.Adapter<DeportesAdapter.DeporteViewHolder> {

    private List<String> deportes;

    public DeportesAdapter(List<String> deportes) {
        this.deportes = deportes;
    }

    @NonNull
    @Override
    public DeporteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_alumno_deporte_seccion, parent, false);
        return new DeporteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DeporteViewHolder holder, int position) {
        String deporte = deportes.get(position);

        TextView titulo = holder.itemView.findViewById(R.id.text_titulo_deporte);
        TextView nombreEntrenamiento = holder.itemView.findViewById(R.id.text_nombre_entrenamiento);
        TextView fecha = holder.itemView.findViewById(R.id.text_fecha);
        ImageView avatar = holder.itemView.findViewById(R.id.image_entrenador);

        // Configurar datos según el deporte
        titulo.setText(deporte);

        // Nombre de entrenamiento personalizado por deporte
        nombreEntrenamiento.setText(obtenerNombreEntrenamiento(deporte));

        // Fecha de ejemplo
        fecha.setText("20 Oct 2025, 16:00");

        // Avatar según el deporte
        avatar.setImageResource(obtenerImagenDeporte(deporte));
    }

    @Override
    public int getItemCount() {
        return deportes.size();
    }

    /**
     * Devuelve el recurso drawable correspondiente al deporte
     */
    private int obtenerImagenDeporte(String deporte) {
        switch (deporte.toLowerCase()) {
            case "fútbol":
            case "futbol":
                return R.drawable.balon_futbol;

            case "natación":
            case "natacion":
                return R.drawable.natacion_logo;

            case "beisbol":
                return R.drawable.pelota_beisbol;

            case "tenis":
                return R.drawable.pelota_tenis;

            case "boxeo":
                return R.drawable.guante_boxeo;

            case "básquetbol":
            case "basquetbol":
            case "basket":
                return R.drawable.balon_basket;

            default:
                return R.drawable.logo_sportine; // Imagen por defecto
        }
    }

    /**
     * Devuelve un nombre de entrenamiento personalizado según el deporte
     */
    private String obtenerNombreEntrenamiento(String deporte) {
        switch (deporte.toLowerCase()) {
            case "fútbol":
            case "futbol":
                return "Entrenamiento de resistencia y control";

            case "natación":
            case "natacion":
                return "Técnica de brazada y velocidad";

            case "beisbol":
                return "Practica de pitcheo";

            case "tenis":
                return "Práctica de saque y volea";

            case "boxeo":
                return "Técnica de golpeo y defensa";

            case "básquetbol":
            case "basquetbol":
            case "basket":
                return "Tiros y jugadas tácticas";

            default:
                return "Entrenamiento especializado";
        }
    }

    static class DeporteViewHolder extends RecyclerView.ViewHolder {
        public DeporteViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}