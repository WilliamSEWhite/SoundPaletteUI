package com.soundpaletteui.Infrastructure.Models;

import java.util.Date;
import java.util.List;

public class PostModel {
    public int PostId;
    public String PostCaption;
    public List<TagModel> PostTags;
    public PostContentModel PostContent;
    public Date CreatedDate;
    public String CreatedByUsername;
    public int PostType;

    public PostModel()
    {

    }
    public PostModel(int postId, String postCaption, List<TagModel> postTags, PostContentModel postContent, Date createdDate, String createdByUsername, int postType)
    {
        PostId = postId;
        PostCaption = postCaption;
        PostTags = postTags;
        PostContent = postContent;
        CreatedDate = createdDate;
        CreatedByUsername = createdByUsername;
        PostType = postType;
    }
}
