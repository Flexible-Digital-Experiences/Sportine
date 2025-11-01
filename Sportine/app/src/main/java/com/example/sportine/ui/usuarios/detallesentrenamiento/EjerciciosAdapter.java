package com.example.sportine.ui.usuarios.detallesentrenamiento;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sportine.R;

public class EjerciciosAdapter extends RecyclerView.Adapter<EjerciciosAdapter.EjercicioViewHolder> {

    @NonNull
    @Override
    public EjercicioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ejercicio, parent, false);
        return new EjercicioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EjercicioViewHolder holder, int position) {
        // La lógica para vincular los datos del ejercicio a la vista irá aquí cuando conectes el backend.
    }

    @Override
    public int getItemCount() {
        // Devolverá 0 hasta que se le pasen ejercicios.
        return 0;
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
}
