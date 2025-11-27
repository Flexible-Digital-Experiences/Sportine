package com.example.sportine.ui.usuarios.detallesEntrenador;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sportine.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeportesAdapter extends RecyclerView.Adapter<DeportesAdapter.DeporteViewHolder> {

    private List<String> deportes = new ArrayList<>();

    // Mapa de iconos por deporte
    private static final Map<String, Integer> DEPORTE_ICONS = new HashMap<String, Integer>() {{
        put("Fútbol", R.drawable.ic_ensena); // Reemplaza con tus iconos
        put("Basketball", R.drawable.ic_ensena);
        put("Natación", R.drawable.ic_ensena);
        put("Running", R.drawable.ic_ensena);
        put("Boxeo", R.drawable.ic_ensena);
        put("Tenis", R.drawable.ic_ensena);
        put("Gimnasio", R.drawable.ic_ensena);
        put("Ciclismo", R.drawable.ic_ensena);
        put("Béisbol", R.drawable.ic_ensena);
    }};

    public void setDeportes(List<String> deportes) {
        this.deportes = deportes != null ? deportes : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DeporteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_alumno_deporte_card, parent, false);
        return new DeporteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DeporteViewHolder holder, int position) {
        String deporte = deportes.get(position);
        holder.tvDeporte.setText(deporte);

        // Asignar icono según el deporte
        Integer iconRes = DEPORTE_ICONS.get(deporte);
        if (iconRes != null) {
            holder.imgDeporte.setImageResource(iconRes);
        } else {
            holder.imgDeporte.setImageResource(R.drawable.ic_ensena); // Icono por defecto
        }
    }

    @Override
    public int getItemCount() {
        return deportes.size();
    }

    static class DeporteViewHolder extends RecyclerView.ViewHolder {
        ImageView imgDeporte;
        TextView tvDeporte;

        public DeporteViewHolder(@NonNull View itemView) {
            super(itemView);
            imgDeporte = itemView.findViewById(R.id.img_deporte);
            tvDeporte = itemView.findViewById(R.id.text_nombre_deporte);
        }
    }
}
