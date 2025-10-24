package com.example.sportine.ui.usuarios.detallesentrenamiento;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sportine.R;

import java.util.List;

public class EjerciciosAdapter extends RecyclerView.Adapter<EjerciciosAdapter.EjercicioViewHolder> {

    private List<Ejercicio> ejercicios;

    public EjerciciosAdapter(List<Ejercicio> ejercicios) {
        this.ejercicios = ejercicios;
    }

    @NonNull
    @Override
    public EjercicioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ejercicio, parent, false);
        return new EjercicioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EjercicioViewHolder holder, int position) {
        Ejercicio ejercicio = ejercicios.get(position);

        holder.checkBox.setChecked(ejercicio.isCompletado());
        holder.nombreEjercicio.setText(ejercicio.getNombre());
        holder.descripcionEjercicio.setText(ejercicio.getDescripcion());
        holder.duracion.setText(ejercicio.getDuracion());

        // Manejar el click del checkbox
        holder.checkBox.setOnClickListener(v -> {
            ejercicio.setCompletado(holder.checkBox.isChecked());
        });

        // También permitir click en toda la tarjeta
        holder.itemView.setOnClickListener(v -> {
            ejercicio.setCompletado(!ejercicio.isCompletado());
            holder.checkBox.setChecked(ejercicio.isCompletado());
        });
    }

    @Override
    public int getItemCount() {
        return ejercicios.size();
    }

    static class EjercicioViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        TextView nombreEjercicio;
        TextView descripcionEjercicio;
        TextView duracion;

        public EjercicioViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkbox_ejercicio);
            nombreEjercicio = itemView.findViewById(R.id.text_nombre_ejercicio);
            descripcionEjercicio = itemView.findViewById(R.id.text_descripcion_ejercicio);
            duracion = itemView.findViewById(R.id.text_duracion);
        }
    }

    // Clase modelo para los ejercicios
    public static class Ejercicio {
        private String nombre;
        private String descripcion;
        private String duracion;
        private boolean completado;

        public Ejercicio(String nombre, String descripcion, String duracion) {
            this.nombre = nombre;
            this.descripcion = descripcion;
            this.duracion = duracion;
            this.completado = false;
        }

        public String getNombre() {
            return nombre;
        }

        public String getDescripcion() {
            return descripcion;
        }

        public String getDuracion() {
            return duracion;
        }

        public boolean isCompletado() {
            return completado;
        }

        public void setCompletado(boolean completado) {
            this.completado = completado;
        }
    }
}
