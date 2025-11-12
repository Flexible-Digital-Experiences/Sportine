package com.example.sportine.ui.entrenadores.home;

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

public class AlumnosAdapter extends RecyclerView.Adapter<AlumnosAdapter.AlumnoViewHolder> {

    private List<Object> alumnos = new ArrayList<>(); // Usaremos Object por ahora

    @NonNull
    @Override
    public AlumnoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_entrenador_alumno, parent, false);
        return new AlumnoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlumnoViewHolder holder, int position) {
        // Aquí se enlazarán los datos del alumno a la vista
        // holder.nombre.setText(alumnos.get(position).getNombre());
    }

    @Override
    public int getItemCount() {
        return alumnos.size();
    }

    // Método para actualizar la lista de alumnos desde el ViewModel
    public void setAlumnos(List<Object> nuevosAlumnos) {
        this.alumnos = nuevosAlumnos;
        notifyDataSetChanged();
    }

    static class AlumnoViewHolder extends RecyclerView.ViewHolder {
        ImageView avatar;
        TextView nombre;

        public AlumnoViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.img_alumno_avatar);
            nombre = itemView.findViewById(R.id.text_alumno_nombre);
        }
    }
}
