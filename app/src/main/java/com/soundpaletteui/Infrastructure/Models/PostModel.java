package com.soundpaletteui.Infrastructure.Models;

import java.util.Date;
import java.util.List;

public class PostModel {
    private int postId;
    private String postCaption;
    private List<TagModel> postTags;
    private PostContentModel postContent;
    private Date createdDate;
    private String createdByUsername;
    private int postType;

    private int likeCount;
    private int commentCount;

    // Default constructor
    public PostModel() { }

    // Parameterized constructor
    public PostModel(int postId, String postCaption, List<TagModel> postTags, PostContentModel postContent,
                     Date createdDate, String createdByUsername, int postType) {
        this.postId = postId;
        this.postCaption = postCaption;
        this.postTags = postTags;
        this.postContent = postContent;
        this.createdDate = createdDate;
        this.createdByUsername = createdByUsername;
        this.postType = postType;
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

}
