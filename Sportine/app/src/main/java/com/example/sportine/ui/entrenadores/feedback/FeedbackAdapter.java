package com.example.sportine.ui.entrenadores.feedback;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.sportine.R;
import com.example.sportine.models.FeedbackResumenDTO;
import java.util.ArrayList;
import java.util.List;

public class FeedbackAdapter extends RecyclerView.Adapter<FeedbackAdapter.ViewHolder> {

    private List<FeedbackResumenDTO> lista = new ArrayList<>();

    public void setList(List<FeedbackResumenDTO> nuevaLista) {
        this.lista = nuevaLista;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Asegúrate de tener 'item_entrenador_feedback.xml' creado
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_entrenador_feedback, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FeedbackResumenDTO item = lista.get(position);

        holder.nombre.setText(item.getNombreAlumno());
        holder.titulo.setText("Completó: " + item.getTituloEntrenamiento());

        // Manejo básico de fecha (puedes usar una librería para "hace 2h")
        holder.fecha.setText(item.getFecha() != null ? item.getFecha().replace("T", " ") : "");

        holder.comentario.setText(item.getComentarios() != null && !item.getComentarios().isEmpty()
                ? item.getComentarios()
                : "Sin comentarios adicionales.");

        holder.cansancio.setText("🥵 Cansancio: " + item.getNivelCansancio() + "/10");
        holder.dificultad.setText("💪 Dificultad: " + item.getDificultad() + "/10");
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nombre, fecha, titulo, comentario, cansancio, dificultad;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.text_nombre_alumno);
            fecha = itemView.findViewById(R.id.text_fecha);
            titulo = itemView.findViewById(R.id.text_titulo_entreno);
            comentario = itemView.findViewById(R.id.text_comentario);
            cansancio = itemView.findViewById(R.id.text_cansancio);
            dificultad = itemView.findViewById(R.id.text_dificultad);
        }
    }
}