package com.soundpaletteui.Infrastructure.Models.Post;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class CommentModel {
    @SerializedName("CommentText")
    private String commentText;
    @SerializedName("CommentUsername")
    private String commentUsername;
    @SerializedName("CommentDate")
    private Date commentDate;

    public CommentModel(String commentText, String commentUsername, Date commentDate) {
        this.commentText = commentText;
        this.commentUsername = commentUsername;
        this.commentDate = commentDate;
    }

    public String getCommentText() {
        return commentText;
    }
    public String getCommentUsername() {
        return commentUsername;
    }
    public Date getCommentDate() {
        return commentDate;
    }
}
