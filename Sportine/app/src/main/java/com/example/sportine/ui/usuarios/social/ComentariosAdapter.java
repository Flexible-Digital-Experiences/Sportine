package com.example.sportine.ui.usuarios.social;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.sportine.R;
import com.example.sportine.models.Comentario;

import org.ocpsoft.prettytime.PrettyTime;
// --- ¡NUEVOS IMPORTS NECESARIOS! ---
import org.ocpsoft.prettytime.TimeUnit;
import org.ocpsoft.prettytime.format.SimpleTimeFormat;
import org.ocpsoft.prettytime.units.JustNow;

import java.util.List;
import java.util.Locale;

public class ComentariosAdapter extends RecyclerView.Adapter<ComentariosAdapter.ComentarioViewHolder> {

    private List<Comentario> listaComentarios;
    private PrettyTime prettyTime;

    public ComentariosAdapter(List<Comentario> listaComentarios) {
        this.listaComentarios = listaComentarios;

        this.prettyTime = new PrettyTime(new Locale("es"));

        // 1. Quitamos la unidad "JustNow" original
        TimeUnit justNowUnit = prettyTime.getUnit(JustNow.class);
        prettyTime.removeUnit(justNowUnit);

        // 2. Creamos el formato CORRECTO
        SimpleTimeFormat customFormat = new SimpleTimeFormat()
                .setSingularName("hace un momento")
                .setPluralName("hace un momento")
                .setPattern("%u") // %u = Solo el nombre
                .setPastPrefix("")
                .setPastSuffix("")
                .setFuturePrefix("")
                .setFutureSuffix("");

        // 3. Registramos
        prettyTime.registerUnit(new JustNow(), customFormat);
    }

    @NonNull
    @Override
    public ComentarioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comentario, parent, false);
        return new ComentarioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ComentarioViewHolder holder, int position) {
        Comentario comentario = listaComentarios.get(position);

        String nombreMostrar = comentario.getAutorNombre() != null ?
                comentario.getAutorNombre() : comentario.getAutorUsername();

        holder.tvUsername.setText(nombreMostrar);
        holder.tvContent.setText(comentario.getTexto());

        if (comentario.getFecha() != null) {
            holder.tvTimestamp.setText(prettyTime.format(comentario.getFecha()));
        } else {
            holder.tvTimestamp.setText("");
        }

        Glide.with(holder.itemView.getContext())
                .load(comentario.getAutorFoto())
                .placeholder(R.drawable.avatar_user_male)
                .error(R.drawable.avatar_user_male)
                .circleCrop()
                .into(holder.ivAvatar);
    }

    @Override
    public int getItemCount() {
        return listaComentarios.size();
    }

    public void setComentarios(List<Comentario> nuevosComentarios) {
        if (this.listaComentarios != null) {
            this.listaComentarios.clear();
            this.listaComentarios.addAll(nuevosComentarios);
        } else {
            this.listaComentarios = nuevosComentarios;
        }
        notifyDataSetChanged();
    }

    static class ComentarioViewHolder extends RecyclerView.ViewHolder {
        ImageView ivAvatar;
        TextView tvUsername, tvContent, tvTimestamp;

        public ComentarioViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.iv_comment_avatar);
            tvUsername = itemView.findViewById(R.id.tv_comment_username);
            tvContent = itemView.findViewById(R.id.tv_comment_text);
            tvTimestamp = itemView.findViewById(R.id.tv_comment_timestamp);
        }
    }
}