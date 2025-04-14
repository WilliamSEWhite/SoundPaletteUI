package com.soundpaletteui.Infrastructure.Models.Post;

public class NewPostCommentModel {
    public int UserId;
    public int PostId;
    public String CommentContent;
    public NewPostCommentModel(int userId, int postId, String commentContent)
    {
        UserId = userId;
        PostId = postId;
        CommentContent = commentContent;
    }
}
