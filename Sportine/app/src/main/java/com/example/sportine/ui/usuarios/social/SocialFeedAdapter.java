package com.example.sportine.ui.usuarios.social;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.sportine.R;
import java.util.List;

public class SocialFeedAdapter extends RecyclerView.Adapter<SocialFeedAdapter.PostViewHolder> {

    private List<Post> postList;
    private RecyclerView recyclerView; // Referencia para poder hacer scroll

    public SocialFeedAdapter(List<Post> postList) {
        this.postList = postList;
    }

    // Método para obtener la referencia del RecyclerView desde el Fragment
    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_alumno_social_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        // Obtenemos el objeto Post para esta posición
        Post post = postList.get(position);

        // Llenamos los datos de la vista
        holder.postTitleTextView.setText(post.getMessage());
        holder.timestampTextView.setText(post.getTimestamp());
        holder.userAvatarImageView.setImageResource(post.getUserAvatarResId());

        // Mostramos la imagen de la publicación solo si existe
        if (post.getPostImageResId() != 0) {
            holder.postImageView.setVisibility(View.VISIBLE);
            holder.postImageView.setImageResource(post.getPostImageResId());
        } else {
            holder.postImageView.setVisibility(View.GONE);
        }

        // --- LÓGICA CORRECTA PARA EL CORAZÓN ---
        // 1. Establece el estado inicial basándose en los datos del objeto Post
        if (post.isLiked()) {
            holder.likeButtonImageView.setImageResource(R.drawable.ic_favorite_black_24dp);
            holder.likeButtonImageView.setColorFilter(Color.RED);
        } else {
            holder.likeButtonImageView.setImageResource(R.drawable.ic_favorite_border_black_24dp);
            holder.likeButtonImageView.setColorFilter(null); // Sin tinte
        }

        // 2. Maneja el clic en el corazón
        holder.likeButtonImageView.setOnClickListener(v -> {
            // Invertimos el estado del "like" EN EL OBJETO DE DATOS
            post.setLiked(!post.isLiked());

            // Actualizamos la vista basándonos en el nuevo estado guardado
            if (post.isLiked()) {
                holder.likeButtonImageView.setImageResource(R.drawable.ic_favorite_black_24dp);
                holder.likeButtonImageView.setColorFilter(Color.RED);
            } else {
                holder.likeButtonImageView.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                holder.likeButtonImageView.setColorFilter(null);
            }
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    // Método para agregar una nueva publicación al inicio de la lista
    public void addPost(Post post) {
        postList.add(0, post);
        notifyItemInserted(0);
        if (recyclerView != null) {
            recyclerView.scrollToPosition(0); // Mueve el scroll hasta arriba
        }
    }

    // Clase interna ViewHolder que contiene las referencias a las vistas
    public static class PostViewHolder extends RecyclerView.ViewHolder {
        ImageView userAvatarImageView;
        TextView postTitleTextView;
        ImageView postImageView;
        TextView timestampTextView;
        ImageView likeButtonImageView;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            userAvatarImageView = itemView.findViewById(R.id.iv_user_avatar);
            postTitleTextView = itemView.findViewById(R.id.tv_post_title);
            postImageView = itemView.findViewById(R.id.iv_post_image);
            timestampTextView = itemView.findViewById(R.id.tv_post_timestamp);
            likeButtonImageView = itemView.findViewById(R.id.iv_like_button);
        }
    }
}