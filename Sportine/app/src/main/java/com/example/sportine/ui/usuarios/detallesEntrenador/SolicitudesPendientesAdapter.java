package com.example.sportine.ui.usuarios.detallesEntrenador;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sportine.R;
import com.example.sportine.models.SolicitudDetalleDTO;

import java.util.ArrayList;
import java.util.List;

public class SolicitudesPendientesAdapter extends RecyclerView.Adapter<SolicitudesPendientesAdapter.SolicitudViewHolder> {

    private List<SolicitudDetalleDTO> solicitudes = new ArrayList<>();

    public void setSolicitudes(List<SolicitudDetalleDTO> solicitudes) {
        this.solicitudes = solicitudes != null ? solicitudes : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SolicitudViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_solicitud_pendiente, parent, false);
        return new SolicitudViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SolicitudViewHolder holder, int position) {
        SolicitudDetalleDTO solicitud = solicitudes.get(position);

        holder.tvDeporte.setText(solicitud.getNombreDeporte());
        holder.tvFecha.setText("Enviada: " + solicitud.getFechaSolicitud());
        holder.tvMotivo.setText(solicitud.getMotivo());
    }

    @Override
    public int getItemCount() {
        return solicitudes.size();
    }

    static class SolicitudViewHolder extends RecyclerView.ViewHolder {
        TextView tvDeporte;
        TextView tvFecha;
        TextView tvMotivo;

        public SolicitudViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDeporte = itemView.findViewById(R.id.text_deporte_solicitud);
            tvFecha = itemView.findViewById(R.id.text_fecha_solicitud);
            tvMotivo = itemView.findViewById(R.id.text_motivo_solicitud);
        }
    }
}