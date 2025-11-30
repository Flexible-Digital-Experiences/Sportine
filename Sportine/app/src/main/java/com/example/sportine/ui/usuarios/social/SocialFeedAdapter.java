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
import com.example.sportine.models.PublicacionFeedDTO;
import com.example.sportine.models.Publicacion;

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

        // --- 1. DATOS Y VISIBILIDAD (PRIMERO CONFIGURAMOS LA VISTA) ---

        // Avatar
        Glide.with(holder.itemView.getContext())
                .load(publicacion.getAutorFotoPerfil())
                .placeholder(R.drawable.avatar_user_male)
                .error(R.drawable.avatar_user_male)
                .circleCrop()
                .into(holder.userAvatarImageView);

        // Nombre
        String nombreMostrar = publicacion.getAutorNombreCompleto() != null ?
                publicacion.getAutorNombreCompleto() : publicacion.getAutorUsername();
        holder.tvUsername.setText(nombreMostrar);

        // Texto
        if (publicacion.getDescripcion() != null && !publicacion.getDescripcion().isEmpty()) {
            holder.tvDescription.setVisibility(View.VISIBLE);
            holder.tvDescription.setText(publicacion.getDescripcion());
        } else {
            holder.tvDescription.setVisibility(View.GONE);
        }

        // Imagen (Aquí decidimos si es VISIBLE o GONE)
        boolean dataTieneImagen = publicacion.getImagen() != null && !publicacion.getImagen().isEmpty();

        if (dataTieneImagen) {
            holder.postImageView.setVisibility(View.VISIBLE);
            Glide.with(holder.itemView.getContext())
                    .load(publicacion.getImagen())
                    .into(holder.postImageView);
        } else {
            holder.postImageView.setVisibility(View.GONE);
        }

        // Fecha y Likes
        if (publicacion.getFechaPublicacion() != null) {
            holder.timestampTextView.setText(prettyTime.format(publicacion.getFechaPublicacion()));
        } else {
            holder.timestampTextView.setText("");
        }
        int likes = publicacion.getTotalLikes();
        holder.tvLikesCount.setText(String.valueOf(likes));
        updateLikeVisuals(holder, publicacion.isLikedByMe());


        // --- 2. DETECTOR DE GESTOS (AHORA USA LA VISIBILIDAD REAL) ---
        GestureDetector detector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) { return true; }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                // CORRECCIÓN MAESTRA:
                // Solo animamos el corazón gigante si la imagen es REALMENTE VISIBLE
                if (holder.postImageView.getVisibility() == View.VISIBLE) {
                    float x = holder.postImageView.getX() + e.getX() - (holder.bigHeartImageView.getWidth() / 2f);
                    float y = holder.postImageView.getY() + e.getY() - (holder.bigHeartImageView.getHeight() / 2f);

                    holder.bigHeartImageView.setX(x);
                    holder.bigHeartImageView.setY(y);
                    animarCorazonGigante(holder.bigHeartImageView);
                }

                // El like normal (botón pequeño) siempre funciona
                animarLike(holder.likeButtonImageView);

                if (!publicacion.isLikedByMe()) {
                    toggleLike(holder, publicacion);
                }
                return true;
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                // Solo abrimos detalle si la imagen es VISIBLE
                if (holder.postImageView.getVisibility() == View.VISIBLE) {
                    abrirDetalleConTransicion(holder, publicacion);
                }
                return true;
            }
        });

        // Asignamos listeners
        holder.itemView.setOnTouchListener((v, event) -> detector.onTouchEvent(event));

        // Solo asignamos touch a la imagen si está visible, si no, null
        if (holder.postImageView.getVisibility() == View.VISIBLE) {
            holder.postImageView.setOnTouchListener((v, event) -> detector.onTouchEvent(event));
        } else {
            holder.postImageView.setOnTouchListener(null);
        }

        // Transición
        holder.postImageView.setTransitionName("transicion_post_" + publicacion.getIdPublicacion());

        // --- 3. CLICKS BOTONES ---
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
            holder.moreOptionsImageView.setVisibility(View.VISIBLE);
            holder.moreOptionsImageView.setOnClickListener(v -> {
                PopupMenu popup = new PopupMenu(context, holder.moreOptionsImageView);
                popup.getMenu().add(0, 1, 0, "Editar");
                popup.getMenu().add(0, 2, 1, "Eliminar");

                popup.setOnMenuItemClickListener(item -> {
                    if (item.getItemId() == 1) {
                        mostrarDialogoEditar(publicacion, holder.getAdapterPosition());
                        return true;
                    } else if (item.getItemId() == 2) {
                        confirmarEliminacion(publicacion.getIdPublicacion(), holder.getAdapterPosition());
                        return true;
                    }
                    return false;
                });
                popup.show();
            });
        } else {
            holder.moreOptionsImageView.setVisibility(View.GONE);
        }
    }

    // ... (El resto de los métodos privados toggleLike, eliminarPost, etc. se quedan igual) ...
    // COPIALOS DE TU VERSIÓN ANTERIOR O TE LOS PONGO AQUÍ ABAJO SI LOS NECESITAS

    private void mostrarDialogoEditar(PublicacionFeedDTO publicacion, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_editar_post, null);
        EditText etDescripcion = view.findViewById(R.id.et_editar_descripcion);
        etDescripcion.setText(publicacion.getDescripcion());
        new android.app.AlertDialog.Builder(context)
                .setView(view)
                .setPositiveButton("Guardar", (dialog, which) -> {
                    String nuevoTexto = etDescripcion.getText().toString().trim();
                    if (!nuevoTexto.isEmpty()) guardarEdicion(publicacion, position, nuevoTexto);
                })
                .setNegativeButton("Cancelar", null).show();
    }

    private void guardarEdicion(PublicacionFeedDTO publicacion, int position, String nuevoTexto) {
        Publicacion datosActualizados = new Publicacion();
        datosActualizados.setDescripcion(nuevoTexto);
        datosActualizados.setImagen(publicacion.getImagen());
        apiService.editarPost(publicacion.getIdPublicacion(), datosActualizados).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    publicacion.setDescripcion(nuevoTexto);
                    notifyItemChanged(position);
                    Toast.makeText(context, "Post actualizado", Toast.LENGTH_SHORT).show();
                } else Toast.makeText(context, "Error al editar", Toast.LENGTH_SHORT).show();
            }
            @Override public void onFailure(Call<Void> call, Throwable t) { Toast.makeText(context, "Error de conexión", Toast.LENGTH_SHORT).show(); }
        });
    }

    private void confirmarEliminacion(Integer postId, int position) {
        new android.app.AlertDialog.Builder(context).setTitle("Borrar publicación").setMessage("¿Estás seguro?")
                .setPositiveButton("Eliminar", (dialog, which) -> eliminarPost(postId, position))
                .setNegativeButton("Cancelar", null).show();
    }

    private void abrirDetalleConTransicion(PostViewHolder holder, PublicacionFeedDTO publicacion) {
        androidx.navigation.fragment.FragmentNavigator.Extras extras = new androidx.navigation.fragment.FragmentNavigator.Extras.Builder().addSharedElement(holder.postImageView, holder.postImageView.getTransitionName()).build();
        android.os.Bundle args = new android.os.Bundle();
        args.putString("imagenUrl", publicacion.getImagen());
        args.putString("descripcion", publicacion.getDescripcion());
        args.putString("transitionName", holder.postImageView.getTransitionName());
        try { androidx.navigation.Navigation.findNavController(holder.itemView).navigate(R.id.action_social_to_detallePost, args, null, extras); } catch (Exception e) { e.printStackTrace(); }
    }

    private void toggleLike(PostViewHolder holder, PublicacionFeedDTO publicacion) {
        Integer postId = publicacion.getIdPublicacion();
        boolean newState = !publicacion.isLikedByMe();
        publicacion.setLikedByMe(newState);
        int currentLikes = publicacion.getTotalLikes();
        if (newState) currentLikes++; else if (currentLikes > 0) currentLikes--;
        publicacion.setTotalLikes(currentLikes);
        holder.tvLikesCount.setText(String.valueOf(currentLikes));
        updateLikeVisuals(holder, newState);
        if (newState) apiService.darLike(postId).enqueue(new Callback<Void>() { public void onResponse(Call<Void> call, Response<Void> response) {} public void onFailure(Call<Void> call, Throwable t) {} });
        else apiService.quitarLike(postId).enqueue(new Callback<Void>() { public void onResponse(Call<Void> call, Response<Void> response) {} public void onFailure(Call<Void> call, Throwable t) {} });
    }

    private void eliminarPost(Integer postId, int position) {
        apiService.borrarPost(postId).enqueue(new Callback<Void>() {
            @Override public void onResponse(Call<Void> call, Response<Void> response) { if (response.isSuccessful()) { publicacionList.remove(position); notifyItemRemoved(position); notifyItemRangeChanged(position, publicacionList.size()); } }
            @Override public void onFailure(Call<Void> call, Throwable t) {}
        });
    }

    private void updateLikeVisuals(PostViewHolder holder, boolean isLiked) {
        if (isLiked) { holder.likeButtonImageView.setImageResource(R.drawable.ic_favorite_black_24dp); holder.likeButtonImageView.setColorFilter(Color.RED); }
        else { holder.likeButtonImageView.setImageResource(R.drawable.ic_favorite_border_black_24dp); holder.likeButtonImageView.setColorFilter(null); }
    }

    private void animarLike(View view) {
        view.animate().scaleX(1.3f).scaleY(1.3f).setDuration(100).withEndAction(() -> view.animate().scaleX(1f).scaleY(1f).setDuration(100).setInterpolator(new android.view.animation.OvershootInterpolator(4f)).start()).start();
    }

    private void animarCorazonGigante(ImageView heart) {
        heart.setVisibility(View.VISIBLE); heart.setAlpha(1f); heart.setScaleX(0f); heart.setScaleY(0f);
        float randomAngle = (float) (Math.random() * 40 - 20); heart.setRotation(randomAngle);
        heart.animate().scaleX(1.3f).scaleY(1.3f).alpha(0f).setDuration(800).setInterpolator(new android.view.animation.DecelerateInterpolator()).withEndAction(() -> { heart.setVisibility(View.GONE); heart.setRotation(0); }).start();
    }

    @Override public int getItemCount() { return publicacionList.size(); }
    public void setPublicaciones(List<PublicacionFeedDTO> nuevasPublicaciones) { this.publicacionList.clear(); this.publicacionList.addAll(nuevasPublicaciones); notifyDataSetChanged(); }

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
}