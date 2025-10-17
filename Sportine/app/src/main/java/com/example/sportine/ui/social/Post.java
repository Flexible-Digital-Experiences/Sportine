package com.example.sportine.ui.social;

public class Post {
    private String userName;
    private String message;
    private int userAvatarResId;
    private int postImageResId;
    private String timestamp;

    private boolean isLiked = false;
    public Post(String userName, String message, int userAvatarResId, String timestamp) {
        this.userName = userName;
        this.message = message;
        this.userAvatarResId = userAvatarResId;
        this.postImageResId = 0; // 0 significa que no hay imagen
        this.timestamp = timestamp;
    }

    public Post(String userName, String message, int userAvatarResId, int postImageResId, String timestamp) {
        this.userName = userName;
        this.message = message;
        this.userAvatarResId = userAvatarResId;
        this.postImageResId = postImageResId;
        this.timestamp = timestamp;
    }

    // Getters
    public String getUserName() { return userName; }
    public String getMessage() { return message; }
    public int getUserAvatarResId() { return userAvatarResId; }
    public int getPostImageResId() { return postImageResId; }
    public String getTimestamp() { return timestamp; }
    public boolean isLiked() {
        return isLiked;
    }
    public void setLiked(boolean liked) {
        isLiked = liked;
    }
}
