package com.example.sportine.ui.usuarios.social;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.sportine.R;
import com.example.sportine.models.UsuarioDetalle;

import java.util.ArrayList;
import java.util.List;

public class AmigosAdapter extends RecyclerView.Adapter<AmigosAdapter.AmigoViewHolder> {

    private List<UsuarioDetalle> listaUsuarios = new ArrayList<>();
    private boolean esModoBusqueda;
    private OnItemActionListener listener;

    public interface OnItemActionListener {
        void onAction(UsuarioDetalle usuario);
    }

    public AmigosAdapter(boolean esModoBusqueda, OnItemActionListener listener) {
        this.esModoBusqueda = esModoBusqueda;
        this.listener = listener;
    }

    public void setUsuarios(List<UsuarioDetalle> nuevosUsuarios) {
        this.listaUsuarios = nuevosUsuarios;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AmigoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_amigo_resultado, parent, false);
        return new AmigoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AmigoViewHolder holder, int position) {
        UsuarioDetalle usuario = listaUsuarios.get(position);

        // 1. Datos de texto
        String nombre = usuario.getNombre();
        String apellidos = usuario.getApellidos();
        if (nombre == null) nombre = usuario.getUsuario();
        if (apellidos == null) apellidos = "";
        holder.tvNombre.setText((nombre + " " + apellidos).trim());

        if (holder.tvUsuario != null) {
            holder.tvUsuario.setText("@" + usuario.getUsuario());
        }

        Glide.with(holder.itemView.getContext())
                .load(usuario.getFotoPerfil())
                .placeholder(R.drawable.avatar_user_male)
                .error(R.drawable.avatar_user_male)
                .circleCrop()
                .into(holder.ivAvatar);

        if (esModoBusqueda) {


            if (usuario.isAmigo()) {

                holder.btnAccion.setText("Siguiendo");
                holder.btnAccion.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY));
                holder.btnAccion.setTextColor(Color.WHITE);

                holder.btnAccion.setEnabled(true);
            } else {

                holder.btnAccion.setText("Seguir");
                holder.btnAccion.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#2196F3"))); // Azul
                holder.btnAccion.setTextColor(Color.WHITE);
                holder.btnAccion.setEnabled(true);
            }
        } else {

            holder.btnAccion.setText("Eliminar");
            holder.btnAccion.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
            holder.btnAccion.setTextColor(Color.WHITE);
            holder.btnAccion.setEnabled(true);
        }

        // 4. Clic Listener
        holder.btnAccion.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAction(usuario);

            }
        });
    }

    @Override
    public int getItemCount() {
        return listaUsuarios.size();
    }

    static class AmigoViewHolder extends RecyclerView.ViewHolder {
        ImageView ivAvatar;
        TextView tvNombre, tvUsuario;
        Button btnAccion;

        public AmigoViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.iv_avatar_amigo);
            tvNombre = itemView.findViewById(R.id.tv_nombre_amigo);
            tvUsuario = itemView.findViewById(R.id.tv_usuario_amigo);
            btnAccion = itemView.findViewById(R.id.btn_agregar_amigo);
        }
    }
}