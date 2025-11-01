package com.example.sportine.ui.usuarios.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sportine.R;

import java.util.ArrayList;
import java.util.List;

public class DeportesAdapter extends RecyclerView.Adapter<DeportesAdapter.DeporteViewHolder> {

    private List<String> deportes = new ArrayList<>();
    private final OnDeporteClickListener listener;

    // Interfaz para manejar clicks
    public interface OnDeporteClickListener {
        void onDeporteClick(String deporte, String tituloEntrenamiento);
    }

    public DeportesAdapter(OnDeporteClickListener listener) {
        this.listener = listener;
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

        // Los datos se llenarán desde el backend
        holder.titulo.setText(deporte);
        holder.nombreEntrenamiento.setText(""); // Se llenará desde el backend
        holder.fecha.setText(""); // Se llenará desde el backend
        // holder.avatar.setImageResource(R.drawable.avatar_default); // Imagen por defecto

        // Configurar el click en toda la tarjeta
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                // El título del entrenamiento vendrá del backend en el futuro.
                listener.onDeporteClick(deporte, "");
            }
        });
    }

    @Override
    public int getItemCount() {
        return deportes.size();
    }

    // Método para actualizar los datos del adaptador
    public void setDeportes(List<String> nuevosDeportes) {
        this.deportes = nuevosDeportes;
        notifyDataSetChanged(); // Se puede optimizar con DiffUtil
    }

    static class DeporteViewHolder extends RecyclerView.ViewHolder {
        TextView titulo;
        TextView nombreEntrenamiento;
        TextView fecha;
        ImageView avatar;

        public DeporteViewHolder(@NonNull View itemView) {
            super(itemView);
            titulo = itemView.findViewById(R.id.text_titulo_deporte);
            nombreEntrenamiento = itemView.findViewById(R.id.text_nombre_entrenamiento);
            fecha = itemView.findViewById(R.id.text_fecha);
            avatar = itemView.findViewById(R.id.image_entrenador);
        }
    }
}
