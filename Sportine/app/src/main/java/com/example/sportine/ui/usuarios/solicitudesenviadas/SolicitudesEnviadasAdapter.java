package com.example.sportine.ui.usuarios.solicitudesenviadas;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.sportine.R;
import com.example.sportine.models.SolicitudEnviadaDTO;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class SolicitudesEnviadasAdapter extends RecyclerView.Adapter<SolicitudesEnviadasAdapter.SolicitudViewHolder> {

    private List<SolicitudEnviadaDTO> solicitudes = new ArrayList<>();
    private OnSolicitudClickListener listener;

    public interface OnSolicitudClickListener {
        void onSolicitudClick(SolicitudEnviadaDTO solicitud);
        void onEliminarClick(SolicitudEnviadaDTO solicitud, int position);
    }

    public SolicitudesEnviadasAdapter(OnSolicitudClickListener listener) {
        this.listener = listener;
    }

    public void setSolicitudes(List<SolicitudEnviadaDTO> solicitudes) {
        this.solicitudes = solicitudes != null ? solicitudes : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void eliminarSolicitud(int position) {
        if (position >= 0 && position < solicitudes.size()) {
            solicitudes.remove(position);
            notifyItemRemoved(position);
        }
    }

    @NonNull
    @Override
    public SolicitudViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_solicitud_enviada, parent, false);
        return new SolicitudViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SolicitudViewHolder holder, int position) {
        SolicitudEnviadaDTO solicitud = solicitudes.get(position);

        // Foto del entrenador
        if (solicitud.getFotoEntrenador() != null && !solicitud.getFotoEntrenador().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(solicitud.getFotoEntrenador())
                    .placeholder(R.drawable.avatar_user_male)
                    .error(R.drawable.avatar_user_male)
                    .circleCrop()
                    .into(holder.imgEntrenador);
        } else {
            holder.imgEntrenador.setImageResource(R.drawable.avatar_user_male);
        }

        // Nombre del entrenador
        holder.tvNombreEntrenador.setText(solicitud.getNombreEntrenador());

        // Deporte
        holder.tvDeporte.setText(solicitud.getNombreDeporte());

        // Fecha
        holder.tvFecha.setText("Enviada: " + solicitud.getFechaSolicitud());

        // Estado y color de la tarjeta según el status
        String status = solicitud.getStatusSolicitud();

        if ("En_revisión".equals(status)) {
            holder.tvEstado.setText("En revisión");
            holder.tvEstado.setTextColor(holder.itemView.getContext().getColor(R.color.purple_200));
            holder.card.setCardBackgroundColor(holder.itemView.getContext().getColor(R.color.purple_200));
            holder.btnEliminar.setVisibility(View.VISIBLE); // Mostrar botón eliminar
        } else if ("Aprobada".equals(status)) {
            holder.tvEstado.setText("Aprobada ✓");
            holder.tvEstado.setTextColor(holder.itemView.getContext().getColor(R.color.teal_200));
            holder.card.setCardBackgroundColor(holder.itemView.getContext().getColor(R.color.teal_200));
            holder.btnEliminar.setVisibility(View.GONE); // Ocultar botón eliminar
        } else if ("Rechazada".equals(status)) {
            holder.tvEstado.setText("Rechazada");
            holder.tvEstado.setTextColor(holder.itemView.getContext().getColor(R.color.sportine_accent));
            holder.card.setCardBackgroundColor(holder.itemView.getContext().getColor(R.color.sportine_accent));
            holder.btnEliminar.setVisibility(View.VISIBLE); // Mostrar botón eliminar
        }

        // Click en la tarjeta - navegar a detalles
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSolicitudClick(solicitud);
            }
        });

        // Click en botón eliminar
        holder.btnEliminar.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEliminarClick(solicitud, holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return solicitudes.size();
    }

    static class SolicitudViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView card;
        ImageView imgEntrenador;
        TextView tvNombreEntrenador;
        TextView tvDeporte;
        TextView tvEstado;
        TextView tvFecha;
        ImageButton btnEliminar;

        public SolicitudViewHolder(@NonNull View itemView) {
            super(itemView);
            card = itemView.findViewById(R.id.card_solicitud);
            imgEntrenador = itemView.findViewById(R.id.img_entrenador);
            tvNombreEntrenador = itemView.findViewById(R.id.tv_nombre_entrenador);
            tvDeporte = itemView.findViewById(R.id.tv_deporte);
            tvEstado = itemView.findViewById(R.id.tv_estado);
            tvFecha = itemView.findViewById(R.id.tv_fecha);
            btnEliminar = itemView.findViewById(R.id.btn_eliminar);
        }
    }
}