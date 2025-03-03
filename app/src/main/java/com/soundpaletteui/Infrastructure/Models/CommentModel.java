package com.soundpaletteui.Infrastructure.Models;

public class CommentModel {

    private int postId;
    private int userId;
    private String message;

    public CommentModel(int postId, int userId, String message) {
        this.postId = postId;
        this.userId = userId;
        this.message = message;
    }

    public int getPostId() {
        return postId;
    }

    public int getUserId() {
        return userId;
    }

    public String getMessage() {
        return message;
    }
}
