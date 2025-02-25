package com.soundpaletteui.Infrastructure.Models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NewPostModel {
    @SerializedName("userId")
    public int UserId;
    @SerializedName("postTypeId")
    public int PostTypeId;
    @SerializedName("caption")
    public String Caption;
    @SerializedName("isMature")
    public boolean IsMature;

    @SerializedName("isPremium")
    public boolean IsPremium;
    @SerializedName("createdDate")
    public Date CreatedDate;
    @SerializedName("publishDate")
    public Date PublishDate;

    @SerializedName("postTags")
    public ArrayList<TagModel> PostTags;
    @SerializedName("postTextContent")
    public String PostTextContent;

    public NewPostModel(int userId, int postTypeId,String caption, boolean isPremium, boolean isMature, Date createdDate, Date publishDate, ArrayList<TagModel> postTags, String postTextContent) {
        UserId = userId;
        PostTypeId = postTypeId;
        Caption = caption;
        IsPremium = isPremium;
        IsMature = isMature;
        CreatedDate = createdDate;
        PublishDate = publishDate;
        PostTags = postTags;
        PostTextContent = postTextContent;
    }
}