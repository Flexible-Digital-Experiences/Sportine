package com.example.sportine.ui.usuarios.detallesEntrenador;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sportine.R;
import com.example.sportine.models.EstadoRelacionDTO;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;

public class RelacionDeporteAdapter extends RecyclerView.Adapter<RelacionDeporteAdapter.ViewHolder> {

    public interface OnRelacionActionListener {
        void onPagar(EstadoRelacionDTO.RelacionDeporteDTO relacion);
        void onCancelar(EstadoRelacionDTO.RelacionDeporteDTO relacion);
    }

    private List<EstadoRelacionDTO.RelacionDeporteDTO> relaciones = new ArrayList<>();
    private OnRelacionActionListener listener;

    public RelacionDeporteAdapter(OnRelacionActionListener listener) {
        this.listener = listener;
    }

    public void setRelaciones(List<EstadoRelacionDTO.RelacionDeporteDTO> relaciones) {
        this.relaciones = relaciones != null ? relaciones : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_relacion_deporte, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        EstadoRelacionDTO.RelacionDeporteDTO relacion = relaciones.get(position);
        boolean esActivo = "activo".equals(relacion.getStatusRelacion());
        boolean suscripcionCancelada = "cancelled".equals(relacion.getStatusSuscripcion());

        holder.tvNombreDeporte.setText(relacion.getNombreDeporte());

        if (esActivo) {
            holder.cardIconoDeporte.setCardBackgroundColor(
                    holder.itemView.getContext().getColor(R.color.paypal_status_completed_bg));
            holder.chipEstado.setText("Activo");
            holder.chipEstado.setChipBackgroundColorResource(R.color.paypal_status_completed_bg);
            holder.tvStatusLabel.setText("Entrenamiento activo");
            holder.tvStatusLabel.setTextColor(
                    holder.itemView.getContext().getColor(R.color.paypal_status_completed_text));

            holder.layoutFechaVencimiento.setVisibility(View.VISIBLE);
            if (relacion.getFinMensualidad() != null) {
                holder.tvFechaVencimiento.setText(formatearFecha(relacion.getFinMensualidad()));
            } else {
                holder.tvFechaVencimiento.setText("Sin fecha");
            }

            holder.btnPagar.setVisibility(View.GONE);
            holder.btnCancelar.setText("Cancelar");

        } else {
            holder.cardIconoDeporte.setCardBackgroundColor(
                    holder.itemView.getContext().getColor(R.color.paypal_status_pending_bg));
            holder.chipEstado.setText("Pendiente de pago");
            holder.chipEstado.setChipBackgroundColorResource(R.color.paypal_status_pending_bg);
            holder.tvStatusLabel.setText("Pago pendiente");
            holder.tvStatusLabel.setTextColor(
                    holder.itemView.getContext().getColor(R.color.paypal_status_pending_text));

            holder.layoutFechaVencimiento.setVisibility(View.GONE);
            holder.btnPagar.setVisibility(View.VISIBLE);
            holder.btnCancelar.setText("Cancelar");
        }

        // Aplicar estado cancelado AL FINAL para no ser sobreescrito
        holder.btnCancelar.setEnabled(!suscripcionCancelada);
        holder.btnCancelar.setAlpha(suscripcionCancelada ? 0.4f : 1.0f);
        if (suscripcionCancelada) {
            holder.btnCancelar.setText("Cancelada");
        }

        holder.btnPagar.setOnClickListener(v -> {
            if (listener != null) listener.onPagar(relacion);
        });

        holder.btnCancelar.setOnClickListener(v -> {
            if (listener != null) listener.onCancelar(relacion);
        });
    }

    private String formatearFecha(String fecha) {
        // fecha viene como "2026-04-17"
        try {
            String[] partes = fecha.split("-");
            String[] meses = {"", "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
                    "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};
            int mes = Integer.parseInt(partes[1]);
            return partes[2] + " de " + meses[mes] + ", " + partes[0];
        } catch (Exception e) {
            return fecha;
        }
    }

    @Override
    public int getItemCount() {
        return relaciones.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardIconoDeporte;
        TextView tvNombreDeporte;
        TextView tvStatusLabel;
        Chip chipEstado;
        LinearLayout layoutFechaVencimiento;
        TextView tvFechaVencimiento;
        MaterialButton btnPagar;
        MaterialButton btnCancelar;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardIconoDeporte     = itemView.findViewById(R.id.card_icono_deporte);
            tvNombreDeporte      = itemView.findViewById(R.id.tv_nombre_deporte);
            tvStatusLabel        = itemView.findViewById(R.id.tv_status_label);
            chipEstado           = itemView.findViewById(R.id.chip_estado);
            layoutFechaVencimiento = itemView.findViewById(R.id.layout_fecha_vencimiento);
            tvFechaVencimiento   = itemView.findViewById(R.id.tv_fecha_vencimiento);
            btnPagar             = itemView.findViewById(R.id.btn_pagar_deporte);
            btnCancelar          = itemView.findViewById(R.id.btn_cancelar_deporte);
        }
    }
}