package com.example.sportine.ui.usuarios.social;

import android.app.AlertDialog; // <-- ¡IMPORTANTE! Para el diálogo de confirmación
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast; // <-- ¡IMPORTANTE!

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
import androidx.fragment.app.FragmentActivity;

import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SocialFeedAdapter extends RecyclerView.Adapter<SocialFeedAdapter.PostViewHolder> {

    private List<PublicacionFeedDTO> publicacionList;
    private PrettyTime prettyTime;
    private ApiService apiService;
    private static final String TAG = "SocialDebug";

    public SocialFeedAdapter(List<PublicacionFeedDTO> publicacionList, Context context, ApiService apiService) {
        this.publicacionList = publicacionList;
        this.apiService = apiService;

        this.prettyTime = new PrettyTime(new Locale("es"));

        TimeUnit justNowUnit = prettyTime.getUnit(JustNow.class);
        prettyTime.removeUnit(justNowUnit);

        SimpleTimeFormat customFormat = new SimpleTimeFormat()
                .setSingularName("hace un momento")
                .setPluralName("hace un momento")
                .setPattern("%u")
                .setPastPrefix("")
                .setPastSuffix("")
                .setFuturePrefix("")
                .setFutureSuffix("");


        prettyTime.registerUnit(new JustNow(), customFormat);
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
            String tiempoBonito = prettyTime.format(publicacion.getFechaPublicacion());
            holder.timestampTextView.setText(tiempoBonito);
        } else {
            holder.timestampTextView.setText("");
        }


        updateLikeVisuals(holder, publicacion.isLikedByMe());
        holder.likeButtonImageView.setOnClickListener(v -> {

            Integer postId = publicacion.getIdPublicacion();
            boolean isCurrentlyLiked = publicacion.isLikedByMe();
            boolean newState = !isCurrentlyLiked;
            publicacion.setLikedByMe(newState);

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
            updateLikeVisuals(holder, newState);
        });


        holder.commentButtonImageView.setOnClickListener(v -> {

            // Obtenemos el contexto y lo convertimos a Activity para poder mostrar el BottomSheet
            if (holder.itemView.getContext() instanceof androidx.fragment.app.FragmentActivity) {

                androidx.fragment.app.FragmentActivity activity =
                        (androidx.fragment.app.FragmentActivity) holder.itemView.getContext();

                // Creamos y mostramos el fragmento de comentarios
                ComentariosFragment dialog = ComentariosFragment.newInstance(publicacion.getIdPublicacion());
                dialog.show(activity.getSupportFragmentManager(), "ComentariosFragment");
            }
        });

        if (publicacion.isMine()) {
            holder.deleteButtonImageView.setVisibility(View.VISIBLE);

            holder.deleteButtonImageView.setOnClickListener(v -> {
                // Diálogo de confirmación
                new AlertDialog.Builder(holder.itemView.getContext())
                        .setTitle("Borrar publicación")
                        .setMessage("¿Estás seguro? No podrás deshacer esto.")
                        .setPositiveButton("Borrar", (dialog, which) -> {
                            // Llamamos al método para borrar
                            eliminarPost(publicacion.getIdPublicacion(), holder.getAdapterPosition());
                        })
                        .setNegativeButton("Cancelar", null)
                        .show();
            });
        } else {
            holder.deleteButtonImageView.setVisibility(View.GONE);
        }
    }

    private void eliminarPost(Integer postId, int position) {
        apiService.borrarPost(postId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // Borrado exitoso en servidor -> Borramos de la lista visual
                    publicacionList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, publicacionList.size());
                    Log.d(TAG, "Post borrado con éxito");
                } else {
                    Log.e(TAG, "Error al borrar: " + response.code());
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Fallo de red al borrar: " + t.getMessage());
            }
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
        // Nuevos íconos
        ImageView commentButtonImageView, deleteButtonImageView;
        TextView postTitleTextView, timestampTextView;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            userAvatarImageView = itemView.findViewById(R.id.iv_user_avatar);
            postTitleTextView = itemView.findViewById(R.id.tv_post_title);
            postImageView = itemView.findViewById(R.id.iv_post_image);
            timestampTextView = itemView.findViewById(R.id.tv_post_timestamp);
            likeButtonImageView = itemView.findViewById(R.id.iv_like_button);
            // Enlazamos los nuevos íconos del XML
            commentButtonImageView = itemView.findViewById(R.id.iv_comment_button);
            deleteButtonImageView = itemView.findViewById(R.id.iv_delete_button);
        }
    }
}