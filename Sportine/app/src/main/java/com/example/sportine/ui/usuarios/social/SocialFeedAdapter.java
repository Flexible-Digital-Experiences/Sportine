package com.example.sportine.ui.usuarios.social;

import android.content.Context;
import android.graphics.Color;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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
    private Context context;

    public SocialFeedAdapter(List<PublicacionFeedDTO> publicacionList, Context context, ApiService apiService) {
        this.publicacionList = publicacionList;
        this.context = context;
        this.apiService = apiService;

        this.prettyTime = new PrettyTime(new Locale("es"));
        TimeUnit justNowUnit = prettyTime.getUnit(JustNow.class);
        prettyTime.removeUnit(justNowUnit);
        TimeFormat justNowFormat = new SimpleTimeFormat()
                .setSingularName("hace un momento")
                .setPluralName("hace un momento")
                .setPattern("%u")
                .setPastPrefix("").setPastSuffix("")
                .setFuturePrefix("").setFutureSuffix("");
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

        // --- 1. DETECTOR DE GESTOS MEJORADO ---
        GestureDetector detector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                // A. Animación Instagram (Posición dinámica)
                // Centramos el corazón donde fue el toque
                float x = holder.postImageView.getX() + e.getX() - (holder.bigHeartImageView.getWidth() / 2f);
                float y = holder.postImageView.getY() + e.getY() - (holder.bigHeartImageView.getHeight() / 2f);

                holder.bigHeartImageView.setX(x);
                holder.bigHeartImageView.setY(y);

                animarCorazonGigante(holder.bigHeartImageView);
                animarLike(holder.likeButtonImageView);

                if (!publicacion.isLikedByMe()) {
                    toggleLike(holder, publicacion);
                }
                return true;
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                // B. Aquí se dispara la navegación al detalle
                abrirDetalleConTransicion(holder, publicacion);
                return true;
            }
        });

        // Asignamos el detector a AMBOS (Tarjeta e Imagen) para que no se bloqueen
        holder.itemView.setOnTouchListener((v, event) -> detector.onTouchEvent(event));
        holder.postImageView.setOnTouchListener((v, event) -> detector.onTouchEvent(event));

        // --- 2. TRANSICIÓN ---
        holder.postImageView.setTransitionName("transicion_post_" + publicacion.getIdPublicacion());

        // --- 3. DATOS ---
        holder.postTitleTextView.setText(publicacion.getDescripcion());

        Glide.with(holder.itemView.getContext())
                .load(publicacion.getAutorFotoPerfil())
                .placeholder(R.drawable.avatar_user_male)
                .error(R.drawable.avatar_user_male)
                .circleCrop()
                .into(holder.userAvatarImageView);

        if (publicacion.getImagen() != null && !publicacion.getImagen().isEmpty()) {
            holder.postImageView.setVisibility(View.VISIBLE);
            Glide.with(holder.itemView.getContext())
                    .load(publicacion.getImagen())
                    .into(holder.postImageView);
        } else {
            holder.postImageView.setVisibility(View.GONE);
        }

        if (publicacion.getFechaPublicacion() != null) {
            holder.timestampTextView.setText(prettyTime.format(publicacion.getFechaPublicacion()));
        } else {
            holder.timestampTextView.setText("");
        }

        // Likes
        int likes = publicacion.getTotalLikes();
        holder.tvLikesCount.setText(String.valueOf(likes));

        updateLikeVisuals(holder, publicacion.isLikedByMe());

        // --- 4. CLICKS ---
        holder.likeButtonImageView.setOnClickListener(v -> {
            v.performHapticFeedback(android.view.HapticFeedbackConstants.VIRTUAL_KEY);
            animarLike(v);
            toggleLike(holder, publicacion);
        });

        holder.commentButtonImageView.setOnClickListener(v -> {
            if (holder.itemView.getContext() instanceof androidx.fragment.app.FragmentActivity) {
                androidx.fragment.app.FragmentActivity activity =
                        (androidx.fragment.app.FragmentActivity) holder.itemView.getContext();
                ComentariosFragment dialog = ComentariosFragment.newInstance(publicacion.getIdPublicacion());
                dialog.show(activity.getSupportFragmentManager(), "ComentariosFragment");
            }
        });

        if (publicacion.isMine()) {
            holder.deleteButtonImageView.setVisibility(View.VISIBLE);
            holder.deleteButtonImageView.setOnClickListener(v -> {
                new android.app.AlertDialog.Builder(holder.itemView.getContext())
                        .setTitle("Borrar publicación")
                        .setMessage("¿Estás seguro?")
                        .setPositiveButton("Borrar", (dialog, which) -> eliminarPost(publicacion.getIdPublicacion(), holder.getAdapterPosition()))
                        .setNegativeButton("Cancelar", null)
                        .show();
            });
        } else {
            holder.deleteButtonImageView.setVisibility(View.GONE);
        }
    }

    // Método helper para la navegación
    private void abrirDetalleConTransicion(PostViewHolder holder, PublicacionFeedDTO publicacion) {
        // 1. Extras para Shared Element
        androidx.navigation.fragment.FragmentNavigator.Extras extras =
                new androidx.navigation.fragment.FragmentNavigator.Extras.Builder()
                        .addSharedElement(holder.postImageView, holder.postImageView.getTransitionName())
                        .build();

        // 2. Argumentos
        android.os.Bundle args = new android.os.Bundle();
        args.putString("imagenUrl", publicacion.getImagen());
        args.putString("descripcion", publicacion.getDescripcion());
        args.putString("transitionName", holder.postImageView.getTransitionName());

        // 3. Navegar
        try {
            androidx.navigation.Navigation.findNavController(holder.itemView).navigate(
                    R.id.action_social_to_detallePost, // ID del NavGraph
                    args,
                    null,
                    extras
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void toggleLike(PostViewHolder holder, PublicacionFeedDTO publicacion) {
        Integer postId = publicacion.getIdPublicacion();
        boolean newState = !publicacion.isLikedByMe();

        publicacion.setLikedByMe(newState);
        int currentLikes = publicacion.getTotalLikes();

        if (newState) currentLikes++;
        else if (currentLikes > 0) currentLikes--;

        publicacion.setTotalLikes(currentLikes);
        holder.tvLikesCount.setText(String.valueOf(currentLikes));
        updateLikeVisuals(holder, newState);

        if (newState) {
            apiService.darLike(postId).enqueue(new Callback<Void>() {
                public void onResponse(Call<Void> call, Response<Void> response) {}
                public void onFailure(Call<Void> call, Throwable t) {}
            });
        } else {
            apiService.quitarLike(postId).enqueue(new Callback<Void>() {
                public void onResponse(Call<Void> call, Response<Void> response) {}
                public void onFailure(Call<Void> call, Throwable t) {}
            });
        }
    }

    private void eliminarPost(Integer postId, int position) {
        apiService.borrarPost(postId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    publicacionList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, publicacionList.size());
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {}
        });
    }

    private void updateLikeVisuals(PostViewHolder holder, boolean isLiked) {
        if (isLiked) {
            holder.likeButtonImageView.setImageResource(R.drawable.ic_favorite_black_24dp);
            holder.likeButtonImageView.setColorFilter(Color.RED);
        } else {
            holder.likeButtonImageView.setImageResource(R.drawable.ic_favorite_border_black_24dp);
            holder.likeButtonImageView.setColorFilter(null);
        }
    }

    private void animarLike(View view) {
        view.animate().scaleX(1.3f).scaleY(1.3f).setDuration(100).withEndAction(() -> {
            view.animate().scaleX(1f).scaleY(1f).setDuration(100)
                    .setInterpolator(new android.view.animation.OvershootInterpolator(4f)).start();
        }).start();
    }

    private void animarCorazonGigante(ImageView heart) {
        heart.setVisibility(View.VISIBLE);
        heart.setAlpha(1f);
        heart.setScaleX(0f);
        heart.setScaleY(0f);
        float randomAngle = (float) (Math.random() * 40 - 20);
        heart.setRotation(randomAngle);

        heart.animate().scaleX(1.3f).scaleY(1.3f).alpha(0f).setDuration(800)
                .setInterpolator(new android.view.animation.DecelerateInterpolator())
                .withEndAction(() -> {
                    heart.setVisibility(View.GONE);
                    heart.setRotation(0);
                }).start();
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
        ImageView commentButtonImageView, deleteButtonImageView, bigHeartImageView;
        TextView postTitleTextView, timestampTextView, tvLikesCount;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            userAvatarImageView = itemView.findViewById(R.id.iv_user_avatar);
            postTitleTextView = itemView.findViewById(R.id.tv_post_title);
            postImageView = itemView.findViewById(R.id.iv_post_image);
            timestampTextView = itemView.findViewById(R.id.tv_post_timestamp);
            likeButtonImageView = itemView.findViewById(R.id.iv_like_button);
            commentButtonImageView = itemView.findViewById(R.id.iv_comment_button);
            deleteButtonImageView = itemView.findViewById(R.id.iv_delete_button);
            tvLikesCount = itemView.findViewById(R.id.tv_likes_count);
            bigHeartImageView = itemView.findViewById(R.id.iv_big_heart);
        }
    }
}