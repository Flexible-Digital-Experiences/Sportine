package com.example.sportine.ui.usuarios.social;

import android.content.Context;
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
import java.util.List;

// Usaremos el objeto 'Post' que ya tienes para simular un amigo
// (En el futuro, aquí usarías un objeto 'Amigo' o 'Usuario')
public class ListaAmigosAdapter extends RecyclerView.Adapter<ListaAmigosAdapter.AmigoViewHolder> {

    private List<Post> listaAmigos; // Usamos Post como ejemplo
    private Context context;
    private OnAmigoEliminarListener listener;

    // Interfaz para avisarle al Fragment que se presionó "Eliminar"
    public interface OnAmigoEliminarListener {
        void onAmigoEliminar(Post amigo);
    }

    public ListaAmigosAdapter(List<Post> listaAmigos, OnAmigoEliminarListener listener) {
        this.listaAmigos = listaAmigos;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AmigoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_amigo, parent, false);
        return new AmigoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AmigoViewHolder holder, int position) {
        Post amigo = listaAmigos.get(position);

        // Seteamos los datos de la fila
        holder.tvNombre.setText(amigo.getUserName()); // Usamos el nombre del Post

        // Usamos Glide para cargar la foto de perfil (del Post)
        Glide.with(context)
                .load(amigo.getUserAvatarResId()) // Usamos el avatar del Post
                .placeholder(R.drawable.ic_launcher_background) // Foto por defecto
                .circleCrop() // La hace circular
                .into(holder.ivAvatar);

        // Configuramos el clic del botón "Eliminar"
        holder.btnEliminar.setOnClickListener(v -> {
            listener.onAmigoEliminar(amigo);
        });
    }

    @Override
    public int getItemCount() {
        return listaAmigos.size();
    }

    // El ViewHolder que "sostiene" las vistas de cada fila
    public static class AmigoViewHolder extends RecyclerView.ViewHolder {
        ImageView ivAvatar;
        TextView tvNombre;
        Button btnEliminar;

        public AmigoViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.iv_amigo_avatar);
            tvNombre = itemView.findViewById(R.id.tv_amigo_nombre);
            btnEliminar = itemView.findViewById(R.id.btn_eliminar_amigo);
        }
    }
}