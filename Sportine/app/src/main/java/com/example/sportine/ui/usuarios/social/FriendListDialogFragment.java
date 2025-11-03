package com.example.sportine.ui.usuarios.social;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sportine.R;

import java.util.ArrayList;
import java.util.List;

public class FriendListDialogFragment extends DialogFragment {

    private RecyclerView rvFriendList;
    private FriendListAdapter friendListAdapter;
    private List<Friend> friendList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_friend_list, container, false);

        rvFriendList = view.findViewById(R.id.rv_friend_list);
        ImageView btnCloseDialog = view.findViewById(R.id.btn_close_dialog);

        // Datos de ejemplo
        friendList = new ArrayList<>();
        friendList.add(new Friend("Amigo 1", ""));
        friendList.add(new Friend("Amigo 2", ""));
        friendList.add(new Friend("Amigo 3", ""));

        friendListAdapter = new FriendListAdapter(friendList);
        rvFriendList.setLayoutManager(new LinearLayoutManager(getContext()));
        rvFriendList.setAdapter(friendListAdapter);

        btnCloseDialog.setOnClickListener(v -> dismiss());

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
    }
}
