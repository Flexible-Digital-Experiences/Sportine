package com.example.sportine.ui.usuarios.social;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.sportine.R;
import com.example.sportine.data.ApiService;
import com.example.sportine.models.PublicacionFeedDTO;

import org.ocpsoft.prettytime.PrettyTime;
// --- ¡NUEVOS IMPORTS! ---
import org.ocpsoft.prettytime.TimeFormat;
import org.ocpsoft.prettytime.TimeUnit;
import org.ocpsoft.prettytime.format.SimpleTimeFormat;
import org.ocpsoft.prettytime.units.JustNow;

import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SocialFeedAdapter extends RecyclerView.Adapter<SocialFeedAdapter.PostViewHolder> {

    private List<PublicacionFeedDTO> publicacionList;
    private PrettyTime prettyTime;
    private ApiService apiService;
    private static final String TAG = "LikeDebug";

    public SocialFeedAdapter(List<PublicacionFeedDTO> publicacionList, Context context, ApiService apiService) {
        this.publicacionList = publicacionList;
        this.apiService = apiService;

        // --- ¡CAMBIO! Lógica correcta para "hace un momento" ---

        // 1. Creamos el objeto
        this.prettyTime = new PrettyTime(new Locale("es"));

        // 2. Obtenemos la unidad "JustNow" original (la que dice "hace instantes")
        TimeUnit justNowUnit = prettyTime.getUnit(JustNow.class);

        // 3. Creamos nuestro NUEVO formato de texto
        TimeFormat justNowFormat = new SimpleTimeFormat()
                .setSingularName("hace un momento")
                .setPluralName("hace un momento") // Por si acaso
                .setPattern("%u") // Solo muestra el nombre ("hace un momento")
                .setPastSuffix("")
                .setFutureSuffix("");

        // 4. Quitamos la regla vieja
        prettyTime.removeUnit(justNowUnit);

        // 5. Registramos la unidad 'JustNow' OTRA VEZ, pero con nuestro formato nuevo
        prettyTime.registerUnit(new JustNow(), justNowFormat);
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_alumno_social_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {

        PublicacionFeedDTO publicacion = publicacionList.get(position);

        // 1. Texto del Post
        holder.postTitleTextView.setText(publicacion.getDescripcion());

        // 2. Foto de Perfil (con Glide)
        Glide.with(holder.itemView.getContext())
                .load(publicacion.getAutorFotoPerfil())
                .placeholder(R.drawable.avatar_user_male)
                .error(R.drawable.avatar_user_male)
                .circleCrop()
                .into(holder.userAvatarImageView);

        // --- ¡LÓGICA DE IMAGEN (YA CORREGIDA)! ---
        // 3. Imagen del Post (con Glide)
        if (publicacion.getImagen() != null && !publicacion.getImagen().isEmpty()) {
            holder.postImageView.setVisibility(View.VISIBLE);
            Glide.with(holder.itemView.getContext())
                    .load(publicacion.getImagen())
                    .into(holder.postImageView);
        } else {
            holder.postImageView.setVisibility(View.GONE);
        }

        // --- ¡LÓGICA DE FECHA (YA CORREGIDA)! ---
        // 4. Timestamp (con PrettyTime)
        if (publicacion.getFechaPublicacion() != null) {
            String tiempoBonito = prettyTime.format(publicacion.getFechaPublicacion());
            holder.timestampTextView.setText(tiempoBonito);
        } else {
            holder.timestampTextView.setText(""); // Oculta si es null
        }

        // 5. Lógica de Likes (ya estaba bien)
        updateLikeVisuals(holder, publicacion.isLikedByMe());
        holder.likeButtonImageView.setOnClickListener(v -> {

            Integer postId = publicacion.getIdPublicacion();
            boolean isCurrentlyLiked = publicacion.isLikedByMe();
            boolean newState = !isCurrentlyLiked;
            publicacion.setLikedByMe(newState);

            if (newState) {
                Log.d(TAG, "Intentando DAR like al post ID: " + postId);
                publicacion.setTotalLikes(publicacion.getTotalLikes() + 1);
                apiService.darLike(postId).enqueue(new Callback<Void>() {
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Log.d(TAG, "ÉXITO al dar like");
                        } else {
                            Log.e(TAG, "ERROR al dar like: " + response.code());
                        }
                    }
                    public void onFailure(Call<Void> call, Throwable t) {
                        Log.e(TAG, "FALLO DE RED al dar like: " + t.getMessage());
                    }
                });
            } else {
                Log.d(TAG, "Intentando QUITAR like al post ID: " + postId);
                publicacion.setTotalLikes(publicacion.getTotalLikes() - 1);
                apiService.quitarLike(postId).enqueue(new Callback<Void>() {
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Log.d(TAG, "ÉXITO al quitar like");
                        } else {
                            Log.e(TAG, "ERROR al quitar like: " + response.code());
                        }
                    }
                    public void onFailure(Call<Void> call, Throwable t) {
                        Log.e(TAG, "FALLO DE RED al quitar like: " + t.getMessage());
                    }
                });
            }
            updateLikeVisuals(holder, newState);
        });
    }

    // (El resto del archivo se queda igual)
    private void updateLikeVisuals(PostViewHolder holder, boolean isLiked) {
        if (isLiked) {
            holder.likeButtonImageView.setImageResource(R.drawable.ic_favorite_black_24dp);
            holder.likeButtonImageView.setColorFilter(Color.RED);
        } else {
            holder.likeButtonImageView.setImageResource(R.drawable.ic_favorite_border_black_24dp);
            holder.likeButtonImageView.setColorFilter(null);
        }
    }

    @Override
    public int getItemCount() { return publicacionList.size(); }
    public void setPublicaciones(List<PublicacionFeedDTO> nuevasPublicaciones) {
        this.publicacionList.clear();
        this.publicacionList.addAll(nuevasPublicaciones);
        notifyDataSetChanged();
    }
    public static class PostViewHolder extends RecyclerView.ViewHolder {
        ImageView userAvatarImageView, postImageView, likeButtonImageView;
        TextView postTitleTextView, timestampTextView;
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