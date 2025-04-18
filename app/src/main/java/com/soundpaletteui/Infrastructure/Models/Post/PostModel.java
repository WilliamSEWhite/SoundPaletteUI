package com.soundpaletteui.Infrastructure.Models.Post;

import com.google.gson.annotations.SerializedName;
import com.soundpaletteui.Infrastructure.Models.TagModel;
import com.soundpaletteui.Infrastructure.Models.User.UserProfileModelLite;

import java.util.Date;
import java.util.List;

public class PostModel {
    @SerializedName("PostId")
    private int postId;
    @SerializedName("PostCaption")
    private String postCaption;
    @SerializedName("PostTags")
    private List<TagModel> postTags;
    @SerializedName("PostUserTags")
    private List<String> postUserTags;
    @SerializedName("PostContent")
    private PostContentModel postContent;
    @SerializedName("CreatedDate")
    private Date createdDate;
    @SerializedName("CreatedByUsername")
    private String createdByUsername;
    @SerializedName("PostType")
    private int postType;
    @SerializedName("CommentCount")
    private int commentCount;
    @SerializedName("LikeCount")
    private int likeCount;
    @SerializedName("IsLiked")
    private boolean isLiked;
    @SerializedName("IsSaved")
    private boolean isSaved;

    // Parameterized constructor
    public PostModel(int postId, String postCaption, List<TagModel> postTags, PostContentModel postContent,
                     Date createdDate, String createdByUsername, int postType, int commentCount, int likeCount, boolean isLiked, boolean isSaved, List<String> postUserTags) {
        this.postId = postId;
        this.postCaption = postCaption;
        this.postTags = postTags;
        this.postContent = postContent;
        this.createdDate = createdDate;
        this.createdByUsername = createdByUsername;
        this.postType = postType;
        this.commentCount = commentCount;
        this.likeCount = likeCount;
        this.isLiked = isLiked;
        this.isSaved = isSaved;
        this.postUserTags = postUserTags;
    }

    // Getters
    public int getPostId() {
        return postId;
    }

    public String getPostCaption() {
        return postCaption;
    }

    public List<TagModel> getPostTags() {
        return postTags;
    }

    public List<String> getPostUserTags() {
        return postUserTags;
    }

    public PostContentModel getPostContent() {
        return postContent;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public String getUsername() {
        return createdByUsername;
    }

    public int getPostType() {
        return postType;
    }
    public int getCommentCount() {
        return commentCount;
    }
    public int getLikeCount() {
        return likeCount;
    }
    public boolean getIsLiked() {
        return isLiked;
    }
    public void setIsLiked(boolean isLiked) {
        this.isLiked = isLiked;
    }
    public boolean getIsSaved() {
        return isSaved;
    }
    public void setIsSaved(boolean isSaved) {
        this.isSaved = isSaved;
    }
    public void setLikeCount(int likeCount) {this.likeCount = likeCount;}
    public void setCommentCount(int count) {this.commentCount = count;}

}
