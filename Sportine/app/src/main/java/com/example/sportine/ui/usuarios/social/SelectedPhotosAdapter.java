package com.example.sportine.ui.usuarios.social;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.sportine.R;

import java.util.List;

public class SelectedPhotosAdapter extends RecyclerView.Adapter<SelectedPhotosAdapter.PhotoViewHolder> {

    private List<Uri> photos;

    public SelectedPhotosAdapter(List<Uri> photos) {
        this.photos = photos;
    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_selected_photo, parent, false);
        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {
        Uri photoUri = photos.get(position);
        Glide.with(holder.itemView.getContext())
                .load(photoUri)
                .into(holder.ivSelectedPhoto);
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    static class PhotoViewHolder extends RecyclerView.ViewHolder {
        ImageView ivSelectedPhoto;

        public PhotoViewHolder(@NonNull View itemView) {
            super(itemView);
            ivSelectedPhoto = itemView.findViewById(R.id.iv_selected_photo);
        }
    }
}
