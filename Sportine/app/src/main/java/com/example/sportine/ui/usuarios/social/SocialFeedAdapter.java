package com.example.sportine.ui.usuarios.social;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout; // <--- ESTA FALTABA
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

        TimeUnit justNowUnit = prettyTime.getUnit(JustNow.class);
        prettyTime.removeUnit(justNowUnit);
        TimeFormat justNowFormat = new SimpleTimeFormat()
                .setSingularName("hace un momento").setPluralName("hace un momento")
                .setPattern("%u").setPastPrefix("").setPastSuffix("")
                .setFuturePrefix("").setFutureSuffix("");
        prettyTime.registerUnit(new JustNow(), justNowFormat);
    }

    @Override
    public int getItemViewType(int position) {
        Integer tipo = publicacionList.get(position).getTipo();
        if (tipo != null && tipo == 2) return TIPO_LOGRO;
        return TIPO_NORMAL;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TIPO_LOGRO) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_social_achievement, parent, false);
            return new AchievementViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_alumno_social_post, parent, false);
            return new PostViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        PublicacionFeedDTO publicacion = publicacionList.get(position);

        if (holder.getItemViewType() == TIPO_LOGRO) {
            bindAchievement((AchievementViewHolder) holder, publicacion);
        } else {
            bindNormalPost((PostViewHolder) holder, publicacion);
        }
    }

    // --- LOGRO (Tarjeta Verde) ---
    private void bindAchievement(AchievementViewHolder holder, PublicacionFeedDTO publicacion) {
        Glide.with(context).load(publicacion.getAutorFotoPerfil())
                .placeholder(R.drawable.avatar_user_male).circleCrop().into(holder.ivAvatar);

        holder.tvUsername.setText(publicacion.getAutorNombreCompleto());
        holder.tvText.setText(publicacion.getDescripcion());

        if (publicacion.getFechaPublicacion() != null) {
            holder.tvTime.setText(prettyTime.format(publicacion.getFechaPublicacion()));
        }

        int likes = publicacion.getTotalLikes();
        holder.tvLikesCount.setText(String.valueOf(likes));

        if (publicacion.isLikedByMe()) {
            holder.ivLike.setImageResource(R.drawable.ic_favorite_black_24dp);
        } else {
            holder.ivLike.setImageResource(R.drawable.ic_favorite_border_black_24dp);
        }

        holder.ivLike.setOnClickListener(v -> toggleLikeGenerico(publicacion, holder.tvLikesCount, holder.ivLike));
    }

    // --- POST NORMAL (Tarjeta Blanca) ---
    private void bindNormalPost(PostViewHolder holder, PublicacionFeedDTO publicacion) {
        String nombre = publicacion.getAutorNombreCompleto() != null ? publicacion.getAutorNombreCompleto() : publicacion.getAutorUsername();
        holder.tvUsername.setText(nombre);

        // Menú de 3 puntitos
        if (publicacion.isMine()) {
            holder.moreOptionsImageView.setVisibility(View.VISIBLE);
            holder.moreOptionsImageView.setOnClickListener(v -> showPopupMenu(v, publicacion, holder.getAdapterPosition()));
        } else {
            holder.moreOptionsImageView.setVisibility(View.GONE);
        }

        // --- DESCRIPCIÓN (TEXTO) ---
        if (publicacion.getDescripcion() != null && !publicacion.getDescripcion().isEmpty()) {
            holder.tvDescription.setVisibility(View.VISIBLE);
            holder.tvDescription.setText(publicacion.getDescripcion());

            // 1. Doble Tap en TEXTO: Solo Like en botón, SIN animación gigante
            final GestureDetector textGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onDown(MotionEvent e) { return true; }

                @Override
                public boolean onDoubleTap(MotionEvent e) {
                    if (!publicacion.isLikedByMe()) {
                        toggleLikeGenerico(publicacion, holder.tvLikesCount, holder.likeButtonImageView);
                    }
                    return true;
                }
            });
            holder.tvDescription.setClickable(true);
            holder.tvDescription.setFocusable(true);
            holder.tvDescription.setOnTouchListener((v, event) -> textGestureDetector.onTouchEvent(event));

        } else {
            holder.tvDescription.setVisibility(View.GONE);
        }

        Glide.with(context).load(publicacion.getAutorFotoPerfil())
                .placeholder(R.drawable.avatar_user_male).circleCrop().into(holder.userAvatarImageView);

        // --- IMAGEN (CON ANIMACIÓN CORAZÓN GIGANTE) ---
        boolean tieneImagen = publicacion.getImagen() != null && !publicacion.getImagen().isEmpty();
        if (tieneImagen) {
            holder.postImageView.setVisibility(View.VISIBLE);
            Glide.with(context).load(publicacion.getImagen()).into(holder.postImageView);

            // 2. Doble Tap en IMAGEN
            final GestureDetector imageGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onDown(MotionEvent e) { return true; }

                @Override
                public boolean onDoubleTap(MotionEvent e) {
                    // CORRECCIÓN: Sumamos getLeft/getTop para ajustar coordenadas relativas al padre
                    float x = e.getX() + holder.postImageView.getLeft();
                    float y = e.getY() + holder.postImageView.getTop();

                    // Animar el corazón
                    animateBigHeart(holder.bigHeartImageView, x, y);

                    // Dar Like
                    if (!publicacion.isLikedByMe()) {
                        toggleLikeGenerico(publicacion, holder.tvLikesCount, holder.likeButtonImageView);
                    }
                    return true;
                }
            });

            holder.postImageView.setOnTouchListener((v, event) -> imageGestureDetector.onTouchEvent(event));

        } else {
            holder.postImageView.setVisibility(View.GONE);
        }

        if (publicacion.getFechaPublicacion() != null) holder.timestampTextView.setText(prettyTime.format(publicacion.getFechaPublicacion()));

        int likes = publicacion.getTotalLikes();
        holder.tvLikesCount.setText(String.valueOf(likes));

        if (publicacion.isLikedByMe()) {
            holder.likeButtonImageView.setImageResource(R.drawable.ic_favorite_black_24dp);
            holder.likeButtonImageView.setColorFilter(Color.RED);
        } else {
            holder.likeButtonImageView.setImageResource(R.drawable.ic_favorite_border_black_24dp);
            holder.likeButtonImageView.setColorFilter(null);
        }

        holder.likeButtonImageView.setOnClickListener(v -> {
            v.performHapticFeedback(android.view.HapticFeedbackConstants.VIRTUAL_KEY);
            v.animate().scaleX(1.2f).scaleY(1.2f).setDuration(100).withEndAction(() -> v.animate().scaleX(1f).scaleY(1f).start()).start();
            toggleLikeGenerico(publicacion, holder.tvLikesCount, holder.likeButtonImageView);
        });
    }

    // --- ANIMACIÓN CORAZÓN (AJUSTADA AL CENTRO) ---
    private void animateBigHeart(ImageView heart, float x, float y) {
        heart.setVisibility(View.VISIBLE);
        heart.setAlpha(1f);

        // Medir si aún no tiene dimensiones
        if (heart.getWidth() == 0 || heart.getHeight() == 0) {
            heart.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        }

        int width = heart.getMeasuredWidth();
        int height = heart.getMeasuredHeight();
        if (width == 0) width = dpToPx(70);
        if (height == 0) height = dpToPx(70);

        // Centrar el corazón en el punto de toque
        float xPosition = x - (width / 2f);
        float yPosition = y - (height / 2f);

        heart.setX(xPosition);
        heart.setY(yPosition);

        heart.setScaleX(0f);
        heart.setScaleY(0f);

        heart.animate()
                .scaleX(1.0f).scaleY(1.0f)
                .setDuration(200)
                .withEndAction(() -> {
                    heart.animate()
                            .scaleX(0.8f).scaleY(0.8f)
                            .alpha(0f)
                            .setDuration(150)
                            .setStartDelay(100)
                            .withEndAction(() -> heart.setVisibility(View.GONE))
                            .start();
                }).start();
    }

    private int dpToPx(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    // --- MÉTODOS DEL MENÚ Y ACCIONES ---
    private void showPopupMenu(View view, PublicacionFeedDTO publicacion, int position) {
        PopupMenu popup = new PopupMenu(context, view);
        if (publicacion.isMine()) {
            popup.getMenu().add("Modificar");
            popup.getMenu().add("Eliminar");
        }

        popup.setOnMenuItemClickListener(item -> {
            String title = item.getTitle().toString();
            if (title.equals("Eliminar")) {
                confirmarEliminar(publicacion, position);
            } else if (title.equals("Modificar")) {
                mostrarDialogoEditar(publicacion, position);
            }
            return true;
        });
        popup.show();
    }

    private void confirmarEliminar(PublicacionFeedDTO publicacion, int position) {
        new AlertDialog.Builder(context)
                .setTitle("Eliminar publicación")
                .setMessage("¿Estás seguro? No podrás recuperarla.")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    apiService.borrarPost(publicacion.getIdPublicacion()).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                publicacionList.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position, publicacionList.size());
                                Toast.makeText(context, "Publicación eliminada", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, "Error al eliminar", Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(context, "Error de conexión", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void mostrarDialogoEditar(PublicacionFeedDTO publicacion, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Editar publicación");

        final EditText input = new EditText(context);
        input.setText(publicacion.getDescripcion());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        builder.setView(input);

        builder.setPositiveButton("Guardar", (dialog, which) -> {
            String nuevoTexto = input.getText().toString();
            if(!nuevoTexto.trim().isEmpty()){
                Publicacion pubActualizada = new Publicacion();
                pubActualizada.setDescripcion(nuevoTexto);
                pubActualizada.setImagen(publicacion.getImagen());

                apiService.editarPost(publicacion.getIdPublicacion(), pubActualizada).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if(response.isSuccessful()){
                            publicacion.setDescripcion(nuevoTexto);
                            notifyItemChanged(position);
                            Toast.makeText(context, "Editado correctamente", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Error al editar", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(context, "Error de conexión", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void toggleLikeGenerico(PublicacionFeedDTO publicacion, TextView tvCounter, ImageView btnLike) {
        Integer postId = publicacion.getIdPublicacion();
        boolean newState = !publicacion.isLikedByMe();
        publicacion.setLikedByMe(newState);

        int current = publicacion.getTotalLikes();
        if (newState) current++; else if (current > 0) current--;
        publicacion.setTotalLikes(current);

        tvCounter.setText(String.valueOf(current));

        if (newState) {
            btnLike.setImageResource(R.drawable.ic_favorite_black_24dp);
            if (publicacion.getTipo() == null || publicacion.getTipo() == 1) {
                btnLike.setColorFilter(Color.RED);
            }
            apiService.darLike(postId).enqueue(new Callback<Void>() { public void onResponse(Call<Void> c, Response<Void> r){} public void onFailure(Call<Void> c, Throwable t){} });
        } else {
            btnLike.setImageResource(R.drawable.ic_favorite_border_black_24dp);
            btnLike.setColorFilter(null);
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

    public static class AchievementViewHolder extends RecyclerView.ViewHolder {
        ImageView ivAvatar, ivTrophy, ivLike;
        TextView tvUsername, tvText, tvTime, tvLikesCount;

        public AchievementViewHolder(@NonNull View itemView) {
            super(itemView);
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