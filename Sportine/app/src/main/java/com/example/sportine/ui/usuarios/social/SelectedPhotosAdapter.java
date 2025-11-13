package com.example.sportine.ui.usuarios.social;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide; // ¡Importante para cargar imágenes!
import com.example.sportine.R;
import java.util.List;

public class SelectedPhotosAdapter extends RecyclerView.Adapter<SelectedPhotosAdapter.PhotoViewHolder> {

    private List<Uri> photoUris;

    public SelectedPhotosAdapter(List<Uri> photoUris) {
        this.photoUris = photoUris;
    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_selected_photo, parent, false);
        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {
        Uri uri = photoUris.get(position);

        // Carga la imagen con Glide (más eficiente)
        Glide.with(holder.itemView.getContext())
                .load(uri)
                .centerCrop()
                .into(holder.photoImageView);

        holder.removeButton.setVisibility(View.VISIBLE);

        holder.removeButton.setOnClickListener(v -> {
            int currentPosition = holder.getAdapterPosition();
            if (currentPosition != RecyclerView.NO_POSITION) {
                photoUris.remove(currentPosition);
                notifyItemRemoved(currentPosition);
                notifyItemRangeChanged(currentPosition, photoUris.size());
            }
        });
    }

    @Override
    public int getItemCount() {
        return photoUris.size();
    }

    public static class PhotoViewHolder extends RecyclerView.ViewHolder {
        ImageView photoImageView;
        ImageView removeButton;

        public PhotoViewHolder(@NonNull View itemView) {
            super(itemView);
            photoImageView = itemView.findViewById(R.id.iv_selected_photo);
            removeButton = itemView.findViewById(R.id.btn_remove_photo);
        }
    }
}