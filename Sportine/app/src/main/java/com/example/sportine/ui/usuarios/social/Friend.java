package com.example.sportine.ui.usuarios.social;

public class Friend {
    private String name;
    private String avatarUrl;

    public Friend(String name, String avatarUrl) {
        this.name = name;
        this.avatarUrl = avatarUrl;
    }

    public String getName() {
        return name;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }
}
