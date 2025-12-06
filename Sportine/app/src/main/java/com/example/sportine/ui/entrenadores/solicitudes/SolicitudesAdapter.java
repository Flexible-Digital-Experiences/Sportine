package com.example.sportine.ui.entrenadores.solicitudes;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.sportine.R;
import com.example.sportine.models.SolicitudEntrenadorDTO;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SolicitudesAdapter extends RecyclerView.Adapter<SolicitudesAdapter.ViewHolder> {

    private List<SolicitudEntrenadorDTO> solicitudes = new ArrayList<>();
    private Set<Integer> solicitudesSeleccionadas = new HashSet<>();
    private OnSolicitudClickListener listener;
    private boolean mostrarCheckbox = true;

    public interface OnSolicitudClickListener {
        void onSolicitudClick(SolicitudEntrenadorDTO solicitud);
    }

    public void setSolicitudes(List<SolicitudEntrenadorDTO> solicitudes) {
        this.solicitudes = solicitudes != null ? solicitudes : new ArrayList<>();
        this.solicitudesSeleccionadas.clear();
        notifyDataSetChanged();
    }

    public void setListener(OnSolicitudClickListener listener) {
        this.listener = listener;
    }

    public void setMostrarCheckbox(boolean mostrar) {
        this.mostrarCheckbox = mostrar;
        notifyDataSetChanged();
    }

    public Set<Integer> getSolicitudesSeleccionadas() {
        return solicitudesSeleccionadas;
    }

    public void clearSelections() {
        solicitudesSeleccionadas.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_solicitud_alumno, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SolicitudEntrenadorDTO solicitud = solicitudes.get(position);
        holder.bind(solicitud);
    }

    @Override
    public int getItemCount() {
        return solicitudes.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private CheckBox checkboxSelect;
        private ImageView imageAlumno;
        private ImageView ivDeporteIcon;
        private TextView tvNombre;
        private Chip chipEdad;
        private TextView tvTiempo;
        private TextView tvDescripcion;
        private TextView tvExpandir;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            checkboxSelect = itemView.findViewById(R.id.checkboxSelect);
            imageAlumno = itemView.findViewById(R.id.image_entrenador);
            ivDeporteIcon = itemView.findViewById(R.id.ivDeporteIcon);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            chipEdad = itemView.findViewById(R.id.chipEdad);
            tvTiempo = itemView.findViewById(R.id.tvTiempo);
            tvDescripcion = itemView.findViewById(R.id.tvDescripcion);
            tvExpandir = itemView.findViewById(R.id.tvExpandir);
        }

        public void bind(SolicitudEntrenadorDTO solicitud) {
            // Mostrar/Ocultar checkbox
            checkboxSelect.setVisibility(mostrarCheckbox ? View.VISIBLE : View.GONE);

            if (mostrarCheckbox) {
                checkboxSelect.setChecked(solicitudesSeleccionadas.contains(solicitud.getIdSolicitud()));
                checkboxSelect.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked) {
                        solicitudesSeleccionadas.add(solicitud.getIdSolicitud());
                    } else {
                        solicitudesSeleccionadas.remove(solicitud.getIdSolicitud());
                    }
                });
            }

            // Foto del alumno
            if (solicitud.getFotoAlumno() != null && !solicitud.getFotoAlumno().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(solicitud.getFotoAlumno())
                        .placeholder(R.drawable.avatar_user_male)
                        .error(R.drawable.avatar_user_male)
                        .circleCrop()
                        .into(imageAlumno);
            } else {
                imageAlumno.setImageResource(R.drawable.avatar_user_male);
            }

            // Icono del deporte
            int deporteIcono = getDeporteIcono(solicitud.getNombreDeporte());
            ivDeporteIcon.setImageResource(deporteIcono);

            // Nombre
            tvNombre.setText(solicitud.getNombreAlumno() != null ? solicitud.getNombreAlumno() : "Sin nombre");

            // Edad
            if (solicitud.getEdad() != null && solicitud.getEdad() > 0) {
                chipEdad.setText(solicitud.getEdad() + " años");
                chipEdad.setVisibility(View.VISIBLE);
            } else {
                chipEdad.setVisibility(View.GONE);
            }

            // Tiempo transcurrido
            tvTiempo.setText(solicitud.getTiempoTranscurrido() != null ?
                    solicitud.getTiempoTranscurrido() : "Fecha desconocida");

            // Descripción/Motivo
            String motivo = solicitud.getMotivoSolicitud();
            if (motivo != null && !motivo.isEmpty()) {
                tvDescripcion.setText(motivo);

                // Mostrar "Ver más" si el texto es largo
                if (motivo.length() > 150) {
                    tvExpandir.setVisibility(View.VISIBLE);
                    tvExpandir.setOnClickListener(v -> {
                        if (tvDescripcion.getMaxLines() == 3) {
                            tvDescripcion.setMaxLines(Integer.MAX_VALUE);
                            tvExpandir.setText("Ver menos");
                        } else {
                            tvDescripcion.setMaxLines(3);
                            tvExpandir.setText("Ver más");
                        }
                    });
                } else {
                    tvExpandir.setVisibility(View.GONE);
                }
            } else {
                tvDescripcion.setText("Sin descripción");
                tvExpandir.setVisibility(View.GONE);
            }

            // Click en la tarjeta completa
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onSolicitudClick(solicitud);
                }
            });
        }

        private int getDeporteIcono(String nombreDeporte) {
            if (nombreDeporte == null) return R.drawable.ic_deporte_default;

            switch (nombreDeporte.toLowerCase()) {
                case "fútbol":
                    return R.drawable.balon_futbol;
                case "basketball":
                    return R.drawable.balon_basket;
                case "natación":
                    return R.drawable.ic_natacion;
                case "running":
                    return R.drawable.ic_running;
                case "boxeo":
                    return R.drawable.ic_boxeo;
                case "tenis":
                    return R.drawable.pelota_tenis;
                case "gimnasio":
                    return R.drawable.ic_gimnasio;
                case "ciclismo":
                    return R.drawable.ic_ciclismo;
                case "béisbol":
                    return R.drawable.ic_beisbol;
                default:
                    return R.drawable.ic_deporte_default;
            }
        }
    }
}