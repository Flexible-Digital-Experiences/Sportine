package com.example.sportine.ui.usuarios.social;

import android.content.Context;
import android.graphics.Color;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.sportine.R;
import com.example.sportine.data.ApiService;
import com.example.sportine.models.Publicacion;
import com.example.sportine.models.PublicacionFeedDTO;

import org.ocpsoft.prettytime.PrettyTime;
import org.ocpsoft.prettytime.TimeFormat;
import org.ocpsoft.prettytime.TimeUnit;
import org.ocpsoft.prettytime.format.SimpleTimeFormat;
import org.ocpsoft.prettytime.units.JustNow;

import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SocialFeedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // Constantes para identificar el tipo
    private static final int TIPO_NORMAL = 1;
    private static final int TIPO_LOGRO = 2;

    private List<PublicacionFeedDTO> publicacionList;
    private PrettyTime prettyTime;
    private ApiService apiService;
    private Context context;

    public SocialFeedAdapter(List<PublicacionFeedDTO> publicacionList, Context context, ApiService apiService) {
        this.publicacionList = publicacionList;
        this.context = context;
        this.apiService = apiService;
        this.prettyTime = new PrettyTime(new Locale("es"));
        // Configuración de PrettyTime (igual que antes)...
        TimeUnit justNowUnit = prettyTime.getUnit(JustNow.class);
        prettyTime.removeUnit(justNowUnit);
        TimeFormat justNowFormat = new SimpleTimeFormat()
                .setSingularName("hace un momento").setPluralName("hace un momento")
                .setPattern("%u").setPastPrefix("").setPastSuffix("")
                .setFuturePrefix("").setFutureSuffix("");
        prettyTime.registerUnit(new JustNow(), justNowFormat);
    }

    // 1. DECIDIR QUÉ DISEÑO USAR
    @Override
    public int getItemViewType(int position) {
        Integer tipo = publicacionList.get(position).getTipo();
        // Si viene nulo o es 1, es Normal. Si es 2, es Logro.
        if (tipo != null && tipo == 2) {
            return TIPO_LOGRO;
        }
        return TIPO_NORMAL;
    }

    // 2. INFLAR EL XML CORRECTO
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TIPO_LOGRO) {
            // Tarjeta Verde
            View view = LayoutInflater.from(context).inflate(R.layout.item_social_achievement, parent, false);
            return new AchievementViewHolder(view);
        } else {
            // Tarjeta Blanca (Normal)
            View view = LayoutInflater.from(context).inflate(R.layout.item_alumno_social_post, parent, false);
            return new PostViewHolder(view);
        }
    }

    // 3. LLENAR LOS DATOS (BINDING)
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        PublicacionFeedDTO publicacion = publicacionList.get(position);

        if (holder.getItemViewType() == TIPO_LOGRO) {
            bindAchievement((AchievementViewHolder) holder, publicacion);
        } else {
            bindNormalPost((PostViewHolder) holder, publicacion);
        }
    }

    // --- LÓGICA PARA TARJETA VERDE (LOGRO) ---
    private void bindAchievement(AchievementViewHolder holder, PublicacionFeedDTO publicacion) {
        // Avatar
        Glide.with(context).load(publicacion.getAutorFotoPerfil())
                .placeholder(R.drawable.avatar_user_male).circleCrop().into(holder.ivAvatar);

        holder.tvUsername.setText(publicacion.getAutorNombreCompleto());
        holder.tvText.setText(publicacion.getDescripcion()); // Aquí viene "¡Nivel Experto!..."

        // Fecha
        if (publicacion.getFechaPublicacion() != null) {
            holder.tvTime.setText(prettyTime.format(publicacion.getFechaPublicacion()));
        }

        // Likes (Blanco)
        int likes = publicacion.getTotalLikes();
        holder.tvLikesCount.setText(String.valueOf(likes));

        if (publicacion.isLikedByMe()) {
            holder.ivLike.setImageResource(R.drawable.ic_favorite_black_24dp); // Relleno
            // Nota: En la tarjeta verde los íconos ya son blancos por el tint del XML,
            // así que solo cambiamos la imagen de "border" a "filled".
        } else {
            holder.ivLike.setImageResource(R.drawable.ic_favorite_border_black_24dp); // Borde
        }

        // Click Like en Logro
        holder.ivLike.setOnClickListener(v -> toggleLikeGenerico(publicacion, holder.tvLikesCount, holder.ivLike));
    }

    // --- LÓGICA PARA TARJETA BLANCA (POST NORMAL - Lo que ya tenías) ---
    private void bindNormalPost(PostViewHolder holder, PublicacionFeedDTO publicacion) {
        // ... (Copia aquí TODO tu código anterior del onBindViewHolder) ...
        // Por espacio, te resumo lo clave, pero PEGA AQUÍ TU LÓGICA DE GESTOS, TRANSICIONES, ETC.

        // 1. Datos
        String nombre = publicacion.getAutorNombreCompleto() != null ? publicacion.getAutorNombreCompleto() : publicacion.getAutorUsername();
        holder.tvUsername.setText(nombre);

        // Texto
        if (publicacion.getDescripcion() != null && !publicacion.getDescripcion().isEmpty()) {
            holder.tvDescription.setVisibility(View.VISIBLE);
            holder.tvDescription.setText(publicacion.getDescripcion());
        } else {
            holder.tvDescription.setVisibility(View.GONE);
        }

        // Avatar
        Glide.with(context).load(publicacion.getAutorFotoPerfil())
                .placeholder(R.drawable.avatar_user_male).circleCrop().into(holder.userAvatarImageView);

        // Imagen (GONE logic)
        boolean tieneImagen = publicacion.getImagen() != null && !publicacion.getImagen().isEmpty();
        if (tieneImagen) {
            holder.postImageView.setVisibility(View.VISIBLE);
            Glide.with(context).load(publicacion.getImagen()).into(holder.postImageView);
        } else {
            holder.postImageView.setVisibility(View.GONE);
        }

        // Fecha y Likes
        if (publicacion.getFechaPublicacion() != null) holder.timestampTextView.setText(prettyTime.format(publicacion.getFechaPublicacion()));
        int likes = publicacion.getTotalLikes();
        holder.tvLikesCount.setText(String.valueOf(likes));

        // Update visual like (Normal)
        if (publicacion.isLikedByMe()) {
            holder.likeButtonImageView.setImageResource(R.drawable.ic_favorite_black_24dp);
            holder.likeButtonImageView.setColorFilter(Color.RED);
        } else {
            holder.likeButtonImageView.setImageResource(R.drawable.ic_favorite_border_black_24dp);
            holder.likeButtonImageView.setColorFilter(null);
        }

        // Clicks (Gestos, Menú, etc. - PEGA TU CÓDIGO AQUÍ)
        // ... (Tu detector de gestos, transiciones, etc.) ...

        // Click Like simple (Botón)
        holder.likeButtonImageView.setOnClickListener(v -> {
            v.performHapticFeedback(android.view.HapticFeedbackConstants.VIRTUAL_KEY);
            // Animación pequeña
            v.animate().scaleX(1.2f).scaleY(1.2f).setDuration(100).withEndAction(() -> v.animate().scaleX(1f).scaleY(1f).start()).start();

            toggleLikeGenerico(publicacion, holder.tvLikesCount, holder.likeButtonImageView);
        });

        // ... Resto de botones (Comentar, Menú 3 puntos) ...
    }

    // --- LÓGICA DE LIKE COMPARTIDA ---
    private void toggleLikeGenerico(PublicacionFeedDTO publicacion, TextView tvCounter, ImageView btnLike) {
        Integer postId = publicacion.getIdPublicacion();
        boolean newState = !publicacion.isLikedByMe();
        publicacion.setLikedByMe(newState);

        int current = publicacion.getTotalLikes();
        if (newState) current++; else if (current > 0) current--;
        publicacion.setTotalLikes(current);

        tvCounter.setText(String.valueOf(current));

        // Actualizar icono visualmente
        if (newState) {
            btnLike.setImageResource(R.drawable.ic_favorite_black_24dp);
            // Solo ponemos rojo si es Post Normal (para Logro lo dejamos blanco)
            if (publicacion.getTipo() == null || publicacion.getTipo() == 1) {
                btnLike.setColorFilter(Color.RED);
            }
            apiService.darLike(postId).enqueue(new Callback<Void>() { public void onResponse(Call<Void> c, Response<Void> r){} public void onFailure(Call<Void> c, Throwable t){} });
        } else {
            btnLike.setImageResource(R.drawable.ic_favorite_border_black_24dp);
            btnLike.setColorFilter(null); // Quitar filtro
            apiService.quitarLike(postId).enqueue(new Callback<Void>() { public void onResponse(Call<Void> c, Response<Void> r){} public void onFailure(Call<Void> c, Throwable t){} });
        }
    }

    @Override
    public int getItemCount() { return publicacionList.size(); }

    public void setPublicaciones(List<PublicacionFeedDTO> nuevas) {
        this.publicacionList.clear();
        this.publicacionList.addAll(nuevas);
        notifyDataSetChanged();
    }

    // --- VIEWHOLDER NORMAL (Blanco) ---
    public static class PostViewHolder extends RecyclerView.ViewHolder {
        ImageView userAvatarImageView, postImageView, likeButtonImageView, commentButtonImageView, moreOptionsImageView, bigHeartImageView;
        TextView tvUsername, tvDescription, timestampTextView, tvLikesCount;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            userAvatarImageView = itemView.findViewById(R.id.iv_user_avatar);
            tvUsername = itemView.findViewById(R.id.tv_username);
            tvDescription = itemView.findViewById(R.id.tv_post_description);
            postImageView = itemView.findViewById(R.id.iv_post_image);
            bigHeartImageView = itemView.findViewById(R.id.iv_big_heart);
            timestampTextView = itemView.findViewById(R.id.tv_post_timestamp);
            likeButtonImageView = itemView.findViewById(R.id.iv_like_button);
            tvLikesCount = itemView.findViewById(R.id.tv_likes_count);
            commentButtonImageView = itemView.findViewById(R.id.iv_comment_button);
            moreOptionsImageView = itemView.findViewById(R.id.iv_more_options);
        }
    }

    // --- VIEWHOLDER LOGRO (Verde) ---
    public static class AchievementViewHolder extends RecyclerView.ViewHolder {
        ImageView ivAvatar, ivTrophy, ivLike;
        TextView tvUsername, tvText, tvTime, tvLikesCount;

        public AchievementViewHolder(@NonNull View itemView) {
            super(itemView);
            // IDs del XML item_social_achievement.xml
            ivAvatar = itemView.findViewById(R.id.iv_ach_avatar);
            tvUsername = itemView.findViewById(R.id.tv_ach_username);
            ivTrophy = itemView.findViewById(R.id.iv_ach_trophy);
            tvText = itemView.findViewById(R.id.tv_ach_text);
            ivLike = itemView.findViewById(R.id.iv_ach_like);
            tvLikesCount = itemView.findViewById(R.id.tv_ach_likes_count);
            tvTime = itemView.findViewById(R.id.tv_ach_time);
        }
    }
}