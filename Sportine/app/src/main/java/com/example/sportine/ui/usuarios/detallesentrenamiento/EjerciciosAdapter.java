package com.example.sportine.ui.usuarios.detallesentrenamiento;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sportine.R;
import com.example.sportine.models.AsignarEjercicioDTO;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EjerciciosAdapter extends RecyclerView.Adapter<EjerciciosAdapter.EjercicioViewHolder> {

    private List<AsignarEjercicioDTO> ejercicios = new ArrayList<>();
    private OnEjercicioCheckListener checkListener;
    private OnLlenarResultadosListener resultadosListener;

    // Mapa: idAsignado → serie actual (1, 2, 3...)
    // Avanza cada vez que el alumno guarda una serie exitosamente
    private final Map<Integer, Integer> serieActualPorEjercicio = new HashMap<>();

    // ── Interfaces ───────────────────────────────────────────────────────────
    public interface OnEjercicioCheckListener {
        void onCheckChanged(AsignarEjercicioDTO ejercicio, boolean isChecked);
    }

    public interface OnLlenarResultadosListener {
        void onLlenar(AsignarEjercicioDTO ejercicio, int numeroSerie);
    }

    // ── Setters de listeners ─────────────────────────────────────────────────
    public void setOnEjercicioCheckListener(OnEjercicioCheckListener listener) {
        this.checkListener = listener;
    }

    public void setOnLlenarResultadosListener(OnLlenarResultadosListener listener) {
        this.resultadosListener = listener;
    }

    // ── Datos ────────────────────────────────────────────────────────────────
    public void setEjercicios(List<AsignarEjercicioDTO> ejercicios) {
        this.ejercicios = ejercicios != null ? ejercicios : new ArrayList<>();
        // Inicializar series en 1 para cada ejercicio
        for (AsignarEjercicioDTO e : this.ejercicios) {
            if (e.getIdAsignado() != null) {
                serieActualPorEjercicio.putIfAbsent(e.getIdAsignado(), 1);
            }
        }
        notifyDataSetChanged();
    }

    /**
     * Actualiza el status visual de un ejercicio específico sin recargar toda la lista.
     */
    public void actualizarStatusEjercicio(int idAsignado, String nuevoStatus) {
        for (int i = 0; i < ejercicios.size(); i++) {
            if (ejercicios.get(i).getIdAsignado() != null
                    && ejercicios.get(i).getIdAsignado() == idAsignado) {
                ejercicios.get(i).setStatusEjercicio(nuevoStatus);
                if ("completado".equals(nuevoStatus)) {
                    ejercicios.get(i).setCompletado(true);
                }
                notifyItemChanged(i);
                break;
            }
        }
    }

    /**
     * Avanza el contador de serie de un ejercicio al guardar exitosamente.
     * Llamar desde el Fragment después de onGuardado.
     */
    public void avanzarSerie(int idAsignado) {
        // Buscar el total de series del ejercicio
        for (AsignarEjercicioDTO e : ejercicios) {
            if (e.getIdAsignado() != null && e.getIdAsignado() == idAsignado) {
                int total = e.getSeries() != null ? e.getSeries() : 1;
                int actual = serieActualPorEjercicio.getOrDefault(idAsignado, 1);
                if (actual < total) {
                    serieActualPorEjercicio.put(idAsignado, actual + 1);
                }
                break;
            }
        }
    }

    // ── RecyclerView ─────────────────────────────────────────────────────────
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

    // ── ViewHolder ───────────────────────────────────────────────────────────
    class EjercicioViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        TextView txtNumero, txtNombre;
        TextView txtSeries, txtReps, txtPeso, txtDistancia, txtDuracion;
        TextView tvStatusEjercicio;
        MaterialButton btnLlenarResultados;

        public EjercicioViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox            = itemView.findViewById(R.id.checkbox_ejercicio);
            txtNumero           = itemView.findViewById(R.id.text_numero_ejercicio);
            txtNombre           = itemView.findViewById(R.id.text_nombre_ejercicio);
            txtSeries           = itemView.findViewById(R.id.text_series);
            txtReps             = itemView.findViewById(R.id.text_repeticiones);
            txtPeso             = itemView.findViewById(R.id.text_peso);
            txtDistancia        = itemView.findViewById(R.id.text_distancia);
            txtDuracion         = itemView.findViewById(R.id.text_duracion);
            tvStatusEjercicio   = itemView.findViewById(R.id.tv_status_ejercicio);
            btnLlenarResultados = itemView.findViewById(R.id.btn_llenar_resultados);
        }

        public void bind(AsignarEjercicioDTO item, int position) {
            if (txtNumero != null) txtNumero.setText(String.valueOf(position + 1));
            if (txtNombre != null) txtNombre.setText(item.getNombreEjercicio());

            // ── Checkbox ─────────────────────────────────────────────────────
            if (checkBox != null) {
                checkBox.setOnCheckedChangeListener(null);
                checkBox.setChecked(item.isCompletado());
                checkBox.setOnCheckedChangeListener((v, isChecked) -> {
                    if (checkListener != null) checkListener.onCheckChanged(item, isChecked);
                });
            }

            // ── Métricas esperadas ────────────────────────────────────────────
            if (item.esCardio()) {
                mostrar(txtDistancia, item.getDistancia() != null && item.getDistancia() > 0,
                        item.getDistancia() + " km");
                mostrar(txtDuracion, item.getDuracion() != null && item.getDuracion() > 0,
                        item.getDuracion() + " min");
                ocultar(txtSeries);
                ocultar(txtReps);
                ocultar(txtPeso);
            } else {
                mostrar(txtSeries, item.getSeries() != null && item.getSeries() > 0,
                        item.getSeries() + " series");
                mostrar(txtReps, item.getRepeticiones() != null && item.getRepeticiones() > 0,
                        item.getRepeticiones() + " reps");
                mostrar(txtPeso, item.getPeso() != null && item.getPeso() > 0,
                        item.getPeso() + " kg");
                ocultar(txtDistancia);
                ocultar(txtDuracion);
            }

            // ── Status visual ─────────────────────────────────────────────────
            if (tvStatusEjercicio != null) {
                String status = item.getStatusEjercicio();
                if (status == null) status = "pendiente";

                switch (status) {
                    case "completado":
                        tvStatusEjercicio.setText("✅ Completado");
                        tvStatusEjercicio.setTextColor(Color.parseColor("#10B981"));
                        tvStatusEjercicio.setVisibility(View.VISIBLE);
                        break;
                    case "parcial":
                        tvStatusEjercicio.setText("⚡ Parcial");
                        tvStatusEjercicio.setTextColor(Color.parseColor("#F59E0B"));
                        tvStatusEjercicio.setVisibility(View.VISIBLE);
                        break;
                    case "omitido":
                        tvStatusEjercicio.setText("⏭️ Omitido");
                        tvStatusEjercicio.setTextColor(Color.parseColor("#9CA3AF"));
                        tvStatusEjercicio.setVisibility(View.VISIBLE);
                        break;
                    default:
                        tvStatusEjercicio.setVisibility(View.GONE);
                        break;
                }
            }

            // ── Botón "Llenar resultados" ──────────────────────────────────────
            if (btnLlenarResultados != null) {
                String status = item.getStatusEjercicio();

                // Calcular texto del botón según serie actual vs total
                int idAsignado = item.getIdAsignado() != null ? item.getIdAsignado() : -1;
                int serieActual = serieActualPorEjercicio.getOrDefault(idAsignado, 1);
                int totalSeries = item.getSeries() != null ? item.getSeries() : 1;

                if ("completado".equals(status) || "parcial".equals(status) || "omitido".equals(status)) {
                    if (serieActual <= totalSeries) {
                        // Aún quedan series por llenar
                        btnLlenarResultados.setText("Serie " + serieActual + "/" + totalSeries);
                    } else {
                        // Todas las series llenadas
                        btnLlenarResultados.setText("✔ Todas las series");
                        btnLlenarResultados.setEnabled(false);
                    }
                } else {
                    btnLlenarResultados.setText("Llenar serie " + serieActual + "/" + totalSeries);
                    btnLlenarResultados.setEnabled(true);
                }

                btnLlenarResultados.setOnClickListener(v -> {
                    if (resultadosListener != null) {
                        int serie = serieActualPorEjercicio.getOrDefault(idAsignado, 1);
                        resultadosListener.onLlenar(item, serie);
                    }
                });
            }
        }

        private void mostrar(TextView v, boolean show, String text) {
            if (v == null) return;
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