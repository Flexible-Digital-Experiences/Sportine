package com.example.sportine.ui.usuarios.detallesentrenamiento;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sportine.R;
import com.example.sportine.models.AsignarEjercicioDTO;

import java.util.ArrayList;
import java.util.List;

public class EjerciciosAdapter extends RecyclerView.Adapter<EjerciciosAdapter.EjercicioViewHolder> {

    private List<AsignarEjercicioDTO> ejercicios = new ArrayList<>();
    private OnEjercicioCheckListener checkListener;

    public interface OnEjercicioCheckListener {
        void onCheckChanged(AsignarEjercicioDTO ejercicio, boolean isChecked);
    }

    public void setOnEjercicioCheckListener(OnEjercicioCheckListener listener) {
        this.checkListener = listener;
    }

    public void setEjercicios(List<AsignarEjercicioDTO> ejercicios) {
        this.ejercicios = ejercicios != null ? ejercicios : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public EjercicioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ejercicio_detalle, parent, false);
        return new EjercicioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EjercicioViewHolder holder, int position) {
        AsignarEjercicioDTO item = ejercicios.get(position);
        holder.bind(item, position);
    }

    @Override
    public int getItemCount() {
        return ejercicios.size();
    }

    class EjercicioViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        TextView txtNumero, txtNombre;
        TextView txtSeries, txtReps, txtPeso, txtDistancia, txtDuracion;

        public EjercicioViewHolder(@NonNull View itemView) {
            super(itemView);
            // Asegúrate de que estos IDs existan en tu XML
            checkBox = itemView.findViewById(R.id.checkbox_ejercicio);
            txtNumero = itemView.findViewById(R.id.text_numero_ejercicio);
            txtNombre = itemView.findViewById(R.id.text_nombre_ejercicio);

            txtSeries = itemView.findViewById(R.id.text_series);
            txtReps = itemView.findViewById(R.id.text_repeticiones);
            txtPeso = itemView.findViewById(R.id.text_peso);
            txtDistancia = itemView.findViewById(R.id.text_distancia);
            txtDuracion = itemView.findViewById(R.id.text_duracion);
        }

        public void bind(AsignarEjercicioDTO item, int position) {
            // Verificar nulos antes de usar setText para evitar crashes
            if (txtNumero != null) txtNumero.setText(String.valueOf(position + 1));
            if (txtNombre != null) txtNombre.setText(item.getNombreEjercicio());

            if (checkBox != null) {
                checkBox.setOnCheckedChangeListener(null);
                checkBox.setChecked(item.isCompletado());
            }

            if (item.esCardio()) {
                mostrar(txtDistancia, item.getDistancia() != null && item.getDistancia() > 0, item.getDistancia() + " km");
                mostrar(txtDuracion, item.getDuracion() != null && item.getDuracion() > 0, item.getDuracion() + " min");

                ocultar(txtSeries);
                ocultar(txtReps);
                ocultar(txtPeso);
            } else {
                mostrar(txtSeries, item.getSeries() != null && item.getSeries() > 0, item.getSeries() + " series");
                mostrar(txtReps, item.getRepeticiones() != null && item.getRepeticiones() > 0, item.getRepeticiones() + " reps");
                mostrar(txtPeso, item.getPeso() != null && item.getPeso() > 0, item.getPeso() + " kg");

                ocultar(txtDistancia);
                ocultar(txtDuracion);
            }

            if (checkBox != null) {
                checkBox.setOnCheckedChangeListener((v, isChecked) -> {
                    if (checkListener != null) checkListener.onCheckChanged(item, isChecked);
                });
            }
        }

        private void mostrar(TextView v, boolean show, String text) {
            if (v == null) return; // Protección contra nulos
            if (show) {
                v.setText(text);
                v.setVisibility(View.VISIBLE);
            } else {
                v.setVisibility(View.GONE);
            }
        }

        private void ocultar(TextView v) {
            if (v != null) v.setVisibility(View.GONE);
        }
    }
}