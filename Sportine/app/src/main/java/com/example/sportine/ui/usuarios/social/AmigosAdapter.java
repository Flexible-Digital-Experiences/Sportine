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

        // Lógica Visual del Botón
        if (esModoBusqueda) {
            // ✅ Usamos isSiguiendo() que ahora sí trae el dato real del servidor
            actualizarEstiloBoton(holder.btnAccion, usuario.isSiguiendo());
            holder.btnAccion.setEnabled(true);
        } else {
            holder.btnAccion.setText("Eliminar");
            holder.btnAccion.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
            holder.btnAccion.setTextColor(Color.WHITE);
            holder.btnAccion.setEnabled(true);
        }

        // Clic Listener
        holder.btnAccion.setOnClickListener(v -> {
            if (listener != null) {
                // 1. Notificamos al fragmento para llamar a la API
                listener.onAction(usuario);

                // 2. ✅ ACTUALIZACIÓN VISUAL INMEDIATA (Optimistic UI)
                // Cambiamos el botón y el modelo aquí mismo para evitar el "parpadeo"
                if (esModoBusqueda) {
                    boolean nuevoEstado = !usuario.isSiguiendo();
                    usuario.setSiguiendo(nuevoEstado); // Guardamos en memoria
                    actualizarEstiloBoton(holder.btnAccion, nuevoEstado); // Pintamos
                }
            }
        });
    }

    // Helper para pintar el botón
    private void actualizarEstiloBoton(Button btn, boolean siguiendo) {
        if (siguiendo) {
            btn.setText("Siguiendo");
            btn.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY));
            btn.setTextColor(Color.WHITE);
        } else {
            btn.setText("Seguir");
            btn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#1ea1db"))); // Azul
            btn.setTextColor(Color.WHITE);
        }
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