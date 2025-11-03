package com.example.sportine.ui.usuarios.social;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.sportine.R;

import java.util.List;

public class FriendListAdapter extends RecyclerView.Adapter<FriendListAdapter.FriendViewHolder> {

    private List<Friend> friendList;

    public FriendListAdapter(List<Friend> friendList) {
        this.friendList = friendList;
    }

    @NonNull
    @Override
    public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend, parent, false);
        return new FriendViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendViewHolder holder, int position) {
        Friend friend = friendList.get(position);
        holder.tvFriendName.setText(friend.getName());

        Glide.with(holder.itemView.getContext())
                .load(friend.getAvatarUrl())
                .placeholder(R.drawable.avatar_user_female) // Imagen por defecto
                .into(holder.ivFriendAvatar);

        holder.btnRemoveFriend.setOnClickListener(v -> {
            // Lógica para eliminar amigo
            Toast.makeText(v.getContext(), "Eliminar a " + friend.getName(), Toast.LENGTH_SHORT).show();
            friendList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, friendList.size());
        });
    }

    @Override
    public int getItemCount() {
        return friendList.size();
    }

    static class FriendViewHolder extends RecyclerView.ViewHolder {
        ImageView ivFriendAvatar; // <-- Cambio aquí: ShapeableImageView -> ImageView
        TextView tvFriendName;
        Button btnRemoveFriend;

        public FriendViewHolder(@NonNull View itemView) {
            super(itemView);
            ivFriendAvatar = itemView.findViewById(R.id.iv_friend_avatar);
            tvFriendName = itemView.findViewById(R.id.tv_friend_name);
            btnRemoveFriend = itemView.findViewById(R.id.btn_remove_friend);
        }
    }
}
