package com.example.sportine.ui.usuarios.social;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
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
    private static final String TAG = "LikeDebug";
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
                .setPastPrefix("")
                .setPastSuffix("")
                .setFuturePrefix("")
                .setFutureSuffix("");


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


        GestureDetector detector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                toggleLike(holder, publicacion); // ¡Like al hacer doble tap!
                return true;
            }
        });


        holder.itemView.setOnTouchListener((v, event) -> {
            return detector.onTouchEvent(event);
        });

        // --- 2. DATOS ---
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

        updateLikeVisuals(holder, publicacion.isLikedByMe());

        holder.likeButtonImageView.setOnClickListener(v -> toggleLike(holder, publicacion));

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

    private void toggleLike(PostViewHolder holder, PublicacionFeedDTO publicacion) {
        Integer postId = publicacion.getIdPublicacion();
        boolean isCurrentlyLiked = publicacion.isLikedByMe();
        boolean newState = !isCurrentlyLiked;

        publicacion.setLikedByMe(newState);
        updateLikeVisuals(holder, newState);

        if (newState) {
            publicacion.setTotalLikes(publicacion.getTotalLikes() + 1);
            apiService.darLike(postId).enqueue(new Callback<Void>() {
                public void onResponse(Call<Void> call, Response<Void> response) {}
                public void onFailure(Call<Void> call, Throwable t) {}
            });
        } else {
            publicacion.setTotalLikes(publicacion.getTotalLikes() - 1);
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

    @Override
    public int getItemCount() { return publicacionList.size(); }
    public void setPublicaciones(List<PublicacionFeedDTO> nuevasPublicaciones) {
        this.publicacionList.clear();
        this.publicacionList.addAll(nuevasPublicaciones);
        notifyDataSetChanged();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        ImageView userAvatarImageView, postImageView, likeButtonImageView;
        ImageView commentButtonImageView, deleteButtonImageView;
        TextView postTitleTextView, timestampTextView;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            userAvatarImageView = itemView.findViewById(R.id.iv_user_avatar);
            postTitleTextView = itemView.findViewById(R.id.tv_post_title);
            postImageView = itemView.findViewById(R.id.iv_post_image);
            timestampTextView = itemView.findViewById(R.id.tv_post_timestamp);
            likeButtonImageView = itemView.findViewById(R.id.iv_like_button);
            commentButtonImageView = itemView.findViewById(R.id.iv_comment_button);
            deleteButtonImageView = itemView.findViewById(R.id.iv_delete_button);
        }
    }
}