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

        // Datos de ejemplo para frontend
        titulo.setText(deporte);
        nombreEntrenamiento.setText("Entrenamiento de ejemplo");
        fecha.setText("20 Oct 2025, 16:00");

        // Avatar de ejemplo (usa tu drawable)
        avatar.setImageResource(R.drawable.logo_sportine); // reemplaza si quieres otro avatar
    }

    @Override
    public int getItemCount() {
        return deportes.size();
    }

    static class DeporteViewHolder extends RecyclerView.ViewHolder {
        public DeporteViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
