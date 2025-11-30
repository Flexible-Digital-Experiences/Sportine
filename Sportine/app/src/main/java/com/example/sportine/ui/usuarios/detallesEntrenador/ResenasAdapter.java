package com.example.sportine.ui.usuarios.detallesEntrenador;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.sportine.R;
import com.example.sportine.models.ResenaDTO;

import java.util.ArrayList;
import java.util.List;

public class ResenasAdapter extends RecyclerView.Adapter<ResenasAdapter.ResenaViewHolder> {

    private List<ResenaDTO> resenas = new ArrayList<>();

    public void setResenas(List<ResenaDTO> resenas) {
        this.resenas = resenas != null ? resenas : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ResenaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_alumno_resenaentre, parent, false);
        return new ResenaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ResenaViewHolder holder, int position) {
        ResenaDTO resena = resenas.get(position);

        // Nombre del alumno
        holder.tvNombre.setText(resena.getNombreAlumno());

        // Calificación
        holder.ratingBar.setRating(resena.getRatingDado());

        // Comentario
        holder.tvComentario.setText(resena.getComentario());

        // Foto del alumno
        if (resena.getFotoAlumno() != null && !resena.getFotoAlumno().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(resena.getFotoAlumno())
                    .placeholder(R.drawable.avatar_user_male)
                    .error(R.drawable.avatar_user_male)
                    .circleCrop()
                    .into(holder.imgPerfil);
        } else {
            holder.imgPerfil.setImageResource(R.drawable.avatar_user_male);
        }
    }

    @Override
    public int getItemCount() {
        return resenas.size();
    }

    static class ResenaViewHolder extends RecyclerView.ViewHolder {
        ImageView imgPerfil;
        TextView tvNombre;
        RatingBar ratingBar;
        TextView tvComentario;

        public ResenaViewHolder(@NonNull View itemView) {
            super(itemView);
            imgPerfil = itemView.findViewById(R.id.image_entrenador);
            tvNombre = itemView.findViewById(R.id.text_nombre_entrenador);
            ratingBar = itemView.findViewById(R.id.rating_entrenador);
            tvComentario = itemView.findViewById(R.id.text_resenatexto);
        }
    }
}