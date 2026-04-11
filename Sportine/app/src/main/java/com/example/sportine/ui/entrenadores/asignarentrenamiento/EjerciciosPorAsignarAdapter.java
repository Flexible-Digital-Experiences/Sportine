package com.example.sportine.ui.entrenadores.asignarentrenamiento;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sportine.R;
import com.example.sportine.models.AsignarEjercicioDTO;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;

public class EjerciciosPorAsignarAdapter extends RecyclerView.Adapter<EjerciciosPorAsignarAdapter.EjercicioViewHolder> {

    private List<AsignarEjercicioDTO> ejercicios = new ArrayList<>();
    private OnEliminarClickListener eliminarListener;

    public interface OnEliminarClickListener {
        void onEliminarClick(int position);
    }

    public void setOnEliminarClickListener(OnEliminarClickListener listener) {
        this.eliminarListener = listener;
    }

    @NonNull
    @Override
    public EjercicioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ejercicio_asignado, parent, false);
        return new EjercicioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EjercicioViewHolder holder, int position) {
        AsignarEjercicioDTO item = ejercicios.get(position);

        holder.txtNumero.setText(String.valueOf(position + 1));
        holder.txtNombre.setText(item.getNombreEjercicio());

        // Badge "⚽ Éxitos" — visible solo si tieneExitosos = true
        if (holder.chipTieneExitosos != null) {
            holder.chipTieneExitosos.setVisibility(
                    item.isTieneExitosos() ? View.VISIBLE : View.GONE);
        }

        // Series
        if (item.getSeries() != null && item.getSeries() > 0) {
            holder.layoutSeries.setVisibility(View.VISIBLE);
            holder.txtSeries.setText(item.getSeries() + " series");
        } else {
            holder.layoutSeries.setVisibility(View.GONE);
        }

        // Repeticiones
        if (item.getRepeticiones() != null && item.getRepeticiones() > 0) {
            holder.layoutReps.setVisibility(View.VISIBLE);
            holder.txtReps.setText(item.getRepeticiones() + " reps");
        } else {
            holder.layoutReps.setVisibility(View.GONE);
        }

        // Peso
        if (item.getPeso() != null && item.getPeso() > 0) {
            holder.layoutPeso.setVisibility(View.VISIBLE);
            holder.txtPeso.setText(item.getPeso() + " kg");
        } else {
            holder.layoutPeso.setVisibility(View.GONE);
        }

        // Duración
        if (item.getDuracion() != null && item.getDuracion() > 0) {
            holder.layoutDuracion.setVisibility(View.VISIBLE);
            holder.txtDuracion.setText(item.getDuracion() + " min");
        } else {
            holder.layoutDuracion.setVisibility(View.GONE);
        }

        // Distancia
        if (item.getDistancia() != null && item.getDistancia() > 0) {
            holder.layoutDistancia.setVisibility(View.VISIBLE);
            holder.txtDistancia.setText(item.getDistancia() + " km");
        } else {
            holder.layoutDistancia.setVisibility(View.GONE);
        }

        holder.btnEliminar.setOnClickListener(v -> {
            if (eliminarListener != null) eliminarListener.onEliminarClick(holder.getAdapterPosition());
        });
    }

    @Override
    public int getItemCount() { return ejercicios.size(); }

    public void setEjercicios(List<AsignarEjercicioDTO> lista) {
        this.ejercicios = lista;
        notifyDataSetChanged();
    }

    public void agregarEjercicio(AsignarEjercicioDTO ejercicio) {
        this.ejercicios.add(ejercicio);
        notifyItemInserted(ejercicios.size() - 1);
        notifyItemRangeChanged(0, ejercicios.size());
    }

    public void eliminarEjercicio(int position) {
        if (position >= 0 && position < ejercicios.size()) {
            this.ejercicios.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, ejercicios.size());
        }
    }

    public List<AsignarEjercicioDTO> getEjercicios() { return ejercicios; }

    static class EjercicioViewHolder extends RecyclerView.ViewHolder {
        TextView txtNumero, txtNombre;
        TextView txtSeries, txtReps, txtPeso, txtDuracion, txtDistancia;
        LinearLayout layoutSeries, layoutReps, layoutPeso, layoutDuracion, layoutDistancia;
        ImageButton btnEliminar;
        Chip chipTieneExitosos; // ← NUEVO

        public EjercicioViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNumero   = itemView.findViewById(R.id.text_numero_ejercicio);
            txtNombre   = itemView.findViewById(R.id.text_nombre_ejercicio);
            btnEliminar = itemView.findViewById(R.id.btn_eliminar);

            layoutSeries   = itemView.findViewById(R.id.layout_series);
            layoutReps     = itemView.findViewById(R.id.layout_repeticiones);
            layoutPeso     = itemView.findViewById(R.id.layout_peso);
            layoutDuracion = itemView.findViewById(R.id.layout_duracion);
            layoutDistancia= itemView.findViewById(R.id.layout_distancia);

            txtSeries    = itemView.findViewById(R.id.text_series);
            txtReps      = itemView.findViewById(R.id.text_repeticiones);
            txtPeso      = itemView.findViewById(R.id.text_peso);
            txtDuracion  = itemView.findViewById(R.id.text_duracion);
            txtDistancia = itemView.findViewById(R.id.text_distancia);

            chipTieneExitosos = itemView.findViewById(R.id.chip_tiene_exitosos); // ← NUEVO
        }
    }
}